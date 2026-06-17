@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims=:" %%A IN (`powershell -NoLogo -NoProfile -NonInteractive -Command "& { $str = $env:__MVNW_ARG0_NAME__; if ($str -eq $null -or $str -eq '') { Write-Output 'mvnw' } else { Write-Output $str } }"`) DO @(
  IF "%%B"=="" (SET "__MVNW_ARG0_NAME__=%%A") ELSE (SET "__MVNW_ARG0_NAME__=%%A:%%B")
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=

@IF "%__MVNW_CMD__%"=="" (
  @SET __MVNW_CMD__="%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6\bin\mvn.cmd"
  @IF NOT EXIST %__MVNW_CMD__% (
    @echo Maven not found at %__MVNW_CMD__%, downloading...
    @powershell -NoLogo -NoProfile -NonInteractive -Command "& { $url='https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip'; $zip='%TEMP%\maven-mvnw.zip'; $dest='%USERPROFILE%\.m2\wrapper\dists'; if (-not (Test-Path '$dest\apache-maven-3.9.6\bin\mvn.cmd')) { Write-Host 'Downloading Maven 3.9.6...'; Invoke-WebRequest -Uri $url -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $dest -Force; Remove-Item $zip -Force } }"
  )
)

@IF NOT EXIST %__MVNW_CMD__% (
  @echo ERROR: Could not find or download Maven.
  @exit /b 1
)

@SETLOCAL
@SET "MAVEN_PROJECTBASEDIR=%~dp0"
@CALL %__MVNW_CMD__% %*
@ENDLOCAL
@EXIT /b %ERRORLEVEL%
