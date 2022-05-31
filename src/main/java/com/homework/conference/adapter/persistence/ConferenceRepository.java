package com.homework.conference.adapter.persistence;

import com.homework.conference.domain.Conference;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    List<Conference> findAll();

    boolean existsByName(String name);

    boolean existsByDate(Date date);
}
