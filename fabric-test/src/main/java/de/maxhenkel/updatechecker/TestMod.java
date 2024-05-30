package de.maxhenkel.updatechecker;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {

    @Override
    public void onInitialize() {
        UpdateManager.instance().getResponseAsync(UpdateLogger::logUpdateState);
    }

}
