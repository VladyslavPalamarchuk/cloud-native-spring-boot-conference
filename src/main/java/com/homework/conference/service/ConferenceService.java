package com.homework.conference.service;

import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;

import java.util.List;

public interface ConferenceService {

    void addConference(Conference conference);

    void addTalk(long conferenceId, Talk talk);

    List<Conference> getAllConferences();

    List<Talk> getTalksByConference(long conferenceId);

    void updateConference(long conferenceId, Conference conference);
}
