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
package isocline.clockwork.flow;

import isocline.clockwork.WorkEvent;
import isocline.clockwork.WorkFlow;
import isocline.clockwork.event.EventRepository;
import isocline.clockwork.event.EventSet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 *
 */
public class WorkFlowImpl implements WorkFlow {

    private int sequence = 0;

    private String[] registReadyEventNameArray;

    private boolean isSetFinish = false;


    private FunctionExecutor lastFuncExecutor = null;

    private EventRepository<String, FunctionExecutorList> eventRepository = new EventRepository();

    private List<FunctionExecutor> functionExecutorList = new ArrayList<FunctionExecutor>();


    WorkFlowImpl() {

    }

    private void clearLastFunctionExecutor() {
        this.lastFuncExecutor = null;
    }

    private void bindEventRepository(String eventName, FunctionExecutor functionExecutor) {



        FunctionExecutorList functionExecutorList = this.eventRepository.get(eventName);
        if(functionExecutorList==null) {
            functionExecutorList = new FunctionExecutorList();
            this.eventRepository.put(eventName, functionExecutorList);
        }

        functionExecutorList.add(functionExecutor);

    }

    public WorkFlow onError(String... eventNames) {
        String[] inputEventNameArray = eventNames;
        for(int i=0;i<inputEventNameArray.length;i++) {
            inputEventNameArray[i] = inputEventNameArray[i]+"::error";
        }

        wait(inputEventNameArray);

        return this;
    }

    public WorkFlow wait(String... eventNames) {

        clearLastFunctionExecutor();

        String[] inputEventNameArray = eventNames;


        String[] newEventNameArray = null;
        if (registReadyEventNameArray != null) {
            newEventNameArray = new String[registReadyEventNameArray.length + inputEventNameArray.length];

            System.arraycopy(registReadyEventNameArray, 0, newEventNameArray, 0, registReadyEventNameArray.length);
            System.arraycopy(inputEventNameArray, 0, newEventNameArray, registReadyEventNameArray.length, inputEventNameArray.length);

        } else {
            newEventNameArray = inputEventNameArray;
        }

        registReadyEventNameArray = newEventNameArray;

        return this;
    }

    public WorkFlow waitAll(String... eventNames) {


        clearLastFunctionExecutor();

        String fullEventName = "";
        for (int i = 0; i < eventNames.length; i++) {

            fullEventName = fullEventName + eventNames[i];

            if (i < (eventNames.length - 1)) {
                fullEventName = fullEventName + "&";
            }
        }

        /*
        String[] newEventNameArray = null;
        if (registReadyEventNameArray != null) {
            newEventNameArray = new String[registReadyEventNameArray.length + 1];

            System.arraycopy(registReadyEventNameArray, 0, newEventNameArray, 0, registReadyEventNameArray.length);

        } else {
            newEventNameArray = new String[1];
        }
        */

        String[] newEventNameArray = new String[1];
        newEventNameArray[0] = fullEventName;

        newEventNameArray[newEventNameArray.length - 1] = fullEventName;

        registReadyEventNameArray = newEventNameArray;

        return this;
    }


    @Override
    public WorkFlow wait(WorkFlow... workFlows) {

        String[] eventNameArray = new String[workFlows.length];

        for(int i=0;i<eventNameArray.length;i++){
            eventNameArray[i] = workFlows[i].cursor();
        }
        return wait(eventNameArray);
    }

    @Override
    public WorkFlow waitAll(WorkFlow... workFlows) {
        String[] eventNameArray = new String[workFlows.length];

        for(int i=0;i<eventNameArray.length;i++){
            eventNameArray[i] = workFlows[i].cursor();
        }
        return waitAll(eventNameArray);
    }

    @Override
    public WorkFlow onError(WorkFlow... workFlows) {
        String[] eventNameArray = new String[workFlows.length];

        for(int i=0;i<eventNameArray.length;i++){
            eventNameArray[i] = workFlows[i].cursor();
        }
        return onError(eventNameArray);
    }

    /**
     *
     * @param funcExecutor
     * @param reset
     * @return
     */
    private boolean bindEvent(FunctionExecutor funcExecutor , boolean reset) {

        if (registReadyEventNameArray == null) {
            return false;
        }
        for (String eventName : registReadyEventNameArray) {

            String[] subEventNames = eventRepository.setBindEventNames(eventName);

            if(funcExecutor==null) {
                funcExecutor = new FunctionExecutor(null);
            }


            bindEventRepository(eventName, funcExecutor);
            //eventRepository.put(eventName, funcExecutor);

            for (String subEventName : subEventNames) {


                if(!eventName.equals(subEventName)) {
                    bindEventRepository(subEventName, funcExecutor);
                    //eventRepository.put(subEventName, funcExecutor);
                }

            }
        }

        if (reset) {
            registReadyEventNameArray = null;
        }

        return true;
    }

    private boolean processBindEvent2(Object runnable, String fireEvent, boolean reset) {

        if (registReadyEventNameArray == null) {
            return false;
        }
        for (String eventName : registReadyEventNameArray) {

            String[] subEventNames = eventRepository.setBindEventNames(eventName);

            lastFuncExecutor = new FunctionExecutor(runnable);
            lastFuncExecutor.setRecvEventName(eventName);
            if(fireEvent!=null) {
                lastFuncExecutor.setFireEventName(fireEvent);
            }


            bindEventRepository(eventName, lastFuncExecutor);
            //eventRepository.put(eventName, lastFuncExecutor);

            for (String subEventName : subEventNames) {
                bindEventRepository(subEventName, lastFuncExecutor);
                //eventRepository.put(subEventName, lastFuncExecutor);
            }
        }

        if (reset) {
            registReadyEventNameArray = null;
        }

        return true;
    }

    public WorkFlowImpl run(Runnable execObject) {
        return processRun(execObject, null);
    }

    public WorkFlowImpl run(Consumer<WorkEvent> execObject) {
        return processRun(execObject, null);
    }

    public WorkFlowImpl run(Runnable execObject, String eventName) {
        return processRun(execObject, eventName);
    }

    public WorkFlowImpl run(Consumer<WorkEvent> execObject, String eventName) {
        return processRun(execObject, eventName);
    }

    /**
     * @param execObject
     * @return
     */
    private WorkFlowImpl processRun(Object execObject, String eventName) {
        lastFuncExecutor = new FunctionExecutor(execObject);
        if (eventName != null) {
            lastFuncExecutor.setFireEventName(eventName);
        }

        //bindEventRepository(eventName, lastFuncExecutor);
        //eventRepository.put(eventName, lastFuncExecutor);

        boolean isRegist = bindEvent(lastFuncExecutor,false);

        if (!isRegist) {
            functionExecutorList.add(lastFuncExecutor);
        }

        return this;
    }

    public WorkFlowImpl fireEvent(String eventName, long delayTime) {

        if(eventName==null || eventName.trim().length()==0) {
            throw new IllegalArgumentException("Event name is empty.");
        }

        return processNext(null, eventName,false , false, delayTime);
    }

    public WorkFlowImpl next(Runnable execObject) {
        return processNext(execObject, null, true);
    }

    public WorkFlowImpl next(Consumer<WorkEvent> execObject) {
        return processNext(execObject, null, true);
    }

    public WorkFlowImpl next(Runnable execObject, String eventName) {
        return processNext(execObject, eventName, true);
    }

    public WorkFlowImpl next(Consumer<WorkEvent> execObject, String eventName) {
        return processNext(execObject, eventName, false);
    }

    private WorkFlowImpl processNext(Object execObject, String eventName, boolean checkNull) {
        return processNext(execObject, eventName,checkNull, false,0);
    }


    private WorkFlowImpl processNext(Object execObject, String eventName, boolean checkNull, boolean isLast, long delayTime) {

        if (checkNull && execObject == null) {
            throw new IllegalArgumentException("function interface is null");
        }

        FunctionExecutor newFuncExecutor = new FunctionExecutor(execObject);
        if (isLast) {
            newFuncExecutor.setLastExecutor(true);
        }
        if (eventName != null) {
            newFuncExecutor.setFireEventName(eventName);
            newFuncExecutor.setDelayTimeFireEvent(delayTime);
        }


        boolean isInitialExecutor = false;
        if (lastFuncExecutor != null) {
            newFuncExecutor.setRecvEventName(lastFuncExecutor.getEventUUID());

            bindEventRepository(lastFuncExecutor.getEventUUID(), newFuncExecutor);
            //eventRepository.put(lastFuncExecutor.getEventUUID(), newFuncExecutor);
        } else {
            isInitialExecutor = true;
        }

        lastFuncExecutor = newFuncExecutor;

        //eventRepository.put(eventName, lastFuncExecutor);


        boolean isRegist = bindEvent(lastFuncExecutor,true);

        if (!isRegist && isInitialExecutor) {
            functionExecutorList.add(lastFuncExecutor);
        }

        return this;
    }

    public WorkFlowImpl finish() {


        this.isSetFinish = true;
        return processNext(null, null, false, true,0);
    }

    public boolean isSetFinish() {
        return this.isSetFinish;
    }

    public FunctionExecutor getNextExecutor() {

        //IndexOutOfBoundsException

        FunctionExecutor exec = null;
        try {
            exec = functionExecutorList.get(this.sequence);
            //System.out.println("getNextExecutor:"+sequence );
        } catch (IndexOutOfBoundsException ie) {
            return null;
        }
        if (exec != null) {
            sequence++;
        }

        return exec;
    }

    public boolean existNexExcutor() {
        if (this.sequence < functionExecutorList.size()) {
            return true;
        }

        return false;
    }


    public FunctionExecutorList getFunctionExecutorList(String eventName) {
        if (eventName == null) return null;



        EventSet eventSet = eventRepository.getEventSet(eventName);

        if (eventSet == null || eventSet.isRaiseEventReady(eventName)) {
            FunctionExecutorList functionExecutorList = this.eventRepository.get(eventName);

            if (functionExecutorList != null) {
                return functionExecutorList;
            }

        }

        return null;


    }

    @Override
    public String cursor() {
        if(lastFuncExecutor!=null) {
            return lastFuncExecutor.getEventUUID();
        }

        return null;
    }


}
