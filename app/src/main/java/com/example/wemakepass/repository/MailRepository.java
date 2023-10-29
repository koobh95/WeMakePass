package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.MailAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 메일과 관련된 네트워크 작업을 처리하는 레포지토리
 *
 * @author BH-Ku
 * @since 2023-07-10
 */
public class MailRepository extends BaseRepository {
    private SingleLiveEvent<Boolean> isSendMailLiveData, isConfirmLiveData;

    private final MailAPI mailAPI;

    private final String TAG = "TAG_MailRepository";

    public MailRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        mailAPI = WmpClient.getRetrofit().create(MailAPI.class);
    }

    /**
     * - 회원가입 후 본인 인증을 수행하기 위한 메일 발송 요청.
     * - 식별자로 아이디를 전송한다.
     *
     * @param userId 인증이 필요한 계정의 ID
     * @return
     */
    public Disposable sendAccountCertMail(String userId){
        return mailAPI.accountCert(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        isSendMailLiveData.setValue(true);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    /**
     * - 아이디 찾기 이메일 발송 요청.
     * - 식별자로 이메일을 전송할 경우 DB에서 이메일과 일치하는 데이터를 찾아 이메일로 아이디를 전송해준다.
     *
     * @param email
     * @return
     */
    public Disposable sendForgetUserIdMail(String email) {
        return mailAPI.forgetUserId(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        isSendMailLiveData.setValue(true);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    /**
     * - 비밀번호 찾기를 수행하기 위해 인증 코드가 담긴 이메일 전송 요청
     * - 비밀번호 찾기에 필요한 아이디를 서버로 전송한다.
     *
     * @param userId
     * @return
     */
    public Disposable sendForgetPasswordMail(String userId) {
        return mailAPI.forgetPassword(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        isSendMailLiveData.setValue(true);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    /**
     * - 인증 메일로 받은 인증 코드를 서버로 보내 일치 여부를 확인한다.
     * - 인증 식별자로서 User id를 사용한다.
     *
     * @param userId
     * @param code
     * @return
     */
    public Disposable certCodeConfirm(String userId, String code){
        return mailAPI.confirm(userId, code)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        isConfirmLiveData.setValue(true);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    public SingleLiveEvent<Boolean> getIsSendMailLiveData() {
        if(isSendMailLiveData == null)
            isSendMailLiveData = new SingleLiveEvent<>();
        return isSendMailLiveData;
    }

    public SingleLiveEvent<Boolean> getIsConfirmLiveData() {
        if(isConfirmLiveData == null)
            isConfirmLiveData = new SingleLiveEvent<>();
        return isConfirmLiveData;
    }
}
