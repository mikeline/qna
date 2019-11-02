package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@ToString
@Entity
@Table(name = "node", schema = "qna")
public class Node {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    @Getter
    private UUID nodeId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @Column(name = "authority_token")
    private String authorityToken;

    @Getter
    @Setter
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "node_user_replication",
            joinColumns = { @JoinColumn(name = "node_id") },
            inverseJoinColumns = { @JoinColumn(name = "replicated_user_id") }
    )
    Set<User> users = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "node_post_replication",
            joinColumns = { @JoinColumn(name = "node_id") },
            inverseJoinColumns = { @JoinColumn(name = "replicated_post_id") }
    )
    Set<Post> posts = new HashSet<>();

}
