package com.homework.conference.adapter.api.mapper;

import com.homework.adapter.model.ConferenceDto;
import com.homework.adapter.model.TalkDto;
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
    Conference map(ConferenceDto conference);

    ConferenceDto map(Conference conference);

    Talk map(TalkDto talk);

    TalkDto map(Talk talk);

    default List<TalkDto> mapTalks(List<Talk> talks) {
        return talks.stream().map(this::map).collect(Collectors.toList());
    }

    default List<ConferenceDto> mapConferences(List<Conference> talks) {
        return talks.stream().map(this::map).collect(Collectors.toList());
    }
}
