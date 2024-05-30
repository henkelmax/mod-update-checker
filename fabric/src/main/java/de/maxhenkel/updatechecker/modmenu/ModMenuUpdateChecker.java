package de.maxhenkel.updatechecker.modmenu;

import com.terraformersmc.modmenu.api.UpdateChecker;
import com.terraformersmc.modmenu.api.UpdateInfo;
import de.maxhenkel.updatechecker.UpdateManager;

public class ModMenuUpdateChecker implements UpdateChecker {

    @Override
    public UpdateInfo checkForUpdates() {
        return UpdateManager.instance().getResponseSync().map(r -> (UpdateInfo) UpdateCheckerUpdateInfo.create(r)).orElse(DummyUpdateInfo.INSTANCE);
    }

}
