package com.b21dccn216.pocketcocktail.view.Main.fragment.Discover;

import androidx.recyclerview.widget.DiffUtil;

import com.b21dccn216.pocketcocktail.model.Category;

import java.util.List;

public class ItemDiffCallback extends DiffUtil.Callback{


    private final List<Category> oldList;
    private final List<Category> newList;

    public ItemDiffCallback(List<Category> oldList, List<Category> newList) {
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
        return oldList.get(oldItemPosition).getUuid().equals(newList.get(newItemPosition).getUuid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUuid().equals(newList.get(newItemPosition).getUuid());
    }
}
