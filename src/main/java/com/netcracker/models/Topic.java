package com.netcracker.models;

import com.fasterxml.jackson.annotation.*;
import com.netcracker.interserver.messages.Replicable;
import com.netcracker.utils.EntityIdResolver;
import com.netcracker.utils.ReplicatedEntityListener;
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
@EntityListeners(ReplicatedEntityListener.class)
@Table(name = "topic")
@Indexed
public class Topic implements Serializable, Replicable {

    @Id
    @GeneratedValue(generator = "ifnull-uuid")
    @GenericGenerator(
            name = "ifnull-uuid",
            strategy = "com.netcracker.services.IfNullUUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;

    @IndexedEmbedded
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    private UUID ownerId;
}
