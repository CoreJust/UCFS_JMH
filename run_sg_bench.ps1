<#
.SYNOPSIS
    Runs JMH benchmarks for sg_bench with a specified graph path.
.DESCRIPTION
    This script sets the Java version via jabba, compiles Kotlin JMH sources,
    builds the JMH jar, runs a verification step, and finally executes the
    benchmark with the given graph path. Execution halts immediately if any
    command fails.
.PARAMETER GraphPath
    The path to the graph file used by the verification and benchmark steps.
#>

param(
    [Parameter(Mandatory = $true)]
    [string]$GraphPath
)

$ErrorActionPreference = 'Stop'   # Stop on cmdlet errors

<#
.SYNOPSIS
    Invokes an external command and checks its exit code.
.DESCRIPTION
    Runs the specified command with the provided arguments. If the exit code
    is not 0, an error is written and the script exits with that code.
.PARAMETER Command
    The executable or script to run.
.PARARAMETER ArgumentList
    An array of arguments to pass to the command.
#>
function Invoke-Command {
    param(
        [string]$Command,
        [string[]]$ArgumentList
    )

    & $Command @ArgumentList
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Command '$Command $ArgumentList' failed with exit code $LASTEXITCODE"
        exit $LASTEXITCODE
    }
}

Write-Host "=== Step 1: Set Java version to openjdk@1.17.0 using jabba ==="
Invoke-Command -Command jabba -ArgumentList @('use', 'openjdk@1.17.0')

Write-Host "=== Step 2: Compile JMH Kotlin sources ==="
Invoke-Command -Command .\gradlew -ArgumentList @(':sg_bench:compileJmhKotlin')

Write-Host "=== Step 3: Build JMH jar ==="
Invoke-Command -Command .\gradlew -ArgumentList @(':sg_bench:jmhJar')

Write-Host "=== Step 4: Run verification (UCFSVerifyKt) ==="
Invoke-Command -Command java -ArgumentList @(
    '-Xmx13g',
    '-Xms13g',
    '-Xss4m',
    '-XX:+UseG1GC',
    '-cp',
    'sg_bench/build/libs/sg_bench-1.0-jmh.jar',
    'sg_bench.UCFSVerifyKt',
    $GraphPath
)

Write-Host "=== Step 5: Execute JMH benchmark ==="
Invoke-Command -Command java -ArgumentList @(
    '-Xmx13g',
    '-Xms13g',
    '-Xss4m',
    '-XX:+UseG1GC',
    "-DgraphPath=$GraphPath",
    '-jar',
    'sg_bench/build/libs/sg_bench-1.0-jmh.jar'
)

Write-Host "=== All steps completed successfully ==="
