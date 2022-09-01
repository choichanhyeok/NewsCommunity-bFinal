package com.teamharmony.newscommunity.domain.supports.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.teamharmony.newscommunity.common.util.Timestamped;
import com.teamharmony.newscommunity.domain.supports.dto.SupportRequestDto;
import com.teamharmony.newscommunity.domain.supports.dto.SupportRequestUpdateDto;
import com.teamharmony.newscommunity.domain.users.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

import org.hibernate.annotations.Fetch;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Support extends Timestamped {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long support_id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String post_title;

    @Column(nullable = false)
    private String post_content;

    @Column(nullable = false)
    private String post_email;


    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Support(SupportRequestDto requestedDto, String username) {
        this.username = username;
        this.post_title = requestedDto.getPost_title();
        this.post_content = requestedDto.getPost_content();
        this.post_email = requestedDto.getPost_email();
    }

    public void update(SupportRequestUpdateDto requestDto){
        this.post_content = requestDto.getPost_content();
    }

}
