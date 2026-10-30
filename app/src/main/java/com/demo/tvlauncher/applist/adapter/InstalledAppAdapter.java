package com.demo.tvlauncher.applist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.tvlauncher.R;
import com.demo.tvlauncher.applist.model.InstalledApp;
import com.demo.tvlauncher.shared.focus.FocusStyle;

import java.util.ArrayList;
import java.util.List;

public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.AppHolder> {

    public interface OnAppClickListener {
        void onAppClick(InstalledApp app);
    }

    private final List<InstalledApp> apps = new ArrayList<>();
    private OnAppClickListener clickListener;

    public void submitList(List<InstalledApp> items) {
        apps.clear();
        if (items != null) {
            apps.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setOnAppClickListener(OnAppClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public AppHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_installed_app, parent, false);
        return new AppHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppHolder holder, int position) {
        InstalledApp app = apps.get(position);
        holder.icon.setImageDrawable(app.getIcon());
        holder.label.setText(app.getLabel());
        FocusStyle.bind(holder.itemView);
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onAppClick(app);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class AppHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView label;

        AppHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_installed_app_icon);
            label = itemView.findViewById(R.id.tv_installed_app_name);
        }
    }
}
