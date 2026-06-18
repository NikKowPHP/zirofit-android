#!/usr/bin/env pwsh

<#
.SYNOPSIS
    Processes an XML response from an AI, executing commands and applying file modifications.
.DESCRIPTION
    Parses the standard AI output format containing <reasoning>, <commands>, and <file> elements.
    Commands are executed via PowerShell. File modifications are written to disk.
    This is the PowerShell 7 equivalent of the apply_changes.sh script.
.PARAMETER InputFile
    Path to the XML response file to process.
.EXAMPLE
    .\apply_changes.ps1 -InputFile ai-response.xml
#>

param(
    [Parameter(Mandatory = $true, Position = 0)]
    [ValidateScript({Test-Path $_ -PathType Leaf})]
    [string]$InputFile
)

$ErrorActionPreference = "Stop"

# --- Helper: process a complete CDATA block ---
function Invoke-ProcessCdataBlock {
    param(
        [string]$BlockType,
        [string]$Content,
        [string]$FilePath
    )
    if ($BlockType -eq "command") {
        # Only execute if there's non-whitespace content
        if (-not [string]::IsNullOrWhiteSpace($Content)) {
            Write-Host "`n--- Executing Commands ---"
            try {
                Invoke-Expression -Command $Content
                Write-Host "-> Commands executed successfully."
            } catch {
                Write-Host "-> ERROR: Command execution failed: $($_.Exception.Message)"
            }
            Write-Host "--------------------------"
        }
    } elseif ($BlockType -eq "file") {
        Write-Host "`n--- Applying File Modification ---"
        Write-Host "-> Writing to: $FilePath"
        $dirName = Split-Path $FilePath -Parent
        if (-not [string]::IsNullOrEmpty($dirName)) {
            $null = New-Item -Path $dirName -ItemType Directory -Force
        }
        # Use .NET to preserve content exactly as captured (no trailing newline)
        [System.IO.File]::WriteAllText($FilePath, $Content, [System.Text.UTF8Encoding]::new($false))
        Write-Host "-> Done."
        Write-Host "--------------------------------"
    }
}

Write-Host "Processing response file: $InputFile"
Write-Host "========================================"

# --- Initialize State ---
# 'search':      Default state, looking for a relevant opening tag.
# 'in_reasoning': Capturing the content of the <reasoning> tag.
# 'in_cdata':     Capturing content from a CDATA block.
$state = "search"
# $blockType determines the action after CDATA is read (either 'command' or 'file').
$blockType = ""
$currentFilePath = ""
$currentContent = ""

# Read the file line by line
$reader = [System.IO.StreamReader]::new($InputFile)
try {
    while (($line = $reader.ReadLine()) -ne $null) {

        # --- CDATA Detection (run before state machine) ---
        # Regardless of the state, if we find a CDATA start, we switch to handle it.
        if ($state -ne "in_cdata" -and $line -match '<!\[CDATA\[') {
            # Detect block type from the same line before switching to CDATA mode.
            # The original bash script cannot detect this because the CDATA check
            # fires first and skips the state machine case for the line.
            if ($line -match '<commands>') {
                $blockType = "command"
            } elseif ($line -match '<file path="([^"]+)"') {
                $blockType = "file"
                $currentFilePath = $Matches[1]
            }

            $state = "in_cdata"
            # Everything after the CDATA tag on this line is the start of our content.
            $currentContent = $line -replace '.*<!\[CDATA\[', ''

            # If CDATA also ends on this line, handle it right away.
            if ($line -match '\]\]>') {
                $currentContent = $currentContent -replace '\]\]>.*', ''
                # Process content immediately (avoids doubling that occurs in bash original
                # when falling through to in_cdata case with same-line CDATA).
                Invoke-ProcessCdataBlock $blockType $currentContent $currentFilePath
                # Reset state for the next block
                $state = "search"
                $blockType = ""
                $currentFilePath = ""
                $currentContent = ""
                continue
            } else {
                # The CDATA block continues on the next line, so loop to the next line.
                continue
            }
        }

        switch ($state) {
            "search" {
                if ($line -match '<reasoning>') {
                    Write-Host "--- Reasoning ---"
                    $state = "in_reasoning"
                    # If </reasoning> is on the same line, handle it and go back to 'search'.
                    if ($line -match '</reasoning>') {
                        $content = ($line -replace '.*<reasoning>', '') -replace '</reasoning>.*', ''
                        Write-Host $content.Trim()
                        Write-Host "-----------------"
                        $state = "search"
                    }
                } elseif ($line -match '<commands>') {
                    $blockType = "command"
                    # The state will be switched to 'in_cdata' by the general check above if needed.
                } elseif ($line -match '<file path="([^"]+)"') {
                    $blockType = "file"
                    $currentFilePath = $Matches[1]
                    # The state will be switched to 'in_cdata' by the general check above if needed.
                }
            }

            "in_reasoning" {
                if ($line -match '</reasoning>') {
                    $contentBeforeTag = $line -replace '</reasoning>.*', ''
                    Write-Host $contentBeforeTag.Trim()
                    Write-Host "-----------------"
                    $state = "search"
                } else {
                    Write-Host $line.Trim()
                }
            }

            "in_cdata" {
                # This state is entered either by the general check above, or if we were already in it.
                # If the CDATA block ends on this line, we process it.
                if ($line -match '\]\]>') {
                    # Append the part of the line before the closing marker.
                    $contentBeforeMarker = $line -replace '\]\]>.*', ''
                    if ([string]::IsNullOrEmpty($currentContent)) {
                        $currentContent = $contentBeforeMarker
                    } else {
                        $currentContent = "$currentContent`n$contentBeforeMarker"
                    }

                    # --- Perform action based on $blockType ---
                    Invoke-ProcessCdataBlock $blockType $currentContent $currentFilePath

                    # Reset state for the next block
                    $state = "search"
                    $blockType = ""
                    $currentFilePath = ""
                    $currentContent = ""

                } else {
                    # We are in a multi-line CDATA block. Append the whole line.
                    if ([string]::IsNullOrEmpty($currentContent)) {
                        $currentContent = $line
                    } else {
                        $currentContent = "$currentContent`n$line"
                    }
                }
            }
        }
    }
} finally {
    $reader.Dispose()
}

Write-Host "========================================"
Write-Host "Response processing complete."
