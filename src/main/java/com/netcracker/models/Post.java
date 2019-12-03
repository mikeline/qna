package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netcracker.interserver.messages.Replicable;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import com.netcracker.utils.PostType;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
//@ToString(exclude = {"ratedUsers"})
@Data
@Entity
@Table(name = "post")
public class Post implements Replicable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID postId;
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType;

    private int rating;

    @CreationTimestamp
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    private boolean original;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owning_node_id")
    private Node originalNodeForPost;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "commentedPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(mappedBy = "posts")
    private Set<Node> nodes = new HashSet<>();


    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserPostVote> ratedUsers = new HashSet<>();


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
