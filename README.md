# Unicode Collector

A simple Minecraft mod for Fabric 1.21.1 that logs non-standard Unicode characters found in chat messages.

## Overview

Unicode Collector tracks and logs any special characters (non-ASCII) that appear in chat messages. Regular Latin alphabet characters, numbers, and basic punctuation are ignored.

## Log Locations

The mod creates two types of logs:

### Analysis Logs
Detailed analysis of messages containing special characters:
```
.minecraft/config/unicodecollector/logs/
```

Each analysis log file is named: `unicode_log_YYYY-MM-DD_HH-mm-ss.txt`

### Chat Logs
Complete chat logs with Unicode characters converted to escaped format:
```
.minecraft/config/unicodecollector/chatlogs/
```

Each chat log file is named: `chat_log_YYYY-MM-DD_HH-mm-ss.txt`
- A new chat log file is created every time you join a server
- All chat messages are logged with timestamps, with special characters converted to Unicode escape sequences

## Example Logs

### Analysis Log
When a message contains special characters:
```
=== New Message with Special Characters ===
Raw message: Hello World! ✨
Selectively Escaped: Hello World! \u2728
Non-Standard Characters Analysis:
Position 13: Character '✨' (Unicode: \u2728)
```

### Chat Log
Complete chat history with timestamps:
```
=== Chat Log Started 2024-10-26_14-30-45 ===

[14:30:46] <Player1> Hello everyone!
[14:31:02] <Player2> Hi there \u2728
[14:31:15] <Player1> Look at this cool symbol: \u26a1
```

The chat log automatically converts all non-ASCII characters to their Unicode escape sequences while preserving standard characters for readability. This makes it easy to identify and copy special characters in their escaped format.
