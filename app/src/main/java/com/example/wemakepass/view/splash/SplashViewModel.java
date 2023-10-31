package com.example.wemakepass.view.splash;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.repository.AuthRepository;

/**
 * SplashActivity의 ViewModel 클래스
 *
 * @author BH-Ku
 * @since 2023-10-31
 */
public class SplashViewModel extends BaseViewModel {
    private AuthRepository authRepository;

    public SplashViewModel() {
        authRepository = new AuthRepository(getNetworkErrorLiveData());
    }

    /**
     * JwtRepository에 토큰의 유효성을 확인을 요청한다.
     */
    public void tokenValidationCheck(){
        authRepository.tokenValidationCheck();
    }

    public SingleLiveEvent<Boolean> getIsReissueLiveData() {
        return authRepository.getIsReissueLiveData();
    }
}
