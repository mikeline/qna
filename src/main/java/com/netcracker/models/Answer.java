package com.netcracker.models;

import com.fasterxml.jackson.annotation.*;
import com.netcracker.interserver.messages.Replicable;
import com.netcracker.utils.ReplicatedEntityListener;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@ToString
@Data
@Entity
@EntityListeners(ReplicatedEntityListener.class)
@Table(name = "answer")
public class Answer implements Replicable {

    @Id
    @GeneratedValue(generator = "ifnull-uuid")
    @GenericGenerator(
            name = "ifnull-uuid",
            strategy = "com.netcracker.services.IfNullUUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @JsonProperty("post_id")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post answerPost;

    @Override
    @JsonIgnore
    public UUID getOwnerId() {
        return answerPost.getOwnerId();
    }

}
