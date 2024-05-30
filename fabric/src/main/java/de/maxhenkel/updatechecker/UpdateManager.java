package de.maxhenkel.updatechecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class UpdateManager {

    private static final Logger GENERIC_LOGGER = LogManager.getLogger("update_manager");

    @Nullable
    protected UpdateResponse response;

    /**
     * Synchronously checks for updates.
     * Uses a cached response if available.
     *
     * @return an optional containing the update response or an empty optional if the mod is not configured properly
     */
    public Optional<UpdateResponse> getResponseSync() {
        try {
            return Optional.of(getResponseSync(ModUpdateMetadata.get()));
        } catch (InvalidModConfigurationException e) {
            GENERIC_LOGGER.error("Invalid update checking configuration: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Synchronously checks for updates.
     * Uses a cached response if available.
     *
     * @param metadata the metadata
     * @return the update response
     */
    public UpdateResponse getResponseSync(ModUpdateMetadata metadata) {
        synchronized (this) {
            if (response != null) {
                return response;
            }
        }
        UpdateResponse r = check(metadata);
        synchronized (this) {
            response = r;
            return response;
        }
    }

    /**
     * Asynchronously checks for updates.
     * May use a cached response if available.
     *
     * @param callback the callback - Does not get called if the mod is not configured properly
     */
    public void getResponseAsync(Consumer<UpdateResponse> callback) {
        try {
            getResponseAsync(ModUpdateMetadata.get(), callback);
        } catch (InvalidModConfigurationException e) {
            GENERIC_LOGGER.error("Invalid update checking configuration: {}", e.getMessage());
        }
    }

    /**
     * Asynchronously checks for updates.
     * May use a cached response if available.
     *
     * @param metadata the metadata
     * @param callback the callback - Always gets called
     */
    public void getResponseAsync(ModUpdateMetadata metadata, Consumer<UpdateResponse> callback) {
        synchronized (this) {
            if (response != null) {
                callback.accept(response);
                return;
            }
        }
        Thread thread = new Thread(() -> {
            UpdateResponse r = check(metadata);
            synchronized (this) {
                response = r;
                callback.accept(response);
            }
        });
        thread.setName(String.format("%s_update_checker", metadata.getModId()));
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Synchronously checks for updates.
     * Does not use a cached response.
     *
     * @param metadata the metadata
     * @return the update response
     */
    public UpdateResponse check(ModUpdateMetadata metadata) {
        try {
            return UpdateChecker.checkUpdates(metadata.getModId(), metadata.getUpdateUrl(), metadata.getMinecraftVersion(), metadata.getModVersion());
        } catch (Throwable t) {
            return UpdateResponse.createWithError(metadata.getModId(), t);
        }
    }

    protected static UpdateManager instance;

    public static UpdateManager instance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }

}
