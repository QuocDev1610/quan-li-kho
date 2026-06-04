#!/usr/bin/env powershell

param(
    [Parameter(Position=0, ValueFromRemainingArguments=$true)]
    [String[]]$MvnArgs
)

$MAVEN_VERSION = "3.9.6"
$MAVEN_URL = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$MVN_HOME = "$PSScriptRoot\.mvn\maven"
$MVN_CMD = "$MVN_HOME\bin\mvn.cmd"

# Download Maven if not exists
if (-not (Test-Path $MVN_HOME)) {
    Write-Host "Downloading Maven $MAVEN_VERSION..." -ForegroundColor Green
    $zipFile = "$PSScriptRoot\.mvn\maven.zip"
    New-Item -ItemType Directory -Path "$PSScriptRoot\.mvn" -Force | Out-Null

    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $MAVEN_URL -OutFile $zipFile

    Write-Host "Extracting Maven..." -ForegroundColor Green
    Expand-Archive -Path $zipFile -DestinationPath "$PSScriptRoot\.mvn" -Force
    Move-Item "$PSScriptRoot\.mvn\apache-maven-$MAVEN_VERSION" $MVN_HOME -Force
    Remove-Item $zipFile -Force
}

# Run Maven
& $MVN_CMD @MvnArgs

