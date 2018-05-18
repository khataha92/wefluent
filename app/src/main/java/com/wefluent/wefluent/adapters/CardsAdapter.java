package com.wefluent.wefluent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.wefluent.wefluent.models.BaseCardModel;
import com.wefluent.wefluent.viewholders.BaseCardViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khalid on 1/20/18.
 */

public class CardsAdapter extends RecyclerView.Adapter<BaseCardViewHolder> {

    List<BaseCardModel> cardModels = new ArrayList<>();

    @Override
    public BaseCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return BaseCardViewHolder.createViewHolder(parent, cardModels.get(viewType));
    }

    @Override
    public void onBindViewHolder(BaseCardViewHolder holder, int position) {

        holder.cardModel = cardModels.get(position);

        holder.initializeView();
    }

    @Override
    public void onViewAttachedToWindow(BaseCardViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(BaseCardViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.onViewDetachedFromWindow();
    }

    public void setCardModels(List<BaseCardModel> cardModels) {

        this.cardModels.clear();

        this.cardModels.addAll(cardModels);
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getItemCount() {

        return cardModels.size();
    }
}