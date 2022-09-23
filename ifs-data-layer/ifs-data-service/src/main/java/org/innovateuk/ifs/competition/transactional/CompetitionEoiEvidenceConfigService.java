package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;

public interface CompetitionEoiEvidenceConfigService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionEoiEvidenceConfigResource> create(CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionEoiDocumentResource> createDocument(CompetitionEoiDocumentResource competitionEoiDocumentResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionEoiEvidenceConfigResource> findOneByCompetitionId(long competitionId);
}
