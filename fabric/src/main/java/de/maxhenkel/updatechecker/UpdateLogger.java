package de.maxhenkel.updatechecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateLogger {

    public static void logUpdateState(UpdateResponse response) {
        Logger logger = LogManager.getLogger(response.getModId());
        switch (response.getState()) {
            case UP_TO_DATE:
                logger.info("Mod '{}' is up to date ({})", response.getModId(), response.getInstalledVersion());
                break;
            case UPDATE_AVAILABLE:
                response.getUpdate().ifPresent(update -> printUpdate(response.getModId(), logger, response, update));
                break;
            case AHEAD:
                logger.info("Mod '{}' is ahead ({})", response.getModId(), response.getInstalledVersion());
                break;
            case ERROR:
                logger.info("Error checking updates for mod '{}': {}", response.getModId(), response.getError().map(Throwable::getMessage).orElse("Unknown error"));
                break;
            case UNKNOWN:
            default:
                logger.info("Could not find updates for mod '{}'", response.getModId());
                break;
        }
    }

    private static void printUpdate(String modId, Logger logger, UpdateResponse updateResponse, Update update) {
        logger.info("Update available for mod '{}': {} -> {}", modId, updateResponse.getInstalledVersion(), update.getVersion());

        if (update.getDownloadLinks().length > 0) {
            logger.info("Download the update from: {}", update.getDownloadLinks()[0]);
        }
    }

}
