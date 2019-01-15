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

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkSchedule {


    private long waitTime = 0;

    private long checkTime = 0;

    private long workEndTime = 0;

    private boolean isLock = false;

    private boolean isStart = false;


    private Work work;

    private WorkSession workSession = null;

    private Object lockOwner = null;

    private ClockWorker clockWorker = null;


    WorkSchedule(ClockWorker clockWorker, Work work) {
        this.clockWorker = clockWorker;
        this.work = work;
    }


    //
    private void checkLocking() throws RuntimeException {
        if (isLock) {
            throw new RuntimeException("Changing settings is prohibited.");
        }

    }


    boolean isExecute() {

        boolean isExecute = false;


        if (this.waitTime == 0) {
            isExecute = true;
        } else if (this.checkTime <= System.currentTimeMillis()) {
            isExecute = true;
        }

        if(isExecute) {
            if(isEnd()) {
                isExecute = false;
            }
        }



        return isExecute;
    }


    long getCheckTime() {
        return this.checkTime;
    }


    boolean isEnd() {
        if(this.workEndTime > 0) {
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

        long chkTime = System.currentTimeMillis() + waitTime;
        if (this.checkTime < chkTime) {
            this.checkTime = chkTime;
        }

        return this;
    }

    private Date getDate(String isoDateTime) throws java.text.ParseException {

        String isoDateTimeTxt = isoDateTime.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");

        System.err.println(isoDateTimeTxt);

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date;
        try {
            date = form.parse(isoDateTimeTxt);
        } catch (java.text.ParseException pe) {
            form = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
            date = form.parse(isoDateTimeTxt);
        }
        return date;

    }

    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime
     */
    public WorkSchedule setStartDateTime(String isoDateTime) throws java.text.ParseException {

        return setStartDateTime(getDate(isoDateTime));
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

        return setEndDateTime(getDate(isoDateTime));
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

        return this;
    }


    public WorkSchedule start() {
        if (isStart) {
            throw new RuntimeException("Already start!");
        }
        this.isStart = true;
        this.clockWorker.addWorkSchedule(this);
        return this;
    }
}