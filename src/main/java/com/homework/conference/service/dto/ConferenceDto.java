package com.homework.conference.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class ConferenceDto {

    private Long id;

    private String name;

    private String subject;

    private Date date;

    private int participantsNumber;
}
