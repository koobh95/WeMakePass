package com.example.wemakepass.data.consts;

/**
 * - 게시글 검색 옵션을 가지는 상수 클래스다.
 * - PostSearchFragment 에서 검색 옵션을 가지는 Spinner를 제공하는데 이 Spinner는
 *  "arrays.xml/post_search_option"에 의해서 초기화된다. 이 스피너의 옵션 index를 그대로 사용하기 위해서
 *  enum이 아닌 클래스로 작성하였다.
 *
 * @author BH-Ku
 * @since 2023-12-15
 */
public class PostSearchOption {
    public static final int TITLE = 0; // 제목
    public static final int CONTENT = 1; // 내용
    public static final int TITLE_AND_CONTENT = 2; // 제목 + 내용
}
