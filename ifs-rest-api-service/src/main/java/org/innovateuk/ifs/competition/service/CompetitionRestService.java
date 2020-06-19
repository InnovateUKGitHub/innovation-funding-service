package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;


/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.competition.resource.CompetitionResource} related data.
 */
public interface CompetitionRestService {
    RestResult<List<CompetitionResource>> getAll();

    RestResult<CompetitionResource> getCompetitionById(long competitionId);

    RestResult<CompetitionResource> getPublishedCompetitionById(long competitionId);

    RestResult<List<CompetitionTypeResource>> getCompetitionTypes();

    RestResult<List<OrganisationTypeResource>> getCompetitionOrganisationType(long id);

    RestResult<Void> updateTermsAndConditionsForCompetition(long competitionId, long termsAndConditionsId);

    RestResult<ByteArrayResource> downloadTerms(long competitionId);
}