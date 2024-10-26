# Unicode Collector Mod

A simple Minecraft mod that logs non-standard Unicode characters found in chat messages.

## Overview

Unicode Collector tracks and logs any special characters (non-ASCII) that appear in chat messages. Regular Latin alphabet characters, numbers, and basic punctuation are ignored.

## Log Location

Logs are stored in:
```
.minecraft/config/unicodecollector/logs/
```

Each log file is named with a timestamp: `unicode_log_YYYY-MM-DD_HH-mm-ss.txt`

## Example Log

When someone sends a message containing special characters, it's logged like this:
```
=== New Message with Special Characters ===
Raw message: Hello World! ✨
Selectively Escaped: Hello World! \u2728
Non-Standard Characters Analysis:
Position 13: Character '✨' (Unicode: \u2728)
```

Only messages containing non-standard characters are logged. Regular chat messages using standard ASCII characters are ignored.
