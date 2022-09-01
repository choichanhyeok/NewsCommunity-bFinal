package com.teamharmony.newscommunity.domain.news.controller;

import com.teamharmony.newscommunity.domain.news.dto.CreateNewsAccessLogRequestDto;
import com.teamharmony.newscommunity.domain.news.service.NewsService;
import com.teamharmony.newscommunity.domain.news.dto.NewsDetailResponseDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 뉴스 일괄 조회, 뉴스 상세 조회, 뉴스 접근 로그 생성, 조회수 증가 기능을 하는 컨트롤러
 * @author  hyeoKing
 */
@RequestMapping("/api/news")
@RequiredArgsConstructor
@RestController
public class NewsController {
    private final NewsService newsService;

    /**
     * @return 뉴스정보(NewsTable)리스트가 담긴 ResponseNewsDTO를 리턴
     * @see         NewsService#getNews
     */

    @GetMapping("")
    @ApiOperation(value = "전체 뉴스 조회", notes = "전체 뉴스를 조회하는 메소드")
    public ResponseEntity<List<NewsDetailResponseDto>> getNews(){
        return ResponseEntity.ok().body(newsService.getNews());
    }


    /**
     *  해당 id의 뉴스 정보를 리턴해주는 역할을 한다.
     * @param       newsId 해당 뉴스 id
     * @return      뉴스 상세 정보가 담긴 ResponseNewsDetailDTO
     * @see         NewsService#getNewsDetail
     */

    @ApiOperation(value = "뉴스 상세 정보 조회", notes = "요청한 뉴스(newsId)의 상세 정보 response해주는 메소드")
    @ApiImplicitParams({
		    @ApiImplicitParam(name = "newsId", value = "news 식별 정보 Id", required = true, dataType = "String", example = "330155b5-be5d-4474-8df8-21858c")
	})
    @GetMapping("/details/{newsId}")
    public ResponseEntity<NewsDetailResponseDto> getNewsDetial(@PathVariable String newsId){
        return ResponseEntity.ok().body(newsService.getNewsDetail(newsId));
    }


    /**
     * @param requestCreateNewsAccessLogDTO newsId, userId, newsTitle
     * @return 뉴스 접근 로그의  id
     * @see NewsService#setNewsAccessLog
     */

    @ApiOperation(value = "뉴스 접근 기록", notes = "요청 바디의 로깅 정보(newsId, userId) 기반으로 접근 기록 생성")
    @ApiResponses({
		    @ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "330155b5-be5d-4474-8df8-21858c"))),
    })
    @PostMapping("/logs")
    public ResponseEntity<String> createNewsAccessLog(@RequestBody CreateNewsAccessLogRequestDto requestCreateNewsAccessLogDTO){
        String newsId = newsService.setNewsAccessLog(requestCreateNewsAccessLogDTO);
        return ResponseEntity.ok().body(newsId);
    }


    /**
     * @param newsId 조회수 업데이트를 위해 뉴스를 찾는데 필요한 newsId
     * @return 조회수 업데이트 성공여부
     * @see NewsService#addView(String) 
     */

    @ApiOperation(value = "조회수 업데이트", notes = "요청 Path의 newsId의 조회수를 1 증가")
    @ApiImplicitParams({
		    @ApiImplicitParam(name = "newsId", value = "조회수 업데이트를 위한 타겟 newsId", required = true, dataType = "String", example = "330155b5-be5d-4474-8df8-21858c")
	})
    @ApiResponses({
		    @ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "success"))),
    })
    @PutMapping("/views/{newsId}")
    public ResponseEntity<String> UpdateView(@PathVariable String newsId){
        newsService.addView(newsId);
        return ResponseEntity.ok().body("success");
    }
}
