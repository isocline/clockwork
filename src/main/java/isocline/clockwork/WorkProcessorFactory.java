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

import javax.servlet.ServletContextEvent;
import java.util.HashMap;
import java.util.Map;


/**
 *
 *
 */
public class WorkProcessorFactory implements
        javax.servlet.ServletContextListener {

    private static WorkProcessor worker;

    private static Map<String, WorkProcessor> processorMap = new HashMap<String, WorkProcessor>();


    /**
     *
     * @return
     */
    public static WorkProcessor getDefaultProcessor() {


        if (worker == null || ! worker.isWorking()) {
            worker = new WorkProcessor("default", Configuration.NOMAL);
        }

        return worker;
    }


    /**
     *
     * @param id
     * @param config
     * @return
     */
    public static synchronized WorkProcessor getProcessor(String id, Configuration config) {
        WorkProcessor processor = processorMap.get(id);
        if (processor == null || ! processor.isWorking()) {
            processor = new WorkProcessor(id, config);
            processorMap.put(id, processor);
        }

        return processor;

    }

    /**
     * @param sce
     */
    public void contextInitialized(ServletContextEvent sce) {
        getDefaultProcessor();

    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
        worker.shutdown();

    }

}
