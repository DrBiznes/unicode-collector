package me.jamino.unicodecollector;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UnicodeCollector implements ModInitializer {
    public static final String MOD_ID = "unicodecollector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final List<String> collectedMessages = new ArrayList<>();
    private static final Path BASE_PATH = FabricLoader.getInstance().getGameDir().resolve("logs").resolve("unicodecollector");
    private static final Path ANALYSIS_PATH = BASE_PATH.resolve("analysis");
    private static final Path CHAT_LOG_PATH = BASE_PATH.resolve("chat");
    private static FileWriter currentChatLogWriter;
    private static String currentChatLogFile;

    @Override
    public void onInitialize() {
        LOGGER.info("Unicode Collector initialized");
        try {
            // Create all necessary directories
            BASE_PATH.toFile().mkdirs();
            ANALYSIS_PATH.toFile().mkdirs();
            CHAT_LOG_PATH.toFile().mkdirs();
            LOGGER.info("Unicode Collector directories created at: " + BASE_PATH.toAbsolutePath());

            // Register server events
            registerServerEvents();

        } catch (Exception e) {
            LOGGER.error("Failed to create directories", e);
        }
    }

    private void registerServerEvents() {
        // Create new chat log file when joining a server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            try {
                createNewChatLogFile();
            } catch (IOException e) {
                LOGGER.error("Failed to create new chat log file", e);
            }
        });

        // Close the chat log file when disconnecting
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            try {
                if (currentChatLogWriter != null) {
                    currentChatLogWriter.close();
                    currentChatLogWriter = null;
                }
            } catch (IOException e) {
                LOGGER.error("Failed to close chat log file", e);
            }
        });
    }

    private void createNewChatLogFile() throws IOException {
        // Close existing writer if there is one
        if (currentChatLogWriter != null) {
            currentChatLogWriter.close();
        }

        // Create new log file with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        currentChatLogFile = "chat_log_" + timestamp + ".txt";
        File logFile = CHAT_LOG_PATH.resolve(currentChatLogFile).toFile();
        currentChatLogWriter = new FileWriter(logFile, true);

        // Write header to new chat log
        currentChatLogWriter.write("=== Chat Log Started " + timestamp + " ===\n\n");
        currentChatLogWriter.flush();
    }

    private static boolean isStandardCharacter(char c) {
        // Check if character is a standard printable ASCII character (including space and basic punctuation)
        return (c >= 32 && c <= 126);
    }

    private static String convertToSelective(String message) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (isStandardCharacter(c)) {
                result.append(c);
            } else {
                result.append(String.format("\\u%04x", (int) c));
            }
        }
        return result.toString();
    }

    public static void logUnicodeMessage(String message) {
        // First, convert the message for the chat log
        String convertedMessage = message;

        // Always convert the full message for the chat log, including player names and other elements
        convertedMessage = convertToSelective(message);

        // Write to chat log if available
        try {
            if (currentChatLogWriter != null) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                currentChatLogWriter.write(String.format("[%s] %s\n", timestamp, convertedMessage));
                currentChatLogWriter.flush();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write to chat log", e);
        }

        // Only continue with detailed unicode logging if message contains non-standard characters
        boolean hasUnicode = message.chars().anyMatch(c -> !isStandardCharacter((char) c));
        if (!hasUnicode) {
            return;
        }

        // Add message to collection for analysis
        collectedMessages.add(message);

        // Log to console with detailed Unicode info
        StringBuilder unicodeInfo = new StringBuilder();
        unicodeInfo.append("Non-Standard Characters Analysis:\n");
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (!isStandardCharacter(c)) {
                String hex = String.format("\\u%04x", (int) c);
                String charInfo = String.format("Position %d: Character '%c' (Unicode: %s)", i, c, hex);
                unicodeInfo.append(charInfo).append("\n");
                LOGGER.info(charInfo);
            }
        }

        // Write to analysis log file
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            File logFile = ANALYSIS_PATH.resolve("unicode_log_" + timestamp + ".txt").toFile();

            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write("=== New Message with Special Characters ===\n");
                writer.write("Raw message: " + message + "\n");
                writer.write("Selectively Escaped: " + convertedMessage + "\n");
                writer.write(unicodeInfo.toString());
                writer.write("\n");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write Unicode log", e);
        }
    }
}