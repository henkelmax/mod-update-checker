package de.maxhenkel.updatechecker;

import javax.annotation.Nullable;
import java.util.Optional;

public class UpdateResponse {

    private String installedVersion;
    private UpdateState state;
    @Nullable
    private Update update;
    @Nullable
    private Throwable error;

    private UpdateResponse(String installedVersion, UpdateState state, @Nullable Update update, @Nullable Throwable error) {
        this.installedVersion = installedVersion;
        this.state = state;
        this.update = update;
        this.error = error;
    }

    public static UpdateResponse createWithUpdate(String installedVersion, Update update) {
        return new UpdateResponse(installedVersion, UpdateState.UPDATE_AVAILABLE, update, null);
    }

    public static UpdateResponse createWithState(String installedVersion, UpdateState state) {
        return new UpdateResponse(installedVersion, state, null, null);
    }

    public static UpdateResponse createWithError(String installedVersion, Throwable error) {
        return new UpdateResponse(installedVersion, UpdateState.ERROR, null, error);
    }

    public static UpdateResponse createWithError(Throwable error) {
        return new UpdateResponse("N/A", UpdateState.ERROR, null, error);
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
