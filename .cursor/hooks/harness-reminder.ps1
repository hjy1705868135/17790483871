$ErrorActionPreference = 'Stop'

$inputText = [Console]::In.ReadToEnd()
if ([string]::IsNullOrWhiteSpace($inputText)) {
  @{ additional_context = 'No hook payload received.' } | ConvertTo-Json -Compress
  exit 0
}

$rawLower = $inputText.ToLowerInvariant()
$message = $null

if ($rawLower.Contains('controller') -and $rawLower.Contains('.java')) {
  $message = 'Controller file changed. Update harness-collab/04-api-docs and harness-collab/func.md, and verify @Operation annotations.'
} elseif (($rawLower.Contains('service') -or $rawLower.Contains('domain')) -and $rawLower.Contains('.java')) {
  $message = 'Business code changed. Sync related tests and run mvn clean verify -Pharness-new for coverage checks.'
} elseif ($rawLower.Contains('pom.xml')) {
  $message = 'pom.xml changed. Confirm Harness Maven profiles (harness-new/harness-legacy) and static analysis config.'
} elseif ($rawLower.Contains('.java')) {
  $message = 'Java file changed. Check layer architecture, dependency direction, and constructor injection constraints.'
}

if ($null -eq $message) {
  @{ additional_context = 'No harness reminder triggered.' } | ConvertTo-Json -Compress
  exit 0
}

@{ additional_context = $message } | ConvertTo-Json -Compress
