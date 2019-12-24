package com.netcracker.models;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "search_result")
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class SearchResult {
    @Id
    @GeneratedValue(generator = "ifnull-uuid")
    @GenericGenerator(
            name = "ifnull-uuid",
            strategy = "com.netcracker.services.IfNullUUIDGenerator"
    )
    private UUID id;

    @Column(name = "search_id")
    private UUID searchId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String result;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

}
