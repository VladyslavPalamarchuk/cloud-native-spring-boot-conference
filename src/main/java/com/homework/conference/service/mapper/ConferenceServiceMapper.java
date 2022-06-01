package com.homework.conference.service.mapper;

import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import com.homework.conference.service.dto.AddOrUpdateConferenceRequestDto;
import com.homework.conference.service.dto.AddTalkRequestDto;
import com.homework.conference.service.dto.ConferenceDto;
import com.homework.conference.service.dto.TalkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConferenceServiceMapper {
    Conference toConference(AddOrUpdateConferenceRequestDto addConferenceRequest);

    ConferenceDto toConferenceDto(Conference conference);

    List<ConferenceDto> toConferencesDto(List<Conference> conferences);

    @Mapping(target = "id", source = "conferenceId")
    Conference toConference(AddOrUpdateConferenceRequestDto updateConferenceRequest, long conferenceId);

    List<TalkDto> toTalksDto(List<Talk> talks);

    Talk toTalk(AddTalkRequestDto addTalkRequest);

    TalkDto toTalkDto(Talk talk);

}
