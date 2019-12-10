package com.netcracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreadDto {
    private QuestionDto questionDto;

    private List<CommentDto> commentListDto;

    private List<AnswerDto> answerListDto;

}
