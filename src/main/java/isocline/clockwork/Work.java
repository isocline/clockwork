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


import isocline.clockwork.object.FunctionExecutor;

/**
 *
 *
 */
public interface Work {


    /**
     *
     */
    long WAIT = -1;


    /**
     * terminate job
     */
    long TERMINATE = -99;


    /**
     *
     */
    long LOOP = 0;


    /**
     * @param flow
     */
    default void processFlow(ProcessFlow flow) {


        throw new UnsupportedOperationException();
    }


    /**
     * @return
     * @throws InterruptedException
     */
    default long execute(EventInfo event) throws InterruptedException {

        WorkSchedule schedule = event.getWorkSchedule();

        ProcessFlow flow = schedule.getProcessFlow();
        if (flow == null) {
            return TERMINATE;
        }


        String eventName = event.getEventName();

        FunctionExecutor exec = null;

        if (eventName != null) {
            exec = flow.getExecutor(eventName);

            if (exec != null) {

                System.out.println("1-exec");
                exec.execute();

                if (exec.isLastExecutor()) {
                    System.out.println("2-TERMINATE");
                    return TERMINATE;
                } else {
                    System.out.println("3-WAIT");
                    return WAIT;
                }


            }
        }


        exec = (FunctionExecutor) event.getAttribute("exec.func");

        if (exec != null) {
            event.removeAttribute("exec.func");
            System.out.println("2-exec");
            exec.execute();

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {


                schedule.raiseLocalEvent(event.setEventName(eventNm));
            }
            if (exec.isLastExecutor()) {
                System.out.println("4-TERMINATE");
                return TERMINATE;
            } else {
                System.out.println("5-WAIT");
                return WAIT;
            }
        }


        exec = flow.getNextExecutor();

        if (exec != null) {

            if (exec.isAsync()) {
                //logger.debug("ASYNC");
                EventInfo newEvent = new EventInfo();
                newEvent.setAttribute("exec.func", exec);
                //worker.createSchedule(this).bindEvent("async").activate();

                //schedule.getWorkProcessor().raiseEvent("async", newEvent);

                schedule.raiseLocalEvent(newEvent);

                //logger.debug("ASYNC - END");
                return 1;
            }


            System.out.println("3-exec");
            exec.execute();

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {
                schedule.raiseLocalEvent(event.setEventName(eventNm));
            }

            if (exec.isLastExecutor()) {
                System.out.println("6-TERMINATE");
                return TERMINATE;
            }

        } else {
            return WAIT;
        }

        return 1;
    }


}
