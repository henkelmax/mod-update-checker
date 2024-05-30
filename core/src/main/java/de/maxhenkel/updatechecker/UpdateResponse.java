package de.maxhenkel.updatechecker;

import javax.annotation.Nullable;
import java.util.Optional;

public class UpdateResponse {

    protected String modId;
    protected String installedVersion;
    protected UpdateState state;
    @Nullable
    protected Update update;
    @Nullable
    protected Throwable error;

    private UpdateResponse(String modId, String installedVersion, UpdateState state, @Nullable Update update, @Nullable Throwable error) {
        this.modId = modId;
        this.installedVersion = installedVersion;
        this.state = state;
        this.update = update;
        this.error = error;
    }

    public static UpdateResponse createWithUpdate(String modId, String installedVersion, Update update) {
        return new UpdateResponse(modId, installedVersion, UpdateState.UPDATE_AVAILABLE, update, null);
    }

    public static UpdateResponse createWithState(String modId, String installedVersion, UpdateState state) {
        return new UpdateResponse(modId, installedVersion, state, null, null);
    }

    public static UpdateResponse createWithError(String modId, String installedVersion, Throwable error) {
        return new UpdateResponse(modId, installedVersion, UpdateState.ERROR, null, error);
    }

    public static UpdateResponse createWithError(String modId, Throwable error) {
        return new UpdateResponse(modId, "N/A", UpdateState.ERROR, null, error);
    }

    public String getModId() {
        return modId;
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public UpdateState getState() {
        return state;
    }

    public Optional<Update> getUpdate() {
        return Optional.ofNullable(update);
    }

    public Optional<Throwable> getError() {
        return Optional.ofNullable(error);
    }
}
