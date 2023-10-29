package com.example.wemakepass.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.vo.ErrorResponse;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * - ViewModel 클래스를 상속받는 모든 클래스의 부모가 되는 베이스 클래스.
 * - 이 프로젝트에서 네트워크, 로컬 DB, 그 외 백그라운드 작업을 처리할 때 RxJava를 사용하기 때문에 Observable
 *  객체를 생성하고 나면 반환되는 Disposable을 관리하기 위해서 CompositeDisposable을 생성 및 관리한다.
 *  ViewModel이 메모리에서 삭제될 때 onClear가 호출되면서 CompositeDisposable을 비워 메모리 낭비를
 *  방지하는 것이 가장 큰 목적이다.
 *
 * @author BH-Ku
 * @since 2023-07-28
 *
 */
public class BaseViewModel extends ViewModel {
    /**
     * - ViewModel의 가장 큰 목적은 비지니스 로직의 분리에 있다. 비지니스 로직을 처리하면서 사용자에게 시각적으로
     *  알려야 할 메시지가 있을 경우 Toast, Snackbar, Dialog 등을 사용해야 하는데 셋 모두 Context를 필요로
     *  한다. 하지만 원칙적으로 ViewModel은 Context를 참조해서는 안되므로 사용자에게 알려야 할 데이터가 있을 경우
     *  라이브 데이터에 메시지를 저장하고 Activity(혹은 Fragment)에서 이를 옵저빙하여 메시지를 출력하도록 했다.
     *
     * - 상속받는 모든 ViewModel 에서는 직접 참조할 수 있도록 접근 제한자를 protected로 설정했다. 이 객체는
     *  getter를 호출해야만 생성되기 때문에 Activity(혹은 Fragment)에서 옵저빙을 하지 않을 경우
     *  NullPointException을 발생시킨다. 이는 ViewModel에서 SystemMessage를 사용했지만 실수로 옵저빙을
     *  수행하지 않는 사고를 방지할 수 있다.
     */
    protected SingleLiveEvent<String> systemMessageLiveData;
    /**
     * - API를 호출하는 과정에서 발생하는 에러 코드를 저장하는 라이브 데이터다. 직관성을 더해주기 위해
     *  errorResponseLiveData가 아닌 networkErrorResponse로 명명하였다.
     * - Activity(혹은 Fragment) 뿐만 아니라 Repository에도 참조를 전달한다. Repository에서는 API를
     * 호출하는 과정에서 에러가 발생했을 때 에러를 파싱한 뒤 APIError 객체를 생성하게 된다. 이 때 생성한 객체를
     * networkError에 세팅하면 Activity에서 옵저빙한 후 처리한다.
     */
    private SingleLiveEvent<ErrorResponse> networkErrorLiveData;
    /**
     *  ViewModel, Repository 등에서 RxJava로 실행되는 백그라운드 작업에서 반환되는 Disposable 객체를
     * 저장하는 객체
     */
    private final CompositeDisposable compositeDisposable;

    private final String TAG = "TAG_BaseViewModel";

    /**
     * 실수로라도 단독으로 사용될 수 없도록 접근제한자를 protected로 설정하였다.
     */
    protected BaseViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    /**
     *  ViewModel LifeCycle의 마지막에 호출되는 콜백 메서드다. 화면이 종료되기 전, ViewModel에서 실행한
     * 백그라운드 작업이 남아 있을 경우 모두 종료하여 메모리 누수를 방지한다.
     */
    @Override
    protected void onCleared() {
        if(!compositeDisposable.isDisposed())
            compositeDisposable.dispose();
        super.onCleared();
    }

    /**
     *  ViewModel 클래스에서 생성한 Repository 객체를 통해 네트워크 혹은 내부 DB를 읽는 작업을 실행했을 때
     * 실행 결과로 Dispoable 객체를 반환받아 CompositeDisposable 객체에 저장한다.
     *
     * @param disposable
     */
    protected void addDisposable(@NonNull  Disposable disposable){
        compositeDisposable.add(disposable);
    }

    /**
     *  CompositeDisposable에 있는 Disposable 객체 중 아직 폐기되지 않은 모든 객체를 즉시 폐기한다. dispose는
     * 모든 Disposable을 폐기시킴과 동시에 CompositeDisposable를 사용 불가 상태로 만들기 때문에 임의로 동작
     * 중인 모든 백그라운드 작업을 종료시켜야 하지만 CompositeDisposable은 계속 사용해야 할 때 이 메서드를 사용
     * 한다.
     */
    protected void disposableClear(){
        compositeDisposable.clear();
    }

    public SingleLiveEvent<String> getSystemMessageLiveData() {
        if(systemMessageLiveData == null)
            systemMessageLiveData = new SingleLiveEvent<>();
        return systemMessageLiveData;
    }

    public SingleLiveEvent<ErrorResponse> getNetworkErrorLiveData() {
        if(networkErrorLiveData == null)
            networkErrorLiveData = new SingleLiveEvent<>();
        return networkErrorLiveData;
    }
}
