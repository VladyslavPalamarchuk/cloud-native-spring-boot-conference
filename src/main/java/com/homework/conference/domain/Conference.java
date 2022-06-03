package com.homework.conference.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conference_id_seq")
    @SequenceGenerator(name = "conference_id_seq", sequenceName = "conference_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String subject;

    private Date date;

    private int participantsNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "conference_talk_map",
            joinColumns = {@JoinColumn(name = "conference_id")},
            inverseJoinColumns = {@JoinColumn(name = "talk_id")})
    private List<Talk> talks = new ArrayList<>();
}
