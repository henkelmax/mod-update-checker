package de.maxhenkel.updatechecker;

import com.vdurmont.semver4j.Semver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpdateChecker {

    public static UpdateResponse checkUpdates(String modId, String updateUrl, String minecraftVersion, String modVersion) {
        try {
            return checkUpdatesInternal(modId, updateUrl, minecraftVersion, modVersion);
        } catch (Throwable t) {
            return UpdateResponse.createWithError(modId, modVersion, t);
        }
    }

    private static UpdateResponse checkUpdatesInternal(String modId, String updateUrl, String minecraftVersion, String modVersionString) throws IOException, JSONException {
        Semver modVersion = new Semver(modVersionString);
        String response = httpGet(updateUrl, minecraftVersion);

        JSONObject responseJson = new JSONObject(response);

        String homepage = null;
        if (responseJson.has("homepage")) {
            homepage = responseJson.getString("homepage");
        }

        JSONObject versions = responseJson.getJSONObject("versions");

        if (!versions.has(minecraftVersion)) {
            return UpdateResponse.createWithState(modId, modVersionString, UpdateState.UNKNOWN);
        }

        JSONObject mcVersion = versions.getJSONObject(minecraftVersion);

        JSONObject latestJson = mcVersion.getJSONObject("latest");

        Semver latest = new Semver(latestJson.getString("version"));

        if (mcVersion.has("recommended")) {
            JSONObject recommendedJson = mcVersion.getJSONObject("recommended");
            Semver recommended = new Semver(recommendedJson.getString("version"));
            if (recommended.equals(modVersion)) {
                return UpdateResponse.createWithState(modId, modVersionString, UpdateState.UP_TO_DATE);
            } else if (modVersion.compareTo(recommended) < 0) {
                return UpdateResponse.createWithUpdate(modId, modVersionString, fromJson(recommendedJson, homepage));
            }
        }

        if (latest.equals(modVersion)) {
            return UpdateResponse.createWithState(modId, modVersionString, UpdateState.UP_TO_DATE);
        } else if (modVersion.compareTo(latest) > 0) {
            return UpdateResponse.createWithState(modId, modVersionString, UpdateState.AHEAD);
        }

        return UpdateResponse.createWithUpdate(modId, modVersionString, fromJson(latestJson, homepage));
    }

    private static Update fromJson(JSONObject jsonObject, @Nullable String defaultUpdateUrl) {
        JSONArray changelogJson = jsonObject.getJSONArray("changelog");
        String[] changelog = new String[changelogJson.length()];
        for (int i = 0; i < changelog.length; i++) {
            changelog[i] = changelogJson.getString(i);
        }
        String[] downloadLinks = defaultUpdateUrl == null ? new String[0] : new String[]{defaultUpdateUrl};
        if (jsonObject.has("downloadLinks")) {
            JSONArray downloadLinksJson = jsonObject.getJSONArray("downloadLinks");
            downloadLinks = new String[downloadLinksJson.length()];
            for (int i = 0; i < downloadLinks.length; i++) {
                downloadLinks[i] = downloadLinksJson.getString(i);
            }
        }
        return new Update(jsonObject.getString("version"), changelog, downloadLinks);
    }

    private static String httpGet(String urlString, String minecraftVersion) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5_000);
        connection.setReadTimeout(5_000);
        connection.setRequestProperty("User-Agent", String.format("Minecraft %s", minecraftVersion));

        int responseCode = connection.getResponseCode();

        if (responseCode / 100 != 2) {
            throw new IOException(String.format("Response code %d from '%s': %s", responseCode, urlString, tryGetError(connection.getErrorStream()).orElse("Unknown error")));
        }
        String response = convertInputStreamToString(connection.getInputStream());
        connection.disconnect();
        return response;
    }

    private static Optional<String> tryGetError(InputStream errorStream) {
        String errorString;
        try {
            errorString = convertInputStreamToString(errorStream);
        } catch (Exception e) {
            return Optional.of(e.getMessage());
        }
        try {
            JSONObject errorJson = new JSONObject(errorString);
            if (errorJson.has("message")) {
                return Optional.of(errorJson.getString("message"));
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

}
