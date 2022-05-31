package com.homework.conference.adapter.persistence;

import com.homework.conference.adapter.persistence.entity.ConferenceEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ConferenceRepository extends CrudRepository<ConferenceEntity, Long> {
    List<ConferenceEntity> findAll();

    boolean existsByName(String name);

    boolean existsByDate(Date date);
}
