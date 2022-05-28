package com.homework.conference.adapter.api;

import com.homework.adapter.api.ConferencesApi;
import com.homework.adapter.model.AddConferenceRequestDto;
import com.homework.adapter.model.ConferenceDto;
import com.homework.adapter.model.TalkDto;
import com.homework.adapter.model.UpdateConferenceRequestDto;
import com.homework.conference.adapter.api.mapper.ConferenceMapper;
import com.homework.conference.service.ConferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConferencesController implements ConferencesApi {

    private final ConferenceService service;
    private final ConferenceMapper mapper;

    @Override
    public ResponseEntity<Void> addConference(AddConferenceRequestDto conferenceDto) {
        if (conferenceDto.getParticipantsNumber() > 100) {
            return ResponseEntity.badRequest().build();
        }
        service.addConference(mapper.map(conferenceDto));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> addTalkToConference(Integer conferenceId, TalkDto talkDto) {
        service.addTalk(conferenceId, mapper.map(talkDto));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ConferenceDto>> findConferences() {
        return ResponseEntity.ok().body(mapper.mapConferences(service.getAllConferences()));
    }

    @Override
    public ResponseEntity<List<TalkDto>> findTalksByConference(Integer conferenceId) {
        return ResponseEntity.ok().body(mapper.mapTalks(service.getTalksByConference(conferenceId)));
    }

    @Override
    public ResponseEntity<Void> updateConference(Integer conferenceId, UpdateConferenceRequestDto conferenceDto) {
        service.updateConference(conferenceId, mapper.map(conferenceDto));
        return ResponseEntity.ok().build();
    }
}
