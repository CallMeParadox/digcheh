package com.digcheh.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.ViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(FoodItem food);
    }

    private final List<FoodItem> fullList;
    private final List<FoodItem> filteredList = new ArrayList<>();
    private final OnFoodClickListener listener;
    private String currentCategory = "همه";
    private String currentQuery = "";

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
        holder.tvDetails.setText(item.getCategory() + " • واحد پایه: " + item.getDefaultUnit());
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

    public void setCategory(String category) {
        this.currentCategory = category != null ? category : "همه";
        applyFilter();
    }

    public void filter(String query) {
        this.currentQuery = query != null ? query : "";
        applyFilter();
    }

    private void applyFilter() {
        filteredList.clear();
        String cleanQuery = normalize(currentQuery.trim());
        boolean hasQuery = !cleanQuery.isEmpty();
        String[] queryTokens = hasQuery ? cleanQuery.split("\\s+") : new String[0];

        List<ScoredItem> scoredList = new ArrayList<>();

        for (FoodItem item : fullList) {
            // Category check
            if (!"همه".equals(currentCategory) && !item.getCategory().contains(currentCategory)) {
                continue;
            }

            if (!hasQuery) {
                scoredList.add(new ScoredItem(item, 0));
                continue;
            }

            String normName = normalize(item.getName());
            String normCategory = normalize(item.getCategory());

            int score = 0;
            // 1. Exact match
            if (normName.equals(cleanQuery)) {
                score += 1000;
            }
            // 2. Starts with query
            else if (normName.startsWith(cleanQuery)) {
                score += 500;
            }
            // 3. Name contains query intact
            else if (normName.contains(cleanQuery)) {
                score += 300;
            }

            // 4. Token matching (e.g. "قیمه سیب" matches "خورش قیمه با سیب زمینی")
            boolean allTokensMatch = true;
            int matchedTokensCount = 0;
            for (String token : queryTokens) {
                if (normName.contains(token)) {
                    score += 80;
                    matchedTokensCount++;
                } else if (normCategory.contains(token)) {
                    score += 30;
                    matchedTokensCount++;
                } else {
                    allTokensMatch = false;
                }
            }

            if (allTokensMatch && queryTokens.length > 1) {
                score += 200;
            }

            // 5. Without spaces check (e.g. "تهچین" matches "ته چین")
            String noSpaceName = normName.replace(" ", "");
            String noSpaceQuery = cleanQuery.replace(" ", "");
            if (noSpaceName.contains(noSpaceQuery)) {
                score += 150;
            }

            if (score > 0 || matchedTokensCount > 0) {
                scoredList.add(new ScoredItem(item, score));
            }
        }

        // Sort descending by score
        if (hasQuery) {
            Collections.sort(scoredList, (a, b) -> Integer.compare(b.score, a.score));
        }

        for (ScoredItem si : scoredList) {
            filteredList.add(si.item);
        }

        notifyDataSetChanged();
    }

    public static String normalize(String input) {
        if (input == null) return "";
        return input.trim()
                .toLowerCase()
                .replace("ي", "ی")
                .replace("ك", "ک")
                .replace("آ", "ا")
                .replace("أ", "ا")
                .replace("إ", "ا")
                .replace("ة", "ه")
                .replace("ۀ", "ه")
                .replace("\u200c", " ") // zero-width non-joiner to space
                .replace("-", " ")
                .replaceAll("[\u064B-\u065F]", ""); // remove Arabic diacritics
    }

    private static class ScoredItem {
        FoodItem item;
        int score;
        ScoredItem(FoodItem item, int score) {
            this.item = item;
            this.score = score;
        }
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
