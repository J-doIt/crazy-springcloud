echo off
title SeckillCloudApplication
set SERVER_PORT=7702
set JAR_NAME=uaa-provider-1.0-SNAPSHOT.jar
set MAIN_CLASS=com.crazymaker.springcloud.seckill.start.SeckillCloudApplication
set JVM= -server -Xms128m -Xmx1g

set APPLICATION_CONFIG= -Dserver.port=%SERVER_PORT% -Dspring.cloud.nacos.discovery.server-addr=%NACOS_ADDR% -Dspring.cloud.nacos.config.server-addr=%NACOS_ADDR%


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