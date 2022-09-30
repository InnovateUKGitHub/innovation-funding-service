package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CompetitionEoiEvidenceConfigService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionEoiEvidenceConfigResource> create(CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionEoiDocumentResource> createDocument(CompetitionEoiDocumentResource competitionEoiDocumentResource);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'VIEW_EOI_EVIDENCE_CONFIG')")
    ServiceResult<CompetitionEoiEvidenceConfigResource> findOneByCompetitionId(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult <List<CompetitionEoiDocumentResource>> findAllByCompetitionEoiDocumentResources(long competitionEoiEvidenceConfigId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<Long>> getValidFileTypesIdsForEoiEvidence(long competitionEoiEvidenceConfigId);
}
