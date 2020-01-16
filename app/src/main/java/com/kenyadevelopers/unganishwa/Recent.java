package com.kenyadevelopers.unganishwa;

public class Recent
{
    String downloadUrl;
    String name;

    public Recent()
    {

    }
    public Recent(String downloadUrl, String name) {
        this.downloadUrl = downloadUrl;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
