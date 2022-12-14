@echo off

set uaa_jar=D:\dev\crazy-springcloud\crazy-springcloud\crazymaker-uaa\uaa-provider\target\uaa-provider-1.0-SNAPSHOT.jar

set uaa_jar_dest=D:\virtual\workcluster\chapter24linktrace\uaa-application\

XCOPY  %uaa_jar%  %uaa_jar_dest%   /Y


set demo_jar=D:\dev\crazy-springcloud\crazy-springcloud\crazymaker-demo\demo-provider\target\demo-provider-1.0-SNAPSHOT.jar

set demo_jar_dest=D:\virtual\workcluster\chapter24linktrace\demo-application\

XCOPY  %demo_jar%  %demo_jar_dest%   /Y