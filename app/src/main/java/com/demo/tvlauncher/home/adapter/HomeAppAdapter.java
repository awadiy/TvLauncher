package com.demo.tvlauncher.home.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.tvlauncher.R;
import com.demo.tvlauncher.home.model.HomeApp;
import com.demo.tvlauncher.shared.focus.FocusStyle;
import com.demo.tvlauncher.shared.widget.ReflectionImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeAppAdapter extends RecyclerView.Adapter<HomeAppAdapter.AppHolder> {

    public interface OnAppClickListener {
        void onAppClick();
    }

    private final List<HomeApp> apps = new ArrayList<>();
    private OnAppClickListener clickListener;
    private RecyclerView recyclerView;
    private int availableWidth;
    private int availableHeight;
    private int itemWidth;

    public void submitList(List<HomeApp> items) {
        apps.clear();
        if (items != null) {
            apps.addAll(items);
        }
        updateItemWidth();
        notifyDataSetChanged();
    }

    public void setAvailableSize(int width, int height) {
        if (availableWidth == width && availableHeight == height) {
            return;
        }
        availableWidth = width;
        availableHeight = height;
        updateItemWidth();
        if (recyclerView != null && !apps.isEmpty()) {
            recyclerView.post(this::notifyDataSetChanged);
        }
    }

    public void setOnAppClickListener(OnAppClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        setAvailableSize(recyclerView.getWidth(), recyclerView.getHeight());
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public AppHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_app, parent, false);
        return new AppHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppHolder holder, int position) {
        HomeApp app = apps.get(position);
        holder.icon.setImageResource(app.getIconRes());
        if (app.getNameImageRes() != 0) {
            holder.nameImage.setImageResource(app.getNameImageRes());
            holder.nameImage.setVisibility(View.VISIBLE);
            holder.nameText.setVisibility(View.GONE);
        } else {
            holder.nameImage.setImageDrawable(null);
            holder.nameImage.setVisibility(View.GONE);
            holder.nameText.setText(app.getTitle());
            holder.nameText.setTypeface(android.graphics.Typeface.create(
                    "sans-serif-condensed", android.graphics.Typeface.BOLD));
            holder.nameText.setVisibility(View.VISIBLE);
        }
        holder.tile.setContentDescription(app.getTitle());
        GradientDrawable tileBackground = new GradientDrawable();
        tileBackground.setShape(GradientDrawable.RECTANGLE);
        tileBackground.setCornerRadius(holder.itemView.getResources()
                .getDimension(R.dimen.home_app_corner_radius));
        tileBackground.setColor(holder.itemView.getResources().getColor(app.getTileColorRes()));
        holder.tile.setBackground(tileBackground);
        holder.reflection.setSource(holder.tile, holder.content);

        FocusStyle.bind(holder.tile);
        holder.tile.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onAppClick();
            }
        });
        setItemSize(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    private void updateItemWidth() {
        if (availableWidth <= 0 || apps.isEmpty()) {
            return;
        }
        itemWidth = availableWidth / apps.size();
    }

    private void setItemSize(View itemView) {
        if (itemWidth <= 0 || availableHeight <= 0) {
            return;
        }
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.width = itemWidth;
        params.height = availableHeight;
        itemView.setLayoutParams(params);
    }

    static class AppHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout tile;
        final ConstraintLayout content;
        final ImageView icon;
        final ImageView nameImage;
        final TextView nameText;
        final ReflectionImageView reflection;

        AppHolder(@NonNull View itemView) {
            super(itemView);
            tile = itemView.findViewById(R.id.home_app_tile);
            content = itemView.findViewById(R.id.app_content);
            icon = itemView.findViewById(R.id.iv_app_icon);
            nameImage = itemView.findViewById(R.id.iv_app_name);
            nameText = itemView.findViewById(R.id.tv_app_name);
            reflection = itemView.findViewById(R.id.iv_app_reflection);
        }
    }
}
