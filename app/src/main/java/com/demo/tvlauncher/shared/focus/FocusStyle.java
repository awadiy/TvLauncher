package com.demo.tvlauncher.shared.focus;

import android.view.View;

public final class FocusStyle {

    private FocusStyle() {
    }

    public static void bind(View view) {
        view.setOnFocusChangeListener((focusedView, hasFocus) -> {
            focusedView.animate().cancel();
            focusedView.animate()
                    .scaleX(hasFocus ? 1.08f : 1f)
                    .scaleY(hasFocus ? 1.08f : 1f)
                    .setDuration(140L)
                    .start();
            focusedView.setElevation(hasFocus
                    ? 8f * view.getResources().getDisplayMetrics().density : 0f);
        });

        if (view.hasFocus()) {
            view.setScaleX(1.08f);
            view.setScaleY(1.08f);
        }
    }
}
