package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.stereotype.Service;

/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
@Service
public interface CompetitionService {

    CompetitionResource getById(Long id);
}
