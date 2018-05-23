package com.example.android.bakingtime.recipedetails;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ItemStepBinding;
import com.example.android.networkmodule.model.Step;

import java.util.ArrayList;
import java.util.List;

class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private final Context context;
    private ArrayList<Step> steps;
    private OnStepItemClickListener onStepItemClickListener;

    StepsAdapter(Context context) {
        this.context = context;
    }

    public void setSteps(List<Step> steps) {
        this.steps = new ArrayList<>(steps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepsAdapter.StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepsAdapter.StepViewHolder holder, int position) {
        if (position != 0) {
            holder.binding.stepName.setText(steps.get(position).getDisplayShortDescription());
        } else {
            holder.binding.stepName.setText(steps.get(position).getShortDescription());
        }
        holder.binding.stepName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onStepItemClickListener != null) {
                    onStepItemClickListener.onStepItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    public void setClickListener(OnStepItemClickListener onStepItemClickListener) {
        this.onStepItemClickListener = onStepItemClickListener;
    }

    public void removeClickListener() {
        this.onStepItemClickListener = null;
    }

    class StepViewHolder extends RecyclerView.ViewHolder {
        private ItemStepBinding binding;

        StepViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
