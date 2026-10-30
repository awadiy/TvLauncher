package com.demo.tvlauncher.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

/** 页面初始化入口：绑定布局和 ViewModel，再初始化控件与数据。 */
public abstract class BaseActivity<VDB extends ViewDataBinding, VM extends BaseViewModel>
        extends AppCompatActivity {

    protected VDB binding;
    protected VM viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, initLayout());
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(getViewModelClass());

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyImmersiveMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyImmersiveMode();
        }
    }

    private void applyImmersiveMode() {
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @LayoutRes
    protected abstract int initLayout();

    protected abstract Class<VM> getViewModelClass();

    /** 初始化控件、Adapter 和监听。 */
    protected abstract void initView();

    /** 加载数据并观察页面状态。 */
    protected abstract void initData();
}
