package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.Competition;

import java.util.List;

public interface CompetitionOrganisationConfigRepository {

    List<Competition> findAll();

    List<Competition> findAllInternalApplicationAllowedIsTrue();
}
