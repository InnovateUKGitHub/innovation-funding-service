package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GrantClaimMaximumServiceImpl extends BaseTransactionalService implements GrantClaimMaximumService {

    private GrantClaimMaximumRepository grantClaimMaximumRepository;
    private CompetitionTypeRepository competitionTypeRepository;
    private GrantClaimMaximumMapper grantClaimMaximumMapper;

    public GrantClaimMaximumServiceImpl(GrantClaimMaximumRepository grantClaimMaximumRepository,
                                        CompetitionTypeRepository competitionTypeRepository,
                                        CompetitionRepository competitionRepository,
                                        GrantClaimMaximumMapper grantClaimMaximumMapper) {
        this.grantClaimMaximumRepository = grantClaimMaximumRepository;
        this.competitionTypeRepository = competitionTypeRepository;
        this.competitionRepository = competitionRepository;
        this.grantClaimMaximumMapper = grantClaimMaximumMapper;
    }

    @Override
    public ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id) {
        return find(grantClaimMaximumRepository.findOne(id), notFoundError(GrantClaimMaximum.class, id)).andOnSuccess(
                maximum -> serviceSuccess(grantClaimMaximumMapper.mapToResource(maximum)));
    }

    @Override
    public ServiceResult<Set<Long>> getGrantClaimMaximumsForCompetitionType(long competitionTypeId) {
        return find(competitionTypeRepository.findOne(competitionTypeId), notFoundError(CompetitionTypeResource.class, competitionTypeId))
                .andOnSuccessReturn(competitionType -> competitionType.getTemplate().getGrantClaimMaximums().stream().map
                        (GrantClaimMaximum::getId).collect(toSet()));
    }

    @Override
    @Transactional
    public ServiceResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource) {
        GrantClaimMaximum gcm = grantClaimMaximumRepository.save(grantClaimMaximumMapper.mapToDomain(grantClaimMaximumResource));
        return serviceSuccess(grantClaimMaximumMapper.mapToResource(gcm));
    }

    @Override
    public ServiceResult<Boolean> isMaximumFundingLevelOverridden(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> !competition.getGrantClaimMaximums()
                .equals(competition.getCompetitionType().getTemplate()
                .getGrantClaimMaximums()));
    }
}
