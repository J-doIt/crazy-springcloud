echo off
title demoCloudApplication
set SERVER_PORT=7700
set JAR_NAME=demo-provider-1.0-SNAPSHOT.jar
set MAIN_CLASS=com.crazymaker.springcloud.demo.start.DemoCloudApplication
set JVM= -server -Xms128m -Xmx1g

set APPLICATION_CONFIG= -Dserver.port=%SERVER_PORT%


set DEBUG_OPTS=
if ""%1"" == ""debug"" (
   set DEBUG_OPTS= -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n
   goto debug
)

echo "Starting the %JAR_NAME%"
java %JVM% %APPLICATION_CONFIG%  -jar ../lib/%JAR_NAME% %MAIN_CLASS%
goto end

:debug
echo "start debug mode ......"
java %JVM% %APPLICATION_CONFIG%  %DEBUG_OPTS%  -jar ../lib/%JAR_NAME% %MAIN_CLASS%
goto end

:end
pause