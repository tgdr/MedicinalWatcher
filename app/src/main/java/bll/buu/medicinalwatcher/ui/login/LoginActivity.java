package bll.buu.medicinalwatcher.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import bll.buu.medicinalwatcher.MedApplication;
import bll.buu.medicinalwatcher.R;
import bll.buu.medicinalwatcher.SecondActivity;
import bll.buu.medicinalwatcher.activity.NavigationActivity;
import bll.buu.medicinalwatcher.activity.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Button bt_login_register;
    MedApplication application;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bt_login_register = findViewById(R.id.bt_login_register);
        application = (MedApplication) getApplication();
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, 0x0016);
}

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        bt_login_register.setOnClickListener(view -> {
            Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(it);
        });
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                if(usernameEditText.getText().toString()!=null &&  !usernameEditText.getText().toString().equals("") && passwordEditText.getText().toString()!=null && !passwordEditText.getText().toString().equals("")){
                    loginButton.setBackgroundResource(R.drawable.bg_login_submit);
                    loginButton.setTextColor(getResources().getColor(R.color.white));
                    loginButton.setClickable(true);
                }
                else {
                    loginButton.setBackgroundResource(R.drawable.bg_login_submit_lock);
                    loginButton.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                    loginButton.setClickable(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    loadingProgressBar.setVisibility(View.VISIBLE);
                if(usernameEditText.getText().toString().equals("liangl")&&passwordEditText.getText().toString().equals("123456")){
                    Intent it =new Intent(LoginActivity.this, NavigationActivity.class);
                    startActivity(it);
                    finish();
            }
                else if(application.getSpf().getString("userName","1")!=null && application.getSpf().getString("userName","1").equals(usernameEditText.getText().toString()) &&
                application.getSpf().getString("passWord","1")!=null && application.getSpf().getString("passWord","1").equals(passwordEditText.getText().toString())){
                    Intent it =new Intent(LoginActivity.this, NavigationActivity.class);
                    startActivity(it);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"用户名或密码输入错误！",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
