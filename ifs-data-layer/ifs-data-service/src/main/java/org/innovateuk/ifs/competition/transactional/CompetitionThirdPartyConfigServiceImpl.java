package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.domain.CompetitionThirdPartyConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionThirdPartyConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionThirdPartyConfigRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionThirdPartyConfigServiceImpl extends RootTransactionalService implements CompetitionThirdPartyConfigService {

    @Autowired
    private CompetitionThirdPartyConfigRepository competitionThirdPartyConfigRepository;

    @Autowired
    private CompetitionThirdPartyConfigMapper mapper;

    @Override
    public ServiceResult<CompetitionThirdPartyConfigResource> findOneByCompetitionId(long competitionId) {
        return find(competitionThirdPartyConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionThirdPartyConfig.class, competitionId))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> update(long competitionId, CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource) {
        return find(competitionThirdPartyConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionThirdPartyConfig.class, competitionId))
                .andOnSuccessReturnVoid((config) -> {
                    config.setTermsAndConditionsLabel(competitionThirdPartyConfigResource.getTermsAndConditionsLabel());
                    config.setTermsAndConditionsGuidance(competitionThirdPartyConfigResource.getTermsAndConditionsGuidance());
                    config.setProjectCostGuidanceUrl(competitionThirdPartyConfigResource.getProjectCostGuidanceUrl());
                });
    }
}
