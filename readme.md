# Mod Update Checker

A library for Minecraft mods, that checks for available updates.
Also see [this](https://github.com/henkelmax/mod-update-server).

## Usage

### Fabric

*build.gradle*
``` groovy
repositories {
    maven { url = 'https://maven.maxhenkel.de/repository/public' }
}
dependencies {
    // Use shadow or include to embed the dependency in your mod
    implementation "de.maxhenkel:update-checker:1.0.0+fabric"
}
```

*fabric.mod.json*
``` json
{
  ...
  "custom": {
    "updateUrl": "https://your-update-server.com/check/fabric/yourmod"
  }
}
```

*Your entrypoint class*
``` java
public class YourMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // This logs if updates are available
        FabricUpdateChecker.logUpdateStateAsync("your_mod_id");
        
        // This checks for updates and gives you the result
        UpdateResponse updateResponse = FabricUpdateChecker.check("your_mod_id");
        //TODO Handle notifying for updates
    }
}
```

