package com.example.wemakepass.network.client.interceptor;

import android.text.TextUtils;

import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.data.model.dto.JwtDTO;
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

    private final int AUTHENTICATION_FAILED_CODE = 401; // 인증 실패 코드
    private static final String TAG = "TAG_WmpInterceptor";
    private static final String TOKEN_TYPE = "Bearer ";

    /**
     * - Request, Response를 가로채서 추가적인 작업을 해주는 Interceptor의 핵심 메서드다.
     * - Request 관련 작업 : 요청을 보낼 때 SharedPreferences에 저장된 AccessToken이 있는지 확인하여 있다면
     *  Header에 추가한다.
     * - Response 관련 작업 : Response의 Http Status Code가 401일 경우 AccessToken 만료로 인한 인증
     *  실패인지 확인할 필요가 있다. 여기서 Response 객체는 한 번만 사용할 수 있기에 Header와 Body를 복사해놓은
     *  뒤 ErrorCode(서버에서 커스텀한 문자열 코드)를 확인하여 AccessToken 만료인지 확인한다.
     *   만료가 아닌 경우 전송한 토큰이 정상적이지 않다고 판단하고 클라이언트가 가진 토큰 정보를 모두 초기화하여
     *  다시 로그인하도록 유도한다. 반면 AccessToken 만료일 경우 동기로 RefreshToken으로 토큰 재발급 요청을
     *  보내고 성공할 경우 기존 요청을 수행한다. 어떤 이유에서든 토큰 재발급에 실패할 경우 암호화된 채로 가지고 있던
     *  RefreshToken이 복호화되어 외부에 노출되었기 때문에 클라이언트가 가진 토큰 정보를 모두 삭제한다. (재발급에
     *  실패한 시점에서 서버가 가진 인증 정보도 삭제된다.)
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = addHeaderToAccessToken(chain);
        Response response = chain.proceed(request);

        if(response.code() == AUTHENTICATION_FAILED_CODE) { // 401 에러일 경우, JWT 인증 에러인지 확인이 필요함.
            Response.Builder newResponse = response.newBuilder();
            String contentType = response.header("Content-Type");

            if (TextUtils.isEmpty(contentType))
                contentType = "application/json";
            String responseBodyStr = response.body().string(); // 기존 Response의 Header, body Backup
            String errorCode = parseErrorCode(responseBodyStr); // ErrorResponse에 담긴 ErrorCode 확인.

            // 401 에러의 에러 메시지가 AccessToken 인증 실패인지 확인.
            if (errorCode.equals(ErrorCode.EXPIRED_ACCESS_TOKEN.name())) {
                // AccessToken이 만료되었음. -> 재발급
                retrofit2.Response<JwtDTO> jwtReissueResponse = requestTokenReissue();

                if (jwtReissueResponse.isSuccessful()) {
                    // 정상적으로 재발급되었음.
                    final JwtDTO jwtDTO = jwtReissueResponse.body();
                    AppConfig.AuthPreference.setAccessToken(jwtDTO.getAccessToken());
                    AppConfig.AuthPreference.setRefreshToken(jwtDTO.getRefreshToken()); // 토큰 저장
                    return chain.proceed(chain.request()
                            .newBuilder()
                            .addHeader("Authorization", TOKEN_TYPE + jwtDTO.getAccessToken())
                            .build()); // 새로 발급된 토큰을 사용하여 기존 요청 새로 요청
                } else {
                    /**
                     * - 토큰 재발급 과정에서 문제 발생.(인증 오류, 네트워크 오류 등 모든 문제를 포함)
                     * - RefreshToken이 노출되었을 가능성이 존재하므로 기존에 가진 RefreshToken을 삭제. 토큰
                     *  재발급에 실패한 순간 서버에서도 DB에서 토큰 정보를 삭제한 상태.
                     */
                    AppConfig.AuthPreference.initTokenData();
                    return newResponse
                            .body(ResponseBody.create(MediaType.parse(contentType),
                                    jwtReissueResponse.errorBody().string()))
                            .build(); // 인증 실패 코드 세팅
                }
            } else if (errorCode.equals(ErrorCode.INVALID_ACCESS_TOKEN.name())) {
                // AccessToken 검증 실패 사유가 만료 이외의 사유 -> 보안에 문제가 있다고 판단, 토큰 정보 삭제
                AppConfig.AuthPreference.initTokenData();
            }

            return newResponse
                    .code(AUTHENTICATION_FAILED_CODE)
                    .body(ResponseBody.create(MediaType.parse(contentType), responseBodyStr))
                    .build(); // 401 이지만 AccessToken 만료로 인한 에러는 아님. 기존 Response 다시 조립.
        }

        return response; // 정상적인 응답일 수도 있고 Error 응답일 수도 있지만 최소한 401 에러는 아님.
    }

    /**
     * 클라이언트가 AccessToken을 가지고 있을 경우 Request Header에 추가한다.
     *
     * @param chain
     * @return
     */
    private Request addHeaderToAccessToken(Chain chain) {
        String accessToken = AppConfig.AuthPreference.getAccessToken();
        Request request = chain.request();
        if (!TextUtils.isEmpty(accessToken)) { // Token을 가지고 있음. 토큰 추가.
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", TOKEN_TYPE + accessToken)
                    .build();
        }
        return request;
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
}
