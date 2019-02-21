/*
 * Copyright 2018 The Isocline Project
 *
 * The Isocline Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package isocline.clockwork;

import isocline.clockwork.event.EventRepository;
import isocline.clockwork.event.EventSet;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;


/**
 *
 */
public class WorkSchedule {

    private static final long UNDEFINED_INTERVAL = -1;


    private String workUuid;

    private long waitTime = 0;

    private long nextExecuteTime = 0;



    private long workEndTime = 0;

    private boolean isLock = false;

    private boolean isStart = false;

    private long intervalTime = UNDEFINED_INTERVAL;

    private long jitter = 0;

    private long contextCheckId;

    private boolean isStrictMode = true;

    private boolean isBetweenStartTimeMode = true;

    private boolean needWaiting = false;

    private static long preemptiveCheckMilliTime = 2;


    private Work work;

    private WorkSession workSession = null;

    private Object lockOwner = null;

    private ClockWorker clockWorker = null;

    private LinkedList<EventInfo> eventList = new LinkedList<EventInfo>();


    private ProcessFlow processFlow = null;





    WorkSchedule(ClockWorker clockWorker, Work work) {
        this.clockWorker = clockWorker;
        this.work = work;

        this.workUuid = UUID.randomUUID().toString();
    }


    public String getId() {
        return this.workUuid;
    }

    //
    private void checkLocking() throws RuntimeException {
        if (isLock) {
            throw new RuntimeException("Changing settings is prohibited.");
        }

    }



    synchronized void adjustWaiting()  throws InterruptedException{
        if(this.isStrictMode && needWaiting) {


            long gap = this.nextExecuteTime - System.currentTimeMillis();


            //this.wait(0,1);
            /*
            if(gap>1) {
                //Thread.sleep((gap-1),100);
                Thread.sleep(0,1);
            }
            */

            for(int i=0;i<10000000;i++) {
                /*
                if(nextExecuteNanoTime<=nanoTime()) {
                    //System.out.println(">>                           "+gap+" "+gap2+ " "+i+ " "+this.nextExecuteTime + " "+this.jitter + " "+System.currentTimeMillis());
                    return;
                }
                */
                if(nextExecuteTime<=System.currentTimeMillis()) {
                    //System.out.println(" . "+i);
                    return;
                }

            }

        }

    }





    long getPreemptiveMilliTime() {
        if(this.isStrictMode) {
            return preemptiveCheckMilliTime;
        }else {
            return 0;
        }

    }

    long checkRemainMilliTime(){
        needWaiting = false;

        if(!isStart) {
            throw new RuntimeException("service end");
        }
        if (isEnd()) {
            return Long.MAX_VALUE;
        }




        if (this.waitTime == 0 ) {
            if(this.isStrictMode) {
                needWaiting = true;
            }

            return 0;
        } else if (this.eventList.size() > 0) {
            return 0;
        } else if (this.nextExecuteTime > 0 ) {

            long t1 = this.nextExecuteTime  - System.currentTimeMillis();

            //System.out.print("("+t1+") "+System.currentTimeMillis());
            //System.err.print("("+nextExecuteNanoTime+")_ ");
            //System.err.print("("+System.nanoTime()+"). ");
            if(t1<=0) {
                return t1;
            }else  if(this.isStrictMode && t1<this.getPreemptiveMilliTime()) {
                needWaiting = true;
                return t1;
            }

            return t1;

        }



        return Clock.HOUR;
    }


    long getNextExecuteTime() {
        return this.nextExecuteTime;
    }


    boolean isEnd() {
        if (this.workEndTime > 0) {
            if (System.currentTimeMillis() >= this.workEndTime) {
                return true;
            }
        }

        return false;
    }


    // public method


    public void lock(Object lockOwner) throws IllegalAccessException {
        if (this.lockOwner != null) {
            throw new IllegalAccessException("Already locking");
        }
        this.lockOwner = lockOwner;
        this.isLock = true;
    }

    public void unlock(Object lockOwner) throws IllegalAccessException {
        if (lockOwner == this.lockOwner) {
            this.isLock = false;
            this.lockOwner = null;
        } else {
            throw new IllegalAccessException("not lock owner");
        }
    }


    public Work getWorkObject() {
        return this.work;
    }

    private boolean isFirstDelaySet = false;


    public WorkSchedule setStartTime(long nextExecuteTime) {

        if(waitTime==0) {
            waitTime = 1;
        }



        this.nextExecuteTime  = nextExecuteTime;



        return this;
    }

    public WorkSchedule setStartDelay(long waitTime) {
        isFirstDelaySet=true;

        checkLocking();
        this.waitTime = waitTime;

        if (waitTime < 0) {
            this.nextExecuteTime = UNDEFINED_INTERVAL;


        } else {

            if(this.isBetweenStartTimeMode && this.nextExecuteTime>0) {




                    long tmp = System.currentTimeMillis()-nextExecuteTime;


                    if(tmp>0) {
                        long x = (long) Math.ceil(  (double) tmp/ (double) waitTime );

                        setStartTime( this.nextExecuteTime + waitTime*(x) );

                        return this;
                    }else if(tmp==0) {
                        setStartTime( this.nextExecuteTime + waitTime );
                        return this;
                    }


            }

            long crntTime = System.currentTimeMillis();
            long adjCrntTime = crntTime;
            if(this.isStrictMode) {
                for(int i=0;i<5;i++) {
                    long adjTime = (crntTime - (crntTime%1000)) + this.jitter + i*1000;
                    long nextTime = adjTime + waitTime;

                    if((crntTime+this.jitter)<nextTime) {
                        adjCrntTime = adjTime;
                        break;
                    }
                }
            }




            long chkTime = adjCrntTime + waitTime;
            if (this.nextExecuteTime < chkTime) {
                setStartTime(chkTime);

            }else {
                System.err.println("overtime : "+this.nextExecuteTime);
            }


        }


        return this;
    }


    public long getIntervalTime() {
        return this.intervalTime;
    }

    public WorkSchedule setRepeatInterval(long intervalTime) {

        this.intervalTime = intervalTime;
        return setStartDelay(intervalTime);

    }




    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime
     */
    public WorkSchedule setStartDateTime(String isoDateTime) throws java.text.ParseException {

        return setStartDateTime(Clock.getDate(isoDateTime));
    }


    public WorkSchedule setStartDateTime(Date startDateTime) {

        this.setStartTime(startDateTime.getTime());
        return this;
    }


    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime
     */
    public WorkSchedule setEndDateTime(String isoDateTime) throws java.text.ParseException {

        return setEndDateTime(Clock.getDate(isoDateTime));
    }

    public WorkSchedule setEndDateTime(Date endDateTime) {

        this.workEndTime = endDateTime.getTime();
        return this;
    }

    public WorkSchedule setFinishTimeFromNow(long milliSeconds) {
        this.workEndTime = System.currentTimeMillis()+milliSeconds;
        return this;
    }


    public WorkSchedule setWorkSession(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        checkLocking();
        this.workSession = (WorkSession) Class.forName(className).newInstance();
        return this;
    }


    public WorkSchedule setWorkSession(WorkSession workSession)  {
        checkLocking();

        this.workSession = workSession;
        return this;
    }


    public WorkSession getWorkSession() {

        if (this.workSession == null) {
            this.workSession = new BasicWorkSession();

        }

        return this.workSession;
    }


    public WorkSchedule setProcessFlow(ProcessFlow processFlow)  {
        checkLocking();

        this.processFlow = processFlow;
        return this;
    }


    public ProcessFlow getProcessFlow() {

        return this.processFlow;
    }


    ////////////////



    public void raiseLocalEvent(EventInfo event) {

        this.clockWorker.addWorkSchedule(this, event);

    }




    public WorkSchedule bindEvent(String... eventNames) {
        checkLocking();

        for(String eventName:eventNames) {
            String[] subEventNames = eventRepository.setBindEventNames(eventName);
            for(String subEventName:subEventNames) {
                this.clockWorker.bindEvent( this, subEventName);
            }
        }
        return this;
    }


    public WorkSchedule setStrictMode(boolean isStrictMode) {
        checkLocking();
        this.isStrictMode = isStrictMode;
        return this;
    }

    public WorkSchedule setBetweenStartTimeMode(boolean isBetweenStartTimeMode) {
        checkLocking();
        this.isBetweenStartTimeMode = isBetweenStartTimeMode;
        return this;
    }

    public WorkSchedule setJitter(long jitter) {
        checkLocking();
        this.jitter = jitter;
        return this;
    }

    public WorkSchedule setSleepMode() {
        checkLocking();
        this.setStartDelay(Work.SLEEP);
        return this;
    }

    public WorkSchedule activate() {
        return activate(false);
    }

    public WorkSchedule activate(boolean checkActivated) {
        if (isStart) {
            if(checkActivated) {
                throw new RuntimeException("Already activate!");
            }else {
                return this;
            }
        }
        this.isStart = true;

        /*
        if(!isFirstDelaySet) {
            long s1 = System.currentTimeMillis();
            long s2 = s1%1000;
            if(s2>900) {
                long nextExecuteTime = (s1-s2)
                this.setStartDateTime()
            }

            this.setStartDelay(1000);
        }
        */

        if(this.waitTime!=Work.SLEEP) {
            this.clockWorker.addWorkSchedule(this);
        }


        this.clockWorker.managedWorkCount.incrementAndGet();

        return this;
    }

    public WorkSchedule setScheduleConfig(ScheduleConfig config) {
        config.setup(this);

        return this;
    }


    public void finish() {
        if(this.isStart) {
            this.isStart = false;
            this.clockWorker.managedWorkCount.decrementAndGet();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkSchedule schedule = (WorkSchedule) o;

        return workUuid.equals(schedule.workUuid);
    }

    @Override
    public int hashCode() {
        return workUuid.hashCode();
    }


    public ClockWorker getClockWorker() {
        return clockWorker;
    }


    ///////////////////////////////////



    private EventInfo defaultEvent = null;

    EventInfo getDefaultEventInfo() {
        if(defaultEvent==null) {
            defaultEvent = new EventInfo();
            defaultEvent.setWorkSechedule(this);
        }

        return defaultEvent;

    }



    private EventRepository eventRepository = new EventRepository();



    String checkRaiseEventEnable(String eventName) {



        EventSet eventSet = eventRepository.getEventSet(eventName);


        if(eventSet==null) {
            //System.err.println("checkRaiseEventEnable normal = "+eventName );
            return eventName;
        }


        if(eventSet.isRaiseEventReady(eventName)) {

            //System.err.println("checkRaiseEventEnable event="+eventName + " set="+eventSet + " fire="+eventSet.getEventSetName());
            return eventSet.getEventSetName();
        }


        return null;

    }


    ////////////////////////////





    ExecuteContext enterQueue(boolean isUserEvent, EventInfo eventInfo) {


        ExecuteContext ctx = new ExecuteContext(this,isUserEvent, eventInfo);

        return ctx;
    }


    ExecuteContext enterQueue(boolean isUserEvent) {

        ExecuteContext ctx = new ExecuteContext(this,isUserEvent ,null);


        return ctx;
    }


    /**
     * ExecuteContext for Queue
     */
    static class ExecuteContext {



        private WorkSchedule workSchedule;

        private boolean isUserEvent = false;

        private EventInfo eventInfo;



        ExecuteContext(WorkSchedule workSchedule, boolean isUserEvent, EventInfo event) {

            this.workSchedule = workSchedule;
            this.isUserEvent = isUserEvent;
            this.eventInfo = event;

        }



        boolean isExecuteImmediately() {
            if(isUserEvent) {
                this.isUserEvent = false;

                return true;
            }

            return false;
        }

        WorkSchedule getWorkSchedule() {

            //if (this.contextId == this.workSchedule.contextCheckId)
            {
                return workSchedule;
            }

            //return null;

        }

        EventInfo getEventInfo() {
            return this.eventInfo;
        }


    }

}