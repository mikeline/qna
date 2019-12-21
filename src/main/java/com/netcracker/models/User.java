package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netcracker.interserver.messages.Replicable;
import com.netcracker.utils.ReplicatedEntityListener;
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
@EntityListeners(ReplicatedEntityListener.class)
@Table(name = "user")
public class User implements Replicable {

    @Id
    @GeneratedValue(generator = "ifnull-uuid")
    @GenericGenerator(
            name = "ifnull-uuid",
            strategy = "com.netcracker.services.IfNullUUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

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
    @Column(name = "role", nullable = false, columnDefinition = "varchar default 'ROLE_CLIENT'")
    private QnaRole role;

    @Column(name = "unblock_time")
    private LocalDateTime unblockTime;

    private boolean original;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owning_node_id")
    private Node originalNodeForUser;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> owningPosts = new ArrayList<>();
//
//    @ManyToMany(mappedBy = "users")
//    private Set<Node> nodes = new HashSet<>();

    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.PERSIST
    )
    private Set<UserPostVote> ratedPosts = new HashSet<>();


    private UUID ownerId;

}
