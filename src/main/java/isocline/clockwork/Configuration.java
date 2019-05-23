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


    /**
     * Initialization settings related to thread settings for WorkProcessor initialization.
     * By default, the initial thread count is 3, the maximum thread count is 12, and the thread priority setting is Thread.NORM_PRIORITY state.
     *
     */
    public final static Configuration NOMAL = create().setInitThreadWorkerSize(3).setMaxThreadWorkerSize(12).setThreadPriority(Thread.NORM_PRIORITY).lock();

    /**
     * Initialization settings related to thread settings for WorkProcessor initialization
     * Preset for low-end environments, the initial thread count is 1, the maximum thread count is 3,
     * and the thread priority setting is Thread.NORM_PRIORITY state.
     */
    public final static Configuration ECHO = create().setInitThreadWorkerSize(1).setMaxThreadWorkerSize(3).setThreadPriority(Thread.MIN_PRIORITY).lock();

    /**
     * Initialization settings related to thread settings for WorkProcessor initialization
     * Preset for low-end environments, the initial thread count is 24, the maximum thread count is 36,
     * and the thread priority setting is Thread.MAX_PRIORITY state.
     */
    public final static Configuration PERFORMANCE = create().setInitThreadWorkerSize(24).setMaxThreadWorkerSize(36).setThreadPriority(Thread.MAX_PRIORITY).lock();

    /**
     * Initialization settings related to thread settings for WorkProcessor initialization
     * Preset for HYPER environments, the initial thread count is 64, the maximum thread count is 128,
     * and the thread priority setting is Thread.MAX_PRIORITY state.
     */
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
     * @return size of ThreadWorker
     */
    public int getInitThreadWorkerSize() {
        return initThreadWorkerSize;
    }


    /**
     * Set a initial thread worker size
     *
     * @param initThreadWorkerSize
     * @return Configuration
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
     * Returns a Thread priority
     * @return thread priority
     */
    public int getThreadPriority() {
        return threadPriority;
    }


    /**
     * Set a priority of Thread
     *
     * @param threadPriority thread priority
     * @return Configuration
     */
    public Configuration setThreadPriority(int threadPriority) {
        this.check();
        this.threadPriority = threadPriority;
        return this;
    }

    /**
     * Returns a timeout for executing job.
     * @return timeout
     */
    public long getExecuteTimeout() {
        return executeTimeout;
    }


    /**
     * Set a timeout
     *
     * @param executeTimeout timeout(milliseconds)
     * @return Configuration instance
     */
    public Configuration setExecuteTimeout(long executeTimeout) {
        this.check();
        this.executeTimeout = executeTimeout;
        return this;
    }

    /**
     * Returns a max queue size
     * @return queue size
     */
    public int getMaxWorkQueueSize() {
        return maxWorkQueueSize;
    }


    /**
     *
     * Set a queue size for max
     *
     * @param maxWorkQueueSize
     * @return Configuration instance
     */
    public Configuration setMaxWorkQueueSize(int maxWorkQueueSize) {
        this.check();
        this.maxWorkQueueSize = maxWorkQueueSize;
        return this;
    }

    /**
     * Returns a milliseconds of execute countdown
     * @return mulliseconds
     */
    public long getExecuteCountdownMilliTime() {
        return executeCountdownMilliTime;
    }


    /**
     * Set a a milliseconds of execute countdown
     *
     * @param countdownTime
     * @return Configuration instance
     */
    public Configuration setExecuteCountdownMilliTime(long countdownTime) {
        this.check();
        this.executeCountdownMilliTime =countdownTime;

        return this;
    }




}
