package de.maxhenkel.updatechecker.modmenu;

import com.terraformersmc.modmenu.api.UpdateChannel;
import com.terraformersmc.modmenu.api.UpdateInfo;

public class DummyUpdateInfo implements UpdateInfo {

    public static final DummyUpdateInfo INSTANCE = new DummyUpdateInfo();

    protected DummyUpdateInfo() {

    }

    @Override
    public boolean isUpdateAvailable() {
        return false;
    }

    @Override
    public String getDownloadLink() {
        return "";
    }

    @Override
    public UpdateChannel getUpdateChannel() {
        return UpdateChannel.RELEASE;
    }
}
