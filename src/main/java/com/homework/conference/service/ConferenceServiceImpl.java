package com.homework.conference.service;

import com.homework.conference.adapter.persistence.ConferenceRepository;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.exception.DuplicateConferenceException;
import com.homework.conference.exception.DuplicateTalkException;
import com.homework.conference.exception.InvalidConferenceException;
import com.homework.conference.exception.InvalidTalkException;
import lombok.NonNull;
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
    // todo add UpdateCommand etc..

    public static final long MONTH_MILLIS = 2629800000L;
    private static final int MAX_AUTHOR_TALK_NUMBER = 3;

    private final ConferenceRepository repository;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addConference(Conference conference) {
        verifyConferenceDate(conference);

        repository.findByName(conference.getName())
                .ifPresentOrElse(
                        c -> {
                            throw new DuplicateConferenceException();
                        },
                        () -> repository.save(conference));

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
        verifyConferenceDate(conference);

        repository.findByName(conference.getName())
                .ifPresentOrElse(
                        c -> {
                            throw new DuplicateConferenceException();
                        },
                        () -> repository.save(conference.setId(conferenceId)));
        log.debug("Updated conference with id: {}", conference.getName());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addTalk(long conferenceId, Talk talk) {
        Optional<Conference> conferenceById = repository.findById(conferenceId);

        if (conferenceById.isPresent()) {
            Conference conference = conferenceById.get();
            verifyTalkAuthor(conference, talk.getAuthor()); // todo refactor
            verifyConferenceDate(conference); // todo refactor

            if (isTalkExist(conference, talk)) {
                throw new DuplicateTalkException();
            } else {
                conference.getTalks().add(talk);
                repository.save(conferenceById.get());
                log.debug("Added talk with name: {} to conference with id: {}", talk.getName(), conferenceId);
            }
        }

    }

    private void verifyTalkAuthor(Conference conference, String author) {
        if (isAuthorTalksLimit(conference.getTalks(), author)) {
            throw new InvalidTalkException();
        }
    }

    @Override
    @Transactional
    public List<Talk> getTalksByConference(long conferenceId) {
        return repository.findById(conferenceId)
                .map(Conference::getTalks)
                .orElse(Collections.emptyList());
    }

    private boolean isAuthorTalksLimit(List<Talk> talks, String author) {
        return talks.stream()
                .filter(t -> t.getAuthor().equals(author))
                .count() > MAX_AUTHOR_TALK_NUMBER;
    }

    private void verifyConferenceDate(Conference conference) {
        if (isDateLessThatMonth(conference.getDate())) { // todo rename
            throw new InvalidConferenceException();
        }
    }

    private boolean isTalkExist(Conference conference, Talk talk) {
        return conference.getTalks().stream().anyMatch(t -> t.getName().equals(talk.getName()));
    }

    private boolean isDateLessThatMonth(@NonNull Date date) {
        return date.getTime() - new Date().getTime() > MONTH_MILLIS;
    }
}
