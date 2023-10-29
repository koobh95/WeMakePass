package com.example.wemakepass.view.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.wemakepass.R;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.databinding.ActivityMainBinding;
import com.example.wemakepass.view.auth.AuthActivity;
import com.example.wemakepass.view.auth.passwordReset.PasswordResetActivity;

/**
 * 이 화면이 실행되면서 수행하는 다음과 같다.
 * 1. 자동 로그인 여부 확인.
 *  - 로그인이 되어 있으면서 자동 로그인이 되어 있는 경우, Refresh 토큰이 만료되지 않은 경우 AuthActivity 실행
 *  - 로그인이 되어 있는 경우 HomeFragment 컨텐츠 로딩
 * 2.
 *
 * @author BH-Ku
 * @since  2023-10-25
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startActivity(new Intent(this, AuthActivity.class));
        //startActivity(new Intent(this, PasswordResetActivity.class)); // test
        initPublicObject();
    }

    private void initPublicObject() {
        new AppConfig(getApplicationContext());
    }

    private void loginCheck(){
        // KeepLogin 여부
        // Jwt 여부, 유효성 여부
    }
}