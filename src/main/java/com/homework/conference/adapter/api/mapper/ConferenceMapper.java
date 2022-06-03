package com.homework.conference.adapter.api.mapper;

import com.homework.adapter.api.dto.AddConferenceRequestApiDto;
import com.homework.adapter.api.dto.AddTalkRequestApiDto;
import com.homework.adapter.api.dto.ConferenceApiDto;
import com.homework.adapter.api.dto.TalkApiDto;
import com.homework.adapter.api.dto.UpdateConferenceRequestApiDto;
import com.homework.conference.service.dto.AddOrUpdateConferenceRequestDto;
import com.homework.conference.service.dto.AddTalkRequestDto;
import com.homework.conference.service.dto.ConferenceDto;
import com.homework.conference.service.dto.TalkDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConferenceMapper {

    AddOrUpdateConferenceRequestDto toAddOrUpdateConferenceRequest(AddConferenceRequestApiDto addConferenceRequest);

    AddOrUpdateConferenceRequestDto toAddOrUpdateConferenceRequest(UpdateConferenceRequestApiDto updateConference);

    AddTalkRequestDto toAddTalkDto(AddTalkRequestApiDto addTalkRequest);

    List<ConferenceApiDto> toConferencesApiDto(List<ConferenceDto> conferences);

    List<TalkApiDto> toTalksApiDto(List<TalkDto> talks);

    ConferenceApiDto toConferenceApiDto(ConferenceDto conference);

    TalkApiDto toTalkApiDto(TalkDto talk);

//    @Mapping(target = "date", dateFormat = "yyyy-MM-dd")

}
