package de.maxhenkel.updatechecker;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.util.Optional;

public class ModUpdateMetadata {

    protected static ModUpdateMetadata instance;

    protected ModContainer modContainer;
    protected String modId;
    protected String modVersion;
    protected String minecraftVersion;
    protected String updateUrl;

    protected ModUpdateMetadata() {

    }

    public static ModUpdateMetadata get() throws InvalidModConfigurationException {
        if (instance == null) {
            instance = create(ModUtils.getModOfClass(ModUpdateMetadata.class).map(c -> c.getMetadata().getId()).orElseThrow(() -> new InvalidModConfigurationException("Could not find own mod")));
        }
        return instance;
    }

    public static ModUpdateMetadata create(String modId) throws InvalidModConfigurationException {
        Optional<ModContainer> optionalMod = FabricLoader.getInstance().getModContainer(modId);
        if (!optionalMod.isPresent()) {
            throw new InvalidModConfigurationException(String.format("Could not find mod container '%s'", modId));
        }
        ModContainer mod = optionalMod.get();

        CustomValue updaterCustomValue = mod.getMetadata().getCustomValue("updater");
        if (updaterCustomValue == null || !updaterCustomValue.getType().equals(CustomValue.CvType.OBJECT)) {
            throw new InvalidModConfigurationException("Missing 'updater' custom value in fabric.mod.json");
        }
        CustomValue.CvObject updater = updaterCustomValue.getAsObject();
        CustomValue updateUrlCustomValue = updater.get("updateUrl");
        if (updateUrlCustomValue == null || !updateUrlCustomValue.getType().equals(CustomValue.CvType.STRING)) {
            throw new InvalidModConfigurationException("Missing 'updateUrl' in 'updater' in fabric.mod.json");
        }

        Optional<ModContainer> optionalMinecraft = FabricLoader.getInstance().getModContainer("minecraft");
        if (!optionalMinecraft.isPresent()) {
            throw new InvalidModConfigurationException("Could not find Minecraft mod container");
        }

        ModUpdateMetadata metadata = new ModUpdateMetadata();
        metadata.modContainer = mod;
        metadata.modId = mod.getMetadata().getId();
        metadata.modVersion = mod.getMetadata().getVersion().getFriendlyString();
        metadata.minecraftVersion = optionalMinecraft.get().getMetadata().getVersion().getFriendlyString();
        metadata.updateUrl = updateUrlCustomValue.getAsString();

        return metadata;
    }

    public ModContainer getModContainer() {
        return modContainer;
    }

    public String getModId() {
        return modId;
    }

    public String getModVersion() {
        return modVersion;
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }
}
