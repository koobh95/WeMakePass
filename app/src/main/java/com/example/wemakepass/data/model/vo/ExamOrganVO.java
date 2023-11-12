package com.example.wemakepass.data.model.vo;

import android.graphics.drawable.Drawable;

/**
 * - 시험 주관 기관에 대한 정보를 갖는 VO 클래스.
 * - 불변성을 띄기 때문에 VO 클래스로 생성하였다.
 * - MainActivity > HomeFragment 에서 사용된다.
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class ExamOrganVO {
    private final Drawable icon;
    private final String name;
    private final String url;

    public ExamOrganVO(Drawable icon, String name, String url) {
        this.icon = icon;
        this.name = name;
        this.url = url;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ExamSupervisionOrganVO{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
