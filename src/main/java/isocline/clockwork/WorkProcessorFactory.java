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

import java.util.HashMap;
import java.util.Map;


/**
 * Creates and returns a default WorkProcessor implementation object.
 * WorkProcessor is the core engine of work execution, and WorkProcessorFactory determines,
 * manages and creates the characteristics of WorkProcessor.
 *
 * @author Richard D. Kim
 */
public class WorkProcessorFactory {

    private static WorkProcessor workProcessor;

    private static Map<String, WorkProcessor> processorMap = new HashMap<String, WorkProcessor>();


    /**
     * Returns the underlying WorkProcessor implementation object.
     *
     * @return WorkProcessor
     */
    public static WorkProcessor getProcessor() {


        if (workProcessor == null || !workProcessor.isWorking()) {
            workProcessor = new WorkProcessor("default", getDefaultConfiguration());
        }

        return workProcessor;
    }

    /**
     *
     * @deprecated
     * @return an instance of WorkProcessor
     */
    public static WorkProcessor getDefaultProcessor() {
        return getProcessor();
    }

    private static Configuration getDefaultConfiguration() {
        String processorType = System.getProperty("isocline.clockwork.processor.type");

        if ("performance".equals(processorType)) {
            return Configuration.PERFORMANCE;
        } else if ("echo".equals(processorType)) {
            return Configuration.ECHO;
        } else if ("hyper".equals(processorType)) {
            return Configuration.HYPER;
        }

        return Configuration.NOMAL;
    }


    /**
     * Returns a customized WorkProcessor implementation object.
     *
     * @param id a unique ID for WorkProcessor
     * @param config an instance of Configuration
     * @return a new instance of WorkProcessor
     */
    public static synchronized WorkProcessor getProcessor(String id, Configuration config) {
        WorkProcessor processor = processorMap.get(id);
        if (processor == null || !processor.isWorking()) {
            processor = new WorkProcessor(id, config);
            processorMap.put(id, processor);
        }

        return processor;

    }


}
