package com.homework.conference.service;

import com.homework.conference.adapter.persistence.ConferenceRepository;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.domain.TalkType;
import com.homework.conference.service.dto.AddOrUpdateConferenceRequestDto;
import com.homework.conference.service.dto.AddTalkRequestDto;
import com.homework.conference.service.dto.ConferenceDto;
import com.homework.conference.service.dto.TalkTypeDto;
import com.homework.conference.service.exception.DuplicateConferenceException;
import com.homework.conference.service.impl.ConferenceServiceImpl;
import com.homework.conference.service.mapper.ConferenceServiceMapperImpl;
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

import static java.lang.Long.MAX_VALUE;
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
        service = new ConferenceServiceImpl(repository, new ConferenceServiceMapperImpl());
    }

    @Test
    void addConference_happyPath() {
        AddOrUpdateConferenceRequestDto addConferenceRequest = generateAddOrUpdateConferenceRequest(Date.from(Instant.parse("2023-11-30T00:00:00.00Z")));
        Conference conference = new Conference()
                .setId(CONFERENCE_ID)
                .setName(addConferenceRequest.getName())
                .setSubject(addConferenceRequest.getSubject())
                .setParticipantsNumber(addConferenceRequest.getParticipantsNumber())
                .setDate(addConferenceRequest.getDate());

        when(repository.existsByName(any())).thenReturn(false);
        when(repository.existsByDate(any())).thenReturn(false);

        when(repository.save(any())).thenReturn(conference);

        ConferenceDto savedConference = service.addConference(addConferenceRequest);
        assertThat(savedConference.getId()).isEqualTo(CONFERENCE_ID);
        assertThat(savedConference.getName()).isEqualTo("conf-name");
    }

    @Test
    void addConference_whenConferenceNameAlreadyExist_thenDoNotProcessConference() {
        when(repository.existsByName("conf-name")).thenReturn(true);
        assertThrows(DuplicateConferenceException.class, () -> service.addConference(generateAddOrUpdateConferenceRequest(Date.from(Instant.parse("2023-11-30T18:35:24.00Z")))));
    }

    @Test
    void addConference_whenConferenceWithDateAlreadyExist_thenDoNotProcessConference() {
        Date conferenceDate = Date.from(Instant.parse("2023-11-30T00:00:00.00Z"));

        when(repository.existsByName("conf-name")).thenReturn(false);
        when(repository.existsByDate(conferenceDate)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.addConference(generateAddOrUpdateConferenceRequest(conferenceDate)));
    }

    @Test
    void updateConference_happyPath() {
        when(repository.existsByName("conf-name")).thenReturn(false);

        service.updateConference(CONFERENCE_ID, generateAddOrUpdateConferenceRequest(Date.from(Instant.parse("2023-11-30T00:00:00.00Z"))));
        verify(repository).save(conferenceCaptor.capture()); // todo

        Conference conference = conferenceCaptor.getValue();
        assertThat(conference.getId()).isEqualTo(CONFERENCE_ID);
        assertThat(conference.getName()).isEqualTo("conf-name");
    }

    @Test
    void updateConference_whenConferenceNameAlreadyExist_thenDoNotProcessConference() {
        when(repository.existsByName("conf-name")).thenReturn(true);
        assertThrows(DuplicateConferenceException.class, () -> service.updateConference(CONFERENCE_ID, generateAddOrUpdateConferenceRequest(Date.from(Instant.parse("2023-11-30T18:35:24.00Z")))));
    }

    @Test
    void addTalk_happyPath() {
        Conference conference = new Conference()
                .setId(CONFERENCE_ID)
                .setName("conf-name")
                .setSubject("subject")
                .setParticipantsNumber(20)
                .setDate(new Date(MAX_VALUE));

        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(conference));

        service.addTalk(CONFERENCE_ID, generateAddTalkRequest("talk-name"));

        verify(repository).save(conferenceCaptor.capture());

        Conference conferenceWithTalk = conferenceCaptor.getValue();
        assertThat(conferenceWithTalk.getId()).isEqualTo(CONFERENCE_ID);
        assertThat(conferenceWithTalk.getTalks().size()).isEqualTo(1);
        assertThat(conferenceWithTalk.getTalks().get(0).getName()).isEqualTo("talk-name");
    }

    @Test
    void addTalk_whenDateLessThanMonth_thenSkipAddingTalk() {
        Conference conference = new Conference()
                .setId(CONFERENCE_ID)
                .setName("conf-name")
                .setSubject("subject")
                .setParticipantsNumber(20)
                .setDate(new Date(0));

        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(conference));

        assertThrows(IllegalArgumentException.class, () -> service.addTalk(CONFERENCE_ID, generateAddTalkRequest("talk-name")));
    }

    @Test
    void addTalk_whenAuthorHasMoreThan4Talks_thenSkipAddingTalk() {
        Conference conference = new Conference()
                .setId(CONFERENCE_ID)
                .setName("conf-name")
                .setSubject("subject")
                .setParticipantsNumber(20)
                .setDate(new Date(MAX_VALUE))
                .setTalks(List.of(
                        generateTalk("talk-name1"),
                        generateTalk("talk-name2"),
                        generateTalk("talk-name3"),
                        generateTalk("talk-name4")
                ));

        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(conference));

        assertThrows(IllegalArgumentException.class, () -> service.addTalk(CONFERENCE_ID, generateAddTalkRequest("talk-name")));
    }

    @Test
    void addTalk_whenDuplicateTalk_thenSkipAddingTalk() {
        Conference conference = new Conference()
                .setId(CONFERENCE_ID)
                .setName("conf-name")
                .setSubject("subject")
                .setParticipantsNumber(20)
                .setDate(new Date(0))
                .setTalks(List.of(
                        generateTalk("talk-name")
                ));

        when(repository.findById(CONFERENCE_ID))
                .thenReturn(Optional.of(conference));

        assertThrows(IllegalArgumentException.class, () -> service.addTalk(CONFERENCE_ID, generateAddTalkRequest("talk-name")));
    }

    private AddTalkRequestDto generateAddTalkRequest(String name) {
        return new AddTalkRequestDto()
                .setName(name)
                .setAuthor("author-name")
                .setDescription("desc")
                .setType(TalkTypeDto.TALK);
    }

    private Talk generateTalk(String name) {
        return new Talk()
                .setName(name)
                .setAuthor("author-name")
                .setDescription("desc")
                .setType(TalkType.TALK);
    }

    private AddOrUpdateConferenceRequestDto generateAddOrUpdateConferenceRequest(Date date) {
        return new AddOrUpdateConferenceRequestDto()
                .setName("conf-name")
                .setSubject("conf-subject")
                .setParticipantsNumber(10)
                .setDate(date);
    }
}