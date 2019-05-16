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

import java.util.function.Consumer;

/**
 * Interface for work flow
 *
 */

public interface WorkFlow {


    WorkFlow wait(String... eventNames);

    WorkFlow waitAll(String... eventNames);


    WorkFlow run(Runnable execObject);

    WorkFlow run(Consumer<WorkEvent> execObject);

    WorkFlow run(Runnable execObject, String eventName);

    WorkFlow run(Consumer<WorkEvent> execObject, String eventName);


    WorkFlow next(Runnable execObject);

    WorkFlow next(Consumer<WorkEvent> execObject);

    WorkFlow next(Runnable execObject, String eventName);

    WorkFlow next(Consumer<WorkEvent> execObject, String eventName);


    WorkFlow finish();

    boolean isSetFinish();

    FunctionExecutor getNextExecutor();

    boolean existNexExcutor();


    FunctionExecutor getExecutor(String eventName);


}
