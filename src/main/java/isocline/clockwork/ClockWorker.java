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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 *
 *
 */
public class ClockWorker extends ThreadGroup {


    private String name;


    private Configuration configuration;

    private List<ThreadWorker> threadWorkers = new ArrayList<ThreadWorker>();

    private AtomicInteger threadWorkerCount = new AtomicInteger(0);

    private AtomicInteger runningWorkCount = new AtomicInteger(0);

    private BlockingQueue<WorkSchedule> workQueue;


    private WorkChecker workChecker;

    private boolean isWorking = false;


    private int checkpointWorkQueueSize = 500;


    /**
     * @param name
     * @param config
     */
    public ClockWorker(String name, Configuration config) {
        super("ClockWorker");

        this.name = "ClockWorker[" + name + "]";

        this.configuration = config.lock();
        this.checkpointWorkQueueSize = config.getMaxWorkQueueSize() / 1000;
        if(this.checkpointWorkQueueSize<500) {
            this.checkpointWorkQueueSize = 500;
        }

        this.workQueue = new LinkedBlockingQueue<WorkSchedule>(this.configuration.getMaxWorkQueueSize());

        init(true);
    }


    /**
     * @param isWorking
     */
    private void init(boolean isWorking) {
        this.isWorking = isWorking;

        workChecker = new WorkChecker(this);
        workChecker.start();

        do {
            addThreadWorker();
        } while (this.threadWorkerCount.get() <= this.configuration.getInitThreadWorkerSize());

    }



    public WorkSchedule createSchedule(Work work) {
        return new WorkSchedule(this,work);
    }

    public WorkSchedule createSchedule(Class workClass) throws InstantiationException, IllegalAccessException {
        return new WorkSchedule(this, (Work) workClass.newInstance());
    }


    public WorkSchedule createSchedule(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return new WorkSchedule(this,(Work) Class.forName(className).newInstance());
    }



    boolean addWorkSchedule(WorkSchedule workSchedule) {
        boolean result = this.workQueue.add(workSchedule);
        if (result) {
            this.runningWorkCount.incrementAndGet();

            int sz = this.workQueue.size();
            if (sz > checkpointWorkQueueSize) {
                this.checkPoolThread();
            }
        } else {
            this.checkPoolThread();
        }

        return result;


    }

    public int getRunningWorkCount() {
        return this.runningWorkCount.get();
    }

    public int getThreadWorkerCount() {
        return this.threadWorkerCount.get();
    }

    /**
     * @return
     */
    public int getWorkQueueSize() {
        return this.workQueue.size();
    }


    private void waiting(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {

        }
    }


    /**
     * @param timeout
     */
    public void waitingJob(long timeout) {

        try {
            Thread.sleep(500);
            for (int i = 0; i < timeout / 100; i++) {
                if (this.getRunningWorkCount() > 0) {
                    Thread.sleep(100);

                } else {
                    if (this.getRunningWorkCount() == 0
                            && workQueue.size() == 0) {
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }

        long tt1 = System.currentTimeMillis();

        for (ThreadWorker t : this.threadWorkers) {
            t.interrupt();
        }

        while (this.runningWorkCount.get() > 0) {
            waiting(100);

            long gap = System.currentTimeMillis() - tt1;
            if (gap > timeout && timeout > 0) {
                break;
            }

        }

        long tt2 = System.currentTimeMillis();

        long gap = (tt2 - tt1);
        if (gap > 0) {
            System.out.println(this.name + " wait time(milisecond) for async job : "
                    + gap);
        }


    }


    public void shutdown() {
        isWorking = false;

        int count = 0;
        while (this.threadWorkerCount.get() > 0) {
            waiting(10);
            count++;

            // 10초 까지 대기
            if (count > 1000) {
                break;
            }
        }

        try {
            super.destroy();
        } catch (IllegalThreadStateException ite) {

        }

    }


    public void shutdownAfterWaiting(long timeout) {

        waitingJob(timeout);

        shutdown();

    }

    private synchronized boolean addThreadWorker() {

        int i = threadWorkerCount.get();
        if (i < this.configuration.getMaxThreadWorkerSize()) {
            ThreadWorker mon = new ThreadWorker(this, this.configuration.getThreadPriority());
            threadWorkers.add(mon);
            mon.setDaemon(true);
            mon.start();

            threadWorkerCount.addAndGet(1);
            return true;
        }

        return false;

    }


    /**
     * @param t
     * @return
     */
    private synchronized boolean removeThreadWorker(ThreadWorker t) {

        threadWorkerCount.addAndGet(-1);
        return threadWorkers.remove(t);

    }

    /**
     * @return
     */
    private synchronized boolean removeThreadWorker() {

        if (threadWorkerCount.get() > this.configuration.getInitThreadWorkerSize()) {
            try {
                ThreadWorker mon = threadWorkers.get(0);
                mon.stopWorking();
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }


    /**
     *
     */
    private synchronized void checkPoolThread() {

        for (ThreadWorker t : this.threadWorkers) {
            if (t.isDelayExecute()) {
                t.stopWorking();
            }
        }
    }


    private static class ThreadWorker extends Thread {

        private ClockWorker clockWorker;


        private int timeoutCount = 0;

        private int stoplessCount = 0;

        private long lastWorkTime = 0;

        private boolean isThreadRunning = false;


        public ThreadWorker(ClockWorker parent, int threadPriority) {
            super(parent, "Clockwork:ThreadWorker-" + parent.threadWorkerCount);
            this.clockWorker = parent;
            this.setPriority(threadPriority);

        }


        private boolean isWorking() {
            if (!this.clockWorker.isWorking) {
                return false;
            } else if (!isThreadRunning) {
                return false;
            }

            return true;
        }


        private boolean check(Work work) {

            // filtering option
            return true;
        }


        public boolean isDelayExecute() {

            long executeTimeout = clockWorker.configuration.getExecuteTimeout();

            if (this.lastWorkTime < 1 || executeTimeout < 0) {
                return false;
            }

            long t1 = System.currentTimeMillis() - this.lastWorkTime;

            if (t1 > executeTimeout) {
                //sSystem.err.println(this.id + " delay " + t1 + " "+ executeTimeout + " " + this.lastWorkTime);
                return true;
            }

            return false;
        }

        public void run() {

            isThreadRunning = true;

            while (isWorking()) {

                try {

                    WorkSchedule workSchedule = this.clockWorker.workQueue.poll(1,
                            TimeUnit.SECONDS);

                    this.lastWorkTime = System.currentTimeMillis();

                    if (workSchedule == null) {
                        timeoutCount++;
                        stoplessCount = 0;
                    } else {
                        timeoutCount = 0;
                        stoplessCount++;
                        Work slc = workSchedule.getWorkObject();

                        if (check(slc)) {

                            if (workSchedule.isExecute()) {

                                EventInfo eventInfo = new EventInfo();
                                eventInfo.setWorkSechedule(workSchedule);

                                long delaytime = slc.execute(eventInfo);
                                while (delaytime == 0) {
                                    delaytime = slc.execute(eventInfo);
                                }
                                this.clockWorker.runningWorkCount.decrementAndGet();
                                if (delaytime >= 0) {
                                    workSchedule.setStartDelay(delaytime);
                                    this.clockWorker.addWorkSchedule(workSchedule);
                                }

                                // Thread.sleep(1);
                            } else if(!workSchedule.isEnd()) {
                                timeoutCount++;
                                stoplessCount = 0;

                                this.clockWorker.runningWorkCount.decrementAndGet();
                                // Thread.sleep(50);
                                // this.parent.addWorkSchedule(workSchedule);
                                this.clockWorker.workChecker
                                        .addWorkStatusWrapper(workSchedule);
                            }
                        } else {
                            this.clockWorker.workQueue.put(workSchedule);
                        }

                    }

                    if (timeoutCount > 10) {
                        this.clockWorker.removeThreadWorker();
                        timeoutCount = 0;
                    } else if (stoplessCount > 1000) {
                        this.clockWorker.addThreadWorker();
                        stoplessCount = 0;
                    }

                } catch (InterruptedException ite) {
                    this.clockWorker.runningWorkCount.decrementAndGet();
                } catch (Throwable e) {
                    e.printStackTrace();

                }

            }

            this.clockWorker.removeThreadWorker(this);

        }


        private void stopWorking() {
            this.isThreadRunning = false;
            this.interrupt();
        }

    }


    /**
     *
     */
    private static class WorkChecker extends Thread {

        private ClockWorker clockWorker;

        private BlockingQueue<WorkSchedule> statusWrappers = new LinkedBlockingQueue<WorkSchedule>();

        WorkChecker(ClockWorker clockWorker) {
            this.clockWorker = clockWorker;
        }

        void addWorkStatusWrapper(WorkSchedule sb) {
            statusWrappers.add(sb);
        }

        @Override
        public void run() {

            while (clockWorker.isWorking) {

                try {

                    WorkSchedule wrapper = statusWrappers.poll(1, TimeUnit.SECONDS);

                    if (wrapper != null) {

                        if (wrapper.getCheckTime() <= System.currentTimeMillis()) {
                            clockWorker.addWorkSchedule(wrapper);
                        } else {
                            statusWrappers.add(wrapper);
                            Thread.sleep(10);
                        }
                    }
                } catch (Exception e) {

                    try {
                        Thread.sleep(200);
                    } catch (Exception ee) {

                    }
                }
            }

            statusWrappers.clear();

        }

    }


}
