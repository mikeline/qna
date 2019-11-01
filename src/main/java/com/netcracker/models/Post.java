package com.netcracker.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import com.netcracker.utils.PostType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@ToString
@Entity
@Table(name = "post", schema = "qna")
public class Post {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private UUID postId;

    @Getter
    @Setter
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    @Getter
    @Setter
    private PostType postType;

    @Getter
    @Setter
    private int rating;

    @Getter
    @Setter
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Getter
    @Setter
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    @Getter
    @Setter
    private boolean original;



    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @Setter
    @OneToMany(mappedBy = "commentedPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Getter
    @Setter
    @ManyToMany(mappedBy = "posts")
    private Set<Node> nodes = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(mappedBy = "ratedPosts")
    private Set<User> ratedUsers = new HashSet<>();

    public Post(String body, PostType postType, User user) {
        this.body = body;
        this.rating = 0;
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
        this.original = true;
        this.user = user;
        this.postType = postType;
    }

}
