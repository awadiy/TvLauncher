package com.demo.tvlauncher.home.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.tvlauncher.R;
import com.demo.tvlauncher.applist.ui.AppListActivity;
import com.demo.tvlauncher.base.BaseActivity;
import com.demo.tvlauncher.databinding.ActivityMainBinding;
import com.demo.tvlauncher.home.adapter.HomeActionAdapter;
import com.demo.tvlauncher.home.adapter.HomeAppAdapter;
import com.demo.tvlauncher.home.state.HomeUiState;
import com.demo.tvlauncher.home.viewmodel.HomeViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity<ActivityMainBinding, HomeViewModel> {

    private final HomeAppAdapter appAdapter = new HomeAppAdapter();
    private final HomeActionAdapter actionAdapter = new HomeActionAdapter();
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private boolean initialFocusRequested;
    private boolean homeReady;

    private final Runnable clockTick = new Runnable() {
        @Override
        public void run() {
            updateClock();
            long delay = 60_000L - System.currentTimeMillis() % 60_000L;
            clockHandler.postDelayed(this, delay);
        }
    };

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected Class<HomeViewModel> getViewModelClass() {
        return HomeViewModel.class;
    }

    @Override
    protected void initView() {
        RecyclerView apps = binding.rvHomeApps;
        apps.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        apps.setAdapter(appAdapter);
        apps.addOnLayoutChangeListener((view, left, top, right, bottom,
                                        oldLeft, oldTop, oldRight, oldBottom) -> {
            int width = right - left;
            int height = bottom - top;
            if (width > 0 && height > 0
                    && (width != oldRight - oldLeft || height != oldBottom - oldTop)) {
                appAdapter.setAvailableSize(width, height);
            }
        });
        appAdapter.setOnAppClickListener(this::openAppList);

        RecyclerView actions = binding.rvHomeActions;
        actions.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        actions.setAdapter(actionAdapter);
        actions.addOnLayoutChangeListener((view, left, top, right, bottom,
                                           oldLeft, oldTop, oldRight, oldBottom) -> {
            int width = right - left;
            if (width > 0 && width != oldRight - oldLeft) {
                actionAdapter.setAvailableWidth(width);
            }
        });
        actionAdapter.setOnActionClickListener(this::openAppList);

        // INVISIBLE 保留测量和布局；就绪前只显示“加载中”。
        binding.getRoot().getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (!isRowReady(apps, appAdapter.getItemCount())
                                || !isRowReady(actions, actionAdapter.getItemCount())) {
                            return true;
                        }
                        binding.getRoot().getViewTreeObserver().removeOnPreDrawListener(this);
                        homeReady = true;
                        binding.statusInfo.setVisibility(View.VISIBLE);
                        apps.setVisibility(View.VISIBLE);
                        actions.setVisibility(View.VISIBLE);
                        binding.tvLoading.setVisibility(View.GONE);
                        requestInitialFocus();
                        return true;
                    }
                });
    }

    private boolean isRowReady(RecyclerView row, int count) {
        if (count == 0 || row.getWidth() <= 0 || row.getHeight() <= 0
                || row.isLayoutRequested() || row.hasPendingAdapterUpdates()
                || row.getChildCount() != count) {
            return false;
        }
        int expectedWidth = row.getWidth() / count;
        for (int position = 0; position < count; position++) {
            RecyclerView.ViewHolder holder = row.findViewHolderForAdapterPosition(position);
            if (holder == null) {
                return false;
            }
            View item = holder.itemView;
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) item.getLayoutParams();
            if (item.isLayoutRequested() || item.getWidth() <= 0 || item.getHeight() <= 0
                    || item.getWidth() + params.leftMargin + params.rightMargin != expectedWidth) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void initData() {
        viewModel.getUiState().observe(this, this::render);
        viewModel.loadHome();
        updateClock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockHandler.removeCallbacks(clockTick);
        clockHandler.post(clockTick);
    }

    @Override
    protected void onPause() {
        clockHandler.removeCallbacks(clockTick);
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            requestInitialFocus();
        }
    }

    private void requestInitialFocus() {
        if (!homeReady || initialFocusRequested || !hasWindowFocus()) {
            return;
        }
        RecyclerView.ViewHolder holder = binding.rvHomeActions
                .findViewHolderForAdapterPosition(actionAdapter.getItemCount() - 1);
        if (holder != null) {
            initialFocusRequested = holder.itemView.requestFocus();
        }
    }

    private void render(HomeUiState state) {
        appAdapter.submitList(state.apps);
        actionAdapter.submitList(state.actions);
    }

    private void updateClock() {
        Date now = new Date();
        binding.tvTime.setText(new SimpleDateFormat(
                getString(R.string.status_time_format), Locale.US).format(now));
        binding.tvDate.setText(new SimpleDateFormat(
                getString(R.string.status_date_format), Locale.US).format(now));
    }

    private void openAppList() {
        startActivity(new Intent(this, AppListActivity.class));
    }
}
