package com.teamharmony.newscommunity.news.controller;


import com.teamharmony.newscommunity.news.common.ApiResponse;
import com.teamharmony.newscommunity.news.dto.responseDTO.ResponseNewsDTO;
import com.teamharmony.newscommunity.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/news")
@RestController
public class NewsController {
    private final NewsService newsService;

    @GetMapping("")
    public ApiResponse<ResponseNewsDTO> getNews(){
        /* index.html에서 뉴스 조회 요청시 DB에 담긴 전체 뉴스 리스트를(ResponseNewsDTO) 리턴
            param: None
            return: 뉴스정보(NewsTable)가 담긴 ResponseNewsDTO를 리턴
         */
        return ApiResponse.success("result", newsService.getNews());
    }
}
