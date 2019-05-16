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

import isocline.clockwork.event.EventRepository;
import isocline.clockwork.event.EventSet;
import isocline.clockwork.object.FunctionExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 *
 *
 */
public class ProcessFlow extends ArrayList<FunctionExecutor> {



    private int sequence = 0;

    private FunctionExecutor functionExecutor;

    private List<FunctionExecutor> waiters = new ArrayList<FunctionExecutor>();



    private EventRepository<String,FunctionExecutor> eventRepository = new EventRepository();


    public void reset() {
        sequence = 0;
    }

    /**
     *
     * @return
     */
    public ProcessFlow end() {
        if(functionExecutor !=null) {
            functionExecutor.setLastExecutor(true);
        }

        return this;
    }


    /**
     *
     * @param runnable
     * @return
     */
    public ProcessFlow run(Runnable runnable) {
        functionExecutor = new FunctionExecutor(runnable, false);

        add(functionExecutor);

        return this;
    }


    /**
     *
     * @param consumer
     * @return
     */
    public ProcessFlow run(Consumer consumer) {
        functionExecutor = new FunctionExecutor(consumer, false);
        add(functionExecutor);

        return this;
    }


    /**
     *
     * @param runnable
     * @return
     */
    public ProcessFlow runAsync(Runnable runnable) {

        add(new FunctionExecutor(runnable, true));
        return this;
    }


    /**
     *
     * @param runnable
     * @param eventName
     * @return
     */
    public ProcessFlow runAsync(Runnable runnable, String eventName) {
        functionExecutor = new FunctionExecutor(runnable, true);
        functionExecutor.setFireEventName(eventName);

        add(functionExecutor);

        return this;
    }


    /**
     *
     * @param runnable
     * @param eventNames
     * @return
     */
    public ProcessFlow runWait(Runnable runnable, String... eventNames) {

        for(String eventName:eventNames) {

            String[] subEventNames = eventRepository.setBindEventNames(eventName);

            functionExecutor = new FunctionExecutor(runnable, false);
            functionExecutor.setRecvEventName(eventName);

            waiters.add(functionExecutor);

            eventRepository.put(eventName, functionExecutor);
            for (String subEventName : subEventNames) {
                eventRepository.put(subEventName, functionExecutor);
            }
        }

        return this;
    }


    /**
     *
     * @param eventName
     * @return
     */
    public FunctionExecutor getExecutor(String eventName) {
        if(eventName==null) return null;


        /*
        FunctionExecutor x =  this.eventRepository.get(eventName);
        System.err.println(eventName + " --> "+x);

        return x;
        */


        EventSet eventSet = eventRepository.getEventSet(eventName);



        if(eventSet==null || eventSet.isRaiseEventReady(eventName)) {
            return this.eventRepository.get(eventName);
        }

        return  null;


    }


    /**
     *
     * @return
     */
    public FunctionExecutor getNextExecutor() {

        //IndexOutOfBoundsException

        FunctionExecutor exec = null;
        try {
            exec = this.get(this.sequence);
            //System.err.println("getNextExecutor:"+sequence );
        } catch (IndexOutOfBoundsException ie) {
            return null;
        }
        if (exec != null) {
            sequence++;
        }

        return exec;
    }

    /**
     *
     *
     * @return
     */
    public List<FunctionExecutor> getWaiters() {
        return this.waiters;
    }


}
