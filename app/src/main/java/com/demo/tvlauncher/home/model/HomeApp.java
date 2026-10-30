package com.demo.tvlauncher.home.model;

public class HomeApp {

    private final String title;
    private final int iconRes;
    private final int nameImageRes;
    private final int tileColorRes;

    public HomeApp(String title, int iconRes, int nameImageRes, int tileColorRes) {
        this.title = title;
        this.iconRes = iconRes;
        this.nameImageRes = nameImageRes;
        this.tileColorRes = tileColorRes;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getNameImageRes() {
        return nameImageRes;
    }

    public int getTileColorRes() {
        return tileColorRes;
    }
}
