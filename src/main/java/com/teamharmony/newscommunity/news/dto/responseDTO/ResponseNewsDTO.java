package com.teamharmony.newscommunity.news.dto.responseDTO;
import com.teamharmony.newscommunity.news.entity.NewsTable;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
public class ResponseNewsDTO {
    /*= NewsController의 /news에 대한 GET요청의 응답으로 뉴스 정보들이 담긴 List<NewsTable>을 담을 클래스 */

    private List<NewsTable> newsTableList;

    @Builder
    public ResponseNewsDTO(List newsTableList){
        this.newsTableList = newsTableList;        // 뉴스 조회를 위해 뉴스 정보리스트를 입력받음
    }
}
