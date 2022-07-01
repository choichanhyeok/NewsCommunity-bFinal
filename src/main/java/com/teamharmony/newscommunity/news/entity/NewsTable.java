package com.teamharmony.newscommunity.news.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Setter @Getter
@NoArgsConstructor
@Entity
public class NewsTable {
    /* 파이썬 뉴스 정보 관리 외부 모듈로부터 RDS에 저장된 NewsTable을 연동하기 위한 엔티티 */

    @Id
    @Column(nullable = false, unique = true, length = 30)
    private String id;              // 뉴스의 id 정보: Oauth 값을 String 형태로 변환

    @Column(nullable = false, length = 60)
    private String title;           // 뉴스의 제목 정보

    @Column(nullable = false, length = 1000)
    private String summary;         // python 외부 모듈에서 요약된 뉴스 요약 정보,

    @Column(nullable = false, length = 200)
    private String image_url;       // html 상의 이미지 표현을 위한 뉴스의 이미지가 저장된 image_url

    @Column(nullable = false, length = 200)
    private String news_url;        // 해당 뉴스의 원본 주소 (네이버 뉴스의 해당 뉴스 주소)

    @Column(nullable = false, length = 100)
    private String explain;         // 뉴스에 대한 설명

    @Column(nullable = false, length = 30)
    private String write_time;      // 작성 시간
}

