package com.netcracker.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name="user_post_rating")
@Entity
@NoArgsConstructor
@Data
public class UserPostVote {

    @EmbeddedId
    private UserPostVoteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @Column(name="vote")
    private int vote;

    public UserPostVote(User user, Post post) {
        this(user, post, 0);
    }

    public UserPostVote(User user, Post post, int vote) {
        this.post = post;
        this.user = user;
        this.id = new UserPostVoteId(post.getPostId(), user.getUserId());
        this.vote = vote;
    }
}
