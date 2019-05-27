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
package isocline.clockwork.flow;

import isocline.clockwork.WorkEvent;

import java.util.UUID;
import java.util.function.Consumer;


/**
 *
 *
 */
public class FunctionExecutor {


    private boolean isLastExecutor = false;

    private String fireEventName;

    private String recvEventName;

    private String fireEventUUID;

    private long delayTimeFireEvent = 0;



    private Runnable runnable = null;

    private Consumer consumer = null;





    FunctionExecutor() {
        this.fireEventUUID = UUID.randomUUID().toString();
    }


    FunctionExecutor(Object obj) {

        if (obj != null) {
            if (obj instanceof Runnable) {
                this.runnable = (Runnable) obj;
            } else if (obj instanceof Consumer) {
                this.runnable = null;
                this.consumer = (Consumer) obj;
            } else {
                throw new IllegalArgumentException("Not Support type");
            }
        }


        this.fireEventUUID = UUID.randomUUID().toString();
    }


    public String getFireEventUUID() {
        return this.fireEventUUID;
    }

    public void setLastExecutor(boolean isEnd) {
        this.isLastExecutor = isEnd;
    }

    public boolean isLastExecutor() {
        return isLastExecutor;
    }


    public void setFireEventName(String eventName) {
        this.fireEventName = eventName;
    }

    public void setRecvEventName(String eventName) {
        this.recvEventName = eventName;
    }

    public String getFireEventName() {
        return this.fireEventName;
    }

    public String getRecvEventName() {
        return this.recvEventName;
    }

    public long getDelayTimeFireEvent() {
        return delayTimeFireEvent;
    }

    public void setDelayTimeFireEvent(long delayTimeFireEvent) {
        this.delayTimeFireEvent = delayTimeFireEvent;
    }

    public void execute(WorkEvent event) {

        if (runnable != null) {

            runnable.run();
            return;
        }

        if (consumer != null) {

            consumer.accept(event);
        }
    }
}
