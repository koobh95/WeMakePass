package com.example.wemakepass.common;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MutableLiveData를 사용할 때 생길 수 있는 옵저빙 문제를 해결하기 위한 클래스.
 * - 모종의 이유로 화면이 재구성되는 경우 LiveData를 가진 ViewModel은 생명주기를 함께하는
 * Activity(혹은 Fragment)가 파괴되어도 메모리에서 제거되지 않는다. 화면이 재구성되기 전, LiveData에 데이터가
 * 세팅된 후 화면이 재구성될 경우 옵저버 역시 새로 세팅되는데 이 과정에서 이미 세팅된 데이터를 옵저버가 감지하여
 * 이전에 처리했던 이벤트를 다시 처리하게 되는 문제점이 있었다.
 *  이와 관련하여 자료를 찾다보니 이 클래스를 발견했다. 이 클래스는 Boolean의 Wrapping 클래스인 AtomicBoolean을
 * 사용하여 값이 세팅될 때 true를 저장하고 옵저빙되었을 때 다시 false로 변경한다. 또한 옵저버가 데이터 변경을
 * 감지하고 이벤트를 처리할 때 true/false 여부를 확인하기 때문에 같은 이벤트를 두 번 실행하지 않는다.
 *  이러한 이유로 MutableLiveData 대체제로 이 클래스를 사용하고 있다.
 *
 * @author BH-Ku
 * @since 2023-06-27
 * @param <T>
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {
    private final String TAG = "TAG_SingleLiveEvent";

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    public SingleLiveEvent() {}

    public SingleLiveEvent(T value) {
        super(value);
    }

    @MainThread
    public void observe(LifecycleOwner owner, final Observer<? super T> observer) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");
        }

        // Observe the internal MutableLiveData
        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false))
                observer.onChanged(t);
        });
    }

    @MainThread
    public void setValue(@Nullable T t) {
        mPending.set(true);
        super.setValue(t);
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
}
