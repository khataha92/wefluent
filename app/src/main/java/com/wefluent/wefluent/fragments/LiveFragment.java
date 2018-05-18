package com.wefluent.wefluent.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wefluent.wefluent.adapters.CardsAdapter;
import com.wefluent.wefluent.controllers.LiveFragmentController;
import com.wefluent.wefluent.databinding.FragmentLiveBinding;
import com.wefluent.wefluent.enums.CardType;
import com.wefluent.wefluent.interfaces.CardFactory;
import com.wefluent.wefluent.managers.FirebaseManager;
import com.wefluent.wefluent.models.BaseCardModel;
import com.wefluent.wefluent.models.Teacher;
import com.wefluent.wefluent.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;


public class LiveFragment extends BaseFragment implements CardFactory {

    LiveFragmentController controller;

    FragmentLiveBinding binding;

    List<Teacher> teachers = new ArrayList<>();

    CardsAdapter cardsAdapter;

    RecyclerView teachersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup vg, @Nullable Bundle savedInstanceState) {

        binding = FragmentLiveBinding.inflate(inflater);

        controller = new LiveFragmentController(this);

        init();

        return binding.getRoot();
    }

    void init() {

        teachersList = binding.teachersList;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        teachersList.setLayoutManager(linearLayoutManager);

        cardsAdapter = new CardsAdapter();

        cardsAdapter.setCardModels(getCardModels());

        teachersList.setAdapter(cardsAdapter);

        UIUtil.showLoadingView(binding.getRoot());

        controller.getTeachers();

    }

    @Override
    public void onDetach() {
        super.onDetach();

        controller.deleteObservers();

        controller = null;
    }

    public void addTeacher(Teacher teacher) {

        teachers.add(teacher);

        refreshList();
    }

    private void refreshList() {

        controller.orderTeachersList(teachers);

        cardsAdapter.setCardModels(getCardModels());

        if(teachersList.getAdapter() != null) {

            teachersList.getAdapter().notifyDataSetChanged();
        }

        if(teachers.size() > 0) {

            UIUtil.hideLoadingView(binding.getRoot());
        }
    }

    public void updateTeacher(Teacher teacher) {

        for(int i = 0 ; i < teachers.size() ; i++) {

            if(teachers.get(i).getId().equalsIgnoreCase(teacher.getId())) {

                teachers.set(i, teacher);

                break;
            }
        }

        refreshList();

    }

    @Override
    public List<BaseCardModel> getCardModels() {

        List<BaseCardModel> cardModels = new ArrayList<>();

        BaseCardModel cardModel = new BaseCardModel();

        cardModel.setCardType(CardType.COVER);

        cardModels.add(cardModel);

        for(int i = 0 ; i < teachers.size() ; i++) {

            cardModel = new BaseCardModel();

            cardModel.setCardType(CardType.TEACHER_CARD);

            cardModel.setCardValue(teachers.get(i));

            teachers.get(i).setLastItem(i == teachers.size() - 1);

            cardModels.add(cardModel);
        }

        return cardModels;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FirebaseManager.getInstance().terminate();
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }
}
