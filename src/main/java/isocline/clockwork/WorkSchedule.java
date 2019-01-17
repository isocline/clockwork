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

    private long checkTime = 0;

    private long workEndTime = 0;

    private boolean isLock = false;

    private boolean isStart = false;

    private long intervalTime = UNDEFINED_INTERVAL;

    private double contextCheckId;

    private boolean isSecondBaseMode = false;


    private Work work;

    private WorkSession workSession = null;

    private Object lockOwner = null;

    private ClockWorker clockWorker = null;


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


    boolean isExecute() {

        if(!isStart) {
            throw new RuntimeException("service end");
        }

        boolean isExecute = false;


        if (this.waitTime == 0) {
            isExecute = true;
        } else if (this.checkTime > 0  && this.checkTime <= System.currentTimeMillis()) {
            isExecute = true;
        } else if (this.eventList.size() > 0) {
            isExecute = true;
        }

        if (isExecute) {
            if (isEnd()) {
                isExecute = false;
            }
        }


        return isExecute;
    }


    long getCheckTime() {
        return this.checkTime;
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


    public WorkSchedule setStartDelay(long waitTime) {

        checkLocking();
        this.waitTime = waitTime;

        if (waitTime < 0) {
            this.checkTime = waitTime;
        } else {
            long crntTime = System.currentTimeMillis();
            if(this.isSecondBaseMode) {
                crntTime = (((long) crntTime / waitTime) * waitTime);
            }



            long chkTime = crntTime + waitTime;
            if (this.checkTime < chkTime) {
                this.checkTime = chkTime;
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

        this.checkTime = startDateTime.getTime();
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


    public WorkSchedule setWorkSession(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        checkLocking();
        this.workSession = (WorkSession) Class.forName(className).newInstance();
        return this;
    }


    public WorkSchedule setWorkSession(WorkSession workSession) throws ClassNotFoundException {
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


    public WorkSchedule bindEvent(String eventName) {
        checkLocking();

        this.clockWorker.bindEvent(eventName, this);

        return this;
    }


    public WorkSchedule setSecondBaseMode(boolean isSecondBaseMode) {
        checkLocking();
        this.isSecondBaseMode = isSecondBaseMode;
        return this;
    }


    public WorkSchedule start() {
        if (isStart) {
            throw new RuntimeException("Already start!");
        }
        this.isStart = true;
        if(this.isSecondBaseMode) {
            this.setStartDelay(Clock.SECOND);
        }
        this.clockWorker.addWorkSchedule(this);

        this.clockWorker.managedWorkCount.incrementAndGet();

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

    private LinkedList<EventInfo> eventList = new LinkedList<EventInfo>();

    void raiseEvent(EventInfo event) {
        eventList.add(event);
    }

    EventInfo checkEvent() {
        try {
            return eventList.removeFirst();
        } catch (NoSuchElementException nse) {
            return null;
        }
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