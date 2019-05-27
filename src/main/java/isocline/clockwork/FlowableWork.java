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
import isocline.clockwork.flow.FunctionExecutorList;

/**
 * It is an interface that enables flow control.
 If you want to control flow with several methods in an object that implements this interface, you can inherit the interface.

 * @see isocline.clockwork.Work
 */
public interface FlowableWork extends Work {


    /**
     * It is a method that must be implemented in order to do flow control.
     *
     * <p>
     * <strong>Example:</strong>
     * <blockquote>
     * <pre>

     *  public void defineWorkFlow(WorkFlow flow) {
     *    // step1 : execute this.checkMemory() then execute this.checkStorage()
     *    WorkFlow p1 = flow.next(this::checkMemory).next(this::checkStorage);
     *
     *    // Until wait finish of step1, then this.sendSignal()
     *    WorkFlow t1 = flow.wait(p1).next(this::sendSignal);
     *
     *    // Until wait finish of step1, then this.sendStatusMsg() and this.sendReportMsg()
     *    WorkFlow t2 = flow.wait(p1).next(this::sendStatusMsg).next(this::sendReportMsg);
     *
     *    // Wait until both step1 and step2 are finished, then execute this.report()
     *    flow.waitAll(t1, t2).next(this::report).finish();
     *  }
     * </pre>
     * </blockquote>
     *
     * @param flow WorkFlow instance
     */
    default void defineWorkFlow(WorkFlow flow) {

        throw new UnsupportedOperationException();
    }


    /**
     * It is not necessary to implement additional methods as extended methods implemented from the Work interface.
     * DO NOT implement this method.
     *
     * @return delay time
     * @throws InterruptedException If interrupt occur
     */
    default long execute(WorkEvent event) throws InterruptedException {


        final WorkSchedule schedule = event.getWorkSchedule();

        final WorkFlow flow = schedule.getWorkFlow();

        if (flow == null) {
            return TERMINATE;
        }

        final String eventName = event.getEventName();

        //System.out.println(">>>>>>>> RECV : ["+eventName+"]");

        FunctionExecutor executor = null;

        boolean existNextExecutor = false;

        if (eventName != null) {
            FunctionExecutorList functionExecutorList = flow.getFunctionExecutorList(eventName);
            if (functionExecutorList != null) {

                FunctionExecutorList.Wrapper wrapper = functionExecutorList.getNextstepFunctionExecutor();

                if (wrapper != null) {

                    executor = wrapper.getFunctionExecutor();

                    if (wrapper.hasNext()) {
                        schedule.raiseLocalEvent(event.createChild(eventName));
                    }

                }
            }
        }

        if (executor == null) {
            executor = flow.getNextExecutor();
            if (executor != null) {
                existNextExecutor = flow.existNexExcutor();
            }
        }


        if (executor != null) {

            boolean isFireEvent = false;

            try {
                if(event.getThrowable()!=null) {
                    isFireEvent = true;
                }
                executor.execute(event);

                isFireEvent = true;

            } catch (Throwable e) {

                final String eventNm = executor.getFireEventName();

                if (eventNm != null) {
                    String errEventName = eventName + "::error";

                    WorkEvent errEvent = WorkEventFactory.create(errEventName);
                    errEvent.setThrowable(e);

                    schedule.raiseLocalEvent(errEvent);

                }
                String errEventName = executor.getEventUUID() + "::error";

                final WorkEvent errEvent = WorkEventFactory.create(errEventName);
                errEvent.setThrowable(e);


                schedule.raiseLocalEvent(errEvent);


                final WorkEvent errEvent2 = WorkEventFactory.create("*::error");
                errEvent2.setThrowable(e);

                schedule.raiseLocalEvent(errEvent2);
            } finally {
                if(isFireEvent) {
                    final String eventNm = executor.getFireEventName();
                    if (eventNm != null) {
                        long delayTime = executor.getDelayTimeFireEvent();
                        schedule.raiseLocalEvent(event.createChild(eventNm), delayTime);
                    }
                    schedule.raiseLocalEvent(event.createChild(executor.getEventUUID()));
                }
            }


            if (executor.isLastExecutor()) {
                return TERMINATE;
            }

            if (existNextExecutor) {
                return LOOP;
            }
        }

        return WAIT;
    }


}
