package com.example.wemakepass.data.model.dto;

import java.time.LocalDate;

/**
 * 한국산업인력공단에서 제공하는 특정 종목의 시험 일정 정보를 가지는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-11-09
 */
public class JmSchedDTO {
    private String implYy; // 시행 년도
    private int implSeq; // 시행 회차
    private String qualCode; // 종목 계열 코드(T, S)
    private String qualGbNm; // 종목 계열 이름
    private String description; // 일정에 대한 설명
    private LocalDate docRegStartDate; // 필기 시험 접수 시작 날짜
    private LocalDate docRegEndDate; // 필기 시험 접수 마감 날짜
    private LocalDate docExamStartDate; // 필기 시험 시작 날짜
    private LocalDate docExamEndDate; // 필기 시험 종료 날짜
    private LocalDate docPassDate; // 필기 시험 합격자 발표(예정) 날짜
    private LocalDate pracRegStartDate; // 필기 시험 접수 시작 날짜
    private LocalDate pracRegEndDate; // 필기 시험 접수 마감 날짜
    private LocalDate pracExamStartDate; // 필기 시험 시작 날짜
    private LocalDate pracExamEndDate; // 필기 시험 종료 날짜
    private LocalDate pracPassDate; // 필기 시험 합격자 발표(예정) 날짜

    public JmSchedDTO(String implYy, int implSeq, String qualCode, String qualGbNm,
                      String description, LocalDate docRegStartDate, LocalDate docRegEndDate,
                      LocalDate docExamStartDate, LocalDate docExamEndDate, LocalDate docPassDate,
                      LocalDate pracRegStartDate, LocalDate pracRegEndDate,
                      LocalDate pracExamStartDate, LocalDate pracExamEndDate,
                      LocalDate pracPassDate) {
        this.implYy = implYy;
        this.implSeq = implSeq;
        this.qualCode = qualCode;
        this.qualGbNm = qualGbNm;
        this.description = description;
        this.docRegStartDate = docRegStartDate;
        this.docRegEndDate = docRegEndDate;
        this.docExamStartDate = docExamStartDate;
        this.docExamEndDate = docExamEndDate;
        this.docPassDate = docPassDate;
        this.pracRegStartDate = pracRegStartDate;
        this.pracRegEndDate = pracRegEndDate;
        this.pracExamStartDate = pracExamStartDate;
        this.pracExamEndDate = pracExamEndDate;
        this.pracPassDate = pracPassDate;
    }

    public String getImplYy() {
        return implYy;
    }

    public int getImplSeq() {
        return implSeq;
    }

    public String getQualCode() {
        return qualCode;
    }

    public String getQualGbNm() {
        return qualGbNm;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDocRegStartDate() {
        return docRegStartDate;
    }

    public LocalDate getDocRegEndDate() {
        return docRegEndDate;
    }

    public LocalDate getDocExamStartDate() {
        return docExamStartDate;
    }

    public LocalDate getDocExamEndDate() {
        return docExamEndDate;
    }

    public LocalDate getDocPassDate() {
        return docPassDate;
    }

    public LocalDate getPracRegStartDate() {
        return pracRegStartDate;
    }

    public LocalDate getPracRegEndDate() {
        return pracRegEndDate;
    }

    public LocalDate getPracExamStartDate() {
        return pracExamStartDate;
    }

    public LocalDate getPracExamEndDate() {
        return pracExamEndDate;
    }

    public LocalDate getPracPassDate() {
        return pracPassDate;
    }
}
