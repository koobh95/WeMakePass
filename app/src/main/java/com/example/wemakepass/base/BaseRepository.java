package com.example.wemakepass.base;

import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.vo.ErrorResponse;

/**
 * - Repository 클래스의 Base Class.
 * - Repository는 네트워크 통신과 관련된 일을 주로 하므로 ErrorResponse를 저장할 라이브 데이터를 가진다. 참조는
 *  이 Repository를 초기화하는 ViewModel에서 받아온다.
 *
 * @author BH-Ku
 * @since 2023-05-24
 */
public class BaseRepository {
    protected final SingleLiveEvent<ErrorResponse> networkErrorLiveData;

    public BaseRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        this.networkErrorLiveData = networkErrorLiveData;
    }
}
