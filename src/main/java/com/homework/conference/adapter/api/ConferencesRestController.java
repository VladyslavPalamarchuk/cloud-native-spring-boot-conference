package com.homework.conference.adapter.api;

import com.homework.adapter.api.ConferencesApi;
import com.homework.adapter.api.dto.AddConferenceRequestApiDto;
import com.homework.adapter.api.dto.AddTalkRequestApiDto;
import com.homework.adapter.api.dto.ConferenceApiDto;
import com.homework.adapter.api.dto.TalkApiDto;
import com.homework.adapter.api.dto.UpdateConferenceRequestApiDto;
import com.homework.conference.adapter.api.mapper.ConferenceMapper;
import com.homework.conference.service.ConferenceService;
import com.homework.conference.service.exception.ConferenceNotFoundException;
import com.homework.conference.service.exception.DuplicateConferenceException;
import com.homework.conference.service.exception.DuplicateTalkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConferencesRestController implements ConferencesApi {

    private final ConferenceService service;
    private final ConferenceMapper mapper;

    @Override
    public ResponseEntity<ConferenceApiDto> addConference(AddConferenceRequestApiDto addConferenceRequest) {
        service.addConference(mapper.toAddOrUpdateConferenceRequest(addConferenceRequest));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<TalkApiDto> addTalkToConference(Long conferenceId, AddTalkRequestApiDto addTalkRequest) {
        service.addTalk(conferenceId, mapper.toAddTalkDto(addTalkRequest));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ConferenceApiDto>> findConferences() {
        return ResponseEntity.ok().body(mapper.toConferencesApiDto(service.getAllConferences()));
    }

    @Override
    public ResponseEntity<List<TalkApiDto>> findTalksByConference(Long conferenceId) {
        return ResponseEntity.ok().body(mapper.toTalksApiDto(service.getTalksByConference(conferenceId)));
    }

    @Override
    public ResponseEntity<Void> updateConference(Long conferenceId, UpdateConferenceRequestApiDto updateConference) {
        service.updateConference(conferenceId, mapper.toAddOrUpdateConferenceRequest(updateConference));
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({DuplicateConferenceException.class, DuplicateTalkException.class})
    public ResponseEntity<Void> handleDuplicateException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({ConferenceNotFoundException.class})
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
