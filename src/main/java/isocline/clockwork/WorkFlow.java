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

import isocline.clockwork.flow.FunctionExecutor;
import isocline.clockwork.flow.FunctionExecutorList;

import java.util.function.Consumer;

/**
 * Interface for work flow
 *
 */

public interface WorkFlow {


    /**
     * If the input event occurs, proceed to the next operation.
     *
     * @param eventNames event names
     * @return an instance of WorkFlow
     */
    WorkFlow wait(String... eventNames);


    /**
     * Only if all the input events have occurred, execute the next definition method.
     *
     * @param eventNames event names
     * @return an instance of WorkFlow
     */
    WorkFlow waitAll(String... eventNames);


    /**
     * If the corresponding error event occurs, the subsequent method is executed.
     *
     * @param eventNames event names
     * @return an instance of WorkFlow
     */
    WorkFlow onError(String... eventNames);


    /**
     * If the input event occurs, proceed to the next operation.
     *
     * @param workFlows  WorkFlows
     * @return an instance of WorkFlow
     */
    WorkFlow wait(WorkFlow... workFlows);


    /**
     * Only if all the input events have occurred, execute the next definition method.
     *
     * @param workFlows Array of WorkFlow
     * @return an instance of WorkFlow
     */
    WorkFlow waitAll(WorkFlow... workFlows);


    /**
     *  If the corresponding error event occurs, the subsequent method is executed.
     *
     * @param workFlows executable object
     * @return an instance of WorkFlow
     */
    WorkFlow onError(WorkFlow... workFlows);


    /**
     * Asynchronously execute method of Runnable implement object
     *
     * @param execObject executable object
     * @return an instance of WorkFlow
     */
    WorkFlow runAsync(Runnable execObject);

    /**
     *
     * Asynchronously execute method of Consumer implement object
     *
     * @param execObject executable object
     * @return an instance of WorkFlow
     */
    WorkFlow runAsync(Consumer<WorkEvent> execObject);

    /**
     *
     * Asynchronously execute method of Runnable implement object.
     * Raises an event after completion of method execution.
     *
     * @param execObject executable object
     * @param fireEventName name of event
     * @return an instance of WorkFlow
     */
    WorkFlow runAsync(Runnable execObject, String fireEventName);

    /**
     *
     * Asynchronously execute method of Consumer implement object
     * Raises an event after completion of method execution.
     *
     * @param execObject executable object
     * @param fireEventName name of event
     * @return an instance of WorkFlow
     */
    WorkFlow runAsync(Consumer<WorkEvent> execObject, String fireEventName);


    /**
     * Execute the corresponding method at completion of the previous step method execution.
     *
     * @param execObject executable object
     * @return an instance of WorkFlow
     */
    WorkFlow next(Runnable execObject);


    /**
     * Execute the corresponding method at completion of the previous step method execution.
     *
     * @param execObject executable object
     * @return an instance of WorkFlow
     */
    WorkFlow next(Consumer<WorkEvent> execObject);

    /**
     * Execute the corresponding method at completion of the previous step method execution.
     * Raises an event after completion of method execution.
     *
     * @param execObject executable object
     * @param fireEventName name of event
     * @return an instance of WorkFlow
     */
    WorkFlow next(Runnable execObject, String fireEventName);

    /**
     * Execute the corresponding method at completion of the previous step method execution.
     * Raises an event after completion of method execution.
     *
     * @param execObject executable object
     * @param fireEventName name of event
     * @return an instance of WorkFlow
     */
    WorkFlow next(Consumer<WorkEvent> execObject, String fireEventName);


    /**
     * And generates an event. Delayed events can also be generated through the delay time setting.
     *
     * @param eventName an name of event
     * @param time delay time
     * @return an instance of WorkFlow
     */
    WorkFlow fireEvent(String eventName, long time);

    /**
     *
     * finish workflow.
     *
     * @return an instance of WorkFlow
     */
    WorkFlow finish();

    /**
     * Check whether the execution completion status is set.
     * @return true/false
     */
    boolean isSetFinish();

    /**
     * Returns a fireEvent FunctionExecutor object.
     *
     * @return FunctionExecutor
     */
    FunctionExecutor getNextExecutor();


    /**
     * Check whether there is an executor for the fireEvent processing.
     *
     * @return true/false
     */
    boolean existNextFunctionExecutor();


    /**
     *
     * Returns an executor list for handling this event.
     *
     * @param eventName name of event
     * @return FunctionExecutorList instance
     */
    FunctionExecutorList getFunctionExecutorList(String eventName);


    /**
     * Returns UUID information related to the unique event of the last state so far.
     *
     * @return UUID
     */
    String cursor();





}
