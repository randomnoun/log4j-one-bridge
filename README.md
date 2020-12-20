
# log4j-one-bridge

**log4j-one-bridge** allows code written for log4j 2 to work using **log4j 1**.

It's another little rectangle on that diagram you have of every Java logging framework that exists, and the shims and wrappers required to get them all working together.

## Overview

The main use case is that you've got an existing webapp with a few dozen dependencies that all work perfectly well, and then you add a new dependency that uses log4j 2 and you don't want to have to reconfigure everything to use that.

log4j-one-bridge is the spiritual equivalent of the [compatibility layer](https://logging.apache.org/log4j/2.x/manual/compatibility.html) that log4j2 supplies to get code written for log4j 1 logging in log4j 2, except 
* it allows code written for log4j 2 to log in log4j 1 instead,
* it actually works, and 
* it doesn't attempt to reimplement the entire logging framework in the process.

It does this by allowing the user to include both the "real" log4j 1 and log4j 2 libraries in their app, and configures log4j 2 to write to log4j 1 loggers.

You might prefer this if 
* you've spent a few hours getting your logging configuration just right, and you can't be arsed going through that again, or 
* you've written some tools around your logging framework that use the log4j 1 configuration API, so you want everything funnelled into that
* 

## Background and opinionated rant

I already wrote this on the log4j-one README, so go and check that out. 


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
