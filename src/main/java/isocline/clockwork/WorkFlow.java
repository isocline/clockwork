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

import java.util.Queue;
import java.util.function.Consumer;

/**
 * Interface for work flow
 *
 */

public interface WorkFlow {


    /**
     * If the input event occurs, proceed to the next operation.
     *
     * @param eventNames
     * @return
     */
    WorkFlow wait(String... eventNames);


    /**
     * Only if all the input events have occurred, execute the next definition method.
     *
     * @param eventNames
     * @return
     */
    WorkFlow waitAll(String... eventNames);


    /**
     * If the corresponding error event occurs, the subsequent method is executed.
     *
     * @param eventNames
     * @return
     */
    WorkFlow onError(String... eventNames);


    /**
     * If the input event occurs, proceed to the next operation.
     *
     * @param workFlows
     * @return
     */
    WorkFlow wait(WorkFlow... workFlows);


    /**
     * Only if all the input events have occurred, execute the next definition method.
     *
     * @param workFlows
     * @return
     */
    WorkFlow waitAll(WorkFlow... workFlows);


    /**
     *  If the corresponding error event occurs, the subsequent method is executed.
     *
     * @param workFlows
     * @return
     */
    WorkFlow onError(WorkFlow... workFlows);


    /**
     * Asynchronously execute method of Runnable implement object
     *
     * @param execObject
     * @return
     */
    WorkFlow run(Runnable execObject);

    /**
     *
     * Asynchronously execute method of Consumer<WorkEvent>  implement object
     *
     * @param execObject
     * @return
     */
    WorkFlow run(Consumer<WorkEvent> execObject);

    /**
     *
     * Asynchronously execute method of Runnable implement object.
     * Raises an event after completion of method execution.
     *
     * @param execObject
     * @param eventName
     * @return
     */
    WorkFlow run(Runnable execObject, String eventName);

    /**
     *
     * Asynchronously execute method of Consumer<WorkEvent> implement object
     * Raises an event after completion of method execution.
     *
     * @param execObject
     * @param eventName
     * @return
     */
    WorkFlow run(Consumer<WorkEvent> execObject, String eventName);


    /**
     * Execute the corresponding method at completion of the previous step method execution.
     *
     * @param execObject
     * @return
     */
    WorkFlow next(Runnable execObject);


    /**
     * Execute the corresponding method at completion of the previous step method execution.
     *
     * @param execObject
     * @return
     */
    WorkFlow next(Consumer<WorkEvent> execObject);

    /**
     * Execute the corresponding method at completion of the previous step method execution.
     * Raises an event after completion of method execution.
     *
     * @param execObject
     * @param eventName
     * @return
     */
    WorkFlow next(Runnable execObject, String eventName);

    /**
     * Execute the corresponding method at completion of the previous step method execution.
     * Raises an event after completion of method execution.
     *
     * @param execObject
     * @param eventName
     * @return
     */
    WorkFlow next(Consumer<WorkEvent> execObject, String eventName);


    /**
     * And generates an event. Delayed events can also be generated through the delay time setting.
     *
     * @param eventName
     * @param time
     * @return
     */
    WorkFlow fireEvent(String eventName, long time);

    /**
     *
     * finish workflow.
     *
     * @return
     */
    WorkFlow finish();

    /**
     * Check whether the execution completion status is set.
     * @return
     */
    boolean isSetFinish();

    /**
     * Returns a fireEvent FunctionExecutor object.
     *
     * @return
     */
    FunctionExecutor getNextExecutor();


    /**
     * Check whether there is an executor for the fireEvent processing.
     *
     * @return
     */
    boolean existNexExcutor();


    /**
     *
     * Returns an executor for handling this event.
     *
     * @param eventName
     * @return
     */
    Queue<FunctionExecutor> getExecutorQueue(String eventName);


    /**
     * Returns UUID information related to the unique event of the last state so far.
     *
     * @return
     */
    String cursor();





}
