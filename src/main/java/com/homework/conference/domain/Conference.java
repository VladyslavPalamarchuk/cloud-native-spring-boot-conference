package com.homework.conference.domain;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CONFERENCE")
@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String name;

    @NonNull
    private String subject;

    @NonNull
    private Date date;

    @Max(value = 100)
    private int participantsNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "conference_talk_map",
            joinColumns = {@JoinColumn(name = "conference_id")},
            inverseJoinColumns = {@JoinColumn(name = "talk_id")})
    private List<Talk> talks = new ArrayList<>();
}
