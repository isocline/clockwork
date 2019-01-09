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

public class WorkSchedule {

    private Work work;

    private long waitTime = 0;

    private long checkTime;


    private boolean isLock = false;

    private WorkSession workSession;

    private Class workSessionClass;


    public WorkSchedule(Work work) {
        this.work = work;
    }


    public WorkSchedule(Class workClass) throws InstantiationException, IllegalAccessException {

        this.work = (Work) workClass.newInstance();
    }

    public WorkSchedule(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this(Class.forName(className));
    }

    boolean isExecute() {
        if (this.waitTime == 0) {
            return true;
        } else if (this.checkTime <= System.currentTimeMillis()) {
            return true;
        }
        return false;
    }


    long getCheckTime() {
        return this.checkTime;
    }

    //
    private void checkLocking() throws RuntimeException {
        if (isLock) {
            throw new RuntimeException("Changing settings is prohibited.");
        }

    }

    public void locking() {
        this.isLock = true;
    }



    public Work getWorkObject() {
        return this.work;
    }


    public WorkSchedule setStartDelay(long waitTime) {
        checkLocking();
        this.waitTime = waitTime;
        this.checkTime = System.currentTimeMillis() + waitTime;

        return this;
    }

    public WorkSchedule setWorkSessionClassName(String className) throws ClassNotFoundException {
        checkLocking();
        this.workSessionClass = Class.forName(className);
        return this;
    }


}