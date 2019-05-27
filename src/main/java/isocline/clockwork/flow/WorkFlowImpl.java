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

    private int funcExecSequence = 0;

    private String[] regReadyEventNameArray;

    private boolean isSetFinish = false;


    private FunctionExecutor lastFuncExecutor = null;

    private FunctionExecutor lastSyncFuncExecutor = null;

    private EventRepository<String, FunctionExecutorList> eventRepository = new EventRepository();

    private List<FunctionExecutor> functionExecutorList = new ArrayList<FunctionExecutor>();


    WorkFlowImpl() {

    }

    private void clearLastFunctionExecutor() {
        this.lastFuncExecutor = null;
        this.lastSyncFuncExecutor = null;
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
        if (regReadyEventNameArray != null) {
            newEventNameArray = new String[regReadyEventNameArray.length + inputEventNameArray.length];

            System.arraycopy(regReadyEventNameArray, 0, newEventNameArray, 0, regReadyEventNameArray.length);
            System.arraycopy(inputEventNameArray, 0, newEventNameArray, regReadyEventNameArray.length, inputEventNameArray.length);

        } else {
            newEventNameArray = inputEventNameArray;
        }

        regReadyEventNameArray = newEventNameArray;

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


        String[] newEventNameArray = new String[1];
        newEventNameArray[0] = fullEventName;

        newEventNameArray[newEventNameArray.length - 1] = fullEventName;

        regReadyEventNameArray = newEventNameArray;

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

        if (regReadyEventNameArray == null) {
            return false;
        }
        for (String eventName : regReadyEventNameArray) {

            String[] subEventNames = eventRepository.setBindEventNames(eventName);

            if(funcExecutor==null) {
                funcExecutor = new FunctionExecutor();
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
            regReadyEventNameArray = null;
        }

        return true;
    }


    public WorkFlowImpl runAsync(Runnable execObject) {
        return processRunAsync(execObject, null);
    }

    public WorkFlowImpl runAsync(Consumer<WorkEvent> execObject) {
        return processRunAsync(execObject, null);
    }

    public WorkFlowImpl runAsync(Runnable execObject, String eventName) {
        return processRunAsync(execObject, eventName);
    }

    public WorkFlowImpl runAsync(Consumer<WorkEvent> execObject, String eventName) {
        return processRunAsync(execObject, eventName);
    }

    /**
     * @param execObject
     * @return
     */
    private WorkFlowImpl processRunAsync(Object execObject, String eventName) {

        final FunctionExecutor asyncFunc = new FunctionExecutor(execObject);;
        this.lastFuncExecutor = asyncFunc;
        if (eventName != null) {
            this.lastFuncExecutor.setFireEventName(eventName);
        }

        if(this.lastSyncFuncExecutor!=null) {
            String eventNm = this.lastSyncFuncExecutor.getFireEventUUID();
            bindEventRepository(eventNm, asyncFunc);
        }

        //bindEventRepository(eventName, this.lastFuncExecutor);
        //eventRepository.put(eventName, this.lastFuncExecutor);

        boolean isRegist = bindEvent(asyncFunc,false);


        if (!isRegist && this.lastSyncFuncExecutor==null) {
            functionExecutorList.add(asyncFunc);
        }

        return this;
    }

    public WorkFlowImpl fireEvent(String eventName, long delayTime) {

        if(eventName==null || eventName.trim().length()==0) {
            throw new IllegalArgumentException("Event name is empty.");
        }

        return processNext(null, eventName,true , false, delayTime);
    }

    public WorkFlowImpl next(Runnable execObject) {
        return processNext(execObject, null, false);
    }

    public WorkFlowImpl next(Consumer<WorkEvent> execObject) {
        return processNext(execObject, null, false);
    }

    public WorkFlowImpl next(Runnable execObject, String eventName) {
        return processNext(execObject, eventName, false);
    }

    public WorkFlowImpl next(Consumer<WorkEvent> execObject, String eventName) {
        return processNext(execObject, eventName, true);
    }

    private WorkFlowImpl processNext(Object execObject, String eventName, boolean allowFuncInfNull) {
        return processNext(execObject, eventName,allowFuncInfNull, false,0);
    }


    private WorkFlowImpl processNext(Object functionalInterface, String fireEventName, boolean allowFuncInfNull, boolean isLastExecuteMethod, long delayTime) {

        if (!allowFuncInfNull && functionalInterface == null) {
            throw new IllegalArgumentException("function interface is null");
        }

        FunctionExecutor newFuncExecutor = new FunctionExecutor(functionalInterface);
        if (isLastExecuteMethod) {
            newFuncExecutor.setLastExecutor(true);
        }
        if (fireEventName != null) {
            newFuncExecutor.setFireEventName(fireEventName);
            newFuncExecutor.setDelayTimeFireEvent(delayTime);
        }


        boolean isInitialExecutor = false;
        if (this.lastFuncExecutor != null) {
            newFuncExecutor.setRecvEventName(this.lastFuncExecutor.getFireEventUUID());

            bindEventRepository(this.lastFuncExecutor.getFireEventUUID(), newFuncExecutor);
            //eventRepository.put(this.lastFuncExecutor.getFireEventUUID(), newFuncExecutor);
        } else {
            isInitialExecutor = true;
        }

        this.lastFuncExecutor = newFuncExecutor;
        this.lastSyncFuncExecutor = newFuncExecutor;


        boolean isRegist = bindEvent(this.lastFuncExecutor,true);

        if (!isRegist && isInitialExecutor) {
            functionExecutorList.add(this.lastFuncExecutor);
        }

        return this;
    }

    public WorkFlowImpl finish() {

        this.isSetFinish = true;
        return processNext(null, null, true, true,0);
    }

    public boolean isSetFinish() {
        return this.isSetFinish;
    }

    public FunctionExecutor getNextExecutor() {

        //IndexOutOfBoundsException

        FunctionExecutor exec = null;
        try {
            exec = functionExecutorList.get(this.funcExecSequence);
        } catch (IndexOutOfBoundsException ie) {
            return null;
        }
        if (exec != null) {
            funcExecSequence++;
        }

        return exec;
    }

    public boolean existNextFunctionExecutor() {
        if (this.funcExecSequence < functionExecutorList.size()) {
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
        if(this.lastFuncExecutor!=null) {
            return this.lastFuncExecutor.getFireEventUUID();
        }

        return null;
    }


}
