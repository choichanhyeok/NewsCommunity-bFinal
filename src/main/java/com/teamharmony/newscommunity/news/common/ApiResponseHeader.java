package com.teamharmony.newscommunity.news.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class ApiResponseHeader {
    // ApiReponse에서 응답값을 만들어주기 위해 만든 헤더 클래스
    private int code;
    private String message;
}
