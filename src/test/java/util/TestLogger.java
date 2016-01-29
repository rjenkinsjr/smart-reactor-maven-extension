package util;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.Logger;

import edu.emory.mathcs.backport.java.util.Collections;

public final class TestLogger implements Logger {

    private final List<Entry> debugLog = new ArrayList<Entry>();
    private final List<Entry> infoLog = new ArrayList<Entry>();
    private final List<Entry> warnLog = new ArrayList<Entry>();
    private final List<Entry> errorLog = new ArrayList<Entry>();
    private final List<Entry> fatalLog = new ArrayList<Entry>();

    @Override
    public void debug(String arg0) {
	this.debugLog.add(new Entry(arg0, null));
    }

    @Override
    public void debug(String arg0, Throwable arg1) {
	this.debugLog.add(new Entry(arg0, arg1));
    }

    @Override
    public void error(String arg0) {
	this.errorLog.add(new Entry(arg0, null));
    }

    @Override
    public void error(String arg0, Throwable arg1) {
	this.errorLog.add(new Entry(arg0, arg1));
    }

    @Override
    public void fatalError(String arg0) {
	this.fatalLog.add(new Entry(arg0, null));
    }

    @Override
    public void fatalError(String arg0, Throwable arg1) {
	this.fatalLog.add(new Entry(arg0, arg1));
    }

    @Override
    public Logger getChildLogger(String arg0) {
	return null;
    }

    @Override
    public String getName() {
	return "TestLogger";
    }

    @Override
    public int getThreshold() {
	return Logger.LEVEL_DEBUG;
    }

    @Override
    public void info(String arg0) {
	this.infoLog.add(new Entry(arg0, null));
    }

    @Override
    public void info(String arg0, Throwable arg1) {
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
    public void setThreshold(int arg0) {
	// do nothing
    }

    @Override
    public void warn(String arg0) {
	this.warnLog.add(new Entry(arg0, null));
    }

    @Override
    public void warn(String arg0, Throwable arg1) {
	this.warnLog.add(new Entry(arg0, arg1));
    }

    @SuppressWarnings("unchecked")
    public List<Entry> getDebugLog() {
	return Collections.unmodifiableList(this.debugLog);
    }

    @SuppressWarnings("unchecked")
    public List<Entry> getInfoLog() {
	return Collections.unmodifiableList(this.warnLog);
    }

    @SuppressWarnings("unchecked")
    public List<Entry> getWarnLog() {
	return Collections.unmodifiableList(this.infoLog);
    }

    @SuppressWarnings("unchecked")
    public List<Entry> getErrorLog() {
	return Collections.unmodifiableList(this.errorLog);
    }

    @SuppressWarnings("unchecked")
    public List<Entry> getFatalLog() {
	return Collections.unmodifiableList(this.fatalLog);
    }

    public static final class Entry {

	private final String message;
	private final Throwable throwable;

	private Entry(final String message, final Throwable throwable) {
	    this.message = message;
	    this.throwable = throwable;
	}

	public String getMessage() {
	    return message;
	}

	public Throwable getThrowable() {
	    return throwable;
	}

    }

}
