package de.maxhenkel.updatechecker;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class FabricUpdateChecker {

    public static void logUpdateStateAsync(String modId) {
        Thread thread = new Thread(() -> logUpdateState(modId));
        thread.setName(String.format("%s_update_checker", modId));
        thread.setDaemon(true);
        thread.start();
    }

    public static void logUpdateState(String modId) {
        Logger logger = LogManager.getLogger(modId);
        logger.info("Checking for available updates for mod '{}'", modId);
        UpdateResponse response = check(modId);
        switch (response.getState()) {
            case UP_TO_DATE:
                logger.info("Mod '{}' is up to date ({})", modId, response.getInstalledVersion());
                break;
            case UPDATE_AVAILABLE:
                response.getUpdate().ifPresent(update -> printUpdate(modId, logger, response, update));
                break;
            case AHEAD:
                logger.info("Mod '{}' is ahead ({})", modId, response.getInstalledVersion());
                break;
            case ERROR:
                logger.info("Error checking updates for mod '{}': {}", modId, response.getError().map(Throwable::getMessage).orElse("Unknown error"));
                break;
            case UNKNOWN:
            default:
                logger.info("Could not find updates for mod '{}'", modId);
                break;
        }
    }

    private static void printUpdate(String modId, Logger logger, UpdateResponse updateResponse, Update update) {
        logger.info("Update available for mod '{}': {} -> {}", modId, updateResponse.getInstalledVersion(), update.getVersion());

        if (update.getDownloadLinks().length > 0) {
            logger.info("Download the update from: {}", update.getDownloadLinks()[0]);
        }
    }

    public static UpdateResponse check(String modId) {
        try {
            return checkInternal(modId);
        } catch (Throwable t) {
            return UpdateResponse.createWithError(t);
        }
    }

    private static UpdateResponse checkInternal(String modId) {
        Optional<ModContainer> optionalMod = FabricLoader.getInstance().getModContainer(modId);
        if (!optionalMod.isPresent()) {
            return UpdateResponse.createWithError(new IllegalStateException(String.format("Could not find mod container '%s'", modId)));
        }

        ModContainer mod = optionalMod.get();

        String modVersion = mod.getMetadata().getVersion().getFriendlyString();

        CustomValue updateUrlCustomValue = mod.getMetadata().getCustomValue("updateUrl");
        if (updateUrlCustomValue == null) {
            return UpdateResponse.createWithError(modVersion, new IllegalStateException("Missing 'updateUrl' in fabric.mod.json"));
        }
        String updateUrl;
        try {
            updateUrl = updateUrlCustomValue.getAsString();
        } catch (Exception e) {
            return UpdateResponse.createWithError(modVersion, new IllegalStateException("Value 'updateUrl' in fabric.mod.json is not a string"));
        }

        Optional<ModContainer> optionalMinecraft = FabricLoader.getInstance().getModContainer("minecraft");
        if (!optionalMinecraft.isPresent()) {
            return UpdateResponse.createWithError(modVersion, new IllegalStateException("Could not find Minecraft mod container"));
        }

        return UpdateChecker.checkUpdates(updateUrl, optionalMinecraft.get().getMetadata().getVersion().getFriendlyString(), modVersion);
    }

}
