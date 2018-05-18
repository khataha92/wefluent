package com.wefluent.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by khalid on 4/25/18.
 */

public class LoginButton extends LinearLayout {

    ImageView icon;

    TextView text;

    public LoginButton(Context context) {
        super(context);

        init(null);
    }

    public LoginButton(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        init(attrs);
    }

    public LoginButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {

        LayoutInflater.from(getContext()).inflate(R.layout.login_button, this, true);

        icon = findViewById(R.id.icon);

        text = findViewById(R.id.button_text);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoginButton);

        if (typedArray != null) {

            for (int i = 0; i < typedArray.getIndexCount(); i++) {

                int attr = typedArray.getIndex(i);

                if(attr == R.styleable.LoginButton_login_background) {

                    int color = typedArray.getColor(attr,getContext().getResources().getColor(R.color.WHITE));

                    setBackgroundColor(color);

                } else if(attr == R.styleable.LoginButton_icon) {

                    int icon = typedArray.getResourceId(attr, 0);

                    this.icon.setImageResource(icon);

                } else if(attr == R.styleable.LoginButton_text) {

                    String text = typedArray.getString(attr).toString();

                    this.text.setText(text);

                } else if(attr == R.styleable.LoginButton_textSize) {

                    this.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimensionPixelOffset(attr, 18));

                }

            }

        }

        typedArray.recycle();
    }
}

