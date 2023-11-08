package com.example.wemakepass.listener;

/**
 * - RecyclerView가 가진 List에서 특정 아이템이 선택될 때 이벤트를 처리할 이벤트 리스너.
 * - 선택된 아이템의 position을 갖는다. 기존 OnItemClickListener는 파라미터로 받는 데이터 중 불필요한 데이터를
 *  너무 많이 가지고 있어 꼭 필요한 데이터만을 가진 인터페이스를 생성하였다.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public interface OnItemClickListener {
    void onItemClick(int position);
}
