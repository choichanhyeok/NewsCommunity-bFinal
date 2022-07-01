package com.teamharmony.newscommunity.news.repository;

import com.teamharmony.newscommunity.news.entity.NewsTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsTable, String> {
    /* 뉴스 정보를 담을 NewsRepository,
     * GET: 뉴스 정보 리스트 조회
     * POST: NULL
     * PUT: NULL
     * DELETE: NULL
     * = 조회를 위한 역할만 수행(/news[GET] from NewsController-NewsService)
     * */
}

