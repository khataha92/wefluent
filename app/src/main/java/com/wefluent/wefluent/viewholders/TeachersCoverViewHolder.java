package com.wefluent.wefluent.viewholders;

import com.wefluent.wefluent.databinding.ViewTeachersCoverBinding;
import com.wefluent.wefluent.models.BaseCardModel;

/**
 * Created by khalid on 4/23/18.
 */

public class TeachersCoverViewHolder extends BaseCardViewHolder {

    ViewTeachersCoverBinding dataBinding;

    public TeachersCoverViewHolder(ViewTeachersCoverBinding dataBinding, BaseCardModel cardModel) {

        super(dataBinding, cardModel);

        this.dataBinding = dataBinding;

    }

}
