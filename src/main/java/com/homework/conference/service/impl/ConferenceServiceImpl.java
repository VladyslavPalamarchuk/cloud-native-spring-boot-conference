package com.homework.conference.service.impl;

import com.homework.conference.adapter.persistence.ConferenceRepository;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.service.ConferenceService;
import com.homework.conference.service.dto.AddOrUpdateConferenceRequestDto;
import com.homework.conference.service.dto.AddTalkRequestDto;
import com.homework.conference.service.dto.ConferenceDto;
import com.homework.conference.service.dto.TalkDto;
import com.homework.conference.service.exception.ConferenceNotFoundException;
import com.homework.conference.service.exception.DuplicateConferenceException;
import com.homework.conference.service.mapper.ConferenceServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConferenceServiceImpl implements ConferenceService {

    private static final int MAX_AUTHOR_TALK_NUMBER = 3;
    private static final int MAX_CONFERENCE_PARTICIPANTS_NUMBER = 100;

    private final ConferenceRepository conferenceRepository;
    private final ConferenceServiceMapper mapper;

    @Override
    public ConferenceDto addConference(AddOrUpdateConferenceRequestDto addConferenceRequest) {
        log.info("Request to add conference: {}", addConferenceRequest.toString());
        verifyConferenceParticipantsNumber(addConferenceRequest);
        verifyDuplicateConferenceName(addConferenceRequest);
        verifyConferenceIntersectionDate(addConferenceRequest);

        Conference savedConference = saveOrUpdateConference(mapper.toConference(addConferenceRequest));
        log.info("Saved new conference with name: {}", addConferenceRequest.getName());
        return mapper.toConferenceDto(savedConference);
    }

    @Override
    @Transactional
    public List<ConferenceDto> getAllConferences() {
        log.info("Request to get all conferences");
        return mapper.toConferencesDto(conferenceRepository.findAll());
    }

    @Override
    public void updateConference(long conferenceId, AddOrUpdateConferenceRequestDto updateConferenceRequest) {
        log.info("Request to update conference with id: {}", conferenceId);
        verifyDuplicateConferenceName(updateConferenceRequest);
        saveOrUpdateConference(mapper.toConference(updateConferenceRequest, conferenceId));
    }

    @Override
    public TalkDto addTalk(long conferenceId, AddTalkRequestDto addTalkRequest) {
        log.info("Request to add talk with name: {} to conference with id: {}", addTalkRequest.getName(), conferenceId);

        Optional<Conference> conferenceById = conferenceRepository.findById(conferenceId);
        if (conferenceById.isPresent()) {
            Conference conference = conferenceById.get();

            verifyDuplicateTalk(conference, addTalkRequest);
            verifyAuthorTalksCount(conference, addTalkRequest);
            verifyTalkAddingDate(conference, addTalkRequest.getName());

            Talk talkToAdd = mapper.toTalk(addTalkRequest);
            conference.getTalks().add(talkToAdd);
            Conference savedConferenceWithNewTalk = saveOrUpdateConference(conference);
            log.info("Talk with name: {} was added to conference with id: {}", addTalkRequest.getName(), conferenceId);
            return mapper.toTalkDto(savedConferenceWithNewTalk.getTalks().stream().filter(t -> t.getName().equals(addTalkRequest.getName())).findFirst().get());
        } else {
            log.error("Fail to find conference with id: {}", conferenceId);
            throw new ConferenceNotFoundException();
        }
    }

    @Override
    @Transactional
    public List<TalkDto> getTalksByConference(long conferenceId) {
        log.info("Request to get all talks by conference id: {}", conferenceId);
        return conferenceRepository.findById(conferenceId)
                .map(Conference::getTalks)
                .map(mapper::toTalksDto)
                .orElse(Collections.emptyList());
    }

    private Conference saveOrUpdateConference(Conference conference) {
        try {
            return conferenceRepository.save(conference);
        } catch (DataIntegrityViolationException e) { // todo constraint exception
            log.error("Fail to save or update duplicated conference with name: {} and date: {}",
                    conference.getName(),
                    conference.getDate());
            throw new DuplicateConferenceException();
        }
    }

    private void verifyConferenceParticipantsNumber(AddOrUpdateConferenceRequestDto addConferenceRequest) {
        if (addConferenceRequest.getParticipantsNumber() > MAX_CONFERENCE_PARTICIPANTS_NUMBER) {
            log.error("Conference participants count must be less than: {}", MAX_CONFERENCE_PARTICIPANTS_NUMBER);
            throw new IllegalArgumentException();
        }
    }

    private void verifyDuplicateConferenceName(AddOrUpdateConferenceRequestDto conference) {
        if (conferenceRepository.existsByName(conference.getName())) {
            log.error("Conference with name: {} already exist", conference.getName());
            throw new DuplicateConferenceException();
        }
    }

    private void verifyDuplicateTalk(Conference conference, AddTalkRequestDto talk) {
        boolean isTalkExist = conference.getTalks().stream()
                .anyMatch(t -> t.getName().equals(talk.getName()));

        if (isTalkExist) {
            log.error("Talk with name: {} already exist", talk.getName());
            throw new IllegalArgumentException();
        }
    }

    private void verifyConferenceIntersectionDate(AddOrUpdateConferenceRequestDto conference) {
        if (conferenceRepository.existsByDate(conference.getDate())) {
            log.error("Conference with date: {} already exist", conference.getDate());
            throw new IllegalArgumentException();
        }
    }

    private void verifyAuthorTalksCount(Conference conference, AddTalkRequestDto talk) {
        long count = conference.getTalks().stream()
                .filter(c -> c.getAuthor().equals(talk.getAuthor()))
                .count();

        if (count >= MAX_AUTHOR_TALK_NUMBER) {
            log.error("Author: {} not allowed to add more than {} talk to conference with id: {}", talk.getAuthor(), MAX_AUTHOR_TALK_NUMBER, conference.getId());
            throw new IllegalArgumentException();
        }
    }

    private void verifyTalkAddingDate(Conference conference, String talk) {
        if (Instant.now().plus(Duration.ofDays(30)).isAfter(conference.getDate().toInstant())) {
            log.error("Fail to add talk with name: {} to conference with id: {} due to the deadline", talk, conference.getId());
            throw new IllegalArgumentException();
        }
    }
}
