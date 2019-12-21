package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netcracker.interserver.messages.Replicable;
import com.netcracker.utils.NodeRole;
import com.netcracker.utils.ReplicatedEntityListener;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EntityListeners(ReplicatedEntityListener.class)
@Data
@Table(name = "node")
public class Node implements Replicable {

    @Id
    @GeneratedValue(generator = "ifnull-uuid")
    @GenericGenerator(
            name = "ifnull-uuid",
            strategy = "com.netcracker.services.IfNullUUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String name;

    @Column(name = "authority_token")
    private String authorityToken;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "originalNodeForUser",  cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<User> childUsers = new ArrayList<>();

    @Column(name = "owner_id")
    private UUID ownerId;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originalNodeForPost", cascade = CascadeType.PERSIST, orphanRemoval = true)
//    private List<Post> childPosts = new ArrayList<>();
//
//    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "node_user_replication",
//            joinColumns = { @JoinColumn(name = "node_id") },
//            inverseJoinColumns = { @JoinColumn(name = "replicated_user_id") }
//    )
//    Set<User> users = new HashSet<>();
//
//    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "node_post_replication",
//            joinColumns = { @JoinColumn(name = "node_id") },
//            inverseJoinColumns = { @JoinColumn(name = "replicated_post_id") }
//    )
//    Set<Post> posts = new HashSet<>();

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "node_role")
    private NodeRole nodeRole;

    @JsonIgnore
    private LocalDateTime lastSeen;

}
