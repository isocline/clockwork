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
import isocline.clockwork.flow.FunctionExecutor;

import java.util.Queue;

/**
 * It is an interface that enables flow control.
 If you want to control flow with several methods in an object that implements this interface, you can inherit the interface.

 * @see isocline.clockwork.Work
 */
public interface FlowableWork extends Work {


    /**
     * It is a method that must be implemented in order to do flow control.
     *
     * @param flow
     */
    default void defineWorkFlow(WorkFlow flow) {

        throw new UnsupportedOperationException();
    }


    /**
     * It is not necessary to implement additional methods as extended methods implemented from the Work interface.
     *
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

        //System.out.println("RECV ___________ >  EVENT  "+eventName);


        FunctionExecutor executor = null;

        boolean existNextExecutor = false;

        if (eventName != null) {
            Queue<FunctionExecutor> q = flow.getExecutorQueue(eventName);
            if (q != null) {

                executor = q.poll();
                if (executor != null) {

                    FunctionExecutor nextFuncExector = q.peek();
                    if (nextFuncExector != null) {
                        schedule.raiseLocalEvent(event.createChild(eventName));
                    }


                    ////System.out.println("___________   EVENT  "+eventName);
                }
            }

        }

        if (executor == null) {
            executor = flow.getNextExecutor();
            if (executor != null) {
                existNextExecutor = flow.existNexExcutor();
                ////System.out.println("~~~~~~~~~~~~~  NEXT  EXEC");
            }


        }


        if (executor != null) {
            //System.out.println("EXEC ___________ >  EVENT  "+eventName);

            boolean isFireEvent = false;

            try {
                if(event.getThrowable()!=null) {
                    isFireEvent = true;
                }
                executor.execute(event);

                isFireEvent = true;

            } catch (Throwable e) {
                String eventNm = executor.getFireEventName();
                if (eventNm != null) {
                    String errEventName = eventName + "::error";

                    WorkEvent errEvent = WorkEventFactory.create(errEventName);
                    errEvent.setThrowable(e);

                    schedule.raiseLocalEvent(errEvent);

                }
                String errEventName = executor.getEventUUID() + "::error";

                WorkEvent errEvent = WorkEventFactory.create(errEventName);
                errEvent.setThrowable(e);


                schedule.raiseLocalEvent(errEvent);


                errEventName = "*::error";

                errEvent = WorkEventFactory.create(errEventName);
                errEvent.setThrowable(e);

                schedule.raiseLocalEvent(errEvent);
            } finally {
                if(isFireEvent) {
                    String eventNm = executor.getFireEventName();
                    if (eventNm != null) {
                        long delayTime = executor.getDelayTimeFireEvent();
                        //System.out.println( "[FlowableWork]raise event C____ --->>>>  "+eventNm +" --------- "+delayTime);
                        schedule.raiseLocalEvent(event.createChild(eventNm), delayTime);
                        //System.out.println("                                            * FIRE "+eventNm );
                    }


                    schedule.raiseLocalEvent(event.createChild(executor.getEventUUID()));
                    //System.out.println("[FlowableWork]raise event  UUIDv--->>>>  "+executor.getEventUUID() );
                }
            }


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
