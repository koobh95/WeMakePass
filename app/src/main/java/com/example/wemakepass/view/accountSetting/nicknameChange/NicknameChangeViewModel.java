package com.example.wemakepass.view.accountSetting.nicknameChange;

import android.text.TextUtils;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.repository.UserRepository;
import com.example.wemakepass.data.util.UserInfoUtils;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * NicknameChangeFragment의 ViewModel 클래스
 *
 * @author BH-Ku
 * @since 2023-11-06
 */
public class NicknameChangeViewModel extends BaseViewModel {
    private SingleLiveEvent<String> nicknameLiveData;
    private Disposable nicknameChangeDisposable;

    private final UserRepository userRepository;
    private final UserInfoUtils userInfoUtil;

    public NicknameChangeViewModel() {
        userRepository = new UserRepository(getNetworkErrorLiveData());
        userInfoUtil = new UserInfoUtils();
    }

    /**
     * - 닉네임 변경 버튼이 선택되면 호촐된다.
     * - 이미 요청이 진행 중인지 확인하고 아니라면 입력된 닉네임의 유효성을 검사한 후 요청을 보낸다.
     * - EditText를 제어하기 위해 bolean값을 반환한다.
     *
     * @return true : 요청을 보냄, false : 요청을 보내는데 실패함.
     */
    public boolean changeNickname(){
        if(nicknameChangeDisposable != null && !nicknameChangeDisposable.isDisposed()) {
            systemMessageLiveData.setValue("처리 중입니다.");
            return false;
        }

        if(!isValidNickname())
            return false;

        addDisposable(nicknameChangeDisposable =
                userRepository.requestNicknameChange(nicknameLiveData.getValue()));
        return true;
    }

    /**
     * 입력된 닉네임에 대해 다음과 같이 유효성을 검증한다.
     *  1. 공백이 아닌가?
     *  2. 형식에 맞게 입력되었는가?
     *  3. 길이가 범위 내인가?
     *   - 최대 범위는 TextInputEditText에서 속성으로 제어하고 있기에 사실상 최소 길이를 체크한다.
     *  4. 현재 사용 중인 닉네임과 같은 닉네임인가?
     *
     * @return true = 사용 가능한 닉네임, false = 사용할 수 없는 닉네임.
     */
    public boolean isValidNickname() {
        final String nickname = nicknameLiveData.getValue();
        UserInfoUtils.NicknameValidator validator = userInfoUtil.nicknameValidator();

        if(TextUtils.isEmpty(nickname)) {
            systemMessageLiveData.setValue(validator.ERR_MSG_EMPTY);
            return false;
        } else if(!validator.isValidFormat(nickname)) {
            systemMessageLiveData.setValue(validator.ERR_MSG_FORMAT);
            return false;
        } else if(!validator.inRange(nickname.length())) {
            systemMessageLiveData.setValue(validator.ERR_MSG_RANGE);
            return false;
        } else if(nickname.equals(AppConfig.UserPreference.getNickname())) {
            systemMessageLiveData.setValue("현재 사용 중인 닉네임과 같습니다.");
            return false;
        }

        return true;
    }

    public SingleLiveEvent<String> getNicknameLiveData() {
        if(nicknameLiveData == null)
            nicknameLiveData = new SingleLiveEvent<>();
        return nicknameLiveData;
    }

    public SingleLiveEvent<Boolean> getNicknameChangedLiveData() {
        return userRepository.getIsConfirmLiveData();
    }
}
