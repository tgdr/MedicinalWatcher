package bll.buu.medicinalwatcher.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.sql.SQLException;

import bll.buu.medicinalwatcher.MedApplication;
import bll.buu.medicinalwatcher.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class RegisterActivity extends AppCompatActivity implements TextWatcher {

    private static final String TAG = "RegisterActivity";
    @BindView(R.id.bt_register_submit)
    Button btn_reg_sugmit;
    @BindView(R.id.et_register_nickname)
    EditText edit_nick;

    @BindView(R.id.et_register_password)
    EditText edit_pass;

    @BindView(R.id.et_register_pwd_repeat)
    EditText edit_pass_re;
    MedApplication app;
    @BindView(R.id.ib_navigation_back)
    ImageView imgback;
    @BindView(R.id.cb_protocol)
    CheckBox cb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_register_step_one);
        app = (MedApplication) getApplication();
        ButterKnife.bind(this);

        edit_nick.addTextChangedListener(this);
        edit_pass.addTextChangedListener(this);
        edit_pass_re.addTextChangedListener(this);
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!TextUtils.isEmpty(edit_nick.getText().toString()) && !TextUtils.isEmpty(edit_pass.getText().toString()) && isChecked
                    && !TextUtils.isEmpty(edit_pass_re.getText().toString())) {
                btn_reg_sugmit.setBackgroundResource(R.drawable.bg_login_submit);
                btn_reg_sugmit.setTextColor(getResources().getColor(R.color.white));
                btn_reg_sugmit.setClickable(true);
            } else {
                btn_reg_sugmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                btn_reg_sugmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                btn_reg_sugmit.setClickable(false);
            }
        });
        btn_reg_sugmit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(edit_nick.getText().toString()) && !TextUtils.isEmpty(edit_pass.getText().toString()) && !TextUtils.isEmpty(edit_pass_re.getText().toString())) {
                if (edit_pass.getText().toString().equals(edit_pass_re.getText().toString())) {
                    app.setUsername(edit_nick.getText().toString());
                    app.getEditor().putString("userName", edit_nick.getText().toString()).commit();
                    app.getEditor().putString("passWord",edit_pass.getText().toString()).commit();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败，请检查输入的内容", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgback.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        //登录按钮是否可用
        if (!TextUtils.isEmpty(edit_pass.getText().toString()) && !TextUtils.isEmpty(edit_nick.getText().toString()) && !TextUtils.isEmpty(edit_pass.getText().toString()) && !TextUtils.isEmpty(edit_pass_re.getText().toString()) && cb.isChecked()) {
            btn_reg_sugmit.setBackgroundResource(R.drawable.bg_login_submit);
            btn_reg_sugmit.setTextColor(getResources().getColor(R.color.white));
            btn_reg_sugmit.setClickable(true);
        } else {
            btn_reg_sugmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            btn_reg_sugmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
            btn_reg_sugmit.setClickable(false);
        }
        if (edit_nick.getText().toString().length() == 3) {
            hideKeyboard(RegisterActivity.this);

        }
    }

    public static void hideKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
    }
}
