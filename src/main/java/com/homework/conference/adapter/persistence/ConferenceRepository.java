package com.homework.conference.adapter.persistence;

import com.homework.conference.domain.Conference;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    List<Conference> findAll();

    Optional<Conference> findByName(String name);
}
