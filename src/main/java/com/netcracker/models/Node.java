package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netcracker.utils.NodeRole;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Data
@Table(name = "node")
public class Node {

    @Id
    @GeneratedValue(generator = "ifnull-uuid")
    @GenericGenerator(
            name = "ifnull-uuid",
            strategy = "com.netcracker.services.IfNullUUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    @Getter
    private UUID nodeId;

    private String name;

    @Column(name = "authority_token")
    private String authorityToken;

    @Column(name = "owner_id")
    private UUID ownerId;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "node_user_replication",
            joinColumns = { @JoinColumn(name = "node_id") },
            inverseJoinColumns = { @JoinColumn(name = "replicated_user_id") }
    )
    Set<User> users = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "node_post_replication",
            joinColumns = { @JoinColumn(name = "node_id") },
            inverseJoinColumns = { @JoinColumn(name = "replicated_post_id") }
    )
    Set<Post> posts = new HashSet<>();

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "node_role")
    private NodeRole nodeRole;

    @JsonIgnore
    private LocalDateTime lastSeen;

}
