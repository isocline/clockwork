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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.*;





@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

/**
 *
 * @deprecated
 */
public @interface WorkInfo {

    int step() default 1;

    boolean async() default false;

    String id() default "1";

    boolean start() default false;

    boolean end() default false;

    String next() default "x";

    String startAfter() default "x";



}
