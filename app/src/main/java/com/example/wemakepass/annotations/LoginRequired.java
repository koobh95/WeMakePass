package com.example.wemakepass.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * - API에서 보안이 필요한 URI와 그렇지 않은 리소스를 구분하기 위해서 생성한 어노테이션이다.
 * - 보안이 필요하다는 즉슨, 로그인이 필요하다는 뜻과 같으므로 이름을 "LoginRequired"로 작성하였다.
 * - Repository에서 요청을 보내는 메서드에도 사용한다.
 * - 보안이 필요한 URI인지 아닌지 구분하는 용도로만 사용되기 때문에 Retention을 SOURCE로 지정하였다.
 *
 *
 * @author BH-Ku
 * @since 2023-11-07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface LoginRequired {

}
