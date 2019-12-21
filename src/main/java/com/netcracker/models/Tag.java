package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.SQLInsert;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tags")
@SQLInsert(sql="INSERT INTO qna.tags(name) VALUES (?) ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name || ';' || qna.tags.name")
public class Tag {
    @Id
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Topic> topics = new HashSet<>();
}
