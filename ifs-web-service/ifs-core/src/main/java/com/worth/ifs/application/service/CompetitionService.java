package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.CompetitionResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
@Service
public interface CompetitionService {
    CompetitionResource getById(Long id);
    List<CompetitionResource> getAllCompetitions();
}
