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

/**
 *
 *
 */
public interface FlowableWork extends Work {


    /**
     * @return
     * @throws InterruptedException
     */
    default long execute(WorkEvent event) throws InterruptedException {


        WorkSchedule schedule = event.getWorkSchedule();

        WorkFlow flow = schedule.getWorkFlow();

        if (flow == null) {
            return TERMINATE;
        }


        String eventName = event.getEventName();

        FunctionExecutor executor = null;

        boolean existNextExecutor = false;

        if (eventName != null) {
            executor = flow.getExecutor(eventName);
            if (executor != null) {
                //System.out.println("___________   EVENT  "+eventName);
            }

        } else {
            executor = flow.getNextExecutor();
            if (executor != null) {
                existNextExecutor = flow.existNexExcutor();
                //System.out.println("~~~~~~~~~~~~~  NEXT  EXEC");
            }


        }


        if (executor != null) {
            executor.execute(event);

            String eventNm = executor.getFireEventName();
            if (eventNm != null) {
                schedule.raiseLocalEvent(event.create(eventNm));
                //System.out.println("                                            * FIRE "+eventNm );
            }


            schedule.raiseLocalEvent(event.create(executor.getEventUUID()));
            //System.out.println("                                            ^ FIRE "+executor.getEventUUID() );


            if (executor.isLastExecutor()) {
                //System.out.println("=============== TEMINATE ============");
                return TERMINATE;
            }

            if (existNextExecutor) {
                return 1;
            }
        }


        return WAIT;
    }


}
