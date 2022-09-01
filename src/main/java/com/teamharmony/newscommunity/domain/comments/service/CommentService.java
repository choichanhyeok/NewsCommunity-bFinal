package com.teamharmony.newscommunity.domain.comments.service;

import com.teamharmony.newscommunity.domain.comments.dto.CommentCreateRequestDto;
import com.teamharmony.newscommunity.domain.comments.dto.CommentEditRequestDto;
import com.teamharmony.newscommunity.domain.comments.dto.CommentResponseDto;
import com.teamharmony.newscommunity.domain.comments.entity.Comment;
import com.teamharmony.newscommunity.domain.comments.entity.Likes;
import com.teamharmony.newscommunity.domain.comments.repository.CommentRepository;
import com.teamharmony.newscommunity.domain.comments.repository.LikesRepository;
import com.teamharmony.newscommunity.exception.InvalidRequestException;
import com.teamharmony.newscommunity.domain.users.entity.User;
import com.teamharmony.newscommunity.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;

    /**
     * 뉴스 아이디와 댓글 내용을 받아서 DB에 저장
     * @param commentCreateRequestDto
     * @param username
     */
    @Transactional
    public void createComment(CommentCreateRequestDto commentCreateRequestDto, String username) {
        if (commentCreateRequestDto.getContent() == null || commentCreateRequestDto.getNewsId() == null) {
            throw InvalidRequestException.builder()
                    .message("댓글 내용 또는 뉴스 아이디가 비어있습니다.")
                    .invalidValue("댓글 내용 " + commentCreateRequestDto.getContent() + " 뉴스 아이디 " + commentCreateRequestDto.getNewsId())
                    .code("C401")
                    .build();
        }
        Comment comment = new Comment(commentCreateRequestDto);

        User user = getUser(username);
        user.addComment(comment);
    }

    /**
     * DB에서 newsId로 데이터를 모두 조회한 다음, 필요한 값들만 CommentResponseDto에 담는 함수
     * @param newsId
     * @return 특정 뉴스에 달린 모든 댓글들
     */
    public Page<CommentResponseDto> findComments(String newsId, int page, int size, String sortBy, boolean isAsc, String currentUser) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Comment> commentList = commentRepository.findAllByNewsId(newsId, pageable);


        if (commentList == null) {
            throw InvalidRequestException.builder()
                    .message("댓글을 불러올 수 없습니다.")
                    .invalidValue("뉴스 아이디가 댓글에 잘 저장됐는지 확인해주세요")
                    .code("C402")
                    .build();
        }
        Page<CommentResponseDto> dtoList = commentList.map(CommentResponseDto::toDto);
        dtoList.forEach(dto -> dto.likeUser(likeCheck(dto.getCommentId(), currentUser)));
        return dtoList;
    }

    /**
     * 특정 댓글에 좋아요를 했는지 여부를 확인
     * @param commentId
     * @param currentUser
     * @return 특정 댓글에 이미 좋아요 했다면 true, 아니라면 false
     */
    private Boolean likeCheck(Long commentId, String currentUser) {
        if(Objects.equals(currentUser, "=")) return false;
        Likes likes = likesRepository.findByComment_CommentIdAndUser_Id(commentId, getUser(currentUser).getId());
        return likes != null;
    }

    /**
     * DB에서 뉴스 아이디로 댓글이 몇 개 저장되어있는지 조회
     * @param newsId
     * @return 특정 뉴스에 달린 댓글의 개수
     */
    public int getCommentCount(String newsId) {
        return commentRepository.countByNewsId(newsId);
    }

    /**
     * 댓글 수정 내용을 받아서 댓글을 수정하고 DB Transaction 해줌
     * @param id
     * @param commentEditRequestDto
     */
    @Transactional
    public void updateComment(Long id, CommentEditRequestDto commentEditRequestDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new InvalidRequestException("해당 댓글은 이미 삭제되었기 때문에 수정할 수 없습니다.", "삭제된 댓글 아이디 " + id, "C403")
        );
        comment.update(commentEditRequestDto);
    }

    /**
     * 댓글의 아이디를 받아서 삭제함
     * @param id
     */
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    /**
     * 프로필 페이지에서 유저가 쓴 댓글을 모아볼 때 유저가 쓴 댓글을 모두 반환해줌
     * @param username
     * @param page
     * @param size
     * @param currentUser
     * @return 특정 유저가 쓴 모든 댓글
     */
    public Page<CommentResponseDto> getCommentsByUserId(String username, int page, int size, String currentUser) {
        User user = getUser(username);
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentList = commentRepository.findByUser_Id(userId, pageable);

        Page<CommentResponseDto> dtoList = commentList.map(CommentResponseDto::toDto);
        dtoList.forEach(dto -> dto.likeUser(likeCheck(dto.getCommentId(), currentUser)));
        return dtoList;
    }

    /**
     * 특정 유저를 찾기 위한 메소드
     * @param username
     * @return
     */
    private User getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
