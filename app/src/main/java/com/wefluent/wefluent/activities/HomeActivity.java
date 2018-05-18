package com.wefluent.wefluent.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.wefluent.wefluent.R;
import com.wefluent.wefluent.databinding.ActivityHomeBinding;
import com.wefluent.wefluent.fragments.LiveFragment;
import com.wefluent.wefluent.managers.FragmentManager;
import com.wefluent.wefluent.utils.App;
import com.wefluent.wefluent.utils.UIUtil;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);



        App.setActivity(this);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        init();
    }

    private void init() {

        UIUtil.removeShiftMode(dataBinding.bottomNavigation);

        FragmentManager.showFragment(LiveFragment.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        App.setActivity(this);
    }

    public static void start(Context context) {

        context.startActivity(new Intent(context, HomeActivity.class));
    }
}
