#!/usr/bin/env bash

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls -ld "$PRG"
    LINK=`ls -l "$PRG" | awk '{print $NF}'`
    case $LINK in
        /*) PRG="$LINK" ;;
        *) PRG=`dirname "$PRG"`"/$LINK" ;;
    esac
done
SAVEPWD=`pwd`
cd "`dirname "$PRG"`/" >/dev/null
APP_HOME=`pwd -P`
cd "$SAVEPWD" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here.
DEFAULT_JVM_OPTS='"$JAVA_OPTS" "-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MSYS* | MINGW* )
    msys=true
    ;;
  NATIVEWIN64 )
    nativewin64=true
    ;;
esac

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

if [ -z "$MAX_FD" ] ; then
    MAX_FD="maximum"
fi

if [ "$cygwin" = "true" -o "$darwin" = "true" -o "$nativewin64" = "true" ] ; then
    if [ "$darwin" = "true" ] ; then
        MAX_FD=unlimited
    else
        MAX_FD=`ulimit -H -n`
    fi
    # We have to use || instead of && because cmd.exe doesn't exit if an exit code is 0
    if [ "$?" = "0" ] ; then
        # Could not query maximum file descriptor limit
        MAX_FD=unbounded
    fi
fi

if [ "$cygwin" = "true" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
    JAVACMD=`cygpath --windows "$JAVACMD"`
    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 3 -type d -name sources 2>/dev/null`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        ROOTDIRS="$ROOTDIRS$SEP$dir"
        SEP="|"
    done
    OURCYGPATTERN="(^($ROOTDIRS))"
    APP_HOME_PATTERN="(^$APP_HOME)"
    CLASSPATH_PATTERN="(^$CLASSPATH)"
    JAVACMD_PATTERN="(^$JAVACMD)"
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    i=0
    for arg in "$@" ; do
        CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
        CHECK2=`echo "$arg"|egrep -c "^-"` ### Determine if an option
        if [ $CHECK -ne 0 ] && [ $CHECK2 -eq 0 ] ; then ### Added a condition
            eval `echo args$i`=`cygpath --path --unix "$arg"`
        else
            eval `echo args$i`="\"$arg\""
        fi
        i=$((i+1))
    done
    case $i in
        0) set -- ;;
        1) set -- "$args0" ;;
        2) set -- "$args0" "$args1" ;;
        3) set -- "$args0" "$args1" "$args2" ;;
        4) set -- "$args0" "$args1" "$args2" "$args3" ;;
        5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
        6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
        7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
        8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
        9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
    esac
fi

if [ "$cygwin" = "true" ] ; then
   [ -n "$JAVA_HOME" ] && {
        JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
    }
    [ -n "$CLASSPATH" ] && {
        CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
    }
fi

if [ "$msys" = "true" ] ; then
    [ -n "$JAVA_HOME" ] && {
        JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
    }
fi

if [ "$nativewin64" = "true" ] ; then
    [ -n "$JAVA_HOME" ] && {
        JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
    }
fi

# Escape application args
save () {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\'\\\\''/g;1s/^/'/;\$s/\$/'" ; done
    echo " "
}
APP_ARGS=`save "$@"`

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval "set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \"\-classpath\" \"$CLASSPATH\" org.gradle.wrapper.GradleWrapperMain  "$APP_ARGS""

exec "$JAVACMD" "$@"
