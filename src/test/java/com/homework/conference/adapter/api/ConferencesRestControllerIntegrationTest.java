package com.homework.conference.adapter.api;

import com.homework.adapter.model.AddConferenceRequestDto;
import com.homework.adapter.model.ConferenceDto;
import com.homework.adapter.model.TalkDto;
import com.homework.adapter.model.TalkTypeDto;
import com.homework.adapter.model.UpdateConferenceRequestDto;
import com.homework.conference.adapter.api.mapper.ConferenceMapper;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.service.ConferenceService;
import com.homework.conference.service.exception.DuplicateConferenceException;
import com.homework.conference.service.exception.DuplicateTalkException;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.PrintingResultHandler;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ConferencesRestController.class)
@ActiveProfiles("test")
class ConferencesRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConferenceMapper mapper;

    @MockBean
    private ConferenceService service;

    @Test
    void addConference_whenConferenceToAddValid_thenItIsStored() throws Exception {
        AddConferenceRequestDto validConferenceRequest = getValidAddConferenceRequestDto();
        Conference validConference = getValidConference();

        when(mapper.toConference(validConferenceRequest)).thenReturn(validConference);

        addConference(validConferenceRequest)
                .andExpect(status().isOk());

        verify(service).addConference(validConference);
    }

    @Test
    void addConference_whenDuplicateConference_thenReturn409StatusCode() throws Exception {
        AddConferenceRequestDto validConferenceRequest = getValidAddConferenceRequestDto();
        Conference validConference = getValidConference();

        when(mapper.toConference(validConferenceRequest)).thenReturn(validConference);
        doThrow(DuplicateConferenceException.class)
                .when(service)
                .addConference(validConference);

        addConference(validConferenceRequest)
                .andExpect(status().isConflict());
    }

    @Test
    void addConference_whenConferenceHasMoreThan100Participants_thenReturn400StatusCode() throws Exception {
        AddConferenceRequestDto validConferenceRequest = getValidAddConferenceRequestDto();
        validConferenceRequest.setParticipantsNumber(101);

        Conference validConference = getValidConference()
                .setParticipantsNumber(101);

        when(mapper.toConference(validConferenceRequest)).thenReturn(validConference);

        addConference(validConferenceRequest)
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @Disabled("wtf")
    void addTalkToConference_whenTalkValid_thenItIsStored() throws Exception {
        TalkDto validTalkDto = getValidTalkDto();
        Talk validTalk = getValidTalk();

        when(mapper.toTalk(validTalkDto)).thenReturn(validTalk);

        addTalk(1L, validTalkDto)
                .andExpect(status().isOk());

        verify(service).addTalk(1L, validTalk);
    }

    @Test
    @Disabled("wtf")
    void addTalkToConference_whenDuplicateTalk_thenReturn409StatusCode() throws Exception {
        TalkDto validTalkDto = getValidTalkDto();
        Talk validTalk = getValidTalk();

        when(mapper.toTalk(validTalkDto)).thenReturn(validTalk);
        doThrow(DuplicateTalkException.class)
                .when(service)
                .addTalk(1L, validTalk);

        addTalk(1L, validTalkDto)
                .andExpect(status().isConflict());

        verify(service).addTalk(1L, validTalk);
    }

    @Test
    void findConferences_whenConferenceExist_thenReturnConferences() throws Exception {
        ConferenceDto validConferenceDto = getValidConferenceDto();
        validConferenceDto.setId(1L);
        Conference validConference = getValidConference()
                .setId(1L);

        when(mapper.toConferences(List.of(validConference))).thenReturn(List.of(validConferenceDto));
        when(service.getAllConferences()).thenReturn(List.of(validConference));

        findAllConferences()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].id").value(1L));

        verify(service).getAllConferences();
    }

    @Test
    void findTalksByConference_whenTalksExist_thenReturnTalks() throws Exception {
        TalkDto validTalkDto = getValidTalkDto();
        validTalkDto.setId(2L);
        Talk validTalk = getValidTalk()
                .setId(2L);

        when(mapper.toTalks(List.of(validTalk))).thenReturn(List.of(validTalkDto));
        when(service.getTalksByConference(1L)).thenReturn(List.of(validTalk));

        findTalksByConference(1L)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].id").value(2L));

        verify(service).getTalksByConference(1L);
    }

    @Test
    void updateConference_whenConferenceToUpdateValid_thenItIsStored() throws Exception {
        UpdateConferenceRequestDto validConferenceRequest = getValidUpdateConferenceRequestDto();
        Conference validConference = getValidConference();

        when(mapper.toConference(validConferenceRequest)).thenReturn(validConference);

        updateConference(1L, validConferenceRequest)
                .andExpect(status().isOk());

        verify(service).updateConference(1L, validConference);
    }

    private ConferenceDto getValidConferenceDto() {
        ConferenceDto conferenceDto = new ConferenceDto();
        conferenceDto.setId(1L);
        conferenceDto.setName("conf-name");
        conferenceDto.setSubject("conf-subj");
        conferenceDto.setParticipantsNumber(50);
        conferenceDto.setDate(LocalDate.parse("2023-11-30"));
        return conferenceDto;
    }

    private AddConferenceRequestDto getValidAddConferenceRequestDto() {
        AddConferenceRequestDto conferenceRequest = new AddConferenceRequestDto();
        conferenceRequest.setName("conf-name");
        conferenceRequest.setSubject("conf-subj");
        conferenceRequest.setParticipantsNumber(50);
        conferenceRequest.setDate(LocalDate.parse("2023-11-30"));
        return conferenceRequest;
    }

    private UpdateConferenceRequestDto getValidUpdateConferenceRequestDto() {
        UpdateConferenceRequestDto conferenceRequest = new UpdateConferenceRequestDto();
        conferenceRequest.setName("conf-name");
        conferenceRequest.setSubject("conf-subj");
        conferenceRequest.setParticipantsNumber(50);
        conferenceRequest.setDate(LocalDate.parse("2023-11-30"));
        return conferenceRequest;
    }

    private Talk getValidTalk() {
        return new Talk()
                .setName("talk-name")
                .setAuthor("talk-author")
                .setDescription("desc")
                .setType(Talk.TalkType.TALK);
    }

    private TalkDto getValidTalkDto() {
        TalkDto talkDto = new TalkDto();
        talkDto.setName("talk-name");
        talkDto.setAuthor("talk-author");
        talkDto.setDescription("desc");
        talkDto.setType(TalkTypeDto.TALK);
        return talkDto;
    }

    private Conference getValidConference() {
        return new Conference()
                .setName("conf-name")
                .setSubject("conf-subj")
                .setParticipantsNumber(50)
                .setDate(Date.from(Instant.parse("2023-11-30T00:00:00.00Z")));
    }

    private ResultActions addConference(AddConferenceRequestDto conference) throws Exception {
        return mockMvc.perform(post("/conferences")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format("""
                                {
                                  "name": "%s",
                                  "subject": "%s",
                                  "date": "%s",
                                  "participantsNumber": %s
                                }""",
                        conference.getName(),
                        conference.getSubject(),
                        conference.getDate(),
                        conference.getParticipantsNumber())));
    }

    private ResultActions addTalk(long conferenceId, TalkDto talkDto) throws Exception {
        return mockMvc.perform(post("/conferences/{conferenceId}/talks", conferenceId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format("""
                                {
                                  "name": "%s",
                                  "description": "%s",
                                  "author": "%s",
                                  "type": %s
                                }""",
                        talkDto.getName(),
                        talkDto.getDescription(),
                        talkDto.getAuthor(),
                        talkDto.getType())));
    }

    private ResultActions findAllConferences() throws Exception {
        return mockMvc.perform(get("/conferences/"));
    }

    private ResultActions findTalksByConference(long conferenceId) throws Exception {
        return mockMvc.perform(get("/conferences/{conferenceId}/talks", conferenceId));
    }

    private ResultActions updateConference(long conferenceId, UpdateConferenceRequestDto conference) throws Exception {
        return mockMvc.perform(put("/conferences/{conferenceId}", conferenceId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format("""
                                {
                                  "name": "%s",
                                  "subject": "%s",
                                  "date": "%s",
                                  "participantsNumber": %s
                                }""",
                        conference.getName(),
                        conference.getSubject(),
                        conference.getDate(),
                        conference.getParticipantsNumber())));
    }

}