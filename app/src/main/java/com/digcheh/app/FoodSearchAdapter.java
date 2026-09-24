package com.digcheh.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.ViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(FoodItem food);
    }

    private final List<FoodItem> fullList;
    private final List<FoodItem> filteredList = new ArrayList<>();
    private final OnFoodClickListener listener;

    public FoodSearchAdapter(List<FoodItem> items, OnFoodClickListener listener) {
        this.fullList = items;
        this.filteredList.addAll(items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = filteredList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDetails.setText("دسته‌بندی: " + item.getCategory() + " • واحد پایه: " + item.getDefaultUnit());
        holder.tvCalories.setText(toPersianDigits(item.getCaloriesPerUnit()) + " kcal");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String lower = query.trim().toLowerCase();
            for (FoodItem item : fullList) {
                if (item.getName().toLowerCase().contains(lower) || item.getCategory().toLowerCase().contains(lower)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    private String toPersianDigits(int number) {
        char[] persianDigits = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'};
        String str = String.valueOf(number);
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(persianDigits[c - '0']);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvCalories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSearchFoodName);
            tvDetails = itemView.findViewById(R.id.tvSearchFoodDetails);
            tvCalories = itemView.findViewById(R.id.tvSearchFoodCalories);
        }
    }
}
