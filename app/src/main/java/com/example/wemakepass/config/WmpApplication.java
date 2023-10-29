package com.example.wemakepass.config;

import android.app.Application;

/**
 * 어플리케이션 내의 모든 클래스에서 공용으로 사용할 데이터들을 초기화하는 클래스다.
 *
 * @author BH-Ku
 * @since 2023-10-29
 */
public class WmpApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new AppConfig(getApplicationContext());
    }
}
