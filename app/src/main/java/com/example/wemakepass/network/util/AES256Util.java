package com.example.wemakepass.network.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wemakepass.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * - 네트워크 공격자에 의해 패킷이 탈취되었을 때 비밀번호같은 노출되면 안되는 데이터들을 암호화하여 전송하기 위한
 *  암호화 유틸 클래스다.
 * - AES 방식은 대칭키 방식이기 때문에 이 어플과 서버가 32byte 크기의 동일한 키를 가지고 있다.
 * - 암호화는 비단 회원가입, 로그인에서만 쓰이는 것이 아니고 공격자에게 노출되어서는 안되는 데이터들이라면 모두
 *  암호화 대상이 되므로 싱글톤 패턴으로 작성하였다.
 *
 * @author BH-Ku
 * @since 2023-05-17
 */
public class AES256Util {
    private static AES256Util aes256Util;
    private final SecretKeySpec SECRET_KEY_SPEC; // 32bytes
    private final IvParameterSpec IV_PRAM_SPEC; // 16bytes(fixed)
    private final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final String TAG = "TAG_AES256Util";

    public static AES256Util getInstance() {
        if(aes256Util == null)
            aes256Util = new AES256Util(BuildConfig.WMP_AES_SECRET_KEY, BuildConfig.WMP_AES_IV);
        return aes256Util;
    }

    private AES256Util(String secretKey, String iv) {
        SECRET_KEY_SPEC = new SecretKeySpec(secretKey.getBytes(), "AES");
        IV_PRAM_SPEC = new IvParameterSpec(iv.getBytes());
    }

    /**
     * Parameter로 들어온 문자열을 암호화하는 메서드다.
     *
     * @param plainText : 서버로 보내고자 하는 문자열 데이터로, 아직 암호화되지 않은 평문이다.
     * @return : 암호화된 데이터를 전송한다.
     */
    public String encrypt(@NonNull String plainText) {
        String encryptedText = null;

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC, IV_PRAM_SPEC);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            String base64EncodedText = Base64.getEncoder().encodeToString(encryptedBytes);
            encryptedText = URLEncoder.encode(base64EncodedText, "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(TAG, "지원하지 않는 암호화 알고리즘이거나 현재 환경에서 사용할 수 없는 알고리즘입니다.");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "암호화 중 문제가 발생했습니다. 비밀 키 혹은 IV 값이 일치하지 않을 수 있습니다.");
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "비밀 키 혹은 알고리즘이 유효하지 않습니다.");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "지원하지 않는 인코딩 방식입니다.");
        }

        return encryptedText;
    }

    /**
     * 암호화된 데이터를 복호화하는 메서드다.
     *
     * @param encryptedText : 서버로부터 받은 암호화된 문자열이다.
     * @return : 암호화 이전의 평문 데이터를 반환한다.
     */
    public String decrypt(@NonNull String encryptedText) {
        String plainText = null;

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC, IV_PRAM_SPEC);
            //byte[] decodeBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decodeBytes = Base64.getUrlDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodeBytes);
            plainText = new String(decryptedBytes, "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(TAG, "지원하지 않는 암호화 알고리즘이거나 현재 환경에서 사용할 수 없는 알고리즘입니다.");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "복호화 중 문제가 발생했습니다. 비밀 키 혹은 IV 값이 일치하지 않을 수 있습니다.");
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "비밀 키 혹은 알고리즘이 유효하지 않습니다.");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "지원하지 않는 인코딩 방식입니다.");
        }

        return plainText;
    }
}