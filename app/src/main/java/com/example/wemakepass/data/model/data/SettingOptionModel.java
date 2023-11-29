package com.example.wemakepass.data.model.data;

import android.view.View;

/**
 * - MyInfoFragment 화면에서 표시될 설정 아이템에 대한 정보를 저장하는 Model 클래스다.
 * - onClickListener가 Adapter를 생성하면서 구현하는 것이 아닌 모델 클래스 내부에 존재하는 이유는 각 아이템이
 *  동일한 이벤트를 발생시키지 않기 때문이다. 밖에서 이벤트를 각각 구현한다고 하면 content 값으로 옵션을 구분해야
 *  하는데 코드가 상당히 더러워질 것이라고 생각되어 리스너를 여기에 구현하였다.
 * - content는 필수적으로 존재해야 하지만 나머지 두 변수는 선택사항이기 때문에 생성자를 오버로딩하거나 설정자를
 *  여러 개 만드는 것보다는 builder 패턴이 코드를 줄여준다고 판단하여 builder 패턴을 사용했다.
 * - content는 상황에 따라 유동적인 값이 세팅될 수 있으므로 예외적으로 설정자를 생성했다.(이 클래스 접미사가 VO가
 *  아닌 이유다.)
 */
public class SettingOptionModel {
    private String summary; // 옵션에 대한 부가 설명
    private String content; // 옵션이 가지는 데이터(아이디, 닉네임 등) 혹은 옵션 이름(로그아웃 등)
    private View.OnClickListener onClickListener;

    private SettingOptionModel(SettingOptionBuilder builder){
        this.summary = builder.summary;
        this.content = builder.content;
        this.onClickListener = builder.onClickListener;
    }

    public String getSummary() {
        return summary;
    }

    public String getContent() {
        return content;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class SettingOptionBuilder {
        private String summary;
        private String content;
        private View.OnClickListener onClickListener;

        public SettingOptionBuilder setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public SettingOptionBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public SettingOptionBuilder setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public SettingOptionModel build() {
            return new SettingOptionModel(this);
        }
    }
}
