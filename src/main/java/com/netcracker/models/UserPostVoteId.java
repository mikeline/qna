package com.netcracker.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPostVoteId implements Serializable {
    private UUID userId;
    private UUID postId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPostVoteId id = (UserPostVoteId) o;
        return userId.equals(id.userId) && postId.equals(id.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}