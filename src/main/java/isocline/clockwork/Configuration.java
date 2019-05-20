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

/**
 *
 * Configuration class for WorkProcessor
 */
public class Configuration {

    private int threadPriority = Thread.NORM_PRIORITY;

    private int initThreadWorkerSize = 3;

    private int maxThreadWorkerSize = 12;



    private int maxWorkQueueSize = 512 * 512 * 512;

    private long executeTimeout = 30 * Clock.SECOND;

    private long executeCountdownMilliTime = 50;




    private boolean isPropertyLocking = false;


    public final static Configuration NOMAL = create().setInitThreadWorkerSize(3).setMaxThreadWorkerSize(12).setThreadPriority(Thread.NORM_PRIORITY).lock();

    public final static Configuration ECHO = create().setInitThreadWorkerSize(1).setMaxThreadWorkerSize(3).setThreadPriority(Thread.MIN_PRIORITY).lock();

    public final static Configuration PERFORMANCE = create().setInitThreadWorkerSize(24).setMaxThreadWorkerSize(36).setThreadPriority(Thread.MAX_PRIORITY).lock();

    public final static Configuration HYPER = create().setInitThreadWorkerSize(64).setMaxThreadWorkerSize(128).setThreadPriority(Thread.MAX_PRIORITY).lock();



    /**
     * Create new instance of Configuration
     */
    public static Configuration create() {
        return new Configuration();
    }

    private Configuration() {

    }

    /**
     *
     * @return
     */
    public Configuration lock() {
        this.isPropertyLocking = true;
        return this;
    }

    Configuration unlock() {
        this.isPropertyLocking = false;
        return this;
    }
    private void check() {
        if (isPropertyLocking) {
            throw new RuntimeException("property is locking");
        }
    }

    /**
     * Returns a initial thread worker size
     *
     * @return
     */
    public int getInitThreadWorkerSize() {
        return initThreadWorkerSize;
    }


    /**
     *
     * @param initThreadWorkerSize
     * @return
     */
    public Configuration setInitThreadWorkerSize(int initThreadWorkerSize) {
        this.check();
        this.initThreadWorkerSize = initThreadWorkerSize;
        return this;
    }

    /**
     *
     * @return
     */
    public int getMaxThreadWorkerSize() {
        return maxThreadWorkerSize;
    }

    /**
     *
     * @param maxThreadWorkerSize
     * @return
     */
    public Configuration setMaxThreadWorkerSize(int maxThreadWorkerSize) {
        this.check();
        this.maxThreadWorkerSize = maxThreadWorkerSize;
        return this;
    }

    /**
     *
     * @return
     */
    public int getThreadPriority() {
        return threadPriority;
    }


    /**
     *
     * @param threadPriority
     * @return
     */
    public Configuration setThreadPriority(int threadPriority) {
        this.check();
        this.threadPriority = threadPriority;
        return this;
    }

    /**
     *
     * @return
     */
    public long getExecuteTimeout() {
        return executeTimeout;
    }

    public Configuration setExecuteTimeout(long executeTimeout) {
        this.check();
        this.executeTimeout = executeTimeout;
        return this;
    }

    /**
     *
     * @return
     */
    public int getMaxWorkQueueSize() {
        return maxWorkQueueSize;
    }


    /**
     *
     * @param maxWorkQueueSize
     * @return
     */
    public Configuration setMaxWorkQueueSize(int maxWorkQueueSize) {
        this.check();
        this.maxWorkQueueSize = maxWorkQueueSize;
        return this;
    }

    /**
     *
     * @return
     */
    public long getExecuteCountdownMilliTime() {
        return executeCountdownMilliTime;
    }


    /**
     *
     * @param countdownTime
     * @return
     */
    public Configuration setExecuteCountdownMilliTime(long countdownTime) {
        this.check();
        this.executeCountdownMilliTime =countdownTime;

        return this;
    }




}
