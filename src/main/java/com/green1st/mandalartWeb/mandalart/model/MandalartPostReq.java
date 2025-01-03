package com.green1st.mandalartWeb.mandalart.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(title = "")
public class MandalartPostReq {
    //    private long projectId;
    @PositiveOrZero(message = "만다라트 ID는 0 이상이어야 합니다.")
    private long mandalartId;  // 만다라트 ID
    @NotEmpty(message = "목표를 입력해주세요")
    private String title;  // 제목
    private String contents;  // 내용
    private Boolean completedFg;  // 완료 여부
    private LocalDate startDate;  // 시작일
    private LocalDate finishDate;  // 종료일
    private int parentId;
}
