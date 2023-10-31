package com.example.wemakepass.network.client.interceptor;

import android.text.TextUtils;
import android.util.Log;

import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.repository.JwtRepository;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * - Access Token을 Request Header에 포함시키는 작업, Response의 Http Status code가 401인지(인증 오류)
 * 확인하여 토큰이 만료되었다면 재요청을 보내는 작업을 수행한다.
 *
 * @author BH-Ku
 * @since 2023-10-30
 * @return
 */
public class WmpInterceptor implements Interceptor {
    private static JwtRepository jwtRepository;

    private final int AUTH_FAILED_CODE = 401; // 인증 실패 코드
    private static final String TAG = "TAG_WmpInterceptor";
    private static final String TOKEN_TYPE = "Bearer ";

    /**
     * - Request, Response를 가로채서 추가적인 작업을 해주는 Interceptor의 핵심 메서드다.
     * - Request 관련 작업 : 여기서는 요청을 보낼 때 SharedPreferences에 저장된 AccessToken이 있는지
     *  확인하여 있다면 Header에 추가해주는 작업을 한다.
     * - Response 관련 작업 : Response의 Http Status Code가 401일 경우 AccessToken 만료로 인한 인증
     *  실패인지 확인할 필요가 있다. 여기서 Response 객체는 한 번만 사용할 수 있기에 Header와 Body를 미리
     *  저장해놓은 뒤 ErrorCode(서버에서 커스텀한 문자열 코드)를 확인하여 AccessToken 만료인지 확인한다.
     *  AccessToken 만료가 아닐 경우 Response를 새로 생성하여 반환하고 만료일 경우 동기로 재발급을 요청한다.
     *  새로운 토큰이 정상적으로 발급되었을 경우 내부에 저장하고 실패했던 이전 요청을 다시 보내게 되지만 정상적으로
     *  발급받지 못한 경우 정상적인 인증 방식이 아니라고 판단, 내부에 존재하는 토큰을 모두 삭제한 뒤 적절한 에러
     *  코드를 새로 세팅하여 반환한다. 기기에서 가진 토큰을 삭제하는 이유는 암호화하여 저장하고 있던 토큰이 요청을
     *  보낼 때 복호화하여 보내졌기 때문에 원래 RefreshToken이 외부에 노출될 가능성이 있기 때문이다. 인증에서
     *  에러가 생긴 시점에서 서버 DB가 가진 RefreshToken도 삭제되었기 때문에 보안상 문제가 없다.
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken = AppConfig.AuthPreference.getAccessToken();
        Request request = chain.request();

        if (!TextUtils.isEmpty(accessToken)) { // Token을 가지고 있음. 토큰 추가.
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", TOKEN_TYPE + accessToken)
                    .build();
        }

        Response response = chain.proceed(request);

        if(response.code() == AUTH_FAILED_CODE){ // 401 에러일 경우
            Response.Builder newResponse = response.newBuilder();
            String contentType = response.header("Content-Type");
            if(TextUtils.isEmpty(contentType))
                contentType = "application/json";
            String responseBodyStr = response.body().string(); // 기존 Response의 Header, body Backup
            String errorCode = parseErrorCode(responseBodyStr); // ErrorResponse에 담긴 ErrorCode 확인.

            // 401 에러의 에러 메시지가 AccessToken 인증 실패인지 확인.
            if(errorCode.equals(ErrorCode.JWT_INVALID_ACCESS_TOKEN.name())) { // AccessToken 검증 실패가 맞는지 확인.
                retrofit2.Response<JwtDTO> jwtReissueResponse = requestTokenReissue(); // 동기로 재발급 요청 수행.

                if(jwtReissueResponse.isSuccessful()) { // 정상적으로 발급되었음.
                    final JwtDTO jwtDTO = jwtReissueResponse.body();
                    AppConfig.AuthPreference.setAccessToken(jwtDTO.getAccessToken());
                    AppConfig.AuthPreference.setRefreshToken(jwtDTO.getRefreshToken()); // 토큰 저장
                    return chain.proceed(chain.request()
                            .newBuilder()
                            .addHeader("Authorization", TOKEN_TYPE + jwtDTO.getAccessToken())
                            .build()); // 새로 발급된 토큰을 사용하여 기존 요청 새로 요청
                }

                // 재발급 과정에서 인증 문제가 생겼음. RefreshToken은 이미 노출되었으므로 가진 인증 코드 삭제.
                // 이미 서버에서도 인증 데이터가 삭제되었음.
                AppConfig.AuthPreference.setAccessToken("");
                AppConfig.AuthPreference.setRefreshToken("");
                return newResponse
                        .body(ResponseBody.create(MediaType.parse(contentType),
                                createAuthenticationFailedErrorBodyString()))
                        .build(); // 인증 실패 코드 세팅
            }
            return newResponse
                    .body(ResponseBody.create(MediaType.parse(contentType), responseBodyStr))
                    .build(); // 401 에러지만 AccessToken 검증 실패로 인한 오류는 아님. 기존 Response를 다시 조립.
        }
        return response; // 정상적인 응답일 수도 있고 Error 응답일 수도 있지만 최소한 401 에러는 아님.
    }

    /**
     * - jwtRepository에서 재발급 요청을 실행한다.
     * - 여기서 jwtRepository 초기화 작업을 수행하는 이유는 멤버 변수 혹은 생성자에서 초기화할 경우 WmpClient와
     *  순환 참조 오류가 발생하기 때문이다.
     *
     * @return
     */
    private static retrofit2.Response<JwtDTO> requestTokenReissue() throws IOException {
        if(jwtRepository == null)
            jwtRepository = new JwtRepository();
        return jwtRepository.syncReissueToken().execute();
    }

    /**
     *  Parsing 과정에서 오류가 발생할 시 null을 반환, null은 여기서 처리하지 않고 repository에서 처리한다.
     * repository에서 알 수 없는 에러에 대해서도 따로 처리하고 있기 때문에 문제없다.
     *
     * @param responseBodyStr
     * @return
     */
    private static String parseErrorCode(String responseBodyStr) {
        try {
            JSONObject json = new JSONObject(responseBodyStr);
            return json.getString("code");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *  유저가 토큰 재발급에 실패하는 사유는 RefreshToken이 만료되었거나 데이터가 변조되었다는 것을 의미하는데
     * 후자의 일반 사용자한테 발생할 수 있는 오류가 아니기 때문에 사용자가 알 필요가 없다고 생각한다. 또한 상대가
     * 일반 사용자가 아니라면 어떤 사유로 로그인에 실패했는지 알려줄 필요는 더더욱 없다.
     *  서버에서는 재발급 실패에 관한 Code를 다양하게 다루고 있지만 여기서는 일관된 메시지를 보여주도록 할 것이다.
     * 물론 공격자가 response body를 열어 보면 에러를 알 수 있겠지만 이 부분은 추후 서버 측의 코드를 수정하여
     * 보완해 줄 예정이다.
     *
     * @return
     */
    private static String createAuthenticationFailedErrorBodyString() {
        try {
            JSONObject json = new JSONObject();
            json.put("code", ErrorCode.AUTHENTICATION_FAILED.name());
            json.put("message", ErrorResponse.AUTHENTICATION_FAILED_MESSAGE);
            return json.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
