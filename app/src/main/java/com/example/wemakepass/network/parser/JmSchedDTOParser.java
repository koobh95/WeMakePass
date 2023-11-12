package com.example.wemakepass.network.parser;

import com.example.wemakepass.data.model.dto.JmSchedDTO;

import com.example.wemakepass.data.util.DateUtils;
import com.google.gson.JsonObject;

/**
 * - 특정 종목에 대한 일정 정보 API의 Response > Body > Items를 Parsing하는 클래스.
 * - Response와 body는 상위 NqeResponseParser에서 처리한다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 */
public class JmSchedDTOParser extends NqeResponseParser<JmSchedDTO> {
    @Override
    protected JmSchedDTO parseItem(JsonObject o) {
        return new JmSchedDTO(o.get("implYy").getAsString(),
                Integer.parseInt(o.get("implSeq").getAsString()),
                o.get("qualgbCd").getAsString(),
                o.get("qualgbNm").getAsString(),
                o.get("description").getAsString(),
                DateUtils.parseLocalDate(o.get("docRegStartDt").getAsString()),
                DateUtils.parseLocalDate(o.get("docRegEndDt").getAsString()),
                DateUtils.parseLocalDate(o.get("docExamStartDt").getAsString()),
                DateUtils.parseLocalDate(o.get("docExamEndDt").getAsString()),
                DateUtils.parseLocalDate(o.get("docPassDt").getAsString()),
                DateUtils.parseLocalDate(o.get("pracRegStartDt").getAsString()),
                DateUtils.parseLocalDate(o.get("pracRegEndDt").getAsString()),
                DateUtils.parseLocalDate(o.get("pracExamStartDt").getAsString()),
                DateUtils.parseLocalDate(o.get("pracExamEndDt").getAsString()),
                DateUtils.parseLocalDate(o.get("pracPassDt").getAsString()));
    }
}
