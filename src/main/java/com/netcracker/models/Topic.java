package com.netcracker.models;

import com.fasterxml.jackson.annotation.*;
import com.netcracker.utils.EntityIdResolver;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@ToString
@Data
@Entity
@Table(name = "topic")
@Indexed
public class Topic implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    @Getter
    private UUID topicId;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;

    private String postIdString;

    @IndexedEmbedded
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    @Getter
    @Setter
    private Post topicPost;


    @IndexedEmbedded
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "topic_tags")
    private Set<Tag> tags = new HashSet<>();

}
