package com.netcracker.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "search_result")
public class SearchResult {
    @Id
    @GeneratedValue(generator = "ifnull-uuid")
    @GenericGenerator(
            name = "ifnull-uuid",
            strategy = "com.netcracker.services.IfNullUUIDGenerator"
    )
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String result;

//    private List<Object> result;
//    private String username;
}
