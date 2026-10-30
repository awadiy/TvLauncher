package com.demo.tvlauncher.home.state;

import com.demo.tvlauncher.home.model.HomeAction;
import com.demo.tvlauncher.home.model.HomeApp;

import java.util.ArrayList;
import java.util.List;

public class HomeUiState {

    public final List<HomeApp> apps;
    public final List<HomeAction> actions;

    public HomeUiState(List<HomeApp> apps, List<HomeAction> actions) {
        this.apps = apps == null ? new ArrayList<>() : apps;
        this.actions = actions == null ? new ArrayList<>() : actions;
    }
}
