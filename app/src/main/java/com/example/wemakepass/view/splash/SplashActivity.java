package com.example.wemakepass.view.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.util.MessageUtil;
import com.example.wemakepass.view.auth.AuthActivity;
import com.example.wemakepass.view.main.MainActivity;

/**
 *  어플리케이션 실행 시 최초 실행되는 SplashActivity다. 여기서는 최소 1초간 Splash 화면을 유지한 뒤
 * 자동 로그인, 토큰의 유효성 등을 근거로 화면의 분기를 결정한다. 유저가 토큰을 가지고 있고 또한 유효할 경우
 * MainActivity로 분기하지만 그게 아닐 경우 AuthActivity로 분기한다.
 *
 * @author BH-Ku
 * @since 2023-10-31
 */
public class SplashActivity extends AppCompatActivity {
    private SplashViewModel viewModel;

    private final int SPLASH_TIME_OUT = 1000; // 최소 유지 시간
    private final String TAG = "TAG_SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        startHandler();
        initObserver();
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        /**
         * 토큰 재발급에 성공한 경우
         */
        viewModel.getIsReissueLiveData().observe(this, aBoolean ->{
            Log.d(TAG, "유효한 토큰입니다. 홈 화면으로 이동합니다.");
            startMainActivity();
        });

        /**
         * 토큰 재발급에 실패한 경우
         */
        viewModel.getNetworkErrorLiveData().observe(this, errorResponse -> {
            Log.d(TAG, "유효하지 않은 토큰입니다. 로그인 화면으로 이동합니다.");
            MessageUtil.showToast(this, errorResponse.getMessage());
            startAuthActivity();
        });
    }

    /**
     *  Splash 화면을 유지하기 위해 1초의 딜레이를 가지고 Handler를 실행한다. Handelr가 실행되면 로그인 여부를
     * 확인하는 메서드를 실행한다.
     */
    private void startHandler() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::loginCheck, SPLASH_TIME_OUT);
    }

    /**
     * - 시작될 Activity를 결정한다.
     * - AutoLogin이 설정되어 있지 않을 경우 Token 보유 여부와 상관없이 로그인 화면으로 이동한다.
     * - AutoLogin이 설정되어 있을 경우 Token의 유무를 확인하여 존재하지 않을 경우 로그인 화면으로 이동한다.
     * - 자동 로그인, 토큰 여부가 확인되었을 경우 서버로 RefreshToken을 전송하여 인증을 시도한다. 사실상 재발급
     *  작업이며 토큰 발급에 성공할 경우 MainActivity, 실패할 경우 AuthActivity를 실행한다.
     */
    private void loginCheck() {
        if(!AppConfig.AuthPreference.isKeepLogin() ||
                AppConfig.AuthPreference.getAccessToken().equals("")) {
            startAuthActivity();
            return;
        }
        Log.d(TAG, "사용자의 로그인 정보가 유효한지 판단하기 위해 토큰 검증을 시작합니다.");
        viewModel.tokenValidationCheck();
    }

    /**
     * AuthActivity를 실행하고 현재 화면은 종료한다.
     */
    private void startAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    /**
     * MainActivity를 실행하고 현재 화면은 종료한다.
     */
    private void startMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}