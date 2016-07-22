package com.worth.ifs.application.service;

import com.worth.ifs.commons.error.Error;
import org.springframework.stereotype.Service;
import com.worth.ifs.competition.resource.MilestoneResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link MilestoneResource} related data.
 */
@Service
public interface MilestoneService {
    List<MilestoneResource> getAllDatesByCompetitionId(Long competitionId);

    List<Error> update(List<MilestoneResource> milestones, Long competitionId);

    MilestoneResource create();
}
