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
import java.util.concurrent.TimeUnit;


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

    private boolean isSecondBaseMode = false;


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

    void adjustWaiting()  throws InterruptedException{
        if(needWaiting) {
            double e = 573D/1000D;
            long gap = nextExecuteNanoTime- System.nanoTime();
            long gap2 = (long) (gap*e);
            TimeUnit.NANOSECONDS.sleep(gap2);

            for(int i=0;i<10000000;i++) {
                if(nextExecuteNanoTime<=System.nanoTime()) {
                    //System.out.println(">>                           "+gap+" "+gap2+ " "+i+ " "+this.nextExecuteTime + " "+this.jitter + " "+System.currentTimeMillis());
                    return;
                }
            }

        }

    }


    private boolean needWaiting = false;

    private static long chkTimeUnit = 3*1000000;

    long checkRemainNanoTime(){
        needWaiting = false;

        if(!isStart) {
            throw new RuntimeException("service end");
        }
        if (isEnd()) {
            return Long.MAX_VALUE;
        }




        if (this.waitTime == 0 ) {
            if(this.isSecondBaseMode) {
                needWaiting = true;
            }
            long tt = System.nanoTime();
            long gt = this.nextExecuteNanoTime - tt;
            return 0;
        } else if (this.eventList.size() > 0) {
            return 0;
        } else if (this.nextExecuteTime > 0 ) {

            long t1 = this.nextExecuteNanoTime  - System.nanoTime();
            if(t1<=0) {
                //System.out.println("XY=="+this.jitter +" "+t1+ " "+this.nextExecuteNanoTime + " "+System.nanoTime());
                return t1;
            }else  if(this.isSecondBaseMode && t1<chkTimeUnit) {
                needWaiting = true;
                //System.out.println("XX=="+this.jitter +" "+t1);
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

    public WorkSchedule setStartDelay(long waitTime) {
        isFirstDelaySet=true;

        checkLocking();
        this.waitTime = waitTime;

        if (waitTime < 0) {
            this.nextExecuteTime = UNDEFINED_INTERVAL;
            this.nextExecuteNanoTime = UNDEFINED_INTERVAL;

        } else {
            long crntTime = System.currentTimeMillis();
            long adjCrntTime = crntTime;
            if(this.isSecondBaseMode) {
                for(int i=0;i<5;i++) {
                    long adjTime = (crntTime - (crntTime%1000)) + this.jitter + i*1000;
                    long nextTime = adjTime + waitTime;

                    if((crntTime+this.jitter)<nextTime) {
                        //if(i!=0)
                        {
                            //System.out.println("XXX "+i + " "+crntTime+ " "+nextTime + " -- "+this.jitter);
                        }
                        //System.err.println("XXX "+i);
                        adjCrntTime = adjTime;
                        //System.out.println("XXX "+i + " "+crntTime+ " "+nextTime + " -- "+adjCrntTime+ " "+this.jitter);
                        break;
                    }
                }


                //crntTime = ((long) crntTime / 1000) *1000+this.jitter;
            }




            long chkTime = adjCrntTime + waitTime;
            if (this.nextExecuteTime < chkTime) {
                this.nextExecuteTime = chkTime;
                this.nextExecuteNanoTime = this.nextExecuteTime * 1000000;
                //System.out.println("----- -- "+this.nextExecuteTime +  "  "+crntTime + "  - "+this.jitter);

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

        this.setStartDelay(startDateTime.getTime());
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


    public WorkSchedule setSecondBaseMode(boolean isSecondBaseMode) {
        checkLocking();
        this.isSecondBaseMode = isSecondBaseMode;
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

        if(!isFirstDelaySet) {
            this.setStartDelay(0);
        }

        this.clockWorker.addWorkSchedule(this);

        this.clockWorker.managedWorkCount.incrementAndGet();

        return this;
    }

    public WorkSchedule setScheduleConfig(ScheduleConfig config) {
        config.settup(this);

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