package com.netcracker.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Topic> topics;
}
