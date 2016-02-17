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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.MavenExecutionException;

/**
 * Thrown when the smart reactor fails one of its release steps.
 *
 * @author Ronald Jack Jenkins Jr.
 */
public class SmartReactorReleaseException extends MavenExecutionException {

  private static final long serialVersionUID = -4704891299848581663L;

  /**
   * Super constructor with integrated {@link IllegalStateException}.
   *
   * @param message
   *          description of release failure, attached to the inner ISE cause.
   *          Null or empty is coerced to a default message.
   */
  public SmartReactorReleaseException(final String message) {
    super("Smart Reactor release failure:", new IllegalStateException(
        StringUtils.isEmpty(message) ? "no further information." : message));
  }

  /**
   * Super constructor with root cause.
   *
   * @param cause
   *          not null.
   */
  public SmartReactorReleaseException(final Throwable cause) {
    super("Smart Reactor release failure:", cause);
  }

}
