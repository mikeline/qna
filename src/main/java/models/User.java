package models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Parameter;
import utils.QnaRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@ToString
@Entity
@Table(name = "qna_user", schema = "qna")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    @Getter
    private UUID userId;

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

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private QnaRole role;

    @Getter
    @Setter
    private boolean original;

    @Getter
    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> owningPosts = new ArrayList<>();;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "users")
    private Set<Node> nodes = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "user_post_rating",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "rated_post_id") }
    )
    private Set<Post> ratedPosts = new HashSet<>();

    public User(String fullName, String username, String passwordEncrypted,
                String email, QnaRole role) {
        this.fullName = fullName;
        this.username = username;
        this.passwordEncrypted = passwordEncrypted;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.role = role;
        this.original = true;
    }

}
