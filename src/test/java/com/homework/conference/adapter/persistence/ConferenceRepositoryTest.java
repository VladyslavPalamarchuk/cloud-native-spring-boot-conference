package com.homework.conference.adapter.persistence;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DBRider
@ActiveProfiles("test")
class ConferenceRepositoryTest {

    @Autowired
    private ConferenceRepository repository;

    @Nested
    class ExistByTests {
        @Test
        void whenNoConferences_thenExistingByNameFalse() {
            assertThat(repository.existsByName("unknown")).isFalse();
        }

        @Test
        @DataSet("exist-conference.xml")
        void whenConferencesExist_thenExistingByNameTrue() {
            assertThat(repository.existsByName("conf-name")).isTrue();
        }
    }

}