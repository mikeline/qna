package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString(exclude = {"topics"})
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Topic> topics = new HashSet<>();
}
