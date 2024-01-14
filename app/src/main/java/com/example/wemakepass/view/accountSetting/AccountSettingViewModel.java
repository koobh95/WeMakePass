package com.example.wemakepass.view.accountSetting;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.network.api.UserAPI;
import com.example.wemakepass.repository.UserRepository;

/**
 * AccountSettingFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2024-01-14
 */
public class AccountSettingViewModel extends BaseViewModel {
    private final UserRepository userRepository;

    public AccountSettingViewModel() {
        userRepository = new UserRepository(getNetworkErrorLiveData());
    }

    // 로그아웃 요청을 발생시킨 뒤 기기에 저장된 인증 정보를 초기화한다.
    public void logout() {
        addDisposable(userRepository.requestLogout());
        AppConfig.AuthPreference.initTokenData();
        AppConfig.UserPreference.initUserData();
    }
}
