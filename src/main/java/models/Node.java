package models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "node")
public class Node {
    @Getter
    private UUID id;

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

    @Getter
    @Setter
    @ManyToMany(mappedBy = "node")
    private Set<Node> nodes = new HashSet<>();

}
