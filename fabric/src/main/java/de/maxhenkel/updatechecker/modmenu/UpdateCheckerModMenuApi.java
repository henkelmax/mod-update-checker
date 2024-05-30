package de.maxhenkel.updatechecker.modmenu;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.UpdateChecker;

public class UpdateCheckerModMenuApi implements ModMenuApi {

    @Override
    public UpdateChecker getUpdateChecker() {
        return new ModMenuUpdateChecker();
    }
}
