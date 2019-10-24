package models;

import lombok.*;
import org.hibernate.type.descriptor.java.LocalDateTimeJavaDescriptor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user")
public class User {

    @Getter
    private UUID id;

    @Getter
    @Setter
    @Column(name = "full_name")
    private String fullName;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    @Column(name = "password_encrypted")
    private String passwordEncrypted;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    private String role;

    @Getter
    @Setter
    private boolean original;

    @Getter
    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> owningPosts = new ArrayList<>();;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "node")
    private Set<Node> nodes = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "user_post_rating",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "rated_post_id") }
    )
    Set<Post> ratedPosts = new HashSet<>();

}
