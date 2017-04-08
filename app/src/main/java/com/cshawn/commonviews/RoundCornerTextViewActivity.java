package com.cshawn.commonviews;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.cshawn.commonviewslib.roundcorner.RoundCornerTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoundCornerTextViewActivity extends AppCompatActivity {
    @BindView(R.id.rctv)
    RoundCornerTextView rctv;
    @BindView(R.id.et_r_l_b)
    EditText et_r_l_b;
    @BindView(R.id.et_r_l_t)
    EditText et_r_l_t;
    @BindView(R.id.et_r_r_b)
    EditText et_r_r_b;
    @BindView(R.id.et_r_r_t)
    EditText et_r_r_t;
    @BindView(R.id.et_radius)
    EditText et_radius;
    @BindView(R.id.et_stroke_width)
    EditText et_stroke_width;
    @BindView(R.id.cb_self)
    CheckBox cb_self;
    @BindView(R.id.sp_shape)
    Spinner sp_shape;
    @BindView(R.id.sp_scale_type)
    Spinner sp_scale_type;
    @BindView(R.id.sp_back_type)
    Spinner sp_back_type;
    @BindView(R.id.rg_back)
    RadioGroup rg_back;

    View.OnFocusChangeListener onFocusChangeListener= new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                updateView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_corner_text_view);
        ButterKnife.bind(this);
        et_radius.setText((int) rctv.getCornerRadius()+"");
        et_stroke_width.setText((int) rctv.getStrokeWidth()+"");
        et_r_l_b.setText((int) rctv.getRadius_left_bottom()+"");
        et_r_l_t.setText((int) rctv.getRadius_left_top()+"");
        et_r_r_b.setText((int) rctv.getRadius_right_bottom()+"");
        et_r_r_t.setText((int) rctv.getRadius_right_top()+"");
        cb_self.setChecked(rctv.isSelfRoundCorner());
        rg_back.check(R.id.rb_color);
        sp_shape.setSelection(rctv.getShape());
        sp_back_type.setSelection(rctv.getBackgroundFitType());
        sp_scale_type.setSelection(rctv.getScaleType());

        et_radius.setOnFocusChangeListener(onFocusChangeListener);
        et_stroke_width.setOnFocusChangeListener(onFocusChangeListener);
        et_r_l_b.setOnFocusChangeListener(onFocusChangeListener);
        et_r_l_t.setOnFocusChangeListener(onFocusChangeListener);
        et_r_r_b.setOnFocusChangeListener(onFocusChangeListener);
        et_r_r_t.setOnFocusChangeListener(onFocusChangeListener);
        cb_self.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateView();
            }
        });
        rg_back.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_pic:
                        rctv.setBackgroundResource(R.drawable.wolverine);
                        rctv.setSolidColor(Color.TRANSPARENT);
                        break;
                    case R.id.rb_color:
                        rctv.setBackgroundDrawable(null);
                        rctv.setSolidColor(getResources().getColorStateList(R.color.solid_color_selector));
                        break;
                }
            }
        });
        sp_shape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rctv.setShape(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_back_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rctv.setBackgroundFitType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_scale_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rctv.setScaleType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cb_self.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rctv.setSelfRoundCorner(isChecked);
            }
        });
    }
    private void updateView() {
        rctv.setStrokeWidth(getNumber(et_stroke_width));
        rctv.setRadius(getNumber(et_radius));
        rctv.setRadius(getNumber(et_r_l_t),getNumber(et_r_r_t),getNumber(et_r_r_b),getNumber(et_r_l_b));
    }

    private int getNumber(EditText et) {
        int s=0;
        String str=et.getText().toString().trim();
        if (str != null && !"".equals(str)) {
            try {
                s=Integer.parseInt(str);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
        return s;
    }
}
