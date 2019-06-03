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
import isocline.clockwork.log.XLogger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Base class for WorkSchedule management and thread management
 *
 * @author Richard D. Kim
 * @see isocline.clockwork.Work
 * @see isocline.clockwork.FlowableWork
 * @see isocline.clockwork.WorkSchedule
 */
public class WorkProcessor extends ThreadGroup {

    protected static XLogger logger = XLogger.getLogger(WorkProcessor.class);


    private String name;

    private boolean isWorking = false;

    private int checkpointWorkQueueSize = 500;


    private Configuration configuration;

    private List<ThreadWorker> threadWorkers = new ArrayList<ThreadWorker>();

    private AtomicInteger currentThreadWorkerCount = new AtomicInteger(0);

    private BlockingQueue<WorkSchedule.ExecuteContext> workQueue;

    private WorkChecker workChecker;

    private Map<String, WorkScheduleList> eventMap = new ConcurrentHashMap<String, WorkScheduleList>();


    AtomicInteger managedWorkCount = new AtomicInteger(0);


    private static WorkProcessor defaultWorkProcessor;

    private static Map<String, WorkProcessor> processorMap = new HashMap<String, WorkProcessor>();


    public static WorkProcessor main() {


        if (defaultWorkProcessor == null || !defaultWorkProcessor.isWorking()) {
            defaultWorkProcessor = new WorkProcessor("default", getDefaultConfiguration());
        }

        return defaultWorkProcessor;
    }

    private static Configuration getDefaultConfiguration() {
        String processorType = System.getProperty("isocline.clockwork.processor.type");

        if ("performance".equals(processorType)) {
            return Configuration.PERFORMANCE;
        } else if ("echo".equals(processorType)) {
            return Configuration.ECHO;
        } else if ("hyper".equals(processorType)) {
            return Configuration.HYPER;
        }

        return Configuration.NOMAL;
    }


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


    private void init(boolean isWorking) {

        this.isWorking = isWorking;

        workChecker = new WorkChecker(this);
        workChecker.start();

        do {
            addThreadWorker();
        } while (this.currentThreadWorkerCount.get() < this.configuration.getInitThreadWorkerSize());

    }

    /**
     * Register the task to be bound to the input events.
     *
     * @param work       an instance of Work
     * @param eventNames an event names
     * @return an new instance of WorkSchedule
     */
    public WorkSchedule newSchedule(Work work, String... eventNames) {
        WorkSchedule workSchedule = new WorkSchedule(this, work);
        workSchedule.setSleepMode();
        workSchedule.bindEvent(eventNames);

        workSchedule.subscribe();

        return workSchedule;
    }


    public WorkSchedule newFlow(AbstractFlowableWork workFlow) {

        WorkSchedule workSchedule = new WorkSchedule(this, workFlow);


        return workSchedule;
    }

    public WorkSchedule execute(Work work) {
        return execute(work, 0);
    }

    public WorkSchedule execute(Work work, long startDelayTime) {
        WorkSchedule workSchedule = new WorkSchedule(this, work);
        if (startDelayTime > 0) {
            workSchedule.startDelayTime(startDelayTime);
        }

        workSchedule.subscribe();

        return workSchedule;
    }

    /**
     * Create a empty WorkSchedule instance
     *
     * @return a new instance of WorkSchedule
     */
    public WorkSchedule newSchedule() {
        return new WorkSchedule(this, null);
    }


    /**
     * Create a WorkSchedule instance.
     *
     * @param work Work implement class object
     * @return new instance of WorkSchedule
     */
    public WorkSchedule newSchedule(Work work) {
        return newSchedule(null, work);
    }


    public WorkSchedule newSchedule(ScheduleDescriptor config, Work work) {
        WorkSchedule workSchedule = new WorkSchedule(this, work);
        if (config != null) {
            workSchedule.scheduleDescriptor(config);
        }

        return workSchedule;
    }


    /**
     * Create a WorkSchedule instance by work class
     *
     * @param workClass class of implement for Work
     * @return new instance of WorkSchedule
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public WorkSchedule newSchedule(Class workClass) throws InstantiationException, IllegalAccessException {
        return newSchedule((Work) workClass.newInstance());
    }


    /**
     * Create a WorkSchedule instance by work class
     *
     * @param descriptor an description for scheduling
     * @param workClass  class of implement for Work
     * @return new instance of WorkSchedule
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public WorkSchedule newSchedule(ScheduleDescriptor descriptor, Class workClass) throws InstantiationException, IllegalAccessException {
        return newSchedule(descriptor, (Work) workClass.newInstance());
    }


    /**
     * Create a WorkSchedule instance by work classname
     *
     * @param className classname of implement for Work
     * @return new instance of WorkSchedule
     * @throws ClassNotFoundException if the class cannot be located
     * @throws InstantiationException if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     */
    public WorkSchedule newSchedule(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
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
     *
     * @return a current count of thread worker
     */
    public int getCurrentThreadWorkerCount() {
        return this.currentThreadWorkerCount.get();
    }

    /**
     * Returns a current queue size for processing Work object.
     *
     * @return a current size of work queue
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

        long tt1 = System.currentTimeMillis();


        long t1 = System.currentTimeMillis() + timeout;

        try {
            long gap = t1 - System.currentTimeMillis();
            Thread.sleep(500);
            while (gap > 1) {
                gap = t1 - System.currentTimeMillis();
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


        for (ThreadWorker t : this.threadWorkers) {
            t.interrupt();
        }


        long tt2 = System.currentTimeMillis();

        long gap = (tt2 - tt1);
        if (gap > 0) {
            logger.warn(this.name + " wait time(milliseconds) for async job : " + gap);
        }
    }


    public void awaitShutdown() {

        long t1 = System.currentTimeMillis();

        while (this.getManagedWorkCount() > 0) {

            waiting(200);

            long t2 = System.currentTimeMillis();

            long gap = t2 - t1;
            if (gap > 1000 * 60 * 3) {
                logger.debug("running work count:" + this.getManagedWorkCount());
                t1 = System.currentTimeMillis();

            }

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
     * @param timeout a milliseconds for timeout
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

    boolean addWorkSchedule(WorkSchedule workSchedule, WorkEvent workEvent) {

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


    boolean addWorkSchedule(WorkSchedule workSchedule, WorkEvent workEvent, long delayTime) {

        workEvent.setFireTime(System.currentTimeMillis() + delayTime);

        this.workChecker.addWorkStatusWrapper(workSchedule, workEvent);

        return true;
    }


    private void waiting(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {

        }
    }


    private synchronized boolean addThreadWorker() {

        int i = currentThreadWorkerCount.get();
        if (i < this.configuration.getMaxThreadWorkerSize()) {
            ThreadWorker mon = new ThreadWorker(this, this.configuration.getThreadPriority());
            threadWorkers.add(mon);
            mon.setDaemon(true);
            mon.start();

            currentThreadWorkerCount.addAndGet(1);
            return true;
        }

        return false;

    }


    /**
     * @param t
     * @return
     */
    private synchronized boolean removeThreadWorker(ThreadWorker t) {

        currentThreadWorkerCount.addAndGet(-1);
        return threadWorkers.remove(t);

    }

    /**
     * @return
     */
    private synchronized boolean removeThreadWorker() {

        if (currentThreadWorkerCount.get() > this.configuration.getInitThreadWorkerSize()) {
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

        logger.debug("checkPoolThread");

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

                String newEventName = schedule.getDeliverableEventName(eventName);

                if (newEventName != null) {
                    WorkEvent newWorkEvent = workEvent;
                    if (!newEventName.equals(eventName)) {
                        /*
                        newWorkEvent = WorkEventFactory.createChild(newEventName);
                        workEvent.copyTo(newWorkEvent);
                        */
                        newWorkEvent = workEvent.createChild(newEventName);

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
     ****************************************/
    static final class WorkScheduleList extends HashSet<WorkSchedule> {

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
     *
     ****************************************/
    static final class ThreadWorker extends Thread {

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
            super(parent, "Clockwork:ThreadWorker-" + parent.currentThreadWorkerCount);
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

            if (!schedule.isSubscribed()) {
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
                return true;
            }

            return false;
        }

        public void run() {

            isThreadRunning = true;


            int count = 0;

            while (isWorking()) {

                WorkSchedule workSchedule = null;

                try {

                    final WorkSchedule.ExecuteContext ctx = this.workProcessor.workQueue.poll(maxWaitTime,
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

                    this.lastWorkTime = System.currentTimeMillis();

                    timeoutCount = 0;
                    stoplessCount++;

                    Work workObject = workSchedule.getWorkObject();


                    if (check(workSchedule, this.lastWorkTime)) {

                        long remainMilliTime = workSchedule.checkRemainMilliTime();

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


                            workSchedule.adjustWaiting();

                            long delaytime = workObject.execute(workEvent);

                            int loopCount = 0;
                            while (delaytime == Work.LOOP && loopCount < 1000) {
                                delaytime = workObject.execute(workEvent);
                                loopCount++;
                            }

                            if (delaytime == Work.LOOP) {
                                delaytime = 1 * Clock.SECOND;
                            }


                            if (delaytime == Work.WAIT) {

                                long repeatInterval = workSchedule.getIntervalTime();

                                if (repeatInterval > 0) {
                                    delaytime = repeatInterval;
                                }
                            }

                            if (delaytime > 0) {

                                workSchedule.adjustDelayTime(delaytime);

                                if (delaytime > this.workProcessor.configuration.getThresholdWaitTimeToReady()) {
                                    this.workProcessor.workChecker
                                            .addWorkStatusWrapper(workSchedule);
                                } else {
                                    this.workProcessor.addWorkSchedule(workSchedule);
                                }

                            } else if (delaytime == Work.WAIT) {
                                workSchedule.adjustRepeatInterval(Work.WAIT);
                            } else {
                                workSchedule.finish();
                            }


                        } else if (!workSchedule.isUntilEndTime()) {
                            timeoutCount++;
                            stoplessCount = 0;

                            if (remainMilliTime > this.workProcessor.configuration.getThresholdWaitTimeToReady()) {
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

    private static class WorkScheduleWrapper {

        private WorkSchedule workSchedule = null;

        private WorkEvent workEvent = null;

        WorkScheduleWrapper(WorkSchedule workSchedule) {
            this.workSchedule = workSchedule;
        }

        WorkScheduleWrapper(WorkSchedule workSchedule, WorkEvent workEvent) {
            this(workSchedule);
            this.workEvent = workEvent;
        }


        WorkSchedule getWorkSchedule() {
            return workSchedule;
        }

        WorkEvent getWorkEvent() {
            return workEvent;
        }


    }


    /****************************************
     *
     * Thread class for checking work status.
     */
    static final class WorkChecker extends Thread {

        private WorkProcessor workProcessor;

        private BlockingQueue<WorkScheduleWrapper> statusWrappers = new LinkedBlockingQueue<WorkScheduleWrapper>();

        WorkChecker(WorkProcessor workProcessor) {
            this.workProcessor = workProcessor;
        }


        void addWorkStatusWrapper(WorkSchedule sb) {
            statusWrappers.add(new WorkScheduleWrapper(sb));
        }

        void addWorkStatusWrapper(WorkSchedule sb, WorkEvent event) {
            statusWrappers.add(new WorkScheduleWrapper(sb, event));
        }

        @Override
        public void run() {

            long thresholdWaitTimeToReady = this.workProcessor.configuration.getThresholdWaitTimeToReady();
            while (workProcessor.isWorking) {

                try {
                    WorkScheduleWrapper workScheduleWrapper = statusWrappers.poll(5, TimeUnit.SECONDS);


                    if (workScheduleWrapper != null) {
                        WorkSchedule workSchedule = workScheduleWrapper.getWorkSchedule();

                        long nextExecuteTime = -1;

                        WorkEvent event = workScheduleWrapper.getWorkEvent();
                        if (event != null) {
                            nextExecuteTime = event.getFireTime();
                        }

                        if (nextExecuteTime < 0) {
                            nextExecuteTime = workSchedule.getNextExecuteTime();
                        }

                        long gap = (System.currentTimeMillis() + thresholdWaitTimeToReady) - nextExecuteTime;
                        if (gap >= 0) {

                            if (event != null) {
                                workProcessor.addWorkSchedule(workSchedule, event);
                            } else {
                                workProcessor.addWorkSchedule(workSchedule);
                            }


                        } else {

                            Thread.sleep(1);
                            statusWrappers.add(workScheduleWrapper);


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
     * Returns a object
     *
     * @param clazz class
     * @param <T>   Type
     * @return instance
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     * @throws InstantiationException if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.
     */
    public <T> T get(Class clazz) throws IllegalAccessException, InstantiationException {
        T instance = (T) clazz.newInstance();
        instance = invokeAnnonations(instance);
        return instance;
    }


}
