package org.innovateuk.ifs.finance.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GrantClaimMaximumServiceImpl extends BaseTransactionalService implements GrantClaimMaximumService {

    @Autowired
    private GrantClaimMaximumRepository grantClaimMaximumRepository;
    @Autowired
    private GrantClaimMaximumMapper grantClaimMaximumMapper;
    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id) {
        return find(grantClaimMaximumRepository.findById(id), notFoundError(GrantClaimMaximum.class, id)).andOnSuccess(
                maximum -> serviceSuccess(grantClaimMaximumMapper.mapToResource(maximum)));
    }

    @Override
    @Transactional
    public ServiceResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource) {
        GrantClaimMaximum gcm = grantClaimMaximumRepository.save(grantClaimMaximumMapper.mapToDomain(grantClaimMaximumResource));
        return serviceSuccess(grantClaimMaximumMapper.mapToResource(gcm));
    }

    @Override
    public ServiceResult<Boolean> isMaximumFundingLevelOverridden(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> {
            if (competition.isNonFinanceType()) {
                return false;
            }
            List<GrantClaimMaximum> competitionGrantClaimMaximums = competition.getGrantClaimMaximums();
            List<GrantClaimMaximum> defaultGrantClaimMaximums = commonBuilders.getDefaultGrantClaimMaximums();
            if (competitionGrantClaimMaximums.size() == defaultGrantClaimMaximums.size()) {
                boolean mismatchFound = false;
                for (GrantClaimMaximum competitionMaximum : competitionGrantClaimMaximums) {
                    mismatchFound = defaultGrantClaimMaximums.stream().noneMatch(
                            defaultMaximum ->
                                    defaultMaximum.getMaximum().equals(competitionMaximum.getMaximum())
                                    && defaultMaximum.getResearchCategory().getId().equals(competitionMaximum.getResearchCategory().getId())
                                    && defaultMaximum.getOrganisationSize() == competitionMaximum.getOrganisationSize()
                    );
                }
                return mismatchFound;
            }
            return true;
        });
    }

    @Override
    @Transactional
    public ServiceResult<Set<Long>> revertToDefault(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> {
            Set<Long> ids = new HashSet<>();
            commonBuilders.getDefaultGrantClaimMaximums().forEach(maximum -> {
                maximum.setCompetitions(Lists.newArrayList(competition));
                ids.add(grantClaimMaximumRepository.save(maximum).getId());
            });
            return ids;
        });
    }
}
