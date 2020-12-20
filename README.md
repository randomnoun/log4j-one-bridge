

# log4j-one-bridge

**log4j-one-bridge** allows code written for log4j 2 to work using **log4j 1**.

( where log4j 1 is your primary logging framework, i.e. where all your logging configuration is, and where all the appenders live ).  

It's another little rectangle on that diagram you have of every Java logging framework that exists, and the shims and wrappers required to get them all working together.

## Overview

The main use case is that you've got an existing webapp with a few dozen dependencies that all work perfectly well, and then you add a new dependency that uses log4j 2 and you don't want to have to reconfigure everything to use that.

log4j-one-bridge is the spiritual equivalent of the [compatibility layer](https://logging.apache.org/log4j/2.x/manual/compatibility.html) that log4j2 supplies to get code written for log4j 1 logging in log4j 2, except 
* it allows code written for log4j 2 to log in log4j 1 instead,
* it actually works [*], and 
* it doesn't attempt to reimplement the entire logging framework in the process.

It does this by allowing the user to include both the "real" log4j 1 and log4j 2 libraries in their app, and configures log4j 2 to write to log4j 1 loggers.

You might prefer this if 
* you've spent a few hours getting your logging configuration just right, and you can't be arsed going through that again, or 
* you've written some tools around your logging framework that use the log4j 1 configuration API, so you want everything funnelled into that


## Background and opinionated rant

I already wrote this on the [log4j-one](https://github.com/randomnoun/log4j-one) README, so go and check that out. 


## What's in the box ?

Surprisingly little.

* **Log4j2To1Appender** - a class which receives log4j 2 events and then emits those same events in log4j 1
* **log4j2.xml** - a configuration file which directs log4j2 to use that appender for everything. 


## How do I use it ? 

Add this JAR to your webapp and log4j2 should pick up that xml config file the first time any code tries to log anything, and then you can go back to ignoring log4j 2.


## Where can I get it ? 

Add this to your pom.xml, or pom.xml equivalent:
```
<dependency>
  <groupId>com.randomnoun.common</groupId>
  <artifactId>log4j-one-bridge</artifactId>
  <version>0.0.1</version>
</dependency>
```
Or from here on maven central, until such time that I create a release on github:  https://repo1.maven.org/maven2/com/randomnoun/common/log4j-one-bridge/0.0.1/

## Licensing

log4j-one-bridge is licensed under the BSD 2-clause license.

## Footnotes

[*] - you may have noticed that the [org.apache.log4j.spi.LoggingEvent](https://git-wip-us.apache.org/repos/asf?p=logging-log4j2.git;a=blob;f=log4j-1.2-api/src/main/java/org/apache/log4j/spi/LoggingEvent.java;h=8d26d26e93b3ff7809dce4b55e2d8a76e5c2aa20;hb=HEAD) provided in log4j 2's log4j-1.2-api-2.14.0.jar, has a ***final*** getTimeStamp() method that returns zero.

Let that sink in for a bit. 

Which means we're up to version 14 of this fancy new wrapper in this fancy new logging framework and all your log4j 1 log events are still being timestamped as 00:00:00 UTC on 1 January 1970. 

I'm surprised amidst the maze of configuration strategy adapter builder classes they didn't set the message to null as well, and maybe pipe it all into /dev/null as a sort of encore.