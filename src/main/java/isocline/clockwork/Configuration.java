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
 * Configuration class for ClockWorker
 */
public class Configuration {

    private int threadPriority = Thread.NORM_PRIORITY;

    private int initThreadWorkerSize = 1;

    private int maxThreadWorkerSize = 10;



    private long executeTimeout = 30 * Clock.SECOND;

    private int maxWorkQueueSize = 512 * 512 * 512;

    private boolean isPropertyLocking = false;


    public final static Configuration DEFAULT = create().lock();

    public final static Configuration PERFORMANCE = create().setInitThreadWorkerSize(3).setMaxThreadWorkerSize(24).setThreadPriority(Thread.MAX_PRIORITY).lock();


    public static Configuration create() {
        return new Configuration();
    }

    private Configuration() {

    }

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

    public int getInitThreadWorkerSize() {
        return initThreadWorkerSize;
    }

    public Configuration setInitThreadWorkerSize(int initThreadWorkerSize) {
        this.check();
        this.initThreadWorkerSize = initThreadWorkerSize;
        return this;
    }

    public int getMaxThreadWorkerSize() {
        return maxThreadWorkerSize;
    }

    public Configuration setMaxThreadWorkerSize(int maxThreadWorkerSize) {
        this.check();
        this.maxThreadWorkerSize = maxThreadWorkerSize;
        return this;
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public Configuration setThreadPriority(int threadPriority) {
        this.check();
        this.threadPriority = threadPriority;
        return this;
    }

    public long getExecuteTimeout() {
        return executeTimeout;
    }

    public Configuration setExecuteTimeout(long executeTimeout) {
        this.check();
        this.executeTimeout = executeTimeout;
        return this;
    }

    public int getMaxWorkQueueSize() {
        return maxWorkQueueSize;
    }

    public Configuration setMaxWorkQueueSize(int maxWorkQueueSize) {
        this.check();
        this.maxWorkQueueSize = maxWorkQueueSize;
        return this;
    }
}
