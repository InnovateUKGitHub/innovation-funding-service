package com.worth.ifs.application.service;

import java.util.List;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;

import org.springframework.stereotype.Service;

/**
 * Interface for CRUD operations on {@link Competition} related data.
 */
@Service
public interface CompetitionService {
    CompetitionResource getById(Long id);
    List<CompetitionResource> getAllCompetitions();
}
