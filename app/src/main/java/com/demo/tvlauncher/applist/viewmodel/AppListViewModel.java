package com.demo.tvlauncher.applist.viewmodel;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.tvlauncher.base.BaseViewModel;
import com.demo.tvlauncher.applist.model.InstalledApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListViewModel extends BaseViewModel {

    private final MutableLiveData<List<InstalledApp>> apps = new MutableLiveData<>();

    public LiveData<List<InstalledApp>> getApps() {
        return apps;
    }

    public void loadApps(PackageManager packageManager, String ownPackageName) {
        List<InstalledApp> result = new ArrayList<>();
        for (ApplicationInfo info : packageManager.getInstalledApplications(0)) {
            if (ownPackageName.equals(info.packageName)) {
                continue;
            }

            Intent launchIntent = findLaunchIntent(packageManager, info.packageName);
            if (launchIntent == null) {
                continue;
            }

            String label = packageManager.getApplicationLabel(info).toString();
            Drawable icon = packageManager.getApplicationIcon(info);
            result.add(new InstalledApp(info.packageName, label, icon, launchIntent));
        }
        Collections.sort(result, (left, right) ->
                left.getLabel().compareToIgnoreCase(right.getLabel()));
        apps.setValue(result);
    }

    private Intent findLaunchIntent(PackageManager packageManager, String packageName) {
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            return launchIntent;
        }

        Intent leanbackIntent = new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
                .setPackage(packageName);
        return packageManager.resolveActivity(leanbackIntent, 0) == null ? null : leanbackIntent;
    }
}
