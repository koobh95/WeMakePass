package com.example.wemakepass.data.enums;

/**
 *  종목 검색에 있어서 일부 공용으로 사용되는 JmSearchFragment에서 검색 타입을 결정한다. 이 값에 따라 어떤
 * 로그 파일을 참조할 지, 어떤 API를 호출할 지가 결정된다.
 *
 * @author BH-Ku
 * @since 2023-11-28
 */
public enum JmSearchType {
    SEARCH_BOARD, // 게시판 검색
    SEARCH_EXAM // 시험 종목 검색
}
