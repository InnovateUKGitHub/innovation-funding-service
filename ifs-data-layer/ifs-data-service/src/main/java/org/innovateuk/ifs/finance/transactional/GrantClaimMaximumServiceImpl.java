package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.stereotype.Service;

import java.util.Set;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GrantClaimMaximumServiceImpl implements GrantClaimMaximumService {

    private GrantClaimMaximumRepository grantClaimMaximumRepository;
    private CompetitionTypeRepository competitionTypeRepository;
    private CompetitionRepository competitionRepository;
    private GrantClaimMaximumMapper grantClaimMaximumMapper;
    private CompetitionMapper competitionMapper;

    public GrantClaimMaximumServiceImpl(GrantClaimMaximumRepository grantClaimMaximumRepository,
                                        CompetitionTypeRepository competitionTypeRepository,
                                        CompetitionRepository competitionRepository,
                                        GrantClaimMaximumMapper grantClaimMaximumMapper,
                                        CompetitionMapper competitionMapper) {
        this.grantClaimMaximumRepository = grantClaimMaximumRepository;
        this.competitionTypeRepository = competitionTypeRepository;
        this.competitionRepository = competitionRepository;
        this.grantClaimMaximumMapper = grantClaimMaximumMapper;
        this.competitionMapper = competitionMapper;
    }

    @Override
    public ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(Long id) {
        return find(grantClaimMaximumRepository.findOne(id), notFoundError(GrantClaimMaximum.class, id)).andOnSuccess(
                maximum -> serviceSuccess(grantClaimMaximumMapper.mapToResource(maximum)));
    }

    @Override
    public ServiceResult<Set<Long>> getGrantClaimMaximumsForCompetitionType(Long competitionTypeId) {
        return find(competitionTypeRepository.findOne(competitionTypeId), notFoundError(CompetitionTypeResource.class, competitionTypeId))
                .andOnSuccess(competitionType -> {
                    CompetitionResource template = competitionMapper.mapToResource(competitionType.getTemplate());
                    return serviceSuccess(template.getGrantClaimMaximums());
                });
    }

    @Override
    public ServiceResult<Set<Long>> getGrantClaimMaximumsForCompetition(Long competitionId) {
        return find(competitionRepository.findOne(competitionId), notFoundError(CompetitionResource.class, competitionId))
                .andOnSuccess(competition -> {
                    CompetitionResource competitionResource = competitionMapper.mapToResource(competition);
                    return serviceSuccess(competitionResource.getGrantClaimMaximums());
                });
    }

    @Override
    public ServiceResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource) {
        GrantClaimMaximum gcm = grantClaimMaximumRepository.save(grantClaimMaximumMapper.mapToDomain(grantClaimMaximumResource));
        return serviceSuccess(grantClaimMaximumMapper.mapToResource(gcm));
    }
}
