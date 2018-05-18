package com.wefluent.wefluent.viewholders;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wefluent.wefluent.databinding.ViewEmptyItemBinding;
import com.wefluent.wefluent.databinding.ViewTeacherItemBinding;
import com.wefluent.wefluent.databinding.ViewTeachersCoverBinding;
import com.wefluent.wefluent.models.BaseCardModel;


public class BaseCardViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = BaseCardViewHolder.class.getSimpleName();

    public BaseCardModel cardModel;

    public ViewDataBinding dataBinding;

    public BaseCardViewHolder(ViewDataBinding dataBinding, BaseCardModel cardModel) {

        super(dataBinding.getRoot());

        this.dataBinding = dataBinding;

        this.cardModel = cardModel;

    }

    public BaseCardViewHolder(View view) {

        super(view);

    }


    public BaseCardModel getCardModel() {

        return cardModel;
    }


    public void setCardModel(BaseCardModel cardModel) {

        this.cardModel = cardModel;
    }

    public static BaseCardViewHolder createViewHolder(ViewGroup parent, BaseCardModel cardModel) {

        return BaseCardViewHolder.createViewHolderInternal(parent, cardModel);


    }

    private static BaseCardViewHolder createViewHolderInternal(ViewGroup parent, BaseCardModel cardModel) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (cardModel.getCardType()) {

            case TEACHER_CARD: {

                ViewTeacherItemBinding dataBinding = ViewTeacherItemBinding.inflate(layoutInflater);

                return new TeacherItemViewHolder(dataBinding, cardModel);

            }

            case COVER: {

                ViewTeachersCoverBinding dataBinding = ViewTeachersCoverBinding.inflate(layoutInflater);

                return new TeachersCoverViewHolder(dataBinding, cardModel);
            }

            default: {

                ViewEmptyItemBinding viewEmptyItemBinding = ViewEmptyItemBinding.inflate(layoutInflater, parent, false);

                return new EmptyViewHolder(viewEmptyItemBinding, cardModel);

            }

        }
    }

    public void initializeView() {

    }

    public void onViewAttachedToWindow() {

    }

    public void onViewDetachedFromWindow() {

    }

}