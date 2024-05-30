package de.maxhenkel.updatechecker;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public class ModUtils {

    /**
     * Gets the mod container of the mod that this class belongs to.
     *
     * @param clazz the class of the mod
     * @return an optional of the mod container
     */
    public static Optional<ModContainer> getModOfClass(Class<?> clazz) {
        URL codeSourceLocation = clazz.getProtectionDomain().getCodeSource().getLocation();
        if (codeSourceLocation == null) {
            return Optional.empty();
        }
        URI modJarOfClassUri;
        try {
            modJarOfClassUri = codeSourceLocation.toURI();
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
        Collection<ModContainer> allMods = FabricLoader.getInstance().getAllMods();
        for (ModContainer mod : allMods) {
            ModOrigin origin = mod.getOrigin();
            if (!origin.getKind().equals(ModOrigin.Kind.PATH)) {
                continue;
            }
            Path modPath = origin.getPaths().stream().findFirst().map(Path::normalize).orElse(null);
            if (modPath == null) {
                continue;
            }
            if (modPath.toUri().equals(modJarOfClassUri)) {
                return Optional.of(mod);
            }
        }

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            for (ModContainer mod : allMods) {
                ModOrigin origin = mod.getOrigin();
                if (!origin.getKind().equals(ModOrigin.Kind.PATH)) {
                    continue;
                }
                if (mod.getMetadata().getId().equals("java")) {
                    continue;
                }
                Path modPath = origin.getPaths().stream().findFirst().map(Path::normalize).orElse(null);
                if (modPath == null) {
                    continue;
                }
                if (Files.isDirectory(modPath)) {
                    return Optional.of(mod);
                }
            }
        }

        return Optional.empty();
    }

}
