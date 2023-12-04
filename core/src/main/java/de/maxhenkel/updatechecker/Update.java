package de.maxhenkel.updatechecker;

public class Update {

    private String version;
    private String[] changelog;
    private String[] downloadLinks;

    public Update(String version, String[] changelog, String[] downloadLinks) {
        this.version = version;
        this.changelog = changelog;
        this.downloadLinks = downloadLinks;
    }

    public Update(String version, String[] changelog) {
        this(version, changelog, new String[0]);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getChangelog() {
        return changelog;
    }

    public void setChangelog(String[] changelog) {
        this.changelog = changelog;
    }

    public String[] getDownloadLinks() {
        return downloadLinks;
    }

    public void setDownloadLinks(String[] downloadLinks) {
        this.downloadLinks = downloadLinks;
    }
}
