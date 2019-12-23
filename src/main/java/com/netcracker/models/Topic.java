package com.netcracker.models;

import com.fasterxml.jackson.annotation.*;
import com.netcracker.interserver.messages.Replicable;
import com.netcracker.utils.ReplicatedEntityListener;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@ToString(exclude = {"tags"})
@Data
@Entity
@EntityListeners(ReplicatedEntityListener.class)
@Table(name = "topic")
@Indexed
public class Topic implements Serializable, Comparable, Replicable {

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
    private Post topicPost;

    @JsonIgnore
    @IndexedEmbedded
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "topic_tags")
    private Set<Tag> tags = new HashSet<>();

    @Override
    public int compareTo(Object o) {
        Topic topic = (Topic)o;
        LocalDateTime compareDateUpdated = topic.getTopicPost().getDateUpdated();

        if(compareDateUpdated.isAfter(this.getTopicPost().getDateUpdated())) {
            return -1;
        }
        else if(compareDateUpdated.isBefore(this.getTopicPost().getDateUpdated())) {
            return 1;
        }
        else {
            return 0;
        }

        /* For Descending order do like this */
        //return compareage-this.studentage;
    }

    @Override
    @JsonIgnore
    public UUID getOwnerId() {
        return topicPost.getOwnerId();
    }
}
