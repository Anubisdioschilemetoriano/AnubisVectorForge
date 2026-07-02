#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

APP_BASE_NAME=`basename "$0"`
APP_HOME=`pwd -P`

APP_HOME=`dirname "$APP_HOME"`

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec java $DEFAULT_JVM_OPTS -classpath "\( CLASSPATH" org.gradle.wrapper.GradleWrapperMain " \)@"
