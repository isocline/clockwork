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

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 * Base class for WorkSchedule management and thread management
 *
 */
public class ClockWorker extends ThreadGroup {

    protected static Logger logger = Logger.getLogger(ClockWorker.class.getName());


    private String name;

    private boolean isWorking = false;

    private int checkpointWorkQueueSize = 500;


    private Configuration configuration;

    private List<ThreadWorker> threadWorkers = new ArrayList<ThreadWorker>();

    private AtomicInteger threadWorkerCount = new AtomicInteger(0);

    private BlockingQueue<WorkSchedule.Context> workQueue;

    private WorkChecker workChecker;

    private Map<String, WorkScheduleList> eventMap = new ConcurrentHashMap<String, WorkScheduleList>();


    AtomicInteger managedWorkCount = new AtomicInteger(0);




    /**
     * Create a ClockWorker object which provice services for WorkSchedule
     *
     * @param name   ClockerWorker name
     * @param config configuration for ClockWorker
     */
    public ClockWorker(String name, Configuration config) {
        super("ClockWorker");

        this.name = "ClockWorker[" + name + "]";

        this.configuration = config.lock();
        this.checkpointWorkQueueSize = config.getMaxWorkQueueSize() / 1000;
        if (this.checkpointWorkQueueSize < 500) {
            this.checkpointWorkQueueSize = 500;
        }

        this.workQueue = new LinkedBlockingQueue<WorkSchedule.Context>(this.configuration.getMaxWorkQueueSize());

        init(true);


        logger.info(this.name + " initialized");
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
        } while (this.threadWorkerCount.get() < this.configuration.getInitThreadWorkerSize());

    }


    /**
     * Create a WorkSchedule instance.
     *
     * @param work Work implement class object
     * @return new instance of WorkSchedule
     */
    public WorkSchedule createSchedule(Work work) {
        return createSchedule(null, work);
    }


    public WorkSchedule createSchedule(ScheduleConfig config, Work work) {
        WorkSchedule workSchedule = new WorkSchedule(this, work);
        if(config!=null) {
            workSchedule.setScheduleConfig(config);
        }

        return workSchedule;
    }


    /**
     * Create a WorkSchedule instance by work class
     *
     * @param workClass class of implement for Work
     * @return new instance of WorkSchedule
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public WorkSchedule createSchedule(Class workClass) throws InstantiationException, IllegalAccessException {
        return createSchedule( (Work) workClass.newInstance());
    }



    public WorkSchedule createSchedule(ScheduleConfig config,Class workClass) throws InstantiationException, IllegalAccessException {
        return createSchedule(config, (Work) workClass.newInstance());
    }


    /**
     * Create a WorkSchedule instance by work classname
     *
     * @param className classname of implement for Work
     * @return new instance of WorkSchedule
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public WorkSchedule createSchedule(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return new WorkSchedule(this, (Work) Class.forName(className).newInstance());
    }


    /**
     * Returns a count of managed Work implement instance.
     *
     * @return count of managed Work implement instance.
     */
    public int getManagedWorkCount() {
        return this.managedWorkCount.get();
    }


    /**
     * Returns a count of running Thread for processing Work object.
     */
    public int getThreadWorkerCount() {
        return this.threadWorkerCount.get();
    }

    /**
     * Returns a current queue size for processing Work object.
     */
    public int getWorkQueueSize() {
        return this.workQueue.size();
    }


    /**
     * Wait until completion of work.when the time is up, stop waiting
     *
     * @param timeout milli seconds for timeout
     */
    public void waitingJob(long timeout) {

        try {
            Thread.sleep(500);
            for (int i = 0; i < timeout / 100; i++) {
                if (this.getManagedWorkCount() > 0) {
                    Thread.sleep(100);

                } else {
                    if (this.getManagedWorkCount() == 0
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

        while (this.managedWorkCount.get() > 0) {
            waiting(100);

            long gap = System.currentTimeMillis() - tt1;
            if (gap > timeout && timeout > 0) {
                break;
            }

        }

        long tt2 = System.currentTimeMillis();

        long gap = (tt2 - tt1);
        if (gap > 0) {
            logger.warn(this.name + " wait time(milisecond) for async job : " + gap);
        }
    }




    public void awaitShutdown() {



        while (this.getManagedWorkCount() > 0) {

            waiting(100);

        }


        shutdown();

    }


    /**
     * Shutdown all process for these services. but wait for process complete until maximum 10 seconds
     */
    public void shutdown() {


        int count = 0;
        isWorking = false;
        while (this.getManagedWorkCount() > 0) {

            waiting(100);
            count++;

            // total 1 second
            if (count > 10) {
                break;
            }
        }


        try {
            super.destroy();
        } catch (IllegalThreadStateException ite) {

        } finally {
            logger.info(this.name + " shutdown");
        }
    }


    /**
     * Shutdown all process for these services. but wait for process complete until timeout
     *
     * @param timeout
     */
    public void shutdown(long timeout) {

        if (timeout > 0) {

            waitingJob(timeout);
        }

        shutdown();
    }


    boolean addWorkSchedule(WorkSchedule workSchedule) {

        boolean result = this.workQueue.offer(workSchedule.enterQueue(false));
        if (result) {


            int sz = this.workQueue.size();
            if (sz > checkpointWorkQueueSize) {
                this.checkPoolThread();
            }
        } else {
            this.checkPoolThread();
        }

        return result;


    }


    private void waiting(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {

        }
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





    private WorkScheduleList getWorkScheduleList(String eventName, boolean isCreate) {
        WorkScheduleList workScheduleList = eventMap.get(eventName);
        if (isCreate && workScheduleList == null) {

            synchronized (this) {
                workScheduleList = eventMap.get(eventName);
                if (workScheduleList == null) {
                    workScheduleList = new WorkScheduleList();

                    eventMap.put(eventName, workScheduleList);
                }
            }
        }
        return workScheduleList;
    }


    void bindEvent(WorkSchedule workSchedule,String... eventNames) {

        for(String eventName:eventNames) {
            WorkScheduleList workScheduleMap = getWorkScheduleList(eventName, true);

            workScheduleMap.add(workSchedule);
        }


    }


    void unindEvent(WorkSchedule workSchedule, String... eventNames) {

        for(String eventName:eventNames) {
            WorkScheduleList workScheduleMap = getWorkScheduleList(eventName, false);
            if (workScheduleMap == null) {
                return;
            }

            workScheduleMap.remove(workSchedule);
        }
    }

    public void raiseEvent(EventInfo event) {
        String eventName = event.getEventName();
        if(eventName!=null && eventName.length()>0) {
            raiseEvent(eventName, event);
        }
    }

    public void raiseEvent(String eventName, EventInfo event) {

        WorkScheduleList workScheduleList = getWorkScheduleList(eventName, false);

        EventInfo eventInfo = event;
        if (event == null) {
            eventInfo = new EventInfo();
        }

        //if (workScheduleList != null)
        {
            WorkSchedule[] array = workScheduleList.getWorkScheduleArray();
            for (WorkSchedule schedule : array) {



                schedule.raiseEvent(eventInfo);
                this.workQueue.offer(schedule.enterQueue(true));

            }
        }

    }



    /****************************************
     *
     * Sub class for WorkSchedule information
     *
     */
    private static class WorkScheduleList extends HashSet<WorkSchedule> {

        private WorkSchedule[] array = null;


        private void setArray() {
            array = this.toArray(new WorkSchedule[this.size()]);
        }

        WorkSchedule[] getWorkScheduleArray() {
            return this.array;
        }


        @Override
        public boolean add(WorkSchedule workSchedule) {
            boolean result;
            synchronized (this) {
                result = super.add(workSchedule);
                this.setArray();
            }
            return result;
        }

        @Override
        public boolean remove(Object o) {
            boolean result;
            synchronized (this) {
                result = super.remove(o);
                this.setArray();
            }
            return result;
        }

        @Override
        public void clear() {

            synchronized (this) {
                super.clear();
                this.setArray();
            }

        }
    }

    /****************************************
     *
     * Thread for Worker
     */
    private static class ThreadWorker extends Thread {

        private ClockWorker clockWorker;


        private int timeoutCount = 0;

        private int stoplessCount = 0;

        private long lastWorkTime = 0;

        private boolean isThreadRunning = false;

        private String uuid;

        private static int totalCount = 0;

        private int sequence=0;

        private int maxWaitTime = 2000;

        public ThreadWorker(ClockWorker parent, int threadPriority) {
            super(parent, "Clockwork:ThreadWorker-" + parent.threadWorkerCount);
            this.clockWorker = parent;
            this.setPriority(threadPriority);

            uuid = UUID.randomUUID().toString();

            totalCount++;
            sequence = totalCount;

            maxWaitTime = 2000 + sequence*100;

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


            //long chkeckNanoTime =  3*1000000;
            int count=0;
            while (isWorking()) {



                WorkSchedule workSchedule = null;
                try {


                    WorkSchedule.Context ctx = this.clockWorker.workQueue.poll(maxWaitTime,
                            TimeUnit.MILLISECONDS);



                    if (ctx == null) {
                        continue;
                    }else if(count==5000) {
                        Thread.sleep(0,1);

                    }else  if(count==10000) {
                        Thread.sleep(10);
                        count=0;
                    }else {
                        count++;
                    }

                    workSchedule = ctx.getWorkSchedule();
                    if (workSchedule == null) continue;
                    //debug(workSchedule.getWorkObject() + " SS");

                    this.lastWorkTime = System.currentTimeMillis();

                    if (workSchedule == null) {
                        timeoutCount++;
                        stoplessCount = 0;
                    } else {
                        timeoutCount = 0;
                        stoplessCount++;
                        Work slc = workSchedule.getWorkObject();

                        if (check(slc)) {

                            long remainMilliTime = workSchedule.checkRemainMilliTime();
                            //System.err.print("["+remainNanoTime+"] ");

                            if (ctx.isExecuteImmediately() || remainMilliTime < workSchedule.getPreemptiveMilliTime()) {


                                EventInfo eventInfo = workSchedule.checkEvent();
                                if (eventInfo == null) {
                                    eventInfo = workSchedule.getDefaultEventInfo();

                                }else {
                                    eventInfo.setWorkSechedule(workSchedule);
                                }

                                /**
                                 *
                                 *
                                 */

                                workSchedule.adjustWaiting();


                                long delaytime = slc.execute(eventInfo);
                                while (delaytime == Work.LOOP) {
                                    delaytime = slc.execute(eventInfo);
                                }

                                if(delaytime>0) {

                                    workSchedule.setRepeatInterval(delaytime);

                                    if (delaytime > this.clockWorker.configuration.getExecuteCountdownMilliTime()) {
                                        this.clockWorker.workChecker
                                                .addWorkStatusWrapper(workSchedule);
                                        //System.err.println("z1");
                                    }else {
                                        this.clockWorker.addWorkSchedule(workSchedule);
                                    }
                                } else if (delaytime == Work.SLEEP) {

                                    workSchedule.setRepeatInterval(-1);

                                    this.clockWorker.addWorkSchedule(workSchedule);
                                } else {

                                    workSchedule.finish();
                                }

                                //Thread.sleep(10);

                            } else if (!workSchedule.isEnd()) {
                                timeoutCount++;
                                stoplessCount = 0;


                                if (remainMilliTime > this.clockWorker.configuration.getExecuteCountdownMilliTime()) {
                                    //System.err.print("|"+remainNanoTime+"| ");
                                    this.clockWorker.workChecker
                                            .addWorkStatusWrapper(workSchedule);

                                }else {
                                    this.clockWorker
                                            .addWorkSchedule(workSchedule);


                                }

                            }

                        } else {
                            this.clockWorker.workQueue.put(workSchedule.enterQueue(false));
                        }

                    }

                    if (timeoutCount > 10) {
                        this.clockWorker.removeThreadWorker();
                        timeoutCount = 0;
                    } else if (stoplessCount > 1000) {
                        this.clockWorker.addThreadWorker();
                        stoplessCount = 0;
                    }

                } catch (RuntimeException re) {
                    workSchedule.finish();
                } catch (InterruptedException ite) {

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


    /****************************************
     *
     * Thread class for checking work status.
     */
    private static class WorkChecker extends Thread {

        private ClockWorker clockWorker;

        private BlockingQueue<WorkSchedule> statusWrappers = new LinkedBlockingQueue<WorkSchedule>();

        WorkChecker(ClockWorker clockWorker) {
            this.clockWorker = clockWorker;

            checkPerm();

        }

        private long adjTimeout = 0;

        private void checkPerm() {
            try {
                long t1 = System.currentTimeMillis();
                for (int i = 0; i < 1000; i++) {
                    Thread.sleep(1);
                }
                long t2 = System.currentTimeMillis();
                long adjTimeout = (t2-t1 -1000)*2 ;


            }catch (Exception e) {

            }
        }

        void addWorkStatusWrapper(WorkSchedule sb) {
            statusWrappers.add(sb);
        }

        @Override
        public void run() {

            long countdown = this.clockWorker.configuration.getExecuteCountdownMilliTime();
            while (clockWorker.isWorking) {

                try {
                    WorkSchedule wrapper = statusWrappers.poll(5, TimeUnit.SECONDS);


                    if (wrapper != null) {
                        long gap = (System.currentTimeMillis()+countdown) - wrapper.getNextExecuteTime();
                        if(gap>=0) {

                            //System.out.print(gap+" "+countdown+ " . ");

                            clockWorker.addWorkSchedule(wrapper);
                        } else {

                            Thread.sleep(1);

                            statusWrappers.add(wrapper);


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
