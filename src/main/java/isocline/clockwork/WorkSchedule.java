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
import isocline.clockwork.event.WorkEventFactory;
import isocline.clockwork.flow.WorkFlowFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;


/**
 * Process various settings related to the execution of the job.
 * You can set scheduling related to tasks such as start time, end time, and repeat time.
 *
 * @see isocline.clockwork.Work
 */
public class WorkSchedule {

    private static final long UNDEFINED_INTERVAL = -1;

    private static final long PREEMPTIVE_CHECK_MILLITIME = 2;


    private String workUUID;

    private long waitingTime = 0;

    private long nextExecuteTime = 0;


    private boolean isDefinedStartTime = false;

    private long workEndTime = 0;

    private boolean isLock = false;

    private boolean isActivated = false;

    private long intervalTime = UNDEFINED_INTERVAL;

    private long jitter = 0;


    private boolean isStrictMode = false;

    private boolean isBetweenStartTimeMode = true;

    private boolean needWaiting = false;

    private boolean isEventBindding = false;


    private Work work;

    private WorkSession workSession = null;

    private Object lockOwner = null;

    private WorkProcessor workProcessor = null;

    private LinkedList<WorkEvent> eventList = new LinkedList<WorkEvent>();


    private WorkFlow workFlow = null;

    private ExecuteEventChecker executeEventChecker = null;


    WorkSchedule(WorkProcessor workProcessor, Work work) {
        this.workProcessor = workProcessor;
        this.work = work;

        this.workUUID = UUID.randomUUID().toString();
    }


    /**
     * Returns a ID
     *
     * @return
     */
    public String getId() {
        return this.workUUID;
    }

    //
    private void checkLocking() throws RuntimeException {
        if (isLock) {
            throw new RuntimeException("Changing settings is prohibited.");
        }

    }


    void adjustWaiting() throws InterruptedException {
        if (this.isStrictMode && needWaiting) {

            synchronized (workUUID) {
                for (int i = 0; i < 10000000; i++) {

                    if (nextExecuteTime <= System.currentTimeMillis()) {
                        return;
                    }
                }
            }
        }
    }


    long getPreemptiveMilliTime() {
        if (this.isStrictMode) {
            return PREEMPTIVE_CHECK_MILLITIME;
        } else {
            return 0;
        }

    }

    long checkRemainMilliTime() {
        needWaiting = false;

        if (!isActivated) {
            return 0;
            //throw new RuntimeException("service end");
        }
        if (isUntilEndTime()) {
            return Long.MAX_VALUE;
        }


        if (this.waitingTime == 0) {
            if (this.isStrictMode) {
                needWaiting = true;
            }

            return 0;
        } else if (this.eventList.size() > 0) {
            return 0;
        } else if (this.nextExecuteTime > 0) {

            long t1 = this.nextExecuteTime - System.currentTimeMillis();


            if (t1 <= 0) {
                return t1;
            } else if (this.isStrictMode && t1 < this.getPreemptiveMilliTime()) {
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


    boolean isUntilEndTime() {
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


    /**
     * @param lockOwner
     * @throws IllegalAccessException
     */
    public void unlock(Object lockOwner) throws IllegalAccessException {
        if (lockOwner == this.lockOwner) {
            this.isLock = false;
            this.lockOwner = null;
        } else {
            throw new IllegalAccessException("not lock owner");
        }
    }


    /**
     * @param workObject
     */
    public void setWorkObject(Work workObject) {
        this.work = workObject;
    }

    /**
     * @return
     */
    public Work getWorkObject() {
        return this.work;
    }


    public WorkSchedule setStartDateTime(long nextExecuteTime) {

        this.isDefinedStartTime = true;
        return setStartTime(nextExecuteTime);
    }

    /**
     * @param nextExecuteTime
     * @return
     */
    private WorkSchedule setStartTime(long nextExecuteTime) {

        if (waitingTime == 0) {
            waitingTime = 1;
        }

        this.nextExecuteTime = nextExecuteTime;

        return this;
    }

    public WorkSchedule setStartDelayTime(long waitTime) {
        checkLocking();
        this.waitingTime = waitTime;

        return this;
    }

    /**
     *
     * @param waitTime
     * @return
     */
    WorkSchedule adjustDelayTime(long waitTime) {


        this.waitingTime = waitTime;

        if (waitTime < 0) {
            this.nextExecuteTime = UNDEFINED_INTERVAL;


        } else {

            if (this.isBetweenStartTimeMode && this.nextExecuteTime > 0) {

                long tmp = System.currentTimeMillis() - nextExecuteTime;


                if (tmp > 0) {
                    long x = (long) Math.ceil((double) tmp / (double) waitTime);

                    setStartTime(this.nextExecuteTime + waitTime * (x));

                    return this;
                } else if (tmp == 0) {
                    setStartTime(this.nextExecuteTime + waitTime);
                    return this;
                }


            }

            long crntTime = System.currentTimeMillis();
            long adjCrntTime = crntTime;
            if (this.isStrictMode) {
                for (int i = 0; i < 5; i++) {
                    long adjTime = (crntTime - (crntTime % 1000)) + this.jitter + i * 1000;
                    long nextTime = adjTime + waitTime;

                    if ((crntTime + this.jitter) < nextTime) {
                        adjCrntTime = adjTime;
                        break;
                    }
                }
            }


            long chkTime = adjCrntTime + waitTime;
            if (this.nextExecuteTime < chkTime) {
                setStartTime(chkTime);

            } else {
                //System.err.println("overtime : "+this.nextExecuteTime);
            }


        }


        return this;
    }


    /**
     * @return
     */
    public long getIntervalTime() {
        return this.intervalTime;
    }


    /**
     * @param intervalTime
     * @return
     */
    public WorkSchedule setRepeatInterval(long intervalTime) {

        checkLocking();

        this.intervalTime = intervalTime;

        return this;
    }


    /**
     * @param intervalTime
     * @return
     */
    WorkSchedule adjustRepeatInterval(long intervalTime) {


        this.intervalTime = intervalTime;

        return setStartDelayTime(intervalTime);

    }


    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime
     */
    public WorkSchedule setStartDateTime(String isoDateTime) throws java.text.ParseException {

        this.isDefinedStartTime = true;

        return setStartDateTime(Clock.toDate(isoDateTime));
    }


    public WorkSchedule setStartDateTime(Date startDateTime) {

        this.isDefinedStartTime = true;

        this.setStartTime(startDateTime.getTime());
        return this;
    }


    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime
     */
    public WorkSchedule setFinishDateTime(String isoDateTime) throws java.text.ParseException {

        return setFinishDateTime(Clock.toDate(isoDateTime));
    }


    /**
     * @param endDateTime
     * @return
     */
    public WorkSchedule setFinishDateTime(Date endDateTime) {

        this.workEndTime = endDateTime.getTime();
        return this;
    }

    /**
     * @param milliSeconds
     * @return
     */
    public WorkSchedule setFinishTimeFromNow(long milliSeconds) {
        this.workEndTime = System.currentTimeMillis() + milliSeconds;
        return this;
    }


    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public WorkSchedule setWorkSession(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        checkLocking();
        this.workSession = (WorkSession) Class.forName(className).newInstance();
        return this;
    }

    /**
     * @param workSession
     * @return
     */
    public WorkSchedule setWorkSession(WorkSession workSession) {
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


    public WorkFlow getWorkFlow() {
        return this.workFlow;
    }


    ////////////////


    public void raiseLocalEvent(WorkEvent event) {

        this.workProcessor.addWorkSchedule(this, event);

    }


    public void raiseLocalEvent(WorkEvent event, long delayTime) {

        if(delayTime>0) {
            //this.workProcessor.workChecker

            this.getWorkProcessor().addWorkSchedule(this,event, delayTime);

        }else {
            raiseLocalEvent(event);
        }



    }


    public WorkSchedule bindEvent(String... eventNames) {
        checkLocking();

        for (String eventName : eventNames) {
            String[] subEventNames = eventRepository.setBindEventNames(eventName);
            for (String subEventName : subEventNames) {
                this.workProcessor.bindEvent(this, subEventName);
            }
        }
        this.isEventBindding = true;

        return this;
    }


    public WorkSchedule setStrictMode() {
        checkLocking();
        this.isStrictMode = true;
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
        this.setStartDelayTime(Work.WAIT);
        return this;
    }

    public WorkSchedule activate() {
        return activate(false);
    }


    /**
     * @param checkActivated
     * @return
     */
    public WorkSchedule activate(boolean checkActivated) {
        if (isActivated) {
            if (checkActivated) {
                throw new RuntimeException("Already activate!");
            } else {
                return this;
            }
        }
        this.isActivated = true;


        try {
            if(this.work instanceof FlowableWork) {
                this.workFlow = WorkFlowFactory.createWorkFlow();

                FlowableWork fw = (FlowableWork) this.work;

                WorkFlow wf = this.workFlow.next(fw::initialize);

                fw.defineWorkFlow(wf);

                if (!wf.isSetFinish()) {
                    wf.finish();
                }
            }

        } catch (UnsupportedOperationException npe) {

        }

        if (this.isStrictMode && !this.isDefinedStartTime) {

            long startTime = Clock.nextSecond(900);
            this.setStartTime(startTime + this.waitingTime);
        } else if (this.waitingTime > 0) {
            this.adjustDelayTime(this.waitingTime);
        }


        if (this.waitingTime != Work.WAIT) {
            if (this.isStrictMode || this.isDefinedStartTime || this.waitingTime > 1 || isEventBindding) {
                this.workProcessor.addWorkSchedule(this, false);
            } else {
                this.workProcessor.addWorkSchedule(this, true);
            }

        }

        this.workProcessor.managedWorkCount.incrementAndGet();

        return this;
    }

    public WorkSchedule setScheduleDescriptor(ScheduleDescriptor descriptor) {
        descriptor.build(this);
        return this;
    }

    /**
     * check activated
     *
     * @return
     */
    public boolean isActivated() {
        return isActivated;
    }


    /**
     * finish job
     *
     */
    synchronized public void finish() {
        if (this.isActivated) {
            this.isActivated = false;
            this.workProcessor.managedWorkCount.decrementAndGet();
        }
        notifyAll();
    }


    synchronized public void waitUntilFinish(long timeout) throws InterruptedException {
        wait(timeout);
    }



    synchronized public void waitUntilFinish() throws InterruptedException{
        wait();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkSchedule schedule = (WorkSchedule) o;

        return workUUID.equals(schedule.workUUID);
    }

    @Override
    public int hashCode() {
        return workUUID.hashCode();
    }


    public WorkProcessor getWorkProcessor() {
        return workProcessor;
    }


    public WorkSchedule setExecuteEventChecker(ExecuteEventChecker checker) {
        this.executeEventChecker = checker;
        return this;
    }

    boolean isExecuteEnable(long time) {
        if (this.executeEventChecker != null) {
            return this.executeEventChecker.check(time);
        }

        return true;
    }


    ///////////////////////////////////


    private WorkEvent defaultEvent = null;

    WorkEvent getDefaultEventInfo() {
        if (defaultEvent == null) {
            defaultEvent = WorkEventFactory.create();
            defaultEvent.setWorkSechedule(this);

        }

        return defaultEvent;

    }


    private EventRepository eventRepository = new EventRepository();


    /**
     *
     *
     * @param eventName
     * @return
     */
    String getDeliverableEventName(String eventName) {


        EventSet eventSet = eventRepository.getEventSet(eventName);


        if (eventSet == null) {
            //System.err.println("getDeliverableEventName normal = "+eventName );
            return eventName;
        }


        if (eventSet.isRaiseEventReady(eventName)) {

            //System.err.println("getDeliverableEventName event="+eventName + " set="+eventSet + " fire="+eventSet.getEventSetName());
            return eventSet.getEventSetName();
        }


        return null;

    }


    ////////////////////////////


    ExecuteContext enterQueue(boolean isUserEvent, WorkEvent workEvent) {


        ExecuteContext ctx = new ExecuteContext(this, isUserEvent, workEvent);

        return ctx;
    }


    ExecuteContext enterQueue(boolean isUserEvent) {

        ExecuteContext ctx = new ExecuteContext(this, isUserEvent, null);


        return ctx;
    }


    /**
     * ExecuteContext for Queue
     */
    static class ExecuteContext {


        private WorkSchedule workSchedule;

        private boolean isUserEvent = false;

        private WorkEvent workEvent;


        ExecuteContext(WorkSchedule workSchedule, boolean isUserEvent, WorkEvent event) {

            this.workSchedule = workSchedule;
            this.isUserEvent = isUserEvent;

            this.workEvent = event;

            /*
            if(event!=null) {
                event.setWorkSechedule(workSchedule);

            }
            */
        }


        boolean isExecuteImmediately() {
            if (isUserEvent) {
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

        WorkEvent getWorkEvent() {
            return this.workEvent;
        }


    }

}