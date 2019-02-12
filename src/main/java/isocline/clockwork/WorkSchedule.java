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

import java.util.Date;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.UUID;


/**
 *
 */
public class WorkSchedule {

    private static final long UNDEFINED_INTERVAL = -1;


    private String workUuid;

    private long waitTime = 0;

    private long nextExecuteTime = 0;

    private long nextExecuteNanoTime = 0;

    private long workEndTime = 0;

    private boolean isLock = false;

    private boolean isStart = false;

    private long intervalTime = UNDEFINED_INTERVAL;

    private long jitter = 0;

    private double contextCheckId;

    private boolean isStrictMode = false;

    private boolean isBetweenStartTimeMode = true;

    private boolean needWaiting = false;

    private static long preemptiveCheckNanoTime = 1800000;


    private Work work;

    private WorkSession workSession = null;

    private Object lockOwner = null;

    private ClockWorker clockWorker = null;

    private LinkedList<EventInfo> eventList = new LinkedList<EventInfo>();


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
            double e = 51D/1000D;
            long gap = nextExecuteNanoTime- System.nanoTime();
            long gap2 = (long) (gap*e);
            //TimeUnit.NANOSECONDS.sleep(gap2);

            this.wait(0,1);

            for(int i=0;i<10000000;i++) {
                if(nextExecuteNanoTime<=System.nanoTime()) {
                    //System.out.println(">>                           "+gap+" "+gap2+ " "+i+ " "+this.nextExecuteTime + " "+this.jitter + " "+System.currentTimeMillis());
                    return;
                }
            }

        }

    }





    long getPreemptiveNanoTime() {
        if(this.isStrictMode) {
            return preemptiveCheckNanoTime;
        }else {
            return 0;
        }

    }

    long checkRemainNanoTime(){
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

            long t1 = this.nextExecuteNanoTime  - System.nanoTime();
            if(t1<=0) {
                return t1;
            }else  if(this.isStrictMode && t1<this.getPreemptiveNanoTime()) {
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
        this.nextExecuteNanoTime = this.nextExecuteTime * 1000000;


        return this;
    }

    public WorkSchedule setStartDelay(long waitTime) {
        isFirstDelaySet=true;

        checkLocking();
        this.waitTime = waitTime;

        if (waitTime < 0) {
            this.nextExecuteTime = UNDEFINED_INTERVAL;
            this.nextExecuteNanoTime = UNDEFINED_INTERVAL;

        } else {

            if(this.isBetweenStartTimeMode && this.nextExecuteNanoTime>0) {




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
                System.err.println("----- 2 "+this.nextExecuteTime);
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

    public WorkSchedule setFinishTime(long milliSeconds) {
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


    public WorkSchedule bindEvent(String... eventNames) {
        checkLocking();

        this.clockWorker.bindEvent( this, eventNames);

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

        this.clockWorker.addWorkSchedule(this);

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

    private boolean isEmpty=true;

    void raiseEvent(EventInfo event) {
        isEmpty=false;
        eventList.add(event);
    }

    EventInfo checkEvent() {
        if(isEmpty) return null;
        try {
            return eventList.removeFirst();
        } catch (NoSuchElementException nse) {
            isEmpty = true;
            return null;
        }
    }

    private EventInfo defaultEvent = null;

    EventInfo getDefaultEventInfo() {
        if(defaultEvent==null) {
            defaultEvent = new EventInfo();
            defaultEvent.setWorkSechedule(this);
        }

        return defaultEvent;

    }




    Context enterQueue() {


        this.contextCheckId = Math.random();
        Context ctx = new Context(contextCheckId, this);


        return ctx;
    }


    /**
     * Context for Queue
     */
    static class Context {

        private double contextId;

        private WorkSchedule workSchedule;


        Context(double contextId, WorkSchedule workSchedule) {
            this.contextId = contextId;
            this.workSchedule = workSchedule;
        }

        WorkSchedule getWorkSchedule() {

            if (this.contextId == this.workSchedule.contextCheckId) {
                return workSchedule;
            }

            return null;

        }
    }

}