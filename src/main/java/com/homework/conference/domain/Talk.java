package com.homework.conference.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class Talk {

    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private String author;

    @NonNull
    private TalkType type;

    public enum TalkType {
        TALK,
        MASTERCLASS,
        WORKSHOP
    }
}
