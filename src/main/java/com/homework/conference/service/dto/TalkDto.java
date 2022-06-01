package com.homework.conference.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TalkDto {

    private Long id;

    private String name;

    private String description;

    private String author;

    private TalkTypeDto type;
}
