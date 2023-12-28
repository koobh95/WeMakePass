package com.example.wemakepass.data.enums;

/**
 * - 서버에 요청한 결과가 정상적인 응답(200번대)이 아닌 경우 어떤 에러가 발생했는지 그 에러에 대한 값들을 가지고
 * 있다. 여기 있는 값들은 일부를 제외하면 서버도 가지고 있는 값들이며 동일하게 ErrorCode라는 enum으로 다루고 있다.
 * <p>
 * - 서버의 ErrorCode enum은 Error Message도 같이 다루고 있다. 여기서 에러 메시지를 다루지 않는 이유는 양쪽
 * 다 메시지에 대한 데이터를 가지고 있을 경우 메시지가 수정되었을 때 양쪽에서 업데이트가 필요하기에 불일치가 발생할
 * 수도 있다고 생각했기 때문이다. 따라서 에러 메시지는 서버측에서 ErrorCode에 저장하고 있다가 ErrorResponse에
 * 담아서 클라이언트로 전송하고 있다.
 * <p>
 * - 클라이언트에서만 다루는 값으로 다음 값들이 있다.
 * WMP_UNKNOWN_ERROR : 서버에서 데이터를 처리하다가 예외가 발생하여 ErrorResponse를 보내왔지만 파싱에 실패
 * 한 경우 이 코드를 세팅한다. 일반적으로 비지니스 로직 처리 과정에서 상정하지 못한 Exception이 발생한 경우에
 * 해당한다.
 * CONNECTION_FAILED : 서버와 통신을 시도하였지만 통신에 실패한 경우 이 코드를 세팅한다.
 * AUTHENTICATION_FAILED : RefershToken 발급에 실패했을 경우 이 코드를 세팅한다.
 *
 * @author BH-Ku
 * @since 2023-05-17
 */
public enum ErrorCode {
    PASSWORD_MISMATCH, // 비밀번호를 통한 인증을 시도했을 때 입력한 비밀번호와 데이터베이스 상의 비밀번호가 불일치하는 경우
    WITHDRAW_ACCOUNT, // 인증 및 접근을 시도한 유저가 탈퇴한 유저일 경우
    UNCERT_USER, // 이메일 인증이 이루어지지 않은 유저일 경우

    USER_ID_DUPLICATED, // 회원가입 시 입력한 아이디가 중복인 경우
    NICKNAME_DUPLICATED, // 회원가입, 닉네임 변경 등을 시도했을 때 이미 사용 중인 닉네임이 존재하는 경우
    MAIL_DUPLICATED, // 회원가입 시 입력한 이메일이 중복일 경우
    MAIL_NOT_FOUND, //
    CERT_CODE_MISMATCH, // 이메일 인증 시 코드가 다른 경우

    PASSWORD_PREVIOUSLY_USED, // 변경하려는 비밀번호가 이전에 사용 중인 비밀번호가 일치하는 경우

    // Common
    WMP_UNKNOWN_ERROR, // Wmp에서 발생한 알 수 없는 에러
    CONNECTION_FAILED, // 네트워크 통신 실패

    // JWT
    EXPIRED_ACCESS_TOKEN, // AccessToken 발급 실패, 토큰이 만료되었음.
    INVALID_ACCESS_TOKEN, // AccessToken 발급 실패, 서버에서 발급한 토큰이 아니거나 정상적인 접근이 아님.
    INVALID_REFRESH_TOKEN, // RefreshToken으로 토큰 재발급 실패

    // BOARD
    BOARD_CATEGORY_LOADING_FAILED, // 게시판 카테고리 조회 실패
    POST_LIST_LOADING_FAILED, // 게시판 게시글 조회 실패

    // Post Viewer
    POST_LOADING_FAILED_POST_DELETED, // 게시글 삭제로 인한 게시글 조회 실패
    POST_LOADING_FAILED_NETWORK_ERROR, // 네트워크 오류로 인한 게시글 조회 실패

    // REPLY
    REPLY_WRITE_FAILED_POST_DELETED, // 게시글 삭제로 인한 댓글(답글) 작성 실패
    REPLY_WRITE_FAILED_PARENT_REPLY_DELETED, // 상위 댓글 삭제로 인한 답글 작성 실패
    REPLY_DELETE_FAILED_POST_DELETED, // 게시글 삭제로 인한 댓글(답글) 작성 실패
    REPLY_LOADING_FAILED_POST_DELETED, // 게시글 삭제로 인한 댓글 목록 조회 실패
}
