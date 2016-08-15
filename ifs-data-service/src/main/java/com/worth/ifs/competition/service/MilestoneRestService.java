package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.Milestone} related data.
 */
public interface MilestoneRestService {

    RestResult<List<MilestoneResource>> getAllDatesByCompetitionId(Long competitionId);

    RestResult<Void> update(List<MilestoneResource> milestones, Long competitionId);

    RestResult<MilestoneResource> create(MilestoneType type, Long competitionId);
}
