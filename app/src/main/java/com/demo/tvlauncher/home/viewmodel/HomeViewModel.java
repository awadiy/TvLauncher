package com.demo.tvlauncher.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.tvlauncher.R;
import com.demo.tvlauncher.base.BaseViewModel;
import com.demo.tvlauncher.home.model.HomeAction;
import com.demo.tvlauncher.home.model.HomeApp;
import com.demo.tvlauncher.home.state.HomeUiState;

import java.util.Arrays;
import java.util.List;

public class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<HomeUiState> uiState = new MutableLiveData<>();

    public LiveData<HomeUiState> getUiState() {
        return uiState;
    }

    public void loadHome() {
        List<HomeApp> apps = Arrays.asList(
                new HomeApp("NETFLIX", R.drawable.logo_netflix,
                        R.drawable.name_netflix, R.color.tile_netflix),
                new HomeApp("YouTube", R.drawable.logo_youtube,
                        R.drawable.name_youtube, R.color.tile_youtube),
                new HomeApp("Google Play", R.drawable.logo_google_play,
                        R.drawable.name_google_play, R.color.tile_google_play),
                new HomeApp("Chrome", R.drawable.logo_chrome,
                        R.drawable.name_chrome, R.color.tile_chrome));

        List<HomeAction> actions = Arrays.asList(
                new HomeAction(R.string.menu_keystone, R.drawable.ic_keystone),
                new HomeAction(R.string.menu_miracast, R.drawable.ic_miracast),
                new HomeAction(R.string.menu_signal_source, R.drawable.ic_signal_source),
                new HomeAction(R.string.menu_my_apps, R.drawable.ic_my_apps),
                new HomeAction(R.string.menu_settings, R.drawable.ic_settings));

        uiState.setValue(new HomeUiState(apps, actions));
    }
}
