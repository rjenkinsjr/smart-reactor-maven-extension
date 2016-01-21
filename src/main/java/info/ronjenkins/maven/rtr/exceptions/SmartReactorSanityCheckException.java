/*
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.ronjenkins.maven.rtr.exceptions;

/**
 * Thrown when the smart reactor fails one of its sanity checks.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public class SmartReactorSanityCheckException extends RuntimeException {

    private static final long serialVersionUID = -3239867428541322183L;

    /**
     * Super constructor.
     * 
     * @param message
     *            description of sanity check failure.
     */
    public SmartReactorSanityCheckException(final String message) {
        super(message);
    }

}
