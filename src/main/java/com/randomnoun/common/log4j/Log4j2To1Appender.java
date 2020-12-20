package com.randomnoun.common.log4j;

import java.io.Serializable;
import java.util.concurrent.locks.*;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

/** A log4j2 appender that writes to log4j1 appenders 
 * 
 * <p>You know how there are four logging frameworks in the world,
 * with each library that you use arbitrarily using one of those so you need to
 * funnel all of those into your "real" logging framework ? Well, log4j 
 * weren't happy with being the defacto standard (seeing it was around before all of them),
 * and went and created a sequel, log4j 2, that provides exactly the same functionality
 * but with a different API for some reason. 
 * 
 * <p>The purpose of this class is to allow to retain your log4j 1 appenders and configuration that
 * you've painfully managed to build up over the years, and causes libraries that
 * feel they need to drag log4j2 into this mess to act like log4j 1 loggers.
 * 
 * <p>To do this, you'll need to drop this log4j2.xml into your classpath, which 
 * is one of the possibly infinite places where you can configure log4j2, and the first
 * time something tries to log something in log4j2, it'll get diverted into log4j 1,
 * the One True and Holy Apostolic Logging Framework, Except For That CVE It Got
 * In 2019 Because Of That SocketAppender Nobody Uses But Which They'll Never Fix.  
 *  
 * <pre>
&lt;?xml version="1.0" encoding="UTF-8"?>
&lt;Configuration packages="com.randomnoun.common.log4j" status="WARN">
    &lt;Appenders>
        &lt;Log4j2To1Appender name="log4j1">&lt;!--  com.randomnoun.common.log4j.Log4j2To1Appender.Log4j2To1Appender  -->
        &lt;/Log4j2To1Appender>
    &lt;/Appenders>
    &lt;Loggers>
	    &lt;Root level="trace">
	        &lt;AppenderRef ref="log4j1"/>
	    &lt;/Root>
	&lt;/Loggers>
&lt;/Configuration>
 * </pre>
 * 
 */

// because I am not going to learn yet another 'powerful' configuration format that does
// almost everything the old one does

// seems to be plenty of log4j1 -> 2 'bridges', but I want struts2 to log using log4j1 appenders

// based on the code at http://stackoverflow.com/questions/24205093/how-to-create-a-custom-appender-in-log4j2

// log4j2 uses annotations to configure itself, which helpfully slows the startup time by a few seconds
// whilst every class in every JAR is scanned for annotations, whilst decentralising your logging
// config into hundreds of little nooks and crannies. 

// which is obviously what people want, and definitely an improvement on log4j 1.

// I would very much prefer doing this in code rather than throwing annotations around the place

@Plugin(name="Log4j2To1Appender", category="Core", elementType="appender", printObject=true)
public final class Log4j2To1Appender extends AbstractAppender {

	/** Generated serialVersionUID */
	private static final long serialVersionUID = -6911739214799667658L;
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();

    protected Log4j2To1Appender(String name, Filter filter,
            Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        readLock.lock();
        try {
            final byte[] bytes = getLayout().toByteArray(event);
            org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(event.getLoggerName());
            logger.log(org.apache.log4j.Level.toLevel(event.getLevel().intLevel()), bytes);
            
            // System.out.write(bytes);
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }

    // Your custom appender needs to declare a factory method
    // annotated with `@PluginFactory`. Log4j will parse the configuration
    // and call this factory method to construct an appender instance with
    // the configured attributes.
    @PluginFactory
    public static Log4j2To1Appender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) 
    {
        if (name == null) {
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new Log4j2To1Appender(name, filter, layout, true);
    }
}
