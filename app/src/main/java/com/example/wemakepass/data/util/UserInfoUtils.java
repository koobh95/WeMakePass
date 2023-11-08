package com.example.wemakepass.data.util;

import java.util.Locale;

/**
 * - User 데이터 관련 검증을 수행하는 유틸리티 클래스다.
 * - 아이디, 비밀번호, 닉네임, 메일 등을 검증한다.
 *
 * @author BH-Ku
 * @since 2023-11-04
 */
public class UserInfoUtils {
    private IdValidator idValidator;
    private PasswordValidator passwordValidator;
    private NicknameValidator nicknameValidator;
    private EmailValidator emailValidator;

    private final String FORMAT_RANGE_ERROR = "%d자 이상 %d자 이하여야 합니다.";

    /**
     * - Id를 검증할 때 사용할 IdValidator를 반환하는 메서드다.
     * - 이 메서드를 포함하여 아래 패스워드, 닉네임, 메일 관련 검증 객체를 한 화면에서 모두 사용하지 않을 수도
     *  있기 때문에 초기에 모두 할당하지 않고 필요에 의해 호출될 경우에만 객체를 할당하도록 하였다.
     *
     * @return
     */
    public IdValidator idValidator() {
        if(idValidator == null)
            idValidator = new IdValidator();
        return idValidator;
    }

    /**
     * Password를 검증할 때 사용할 IdValidator를 반환하는 메서드다.
     *
     * @return
     */
    public PasswordValidator passwordValidator() {
        if(passwordValidator == null)
            passwordValidator = new PasswordValidator();
        return passwordValidator;
    }

    /**
     * Nickname을 검증할 때 사용할 IdValidator를 반환하는 메서드다.
     *
     * @return
     */
    public NicknameValidator nicknameValidator() {
        if(nicknameValidator == null)
            nicknameValidator = new NicknameValidator();
        return nicknameValidator;
    }
    /**
     * Email을 검증할 때 사용할 IdValidator를 반환하는 메서드다.
     *
     * @return
     */
    public EmailValidator emailValidator() {
        if(emailValidator == null)
            emailValidator = new EmailValidator();
        return emailValidator;
    }

    /**
     * ID 관련 검증 메서드, 에러 메시지를 가지는 클래스.
     */
    public class IdValidator {
        private final int MIN_LEN = 6;
        private final int MAX_LEN = 20;

        public final String ERR_MSG_FORMAT = "숫자 혹은 영문 대소문자만 가능합니다.";
        public final String ERR_MSG_RANGE = String.format(Locale.KOREA, FORMAT_RANGE_ERROR, MIN_LEN, MAX_LEN);
        public final String ERR_MSG_EMPTY = "아이디를 입력해주세요.";

        // Check ID Format
        public boolean isValidFormat(String id) {
            return id.matches("^[\\d|a-zA-Z]*$");
        }

        // Check range
        public boolean inRange(int idLen) {
            return idLen >= MIN_LEN && idLen <= MAX_LEN;
        }
    }

    /**
     * Password 관련 검증 메서드, 에러 메시지를 가지는 클래스.
     */
    public class PasswordValidator {
        private final int MIN_LEN = 10;
        private final int MAX_LEN = 20;

        public final String ERR_MSG_FORMAT = "숫자, 영문, 특수 문자(!@#$%^&*)만 사용 가능합니다.";
        public final String ERR_MSG_CONDITIONS = "숫자, 영문, 특수 문자를 모두 포함해야 합니다.";
        public final String ERR_MSG_EMPTY = "비밀번호를 입력해주세요.";
        public final String ERR_MSG_RANGE = String.format(Locale.KOREA, FORMAT_RANGE_ERROR, MIN_LEN, MAX_LEN);

        // 외부에서 바로 호출할 수 있도록 public 으로 설정.
        public static final String ERR_MSG_MISMATCH = "비밀번호가 서로 다릅니다.";

        // Check Password Format
        public boolean isValidFormat(String password) {
            return password.matches("^[\\d|가-힣|a-zA-Z|!@#$%^&*]*$");
        }

        // Check Password Conditions
        public boolean isValidConditions(String password) {
            return password.matches("^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).*$");
        }

        // Check range
        public boolean inRange(int passwordLen) {
            return passwordLen >= MIN_LEN && passwordLen <= MAX_LEN;
        }
    }

    /**
     * Nickname 관련 검증 메서드, 에러 메시지를 가지는 클래스.
     */
    public class NicknameValidator {
        private final int MIN_LEN = 2;
        private final int MAX_LEN = 10;

        public final String ERR_MSG_FORMAT = "한글, 숫자, 영문 대소문자만 입력 가능합니다.";
        public final String ERR_MSG_EMPTY = "닉네임을 입력해주세요.";
        public final String ERR_MSG_RANGE = String.format(Locale.KOREA, FORMAT_RANGE_ERROR, MIN_LEN, MAX_LEN);

        // Check Nickname Format
        public boolean isValidFormat(String nickname) {
            return nickname.matches("^[\\d|가-힣|a-zA-Z]*$");
        }

        // Check range
        public boolean inRange(int nicknameLen) {
            return nicknameLen >= MIN_LEN && nicknameLen <= MAX_LEN;
        }
    }

    /**
     * Email 관련 검증 메서드, 에러 메시지를 가지는 클래스.
     */
    public class EmailValidator {
        public final String ERR_MSG_FORMAT = "이메일 형식을 확인해주세요.";
        public final String ERR_MSG_EMPTY = "이메일을 입력해주세요.";

        // Check Email Format
        public boolean isValidFormat(String email) {
            return email.matches("^\\w+@\\w+\\.\\w+(\\.\\w+)?$");
        }
    }
}
