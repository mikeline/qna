package com.netcracker.interserver.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.netcracker.models.*;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "Answer", value = Answer.class),
        @JsonSubTypes.Type(name = "Comment", value = Comment.class),
        @JsonSubTypes.Type(name = "Node", value = Node.class),
        @JsonSubTypes.Type(name = "Post", value = Post.class),
        @JsonSubTypes.Type(name = "Topic", value = Topic.class),
        @JsonSubTypes.Type(name = "User", value = User.class)

})
public interface Replicable {
//    UUID getUUID();
//    UUID getOwnerUUID();
    UUID getId();
    UUID getOwnerId();
}
