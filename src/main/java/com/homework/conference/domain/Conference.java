package com.homework.conference.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class Conference {

    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String subject;

    @NonNull
    private Date date;

    private int participantsNumber;

    private List<Talk> talks = new ArrayList<>();
}
