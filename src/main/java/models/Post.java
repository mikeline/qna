package models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "post")
public class Post {

    @Getter
    private UUID id;

    @Getter
    @Setter
    private String body;

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
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Getter
    @Setter
    @ManyToMany(mappedBy = "node")
    private Set<Node> nodes = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(mappedBy = "user")
    private Set<Node> ratedUsers = new HashSet<>();


}
