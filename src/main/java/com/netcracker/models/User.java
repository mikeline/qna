package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import com.netcracker.utils.QnaRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
//@ToString(exclude = "ratedPosts")
@Data
@Entity
@Table(name = "qna_user")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "full_name")
    private String fullName;

    private String username;

    @Column(name = "password_encrypted")
    private String passwordEncrypted;

    private String email;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private QnaRole role;

    private boolean original;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owning_node_id")
    private Node originalNodeForUser;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> owningPosts = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    private Set<Node> nodes = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.PERSIST
    )
    private Set<UserPostVote> ratedPosts = new HashSet<>();




}
