package com.example.wemakepass.network.util;

import com.example.wemakepass.network.client.WmpClient;

/**
 *  Glide로 이미지를 바인딩할 때 이미지의 위치(URL)를 전달해야 하는데 이 때 이미지에 대한 URL을 생성하여 반환
 * 해주는 역할을 한다.
 *
 * @author BH-Ku
 * @since 2023-11-26
 */
public class FileRequestURLBuilder {
    private static final String URI_EXAM_REF_IMAGE = "file/exam_ref_image";

    /**
     * 시험 문제에서 표시해야 할 참고 이미지가 있을 때 호출된다.
     *
     * @param fileName 파일 이름
     * @return
     */
    public static String getExamRefImageURL(String fileName) {
        return new StringBuilder().
                append(WmpClient.BASE_URL)
                .append(URI_EXAM_REF_IMAGE)
                .append("?fileName=")
                .append(fileName)
                .toString();
    }
}
