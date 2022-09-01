package com.teamharmony.newscommunity.domain.bookmarks.controller;

import com.teamharmony.newscommunity.domain.bookmarks.dto.BookmarkRequestDto;
import com.teamharmony.newscommunity.domain.bookmarks.dto.BookmarkResponseDto;
import com.teamharmony.newscommunity.domain.bookmarks.service.BookmarkService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /**
     * detail.html에서 해당 뉴스의 id와 읽고 있는 userId를 가지고 bookmarkLog를 생성
     * @param requestBookmarkDTO
     * @return 생성된 북마크의 식별번호(Id: String, based on UUID4)
     * @see BookmarkService#createBookmark(BookmarkRequestDto)
     */

    @ApiOperation(value = "북마크 생성", notes = "요청 바디의(newsId, userId, title) 정보를 바탕으로 북마크 로그 생성")
    @ApiImplicitParams({
		    @ApiImplicitParam(name = "requestBookmarkDTO", value = "북마크 생성을 위한 정보(newsId, userId, title)", required = true, dataType = "BookmarkRequestDto")
	})
    @ApiResponses({
		    @ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "339cfa70-5205-43bd-9b3e-489b8a06883c"))),
    })
    @PostMapping("")
    public ResponseEntity<String> createBookmark(@RequestBody BookmarkRequestDto requestBookmarkDTO){
        return ResponseEntity.ok().body(bookmarkService.createBookmark(requestBookmarkDTO));
    }



    /**
     * detail.html에서 해당 뉴스의 id와 읽고 있는 userId를 가지고 사용자가 뉴스를 북마크했는지 판별
     * @param newsId
     * @param userId
     * @return 해당 뉴스에 대한 사용자의 북마크 여부 (True||False)
     * @see BookmarkService#isBookmarkCheck(BookmarkRequestDto) 
     **/

    @ApiOperation(value = "북마크 여부 확인", notes = "request param에 담겨온 newsId, userId를 통해 해당 뉴스의 북마크가 존재하는지 판별")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newsId", value = "news 식별 정보 Id", required = true, dataType = "String", example = "330155b5-be5d-4474-8df8-21858c"),
            @ApiImplicitParam(name = "userId", value = "user 식별 정보 Id", required = true, dataType = "String", example = "chlcksgur1")
	})
    @GetMapping("")
    public ResponseEntity<Boolean> isBookmark(@RequestParam String newsId, @RequestParam String userId){
        BookmarkRequestDto requestBookmarkDTO = new BookmarkRequestDto(newsId, userId, null);
        return ResponseEntity.ok().body(bookmarkService.isBookmarkCheck(requestBookmarkDTO));
    }



    /**
     * detail.html에서 해당 뉴스의 id와 읽고 있는 userId를 가지고 사용자가 뉴스를 북마크했는지 판별
     * @param requestBookmarkDTO
     * @return 해당 뉴스에 대한 사용자의 북마크 여부 (True||False)
     * @see BookmarkService#deleteBookmark(BookmarkRequestDto) 
     */

    @ApiOperation(value = "북마크 삭제", notes = "요청 바디의(newsId, userId, title) 정보를 바탕으로 북마크 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestBookmarkDTO", value = "북마크 삭제를 위한 정보(newsId, userId, title)", required = true, dataType = "BookmarkRequestDto")
	})
    @ApiResponses({
		    @ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "02c1e768-a5c0-4958-aa44-130512"))),
    })
    @DeleteMapping("")
    public ResponseEntity<String> cancel(@RequestBody BookmarkRequestDto requestBookmarkDTO){
        return ResponseEntity.ok().body(bookmarkService.deleteBookmark(requestBookmarkDTO));
    }



    /**
     * profile.html에서 보내준 profile userId의 bookmark 일괄 조회
     * @param userId
     * @return 해당 유저가 북마킹한 내역인 List<Bookmark>를 리턴
     * @see BookmarkService#selectAllUserBookmark(String)
     */

    @ApiOperation(value = "특정 회원의 북마크 일괄 조회", notes = "url path에 담겨온 userId의 모든 북마크 조회")
    @ApiImplicitParams({
		    @ApiImplicitParam(name = "userId", value = "북마크 조회를 userId", required = true, dataType = "String", example = "chlcksgur1")
	})
    @GetMapping("/profiles/{userId}")
    public ResponseEntity<List<BookmarkResponseDto>> bookmark(@PathVariable String userId){
        return ResponseEntity.ok().body(bookmarkService.selectAllUserBookmark(userId));
    }
}