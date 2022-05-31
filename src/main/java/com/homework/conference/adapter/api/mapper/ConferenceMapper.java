package com.homework.conference.adapter.api.mapper;

import com.homework.adapter.model.AddConferenceRequestDto;
import com.homework.adapter.model.ConferenceDto;
import com.homework.adapter.model.TalkDto;
import com.homework.adapter.model.UpdateConferenceRequestDto;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConferenceMapper {

    @Mapping(target = "date", dateFormat = "yyyy-MM-dd")
    Conference toConference(AddConferenceRequestDto addConferenceRequestDto);

    @Mapping(target = "date", dateFormat = "yyyy-MM-dd")
    Conference toConference(UpdateConferenceRequestDto updateConferenceRequestDto);

    ConferenceDto toConferenceDto(Conference conference);

    Talk toTalk(TalkDto talk);

    TalkDto toTalkDto(Talk talk);

    default List<TalkDto> toTalks(List<Talk> talks) {
        return talks.stream().map(this::toTalkDto).collect(Collectors.toList());
    }

    default List<ConferenceDto> toConferences(List<Conference> talks) {
        return talks.stream().map(this::toConferenceDto).collect(Collectors.toList());
    }
}
