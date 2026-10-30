package com.demo.tvlauncher.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.tvlauncher.R;
import com.demo.tvlauncher.home.model.HomeAction;
import com.demo.tvlauncher.shared.focus.FocusStyle;

import java.util.ArrayList;
import java.util.List;

public class HomeActionAdapter extends RecyclerView.Adapter<HomeActionAdapter.ActionHolder> {

    public interface OnActionClickListener {
        void onActionClick();
    }

    private final List<HomeAction> actions = new ArrayList<>();
    private OnActionClickListener clickListener;
    private RecyclerView recyclerView;
    private int availableWidth;
    private int itemWidth;

    public void submitList(List<HomeAction> items) {
        actions.clear();
        if (items != null) {
            actions.addAll(items);
        }
        updateItemWidth();
        notifyDataSetChanged();
    }

    public void setAvailableWidth(int width) {
        availableWidth = width;
        updateItemWidth();
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        setAvailableWidth(recyclerView.getWidth());
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ActionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_action, parent, false);
        return new ActionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionHolder holder, int position) {
        HomeAction action = actions.get(position);
        holder.icon.setImageResource(action.getIconRes());
        holder.label.setText(action.getLabelRes());
        FocusStyle.bind(holder.itemView);
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onActionClick();
            }
        });
        setItemWidth(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    private void updateItemWidth() {
        if (availableWidth <= 0 || actions.isEmpty()) {
            return;
        }
        int nextWidth = availableWidth / actions.size();
        if (itemWidth == nextWidth) {
            return;
        }
        itemWidth = nextWidth;
        if (recyclerView != null) {
            recyclerView.post(this::notifyDataSetChanged);
        }
    }

    private void setItemWidth(View itemView) {
        if (itemWidth <= 0) {
            return;
        }
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        int margin = Math.round(itemView.getResources().getDisplayMetrics().density * 4f);
        if (params instanceof RecyclerView.LayoutParams) {
            RecyclerView.LayoutParams recyclerParams = (RecyclerView.LayoutParams) params;
            recyclerParams.leftMargin = margin;
            recyclerParams.rightMargin = margin;
            recyclerParams.width = Math.max(0, itemWidth - margin * 2);
        } else {
            params.width = itemWidth;
        }
        itemView.setLayoutParams(params);
    }

    static class ActionHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView label;

        ActionHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_action_icon);
            label = itemView.findViewById(R.id.tv_action_label);
        }
    }
}
