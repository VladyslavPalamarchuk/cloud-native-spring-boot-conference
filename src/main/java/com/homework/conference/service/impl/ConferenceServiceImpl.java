package com.homework.conference.service.impl;

import com.homework.conference.adapter.persistence.PersistenceGateway;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.service.ConferenceService;
import com.homework.conference.service.exception.DuplicateConferenceException;
import com.homework.conference.service.exception.InvalidConferenceException;
import com.homework.conference.service.exception.InvalidTalkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConferenceServiceImpl implements ConferenceService {

    private static final long MONTH_MILLIS = 2629800000L;
    private static final int MAX_AUTHOR_TALK_NUMBER = 3;

    private final PersistenceGateway persistenceGateway;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addConference(Conference conference) {
        verifyDuplicateConferenceName(conference);
        verifyConferenceIntersectionDate(conference);

        persistenceGateway.save(conference);
        log.debug("Saved new conference with name: {}", conference.getName());
    }

    @Override
    @Transactional
    public List<Conference> getAllConferences() {
        return persistenceGateway.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateConference(long conferenceId, Conference conference) {
        verifyDuplicateConferenceName(conference);

        persistenceGateway.save(conference.setId(conferenceId));
        log.debug("Updated conference with id: {}", conference.getId());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addTalk(long conferenceId, Talk talk) {
        Optional<Conference> conferenceEntity = persistenceGateway.findById(conferenceId); // todo refactor
        if (conferenceEntity.isPresent()) {
            Conference conference = conferenceEntity.get();
            verifyDuplicateTalk(conference, talk);
            verifyAuthorTalksCount(conference, talk);
            verifyTalkAddingDate(conference, talk.getName());

            conference.getTalks().add(talk);
            persistenceGateway.save(conference);
            log.debug("Talk with name: {} was added to conference with id: {}", talk.getName(), conference.getId());
        }
    }

    @Override
    @Transactional
    public List<Talk> getTalksByConference(long conferenceId) {
        return persistenceGateway.findById(conferenceId)
                .map(Conference::getTalks)
                .orElse(Collections.emptyList());
    }

    private void verifyDuplicateConferenceName(Conference conference) {
        if (persistenceGateway.existsByName(conference.getName())) {
            log.error("Conference with name: {} already exist", conference.getName());
            throw new DuplicateConferenceException();
        }
    }

    private void verifyDuplicateTalk(Conference conference, Talk talk) {
        boolean isTalkExist = conference.getTalks().stream()
                .anyMatch(t -> t.getName().equals(talk.getName()));

        if (isTalkExist) {
            log.error("Talk with name: {} already exist", talk.getName());
            throw new InvalidTalkException();
        }
    }

    private void verifyConferenceIntersectionDate(Conference conference) {
        if (persistenceGateway.existsByDate(conference.getDate())) {
            log.error("Conference with date: {} already exist", conference.getDate());
            throw new InvalidConferenceException();
        }
    }

    private void verifyAuthorTalksCount(Conference conference, Talk talk) {
        long count = conference.getTalks().stream()
                .filter(c -> c.getAuthor().equals(talk.getAuthor()))
                .count();

        if (count >= MAX_AUTHOR_TALK_NUMBER) {
            log.error("Author: {} not allowed to add more than {} talk to conference with id: {}", talk.getAuthor(), MAX_AUTHOR_TALK_NUMBER, conference.getId());
            throw new InvalidTalkException();
        }
    }

    private void verifyTalkAddingDate(Conference conference, String talk) {
        if (conference.getDate().getTime() - new Date().getTime() < MONTH_MILLIS) {
            log.error("Fail to add talk with name: {} to conference with id: {} due to the deadline", talk, conference.getId());
            throw new InvalidTalkException();
        }
    }
}
