package com.wefluent.wefluent.viewholders;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.wefluent.wefluent.R;
import com.wefluent.wefluent.databinding.ViewTeacherItemBinding;
import com.wefluent.wefluent.enums.TeacherStatus;
import com.wefluent.wefluent.managers.FirebaseManager;
import com.wefluent.wefluent.models.BaseCardModel;
import com.wefluent.wefluent.models.Teacher;
import com.wefluent.wefluent.utils.App;
import com.wefluent.wefluent.utils.UIUtil;

/**
 * Created by khalid on 4/23/18.
 */

public class TeacherItemViewHolder extends BaseCardViewHolder {

    ViewTeacherItemBinding dataBinding;

    public TeacherItemViewHolder(ViewTeacherItemBinding dataBinding, BaseCardModel cardModel) {

        super(dataBinding, cardModel);

        this.dataBinding = dataBinding;

    }

    @Override
    public void initializeView() {
        super.initializeView();

        Teacher teacher = cardModel.getCardValue();

        teacher.setOnCallClick(v -> {

            if(teacher.getTeacherStatus() == TeacherStatus.ONLINE) {

                UIUtil.showConfirmCallDialog(teacher.getName(), () -> FirebaseManager.getInstance().callTeacher(teacher));
            }

        });

        dataBinding.setTeacher(teacher);

        Glide.with(App.getAppContext()).load(teacher.getImageUrl()).into(dataBinding.profileImage);

        int statusColor = R.color.OFFLINE;

        int callColor = R.color.OFFLINE;

        switch (teacher.getTeacherStatus()) {

            case ONLINE:

                statusColor = R.color.ONLINE;

                callColor = R.color.CALL_COLOR;

                break;

            case BUSY:

                statusColor = R.color.BUSY;

                callColor = R.color.OFFLINE;

                break;

            case INVISIBLE:
            case OFFLINE:

                callColor = statusColor = R.color.OFFLINE;

                break;
        }

        dataBinding.call.setColorFilter(ContextCompat.getColor(App.getActiveContext(), callColor), PorterDuff.Mode.SRC_ATOP);

        dataBinding.profileImage.setBorderColor(App.getActiveContext().getResources().getColor(statusColor));
    }
}
