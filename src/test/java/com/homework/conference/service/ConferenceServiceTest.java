package com.homework.conference.service;

import com.homework.conference.adapter.persistence.ConferenceRepository;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.service.exception.DuplicateConferenceException;
import com.homework.conference.service.exception.InvalidConferenceException;
import com.homework.conference.service.exception.InvalidTalkException;
import com.homework.conference.service.impl.ConferenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConferenceServiceTest {

    private static long CONFERENCE_ID = 1L;

    @Mock
    private ConferenceRepository repository;

    private ConferenceService service;

    @Captor
    private ArgumentCaptor<Conference> conferenceCaptor;

    @BeforeEach
    void init() {
        service = new ConferenceServiceImpl(repository);
    }

    @Test
    void addConference_happyPath() {
        Conference conference = generateConference(Date.from(Instant.parse("2023-11-30T00:00:00.00Z")));

        when(repository.existsByName(any())).thenReturn(false);
        when(repository.existsByDate(any())).thenReturn(false);

        service.addConference(conference);
        verify(repository).save(conference);
    }

    @Test
    void addConference_whenConferenceNameAlreadyExist_thenDoNotProcessConference() {
        when(repository.existsByName("conf-name")).thenReturn(true);
        assertThrows(DuplicateConferenceException.class, () -> service.addConference(generateConference(Date.from(Instant.parse("2023-11-30T18:35:24.00Z")))));
    }

    @Test
    void addConference_whenConferenceWithDateAlreadyExist_thenDoNotProcessConference() {
        Date conferenceDate = Date.from(Instant.parse("2023-11-30T00:00:00.00Z"));

        when(repository.existsByName("conf-name")).thenReturn(false);
        when(repository.existsByDate(conferenceDate)).thenReturn(true);

        assertThrows(InvalidConferenceException.class, () -> service.addConference(generateConference(conferenceDate)));
    }

    @Test
    void updateConference_happyPath() {
        when(repository.existsByName("conf-name")).thenReturn(false);

        service.updateConference(CONFERENCE_ID, generateConference(Date.from(Instant.parse("2023-11-30T00:00:00.00Z"))));
        verify(repository).save(conferenceCaptor.capture());

        Conference conference = conferenceCaptor.getValue();
        assertThat(conference.getId()).isEqualTo(CONFERENCE_ID);
        assertThat(conference.getName()).isEqualTo("conf-name");
    }

    @Test
    void updateConference_whenConferenceNameAlreadyExist_thenDoNotProcessConference() {
        when(repository.existsByName("conf-name")).thenReturn(true);
        assertThrows(DuplicateConferenceException.class, () -> service.updateConference(CONFERENCE_ID, generateConference(Date.from(Instant.parse("2023-11-30T18:35:24.00Z")))));
    }

    @Test
    void addTalk_happyPath() {
        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(generateConference(new Date(Long.MAX_VALUE))));

        service.addTalk(CONFERENCE_ID, generateTalk("talk-name"));

        verify(repository).save(conferenceCaptor.capture());

        Conference conferenceWithTalk = conferenceCaptor.getValue();
        assertThat(conferenceWithTalk.getId()).isEqualTo(CONFERENCE_ID);
        assertThat(conferenceWithTalk.getTalks().size()).isEqualTo(1);
        assertThat(conferenceWithTalk.getTalks().get(0).getName()).isEqualTo("talk-name");
    }

    @Test
    void addTalk_whenDateLessThanMonth_thenSkipAddingTalk() {
        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(generateConference(new Date(0))));

        assertThrows(InvalidTalkException.class, () -> service.addTalk(CONFERENCE_ID, generateTalk("talk-name")));
    }

    @Test
    void addTalk_whenAuthorHasMoreThan4Talks_thenSkipAddingTalk() {
        Conference conference = generateConference(new Date(Long.MAX_VALUE));
        conference.setTalks(List.of(
                generateTalk("talk-name1"),
                generateTalk("talk-name2"),
                generateTalk("talk-name3"),
                generateTalk("talk-name4"))
        );
        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(conference));

        assertThrows(InvalidTalkException.class, () -> service.addTalk(CONFERENCE_ID, generateTalk("talk-name")));
    }

    @Test
    void addTalk_whenDuplicateTalk_thenSkipAddingTalk() {
        Conference conference = generateConference(new Date(Long.MAX_VALUE));
        conference.setTalks(List.of(generateTalk("talk-name")));

        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(conference));

        assertThrows(InvalidTalkException.class, () -> service.addTalk(CONFERENCE_ID, generateTalk("talk-name")));
    }

    private Talk generateTalk(String name) {
        return new Talk()
                .setName(name)
                .setAuthor("author-name")
                .setDescription("desc")
                .setType(Talk.TalkType.TALK);
    }

    private Conference generateConference(Date date) {
        return new Conference()
                .setId(CONFERENCE_ID)
                .setName("conf-name")
                .setSubject("conf-subject")
                .setParticipantsNumber(10)
                .setDate(date);
    }
}