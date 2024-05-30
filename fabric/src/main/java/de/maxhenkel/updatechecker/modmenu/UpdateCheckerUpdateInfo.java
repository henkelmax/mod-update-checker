package de.maxhenkel.updatechecker.modmenu;

import com.terraformersmc.modmenu.api.UpdateChannel;
import com.terraformersmc.modmenu.api.UpdateInfo;
import de.maxhenkel.updatechecker.Update;
import de.maxhenkel.updatechecker.UpdateResponse;
import de.maxhenkel.updatechecker.UpdateState;

import java.util.Arrays;

public class UpdateCheckerUpdateInfo implements UpdateInfo {

    protected UpdateResponse response;

    protected UpdateCheckerUpdateInfo(UpdateResponse response) {
        this.response = response;
    }

    public static UpdateCheckerUpdateInfo create(UpdateResponse response) {
        return new UpdateCheckerUpdateInfo(response);
    }

    @Override
    public boolean isUpdateAvailable() {
        return response.getState().equals(UpdateState.UPDATE_AVAILABLE);
    }

    @Override
    public String getDownloadLink() {
        return response.getUpdate().map(Update::getDownloadLinks).flatMap(links -> Arrays.stream(links).findFirst()).orElse(null);
    }

    @Override
    public UpdateChannel getUpdateChannel() {
        return UpdateChannel.RELEASE;
    }
}
