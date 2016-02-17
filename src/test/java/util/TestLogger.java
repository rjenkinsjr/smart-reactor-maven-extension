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
package util;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.Logger;

import edu.emory.mathcs.backport.java.util.Collections;

public final class TestLogger implements Logger {

  public static final class Entry {

    private final String message;
    private final Throwable throwable;

    private Entry(final String message, final Throwable throwable) {
      this.message = message;
      this.throwable = throwable;
    }

    public String getMessage() {
      return this.message;
    }

    public Throwable getThrowable() {
      return this.throwable;
    }

  }

  private final List<Entry> debugLog = new ArrayList<Entry>();
  private final List<Entry> infoLog = new ArrayList<Entry>();
  private final List<Entry> warnLog = new ArrayList<Entry>();
  private final List<Entry> errorLog = new ArrayList<Entry>();

  private final List<Entry> fatalLog = new ArrayList<Entry>();

  @Override
  public void debug(final String arg0) {
    this.debugLog.add(new Entry(arg0, null));
  }

  @Override
  public void debug(final String arg0, final Throwable arg1) {
    this.debugLog.add(new Entry(arg0, arg1));
  }

  @Override
  public void error(final String arg0) {
    this.errorLog.add(new Entry(arg0, null));
  }

  @Override
  public void error(final String arg0, final Throwable arg1) {
    this.errorLog.add(new Entry(arg0, arg1));
  }

  @Override
  public void fatalError(final String arg0) {
    this.fatalLog.add(new Entry(arg0, null));
  }

  @Override
  public void fatalError(final String arg0, final Throwable arg1) {
    this.fatalLog.add(new Entry(arg0, arg1));
  }

  @Override
  public Logger getChildLogger(final String arg0) {
    return null;
  }

  @SuppressWarnings("unchecked")
  public List<Entry> getDebugLog() {
    return Collections.unmodifiableList(this.debugLog);
  }

  @SuppressWarnings("unchecked")
  public List<Entry> getErrorLog() {
    return Collections.unmodifiableList(this.errorLog);
  }

  @SuppressWarnings("unchecked")
  public List<Entry> getFatalLog() {
    return Collections.unmodifiableList(this.fatalLog);
  }

  @SuppressWarnings("unchecked")
  public List<Entry> getInfoLog() {
    return Collections.unmodifiableList(this.infoLog);
  }

  @Override
  public String getName() {
    return "TestLogger";
  }

  @Override
  public int getThreshold() {
    return Logger.LEVEL_DEBUG;
  }

  @SuppressWarnings("unchecked")
  public List<Entry> getWarnLog() {
    return Collections.unmodifiableList(this.warnLog);
  }

  @Override
  public void info(final String arg0) {
    this.infoLog.add(new Entry(arg0, null));
  }

  @Override
  public void info(final String arg0, final Throwable arg1) {
    this.infoLog.add(new Entry(arg0, arg1));
  }

  @Override
  public boolean isDebugEnabled() {
    return true;
  }

  @Override
  public boolean isErrorEnabled() {
    return true;
  }

  @Override
  public boolean isFatalErrorEnabled() {
    return true;
  }

  @Override
  public boolean isInfoEnabled() {
    return true;
  }

  @Override
  public boolean isWarnEnabled() {
    return true;
  }

  @Override
  public void setThreshold(final int arg0) {
    // do nothing
  }

  @Override
  public void warn(final String arg0) {
    this.warnLog.add(new Entry(arg0, null));
  }

  @Override
  public void warn(final String arg0, final Throwable arg1) {
    this.warnLog.add(new Entry(arg0, arg1));
  }

}
