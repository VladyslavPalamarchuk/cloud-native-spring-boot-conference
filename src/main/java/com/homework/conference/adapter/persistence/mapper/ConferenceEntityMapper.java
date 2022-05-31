package com.homework.conference.adapter.persistence.mapper;

import com.homework.conference.adapter.persistence.entity.ConferenceEntity;
import com.homework.conference.adapter.persistence.entity.TalkEntity;
import com.homework.conference.domain.Conference;
import com.homework.conference.domain.Talk;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConferenceEntityMapper {

    ConferenceEntity toConferenceEntity(Conference conference);

    Conference toConference(ConferenceEntity conferenceEntity);

    TalkEntity toTalkEntity(Talk talk);

    List<Talk> toTalks(List<TalkEntity> talkEntities);

    List<Conference> toConferences(List<ConferenceEntity> conferenceEntities);
}
