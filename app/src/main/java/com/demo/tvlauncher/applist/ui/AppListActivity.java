package com.demo.tvlauncher.applist.ui;

import android.content.ActivityNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.demo.tvlauncher.R;
import com.demo.tvlauncher.applist.adapter.InstalledAppAdapter;
import com.demo.tvlauncher.applist.model.InstalledApp;
import com.demo.tvlauncher.applist.viewmodel.AppListViewModel;
import com.demo.tvlauncher.base.BaseActivity;
import com.demo.tvlauncher.databinding.ActivityAppListBinding;

import java.util.List;

public class AppListActivity extends BaseActivity<ActivityAppListBinding, AppListViewModel> {

    private final InstalledAppAdapter adapter = new InstalledAppAdapter();

    @Override
    protected int initLayout() {
        return R.layout.activity_app_list;
    }

    @Override
    protected Class<AppListViewModel> getViewModelClass() {
        return AppListViewModel.class;
    }

    @Override
    protected void initView() {
        int spanCount = getSpanCount();
        binding.rvInstalledApps.setLayoutManager(new GridLayoutManager(this, spanCount));
        binding.rvInstalledApps.setAdapter(adapter);
        adapter.setOnAppClickListener(this::launchApp);
    }

    @Override
    protected void initData() {
        viewModel.getApps().observe(this, this::renderApps);
        viewModel.loadApps(getPackageManager(), getPackageName());
    }

    private int getSpanCount() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthDp = Math.round(metrics.widthPixels / metrics.density);
        return Math.max(4, widthDp / 170);
    }

    private void renderApps(List<InstalledApp> apps) {
        adapter.submitList(apps);
        boolean empty = apps == null || apps.isEmpty();
        binding.tvEmptyApps.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (!empty) {
            binding.rvInstalledApps.post(() -> {
                androidx.recyclerview.widget.RecyclerView.ViewHolder first =
                        binding.rvInstalledApps.findViewHolderForAdapterPosition(0);
                if (first != null) {
                    first.itemView.requestFocus();
                }
            });
        }
    }

    private void launchApp(InstalledApp app) {
        try {
            startActivity(app.getLaunchIntent());
        } catch (ActivityNotFoundException exception) {
            Log.w("AppListActivity", "App is no longer available: " + app.getPackageName());
        }
    }
}
