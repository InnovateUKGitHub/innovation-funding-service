package com.worth.ifs.application.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for CRUD operations on {@link MilestoneResource} related data.
 */
@Service
public interface MilestoneService {
    List<MilestoneResource> getAllDatesByCompetitionId(Long competitionId);

    List<Error> update(List<MilestoneResource> milestones, Long competitionId);

    MilestoneResource create(MilestoneType type, Long competitionId);
}