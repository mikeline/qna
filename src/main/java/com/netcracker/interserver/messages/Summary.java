package com.netcracker.interserver.messages;

import com.netcracker.models.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Summary {
    private Node node;
    // private int dbSize;
    // private List<String> tags;
}
