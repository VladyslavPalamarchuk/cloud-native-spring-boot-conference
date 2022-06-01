package com.homework.conference.adapter.api;

import com.homework.adapter.model.AddConferenceRequestDto;
import com.homework.conference.adapter.api.mapper.ConferenceMapper;
import com.homework.conference.domain.Conference;
import com.homework.conference.service.ConferenceService;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void whenConferenceValid_thenItIsStored() throws Exception {
        AddConferenceRequestDto validConferenceRequest = getValidConferenceRequestDto();
        Conference validConference = getValidConference();

        when(mapper.toConference(validConferenceRequest)).thenReturn(validConference);

        addConference(validConferenceRequest)
                .andExpect(status().isOk());

        verify(service).addConference(validConference);
    }

    private AddConferenceRequestDto getValidConferenceRequestDto() {
        AddConferenceRequestDto conferenceRequest = new AddConferenceRequestDto();
        conferenceRequest.setName("conf-name");
        conferenceRequest.setSubject("conf-subj");
        conferenceRequest.setParticipantsNumber(50);
        conferenceRequest.setDate(LocalDate.parse("2023-11-30"));
        return conferenceRequest;
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

}