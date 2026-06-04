@echo off
REM Run Spring Boot Application from classpath

setlocal enabledelayedexpansion

set PROJECT_DIR=%~dp0
set TARGET_CLASSES=%PROJECT_DIR%target\classes
set M2_REPO=%USERPROFILE%\.m2\repository

REM Check if classes are compiled
if not exist "%TARGET_CLASSES%" (
    echo Error: target/classes not found. Please compile first!
    echo Run from IntelliJ: Build ^> Build Project
    pause
    exit /b 1
)

REM Build classpath
set CLASSPATH=%TARGET_CLASSES%
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\springframework\boot\spring-boot\2.7.18\spring-boot-2.7.18.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\springframework\boot\spring-boot-starter-web\2.7.18\spring-boot-starter-web-2.7.18.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\springframework\spring-core\5.3.31\spring-core-5.3.31.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\springframework\spring-context\5.3.31\spring-context-5.3.31.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\springframework\spring-beans\5.3.31\spring-beans-5.3.31.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\springframework\spring-web\5.3.31\spring-web-5.3.31.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\springframework\spring-webmvc\5.3.31\spring-webmvc-5.3.31.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\apache\tomcat\embed\tomcat-embed-core\9.0.83\tomcat-embed-core-9.0.83.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\apache\tomcat\embed\tomcat-embed-jasper\9.0.83\tomcat-embed-jasper-9.0.83.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\javax\servlet\jstl\1.2\jstl-1.2.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\jakarta\persistence\jakarta.persistence-api\3.1.0\jakarta.persistence-api-3.1.0.jar
set CLASSPATH=!CLASSPATH!;%M2_REPO%\org\hibernate\hibernate-core\5.6.15.Final\hibernate-core-5.6.15.Final.jar

echo.
echo Starting Inventory Management Application...
echo Port: 1610
echo URL: http://localhost:1610
echo.

java -Dspring.config.location=classpath:application.properties -cp "%CLASSPATH%" dao.ProjectApplication

pause

