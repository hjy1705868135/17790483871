# Start script for cupro project
$ErrorActionPreference = "Stop"
$JAVA_HOME = "C:\Users\69394\.jdks\openjdk-25.0.1"
$env:JAVA_HOME = $JAVA_HOME
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

$MAVEN_VERSION = "3.9.6"
$MAVEN_HOME = "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-$MAVEN_VERSION"
$MVN_CMD = "$MAVEN_HOME\bin\mvn.cmd"

if (-not (Test-Path $MVN_CMD)) {
    Write-Host "Maven $MAVEN_VERSION not found, downloading..."
    $zipUrl = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
    $zipFile = "$env:TEMP\maven-$MAVEN_VERSION.zip"
    
    # Use .NET WebClient for faster download
    $wc = New-Object System.Net.WebClient
    $wc.DownloadFile($zipUrl, $zipFile)
    $wc.Dispose()
    
    Write-Host "Extracting Maven..."
    Expand-Archive -Path $zipFile -DestinationPath "$env:USERPROFILE\.m2\wrapper\dists" -Force
    Remove-Item $zipFile -Force
    Write-Host "Maven installed."
}

Write-Host "Using Maven: $MVN_CMD"
Write-Host "Using Java: $JAVA_HOME"

$projectDir = "c:\Users\69394\Desktop\cupro1.0\baseplatform"
Set-Location $projectDir

Write-Host "Compiling project..."
$compileArgs = @("clean", "compile", "-DskipTests")
& $MVN_CMD $compileArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Starting Spring Boot application..."
$runArgs = @("spring-boot:run")
& $MVN_CMD $runArgs
