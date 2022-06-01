package com.homework.conference.service;

import com.homework.conference.service.dto.AddOrUpdateConferenceRequestDto;
import com.homework.conference.service.dto.AddTalkRequestDto;
import com.homework.conference.service.dto.ConferenceDto;
import com.homework.conference.service.dto.TalkDto;

import java.util.List;

public interface ConferenceService {

    ConferenceDto addConference(AddOrUpdateConferenceRequestDto conference);

    TalkDto addTalk(long conferenceId, AddTalkRequestDto talk);

    List<ConferenceDto> getAllConferences();

    List<TalkDto> getTalksByConference(long conferenceId);

    void updateConference(long conferenceId, AddOrUpdateConferenceRequestDto conference);
}
