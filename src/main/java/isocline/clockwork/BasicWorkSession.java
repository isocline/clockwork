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
 *
 * Basic implementation of WorkSession
 *
 */
public class BasicWorkSession implements WorkSession {


    private Map<String, Object> map = new HashMap<>();


    @Override
    public void setAttribute(String key, Object object) {

        this.map.put(key, object);
    }

    @Override
    public Object getAttribute(String key) {
        return this.map.get(key);
    }


    @Override
    public Object removeAttribute(String key) {
        return this.map.remove(key);
    }

    @Override
    public void onError(Work work, Throwable error) {

    }

    @Override
    public void beforeExecute(Work work) {

    }

    @Override
    public void afterExecute(Work work) {

    }
}
