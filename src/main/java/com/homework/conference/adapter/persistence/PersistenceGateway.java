package com.homework.conference.adapter.persistence;

import com.homework.conference.adapter.persistence.mapper.ConferenceEntityMapper;
import com.homework.conference.domain.Conference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersistenceGateway {

    private final ConferenceRepository repository;
    private final ConferenceEntityMapper mapper;

    public boolean existsByDate(Date date) {
        return repository.existsByDate(date);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public void save(Conference conference) {
        repository.save(mapper.toConferenceEntity(conference));
    }

    public Optional<Conference> findById(long conferenceId) {
        return repository.findById(conferenceId).map(mapper::toConference);
    }

    public List<Conference> findAll() {
        return mapper.toConferences(repository.findAll());
    }
}
