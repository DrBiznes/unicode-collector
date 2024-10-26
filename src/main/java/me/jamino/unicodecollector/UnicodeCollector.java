package me.jamino.unicodecollector;

import net.fabricmc.api.ModInitializer;
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
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("unicodecollector");
    private static final Path LOG_PATH = CONFIG_PATH.resolve("logs");

    @Override
    public void onInitialize() {
        LOGGER.info("Unicode Collector initialized");
        try {
            // Create config and log directories
            CONFIG_PATH.toFile().mkdirs();
            LOG_PATH.toFile().mkdirs();
            LOGGER.info("Unicode Collector log directory created at: " + LOG_PATH.toAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("Failed to create log directory", e);
        }
    }

    public static void logUnicodeMessage(String message) {
        // Add message to collection
        collectedMessages.add(message);

        // Log to console with detailed Unicode info
        StringBuilder unicodeInfo = new StringBuilder();
        unicodeInfo.append("Message Unicode Analysis:\n");
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            String hex = String.format("\\u%04x", (int) c);
            String charInfo = String.format("Position %d: Character '%c' (Unicode: %s)", i, c, hex);
            unicodeInfo.append(charInfo).append("\n");
            LOGGER.info(charInfo);
        }

        // Write to file
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            File logFile = LOG_PATH.resolve("unicode_log_" + timestamp + ".txt").toFile();

            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write("=== New Message ===\n");
                writer.write("Raw message: " + message + "\n");
                writer.write(unicodeInfo.toString());
                writer.write("\n");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write Unicode log", e);
        }
    }
}