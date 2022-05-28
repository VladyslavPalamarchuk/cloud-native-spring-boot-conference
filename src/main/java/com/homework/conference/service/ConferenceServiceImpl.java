package com.homework.conference.service;

import com.homework.conference.adapter.persistence.ConferenceRepository;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.exception.DuplicateConferenceException;
import com.homework.conference.exception.InvalidConferenceException;
import com.homework.conference.exception.InvalidTalkException;
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

    public static final long MONTH_MILLIS = 2629800000L;
    private static final int MAX_AUTHOR_TALK_NUMBER = 3;

    private final ConferenceRepository repository;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addConference(Conference conference) {
        verifyDuplicateConferenceName(conference);
        verifyConferenceIntersectionDate(conference);

        repository.save(conference);
        log.debug("Saved new conference with name: {}", conference.getName());
    }

    @Override
    @Transactional
    public List<Conference> getAllConferences() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateConference(long conferenceId, Conference conference) {
        verifyDuplicateConferenceName(conference);

        repository.save(conference.setId(conferenceId));
        log.debug("Updated conference with id: {}", conference.getName());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addTalk(long conferenceId, Talk talk) {
        Optional<Conference> conferenceById = repository.findById(conferenceId);
        if (conferenceById.isPresent()) {
            Conference conference = conferenceById.get();

            verifyDuplicateTalk(conference, talk);
            verifyAuthorTalksCount(conference, talk);
            verifyTalkAddingDate(conference);

            conference.getTalks().add(talk);
            repository.save(conference);
        }
    }

    @Override
    @Transactional
    public List<Talk> getTalksByConference(long conferenceId) {
        return repository.findById(conferenceId)
                .map(Conference::getTalks)
                .orElse(Collections.emptyList());
    }

    private void verifyDuplicateConferenceName(Conference conference) {
        if (repository.existsByName(conference.getName())) {
            throw new DuplicateConferenceException();
        }
    }

    private void verifyDuplicateTalk(Conference conference, Talk talk) {
        boolean isTalkExist = conference.getTalks().stream()
                .anyMatch(t -> t.getName().equals(talk.getName()));

        if (isTalkExist) {
            throw new InvalidTalkException();
        }
    }

    private void verifyConferenceIntersectionDate(Conference conference) {
        if (repository.existsByDate(conference.getDate())) {
            throw new InvalidConferenceException();
        }
    }

    private void verifyAuthorTalksCount(Conference conference, Talk talk) {
        long count = conference.getTalks().stream()
                .filter(c -> c.getAuthor().equals(talk.getAuthor()))
                .count();

        if (count >= MAX_AUTHOR_TALK_NUMBER) {
            throw new InvalidTalkException();
        }
    }

    private void verifyTalkAddingDate(Conference conference) {
        if (conference.getDate().getTime() - new Date().getTime() < MONTH_MILLIS) {
            throw new InvalidTalkException();
        }
    }
}
