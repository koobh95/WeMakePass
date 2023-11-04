package com.example.wemakepass.view.auth.findAccount.id;

import android.text.TextUtils;
import android.view.View;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.repository.MailRepository;
import com.example.wemakepass.util.UserInfoUtil;

/**
 * FindUserIdFragment의 ViewModel 클래스
 *
 * @author BH-Ku
 * @since 2023
 */
public class FindUserIdViewModel extends BaseViewModel {
    private SingleLiveEvent<String> emailLiveData;
    private MailRepository mailRepository;
    private UserInfoUtil userUtil;

    public FindUserIdViewModel() {
        mailRepository = new MailRepository(getNetworkErrorLiveData());
        userUtil = new UserInfoUtil();
    }

    public void onSendMailButtonClick(View view){
        final String email = emailLiveData.getValue();
        if(TextUtils.isEmpty(email)){
            systemMessageLiveData.setValue("이메일을 입력해주세요.");
            return;
        } else if (!email.matches("^\\w+@\\w+\\.\\w+(\\.\\w+)?$")) { // email format
            systemMessageLiveData.setValue("이메일 형식을 확인해주세요.");
            return;
        }

        addDisposable(mailRepository.sendForgetUserIdMail(email));
}

    public SingleLiveEvent<String> getEmailLiveData() {
        if(emailLiveData == null)
            emailLiveData = new SingleLiveEvent<>();
        return emailLiveData;
    }

    public SingleLiveEvent<Boolean> getIsSendMailLiveData() {
        return mailRepository.getIsSendMailLiveData();
    }
}
