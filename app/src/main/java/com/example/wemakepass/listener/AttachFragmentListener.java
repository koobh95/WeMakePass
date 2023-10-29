package com.example.wemakepass.listener;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * - 어떤 Fragment에서 다른 Fragment를 Backstack에 추가하거나 Backstack 가장 위의 Fragment를 교체할 때
 *  코드를 줄이기 위해 생성한 리스너로서 Container View를 가지는 Activity가 이 리스너를 상속받는다.
 *
 * @author BH-Ku
 * @since 2023-10-15
 */
public interface AttachFragmentListener {
    /**
     *  ContainerView에 Fragment를 Add하는 메서드
     *
     * @param fragment : 부착할 Fragment
     * @param pushAnim : Fragment가 부착될 때 Animation
     * @param popAnim : Fragment가 분리될 때 Animation
     */
    void addFragment(Fragment fragment,
                 @AnimatorRes @AnimRes @Nullable int pushAnim,
                 @AnimatorRes @AnimRes @Nullable int popAnim);

    /**
     *  ContainerView에 부착된 Fragment 제거하고 새로운 Fragment 부착
     * @param fragment : 새로 부착할 Fragment
     */
    void replaceFragment(Fragment fragment);
}
