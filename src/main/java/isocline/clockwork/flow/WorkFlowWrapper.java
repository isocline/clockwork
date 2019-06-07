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

import isocline.clockwork.WorkFlow;
import isocline.clockwork.WorkFlowPattern;
import isocline.clockwork.flow.func.*;

import java.util.function.Consumer;

public class WorkFlowWrapper<T> implements WorkFlow<T> {

    protected WorkFlowImpl workFlowInstance;

    private String cursor;


    WorkFlowWrapper(WorkFlowImpl workFlow) {
        this.workFlowInstance = workFlow;
        this.cursor = workFlow.cursor();
    }


    @Override
    public WorkFlow wait(String... eventNames) {
        this.workFlowInstance.wait(eventNames);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow waitAll() {
        this.workFlowInstance.waitAll();
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow waitAll(String... eventNames) {
        this.workFlowInstance.waitAll(eventNames);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow onError(String... eventNames) {
        this.workFlowInstance.onError(eventNames);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow wait(WorkFlow... workFlows) {
        this.workFlowInstance.wait(workFlows);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow waitAll(WorkFlow... workFlows) {
        this.workFlowInstance.waitAll(workFlows);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow onError(WorkFlow... workFlows) {
        this.workFlowInstance.onError(workFlows);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow onError(Class... errorClasses) {
        this.workFlowInstance.onError(errorClasses);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow onError() {
        this.workFlowInstance.onError(this);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow runAsync(Runnable execObject) {
        this.workFlowInstance.runAsync(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    //@Override
    public WorkFlow runAsync(Consumer execObject) {
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow runAsync(Runnable execObject, String eventName) {
        this.workFlowInstance.runAsync(execObject, eventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow runAsync(Runnable execObject, int count) {
        this.workFlowInstance.runAsync(execObject, count);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    //@Override
    public WorkFlow runAsync(Consumer execObject, int count) {
        this.workFlowInstance.runAsync(execObject, count);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    //@Override
    public WorkFlow runAsync(Consumer execObject, String eventName) {
        this.workFlowInstance.runAsync(execObject, eventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow runAsync(WorkEventConsumer execObject) {
        this.workFlowInstance.runAsync(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow runAsync(WorkEventConsumer execObject, String fireEventName) {
        this.workFlowInstance.runAsync(execObject, fireEventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow runAsync(WorkEventConsumer execObject, int count) {
        this.workFlowInstance.runAsync(execObject, count);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow applyAsync(WorkEventFunction execObject) {
        this.workFlowInstance.applyAsync(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow applyAsync(WorkEventFunction execObject, String fireEventName) {
        this.workFlowInstance.applyAsync(execObject, fireEventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow applyAsync(WorkEventFunction execObject, int count) {
        this.workFlowInstance.applyAsync(execObject, count);
        return new WorkFlowWrapper(this.workFlowInstance);
    }



    @Override
    public WorkFlow mapAsync(WorkEventFunction... execObjects) {
        this.workFlowInstance.mapAsync(execObjects);
        return new WorkFlowWrapper(this.workFlowInstance);
    }








    @Override
    public WorkFlow branch(ReturnEventFunction execObject) {
        this.workFlowInstance.branch(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow check(CheckFunction execObject) {
        this.workFlowInstance.check(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow check(int maxCount) {
        this.workFlowInstance.check(maxCount);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow next(Runnable execObject) {
        this.workFlowInstance.next(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }


    @Override
    public WorkFlow next(Runnable execObject, String eventName) {
        this.workFlowInstance.next(execObject, eventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }



    //@Override
    public WorkFlow next(Consumer<? super T> execObject) {
        this.workFlowInstance.next(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }



    // @Override
    public WorkFlow next(Consumer<? super T> execObject, String eventName) {
        this.workFlowInstance.next(execObject, eventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }


    @Override
    public WorkFlow next(WorkEventConsumer execObject) {
        this.workFlowInstance.next(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow next(WorkEventConsumer execObject, String eventName) {
        this.workFlowInstance.next(execObject, eventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }


    @Override
    public WorkFlow next(WorkEventFunction execObject) {
        this.workFlowInstance.next(execObject);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow next(WorkEventFunction execObject, String eventName) {
        this.workFlowInstance.next(execObject, eventName);
        return new WorkFlowWrapper(this.workFlowInstance);
    }



    @Override
    public WorkFlow pattern(WorkFlowPattern pattern, WorkFlowPatternFunction func) {
        this.workFlowInstance.pattern(pattern, func);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow fireEvent(String eventName, long time) {
        this.workFlowInstance.fireEvent(eventName, time);
        return new WorkFlowWrapper(this.workFlowInstance);

    }


    @Override
    public WorkFlow fireEventOnError(String eventName, long time) {
        this.workFlowInstance.fireEventOnError(eventName, time);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow count(int maxCount) {
        this.workFlowInstance.count(maxCount);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow retryOnError(int maxCount, long delayTime) {
        this.workFlowInstance.retryOnError(maxCount, delayTime);
        return new WorkFlowWrapper(this.workFlowInstance);
    }

    @Override
    public WorkFlow finish() {
        this.workFlowInstance.finish();
        return new WorkFlowWrapper(this.workFlowInstance);

    }

    @Override
    public boolean isSetFinish() {
        return this.workFlowInstance.isSetFinish();
    }

    @Override
    public FunctionExecutor getNextExecutor() {
        return this.workFlowInstance.getNextExecutor();
    }

    @Override
    public boolean existNextFunctionExecutor() {
        return this.workFlowInstance.existNextFunctionExecutor();
    }

    @Override
    public FunctionExecutorList getFunctionExecutorList(String eventName) {
        return this.workFlowInstance.getFunctionExecutorList(eventName);

    }

    @Override
    public String cursor() {
        return this.cursor;
    }

    @Override
    public String toString() {
        return cursor;
    }
}
