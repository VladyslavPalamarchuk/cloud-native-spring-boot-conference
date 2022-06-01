package com.homework.conference.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class Talk {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "talk_id_seq")
    private Long id;

    private String name;

    private String description;

    private String author;

    private TalkType type;
}
