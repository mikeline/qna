package com.netcracker.dto;

import com.netcracker.models.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private UUID topicId;

    private String title;

    private PostDto postDto;

    private UserDto author;

    private int numberOfAnswers;

}
