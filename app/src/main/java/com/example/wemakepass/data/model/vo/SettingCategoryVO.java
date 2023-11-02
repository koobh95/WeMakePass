package com.example.wemakepass.data.model.vo;

import com.example.wemakepass.data.model.data.SettingOptionModel;

import java.util.List;

/**
 * - MyInfoFragment 화면에서 표시될 설정의 카테고리에 대한 정보를 저장하는 모델 클래스다.
 * - categoryTitle는 카테고리에 표시해 줄 타이틀을 가진다.
 * - 설정 RecyclerView는 NestedRecyclerView이기 때문에 ChildRecyclerView가 가질 데이터 리스트를 멤버 변수로
 * 갖는다.
 * - 한 번 데이터가 초기화되면 리스트에 표시된 데이터가 변경될 일이 없으므로 VO 클래스로 생성하였다.
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class SettingCategoryVO {
    private String categoryTitle;
    private final List<SettingOptionModel> optionList;

    public SettingCategoryVO(List<SettingOptionModel> optionList) {
        this.optionList = optionList;
    }

    public SettingCategoryVO(String categoryTitle, List<SettingOptionModel> optionList) {
        this.categoryTitle = categoryTitle;
        this.optionList = optionList;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public List<SettingOptionModel> getOptionList() {
        return optionList;
    }
}
