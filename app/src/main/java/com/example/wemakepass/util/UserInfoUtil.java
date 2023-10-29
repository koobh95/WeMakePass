package com.example.wemakepass.util;

import android.text.TextUtils;

import com.example.wemakepass.common.SingleLiveEvent;

/**
 * - User 데이터 관련 검증, 비교 등의 작업을 수행하는 유틸리티 클래스다.
 * - 아이디 형식, 비밀번호 형식, 닉네임 형식, 메일 형식 등을 검증한다.
 * - ErrorMessage를 즉시 라이브 데이터에 세팅할 수 있도록 파라미터로 받고 있기 때문에 사용할 수 있는 곳이
 *  한정적이다. SignUpFragment, PasswordResetActivity 등에서 사용된다.
 *
 * @author BH-Ku
 * @since 2023-06-05
 */
public class UserInfoUtil {
    /**
     * ID 형식을 검증한다.
     *
     * 검증 조건
     * 1. ID Null, Empty 여부
     * 2. a-z, A-Z, 0~9 외에는 포함될 수 없음.
     * 3. 6자 이상 20자 이하
     *
     * @param id 검증할 값
     * @param errMsgLiveData TextInputLayout에 표시될 에러 메시지 라이브 데이터 참조
     * @return
     */
    public boolean isValidId(String id, SingleLiveEvent<String> errMsgLiveData) {
        if(TextUtils.isEmpty(id)) {
            errMsgLiveData.setValue("아이디를 입력해주세요.");
            return false;
        } else if (!id.matches("^[\\d|a-zA-Z]*$")){
            errMsgLiveData.setValue("숫자 혹은 영문 대소문자만 가능합니다.");
            return false;
        } else if(id.length() < 6){
            errMsgLiveData.setValue("6자 이상 20자 이하로 입력해주세요.");
            return false;
        }

        errMsgLiveData.setValue("");
        return true;
    }

    /**
     * Nickname 형식을 검증한다.
     *
     * 검증 조건
     * 1. Nickname Null, Empty 여부
     * 2. 한글, 숫자, 영문 외에는 포함될 수 없음.
     * 3. 문자열 길이 4바이트 이상, 16바이트 이하(한글은 2byte, 영어는 1byte이기 때문에 length가 아닌 byte로 검사)
     *
     * @param nickname 검증할 값
     * @param errMsgLiveData TextInputLayout에 표시될 에러 메시지 라이브 데이터 참조
     * @return
     */
    public boolean isValidNickname(String nickname, SingleLiveEvent<String> errMsgLiveData) {
        if(TextUtils.isEmpty(nickname)) {
            errMsgLiveData.setValue("닉네임을 입력해주세요.");
            return false;
        } else if (!nickname.matches("^[\\d|가-힣|a-zA-Z]*$")) {
            errMsgLiveData.setValue("한글, 숫자, 영문 대소문자만 입력 가능합니다.");
            return false;
        } else if(nickname.getBytes().length < 4) {
            errMsgLiveData.setValue("길이가 너무 짧습니다.");
            return false;
        }

        errMsgLiveData.setValue("");
        return true;
    }

    /**
     * Password 형식을 검증한다.
     *
     * 검증 조건
     * 1. Password Null, Empty 여부
     * 2. 숫자, 영어(a-z, A-Z), 특수문자(!@#$%^&*) 외에는 포함될 수 없음.
     * 3. 최소 10자 이상, 최대 길이는 코드로 제한.
     * 4. 문자열에 숫자, 영어, 특수 문자 중 2개 이상 혼합
     *
     * @param password 검증할 값
     * @param errMsgLiveData TextInputLayout에 표시될 에러 메시지 라이브 데이터 참조
     * @return
     */
    public boolean isValidPassword(String password, SingleLiveEvent<String> errMsgLiveData) {
        if(TextUtils.isEmpty(password)){
            errMsgLiveData.setValue("비밀번호를 입력해주세요.");
            return false;
        } else if(password.length() < 10){
            errMsgLiveData.setValue("10자 이상 20자 이하입니다.");
            return false;
        } else if(!password.matches("^[\\d|가-힣|a-zA-Z|!@#$%^&*]*$")){
            errMsgLiveData.setValue("숫자, 영문, 특수 문자(!@#$%^&*)만 사용 가능합니다.");
            return false;
        } else if(!password.matches("^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).*$")) {
            errMsgLiveData.setValue("숫자, 영문, 특수 문자를 모두 포함해야 합니다.");
            return false;
        }

        errMsgLiveData.setValue("");
        return true;
    }

    /**
     * 패드워드가 같은지 확인
     *
     * @param password 검증할 값 1
     * @param passwordRe 검증할 값 2
     * @param errMsgLiveData TextInputLayout에 표시될 에러 메시지 라이브 데이터 참조
     * @return
     */
    public boolean passwordEquals(String password, String passwordRe,
                                  SingleLiveEvent<String> errMsgLiveData) {
        if(!password.equals(passwordRe)){
            errMsgLiveData.setValue("비밀번호가 서로 다릅니다.");
            return false;
        }

        errMsgLiveData.setValue("");
        return true;
    }

    /**
     * Email의 형식을 검증한다.
     *
     * 검증 조건
     * 1. Email Null, Empty 여부
     * 2. 이메일을
     *
     * @param email 검증할 값
     * @param errMsgLiveData TextInputLayout에 표시될 에러 메시지 라이브 데이터 참조
     * @return
     */
    public boolean isValidEmail(String email, SingleLiveEvent<String> errMsgLiveData) {
        if(TextUtils.isEmpty(email)){
            errMsgLiveData.setValue("이메일을 입력해주세요.");
            return false;
        } else if (!email.matches("^\\w+@\\w+\\.\\w+(\\.\\w+)?$")) { // email format
            errMsgLiveData.setValue("이메일 형식을 확인해주세요.");
            return false;
        }

        errMsgLiveData.setValue("");
        return true;
    }
}
