package com.b21dccn216.pocketcocktail.view.Search.helper;

import androidx.recyclerview.widget.DiffUtil;

import com.b21dccn216.pocketcocktail.model.Drink;

import java.util.List;

public class DrinkDiffCallback extends DiffUtil.Callback {

    private final List<Drink> oldList;
    private final List<Drink> newList;

    public DrinkDiffCallback(List<Drink> oldList, List<Drink> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUuid()
                .equals(newList.get(newItemPosition).getUuid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
