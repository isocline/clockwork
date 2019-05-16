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

import isocline.clockwork.event.WorkEventFactory;
import isocline.clockwork.flow.WorkInfo;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Base class for WorkSchedule management and thread management
 */
public class WorkProcessor extends ThreadGroup {

    protected static Logger logger = Logger.getLogger(WorkProcessor.class.getName());


    private String name;

    private boolean isWorking = false;

    private int checkpointWorkQueueSize = 500;


    private Configuration configuration;

    private List<ThreadWorker> threadWorkers = new ArrayList<ThreadWorker>();

    private AtomicInteger threadWorkerCount = new AtomicInteger(0);

    private BlockingQueue<WorkSchedule.ExecuteContext> workQueue;

    private WorkChecker workChecker;

    private Map<String, WorkScheduleList> eventMap = new ConcurrentHashMap<String, WorkScheduleList>();


    AtomicInteger managedWorkCount = new AtomicInteger(0);


    /**
     * Create a WorkProcessor object which provice services for WorkSchedule
     *
     * @param name   ClockerWorker name
     * @param config configuration for WorkProcessor
     */
    WorkProcessor(String name, Configuration config) {
        super("WorkProcessor");

        this.name = "WorkProcessor[" + name + "]";

        this.configuration = config.lock();
        this.checkpointWorkQueueSize = config.getMaxWorkQueueSize() / 1000;
        if (this.checkpointWorkQueueSize < 500) {
            this.checkpointWorkQueueSize = 500;
        }

        this.workQueue = new LinkedBlockingQueue<WorkSchedule.ExecuteContext>(this.configuration.getMaxWorkQueueSize());

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
     *
     * @param work
     * @param eventNames
     * @return
     */
    public WorkSchedule regist(Work work, String... eventNames) {
        WorkSchedule workSchedule = new WorkSchedule(this, work);
        workSchedule.setSleepMode();
        workSchedule.bindEvent(eventNames);

        workSchedule.activate();

        return workSchedule;
    }

    public WorkSchedule execute(Work work) {
        return execute(work, 0);
    }

    public WorkSchedule execute(Work work,long startDelayTime) {
        WorkSchedule workSchedule = new WorkSchedule(this, work);
        if(startDelayTime>0) {
            workSchedule.setStartDelayTime(startDelayTime);
        }

        workSchedule.activate();

        return workSchedule;
    }

    /**
     * Create a empty WorkSchedule instance
     *
     * @return
     */
    public WorkSchedule createSchedule() {
        return new WorkSchedule(this, null);
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


    public WorkSchedule createSchedule(ScheduleDescriptor config, Work work) {
        WorkSchedule workSchedule = new WorkSchedule(this, work);
        if (config != null) {
            workSchedule.setScheduleDescriptor(config);
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
        return createSchedule((Work) workClass.newInstance());
    }


    public WorkSchedule createSchedule(ScheduleDescriptor config, Class workClass) throws InstantiationException, IllegalAccessException {
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

        return addWorkSchedule(workSchedule, false);
    }


    boolean addWorkSchedule(WorkSchedule workSchedule, boolean isUserEvent) {

        boolean result = this.workQueue.offer(workSchedule.enterQueue(isUserEvent));
        //System.err.println("add >>> "+result);
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

    boolean addWorkSchedule(WorkSchedule workSchedule,WorkEvent workEvent) {

        boolean result = this.workQueue.offer(workSchedule.enterQueue(true, workEvent));
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

    void bindEvent(WorkSchedule workSchedule, String eventName) {


        WorkScheduleList workScheduleMap = getWorkScheduleList(eventName, true);

        workScheduleMap.add(workSchedule);


    }


    void bindEvent(WorkSchedule workSchedule, String... eventNames) {

        for (String eventName : eventNames) {
            bindEvent(workSchedule, eventName);
        }


    }


    void unbindEvent(WorkSchedule workSchedule, String... eventNames) {

        for (String eventName : eventNames) {
            WorkScheduleList workScheduleMap = getWorkScheduleList(eventName, false);
            if (workScheduleMap == null) {
                return;
            }

            workScheduleMap.remove(workSchedule);
        }
    }

    public void raiseEvent(WorkEvent event) {
        String eventName = event.getEventName();
        if (eventName != null && eventName.length() > 0) {
            raiseEvent(eventName, event);
        }
    }


    public void raiseEvent(String eventName, WorkEvent event) {

        WorkScheduleList workScheduleList = getWorkScheduleList(eventName, false);

        WorkEvent workEvent = event;
        if (event == null) {
            workEvent = WorkEventFactory.create();
        }

        //if (workScheduleList != null)
        {

            WorkSchedule[] array = workScheduleList.getWorkScheduleArray();
            for (WorkSchedule schedule : array) {

                String newEventName = schedule.checkRaiseEventEnable(eventName);

                if (newEventName != null) {
                    WorkEvent newWorkEvent = workEvent;
                    if (!newEventName.equals(eventName)) {
                        newWorkEvent = WorkEventFactory.create(newEventName);
                        workEvent.copyTo(newWorkEvent);

                    }
                    //schedule.raiseEvent(newWorkEvent);
                    this.workQueue.offer(schedule.enterQueue(true, newWorkEvent));

                }


            }
        }

    }

    public boolean isWorking() {
        return this.isWorking;
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

        private WorkProcessor workProcessor;


        private int timeoutCount = 0;

        private int stoplessCount = 0;

        private long lastWorkTime = 0;

        private boolean isThreadRunning = false;

        private String uuid;

        private static int totalCount = 0;

        private int sequence = 0;

        private int maxWaitTime = 2000;

        public ThreadWorker(WorkProcessor parent, int threadPriority) {
            super(parent, "Clockwork:ThreadWorker-" + parent.threadWorkerCount);
            this.workProcessor = parent;
            this.setPriority(threadPriority);

            uuid = UUID.randomUUID().toString();

            totalCount++;
            sequence = totalCount;

            maxWaitTime = 2000 + sequence * 100;

        }


        private boolean isWorking() {
            if (!this.workProcessor.isWorking) {
                return false;
            } else if (!isThreadRunning) {
                return false;
            }

            return true;
        }


        private boolean check(WorkSchedule schedule, long time) {

            if(!schedule.isActivated()) {
                return false;
            }

            return schedule.isExecuteEnable(time);
        }


        public boolean isDelayExecute() {

            long executeTimeout = workProcessor.configuration.getExecuteTimeout();

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
            int count = 0;

            while (isWorking()) {



                WorkSchedule workSchedule = null;
                try {

                    //System.err.print(">"+maxWaitTime+"< ");

                    WorkSchedule.ExecuteContext ctx = this.workProcessor.workQueue.poll(maxWaitTime,
                            TimeUnit.MILLISECONDS);



                    if (ctx == null) {
                        continue;
                    } else if (count == 5000) {
                        Thread.sleep(0, 1);

                    } else if (count == 10000) {
                        Thread.sleep(10);
                        count = 0;
                    } else {
                        count++;
                    }


                    workSchedule = ctx.getWorkSchedule();
                    if (workSchedule == null) continue;
                    //debug(workSchedule.getWorkObject() + " SS");

                    this.lastWorkTime = System.currentTimeMillis();


                    /*
                    if (workSchedule == null) {
                        timeoutCount++;
                        stoplessCount = 0;
                    } else
                    */

                    {
                        timeoutCount = 0;
                        stoplessCount++;
                        Work slc = workSchedule.getWorkObject();




                        if (check(workSchedule, this.lastWorkTime)) {

                            long remainMilliTime = workSchedule.checkRemainMilliTime();
                            //System.err.println("["+remainMilliTime+"] " + workSchedule.getPreemptiveMilliTime() +"
                            // " + ""+ctx.isExecuteImmediately());


                            /*
                            boolean chk1 = ctx.isExecuteImmediately();
                            boolean chk2 = (remainMilliTime < workSchedule.getPreemptiveMilliTime());
                            System.err.println(chk1 +" "+ chk2);
                            if (chk1 || chk2) {
                            */


                            if (ctx.isExecuteImmediately() || remainMilliTime < workSchedule.getPreemptiveMilliTime()) {


                                WorkEvent workEvent = ctx.getWorkEvent();
                                if (workEvent == null) {
                                    workEvent = workSchedule.getDefaultEventInfo();

                                } else {
                                    workEvent.setWorkSechedule(workSchedule);
                                }

                                /**
                                 *
                                 *
                                 */

                                workSchedule.adjustWaiting();



                                long delaytime = slc.execute(workEvent);


                                int loopCount = 0;
                                while (delaytime == Work.LOOP && loopCount<1000) {
                                    delaytime = slc.execute(workEvent);
                                    loopCount++;
                                }

                                if(delaytime==Work.LOOP) {
                                    delaytime = 1*Clock.SECOND;
                                }


                                if (delaytime == Work.WAIT) {

                                    long repeatInterval  = workSchedule.getIntervalTime();

                                    if(repeatInterval>0) {
                                        delaytime = repeatInterval;
                                    }
                                }

                                if (delaytime > 0) {

                                    //TODO
                                    //workSchedule.adjustRepeatInterval(delaytime);
                                    workSchedule.adjustDelayTime(delaytime);

                                    if (delaytime > this.workProcessor.configuration.getExecuteCountdownMilliTime()) {
                                        this.workProcessor.workChecker
                                                .addWorkStatusWrapper(workSchedule);
                                        //System.err.println("z1");
                                    } else {
                                        this.workProcessor.addWorkSchedule(workSchedule);
                                    }
                                } else if (delaytime == Work.WAIT) {

                                    workSchedule.adjustRepeatInterval(-1);


                                    //v2
                                    //this.workProcessor.addWorkSchedule(workSchedule);
                                } else {

                                    workSchedule.finish();
                                }

                                //Thread.sleep(10);

                            } else if (!workSchedule.isUntilEndTime()) {
                                timeoutCount++;
                                stoplessCount = 0;


                                if (remainMilliTime > this.workProcessor.configuration.getExecuteCountdownMilliTime()) {
                                    //System.err.print("|"+remainNanoTime+"| ");
                                    this.workProcessor.workChecker
                                            .addWorkStatusWrapper(workSchedule);

                                } else {
                                    this.workProcessor
                                            .addWorkSchedule(workSchedule);


                                }

                            } else {
                                workSchedule.finish();
                            }

                        } else {
                            this.workProcessor.workQueue.put(workSchedule.enterQueue(false));
                        }

                    }

                    if (timeoutCount > 10) {
                        this.workProcessor.removeThreadWorker();
                        timeoutCount = 0;
                    } else if (stoplessCount > 1000) {
                        this.workProcessor.addThreadWorker();
                        stoplessCount = 0;
                    }

                } catch (RuntimeException re) {
                    re.printStackTrace();
                    workSchedule.finish();
                } catch (InterruptedException ite) {
                    //ite.printStackTrace();

                } catch (Throwable e) {
                    e.printStackTrace();

                }

            }

            this.workProcessor.removeThreadWorker(this);

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

        private WorkProcessor workProcessor;

        private BlockingQueue<WorkSchedule> statusWrappers = new LinkedBlockingQueue<WorkSchedule>();

        WorkChecker(WorkProcessor workProcessor) {
            this.workProcessor = workProcessor;

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
                long adjTimeout = (t2 - t1 - 1000) * 2;


            } catch (Exception e) {

            }
        }

        void addWorkStatusWrapper(WorkSchedule sb) {
            statusWrappers.add(sb);
        }

        @Override
        public void run() {

            long countdown = this.workProcessor.configuration.getExecuteCountdownMilliTime();
            while (workProcessor.isWorking) {

                try {
                    WorkSchedule wrapper = statusWrappers.poll(5, TimeUnit.SECONDS);


                    if (wrapper != null) {
                        long gap = (System.currentTimeMillis() + countdown) - wrapper.getNextExecuteTime();
                        if (gap >= 0) {

                            workProcessor.addWorkSchedule(wrapper);
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


    private <T> T invokeAnnonations(T instance) throws IllegalAccessException {
        Method[] methods = instance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            WorkInfo annotation = method.getAnnotation(WorkInfo.class);
            if (annotation != null) {
                //field.setAccessible(true);
                //field.set(instance, annotation.isAsync());
            }
        }
        return instance;
    }

    /**
     * 매개변수로 받은 클래스의 객체를 반환합니다.
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T get(Class clazz) throws IllegalAccessException, InstantiationException {
        T instance = (T) clazz.newInstance();
        instance = invokeAnnonations(instance);
        return instance;
    }


}
