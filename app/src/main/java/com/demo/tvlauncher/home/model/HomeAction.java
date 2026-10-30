package com.demo.tvlauncher.home.model;

import androidx.annotation.StringRes;

public class HomeAction {

    @StringRes
    private final int labelRes;
    private final int iconRes;

    public HomeAction(@StringRes int labelRes, int iconRes) {
        this.labelRes = labelRes;
        this.iconRes = iconRes;
    }

    @StringRes
    public int getLabelRes() {
        return labelRes;
    }

    public int getIconRes() {
        return iconRes;
    }
}
