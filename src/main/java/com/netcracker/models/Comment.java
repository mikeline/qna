package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@ToString
@Entity
@Table(name = "comment", schema = "qna")
public class Comment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    @Getter
    private UUID commentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @Getter
    @Setter
    private Post commentPost;

    @JsonIgnore
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commented_post_id")
    private Post commentedPost;

}
