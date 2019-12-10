package com.netcracker.dto;

import com.netcracker.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto {
    private PostDto postDto;

    private UserDto author;

    private List<CommentDto> commentListDto;
}
