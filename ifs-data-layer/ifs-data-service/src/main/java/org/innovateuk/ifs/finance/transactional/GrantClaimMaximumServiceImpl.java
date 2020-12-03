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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
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
    public ServiceResult<List<GrantClaimMaximumResource>> getGrantClaimMaximumByCompetitionId(long competitionId) {
        return find(grantClaimMaximumRepository.findByCompetitionsId(competitionId), notFoundError(GrantClaimMaximum.class, competitionId))
                .andOnSuccessReturn(maximums ->
                        maximums.stream().map(grantClaimMaximumMapper::mapToResource)
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ServiceResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource) {
        return find(grantClaimMaximumRepository.findById(grantClaimMaximumResource.getId()), notFoundError(GrantClaimMaximum.class, grantClaimMaximumResource.getId())).andOnSuccessReturn((maximum) -> {
            maximum.setMaximum(grantClaimMaximumResource.getMaximum());
            return grantClaimMaximumMapper.mapToResource(grantClaimMaximumRepository.save(maximum));
        });
    }

    @Override
    public ServiceResult<Boolean> isMaximumFundingLevelOverridden(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> {
            if (competition.isNonFinanceType()) {
                return false;
            }
            long count = competition.getGrantClaimMaximums().stream()
                    .map(GrantClaimMaximum::getMaximum)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
            return count == 1;
        });
    }

    @Override
    @Transactional
    public ServiceResult<Set<Long>> revertToDefault(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> {
            List<GrantClaimMaximum> maximums;
            if (TRUE.equals(competition.getStateAid()) && !competition.getResearchCategories().isEmpty()) {
                maximums = commonBuilders.getStateAidGrantClaimMaxmimums();
            } else {
                maximums = commonBuilders.getBlankGrantClaimMaxmimums();
            }
            competition.getGrantClaimMaximums().clear();
            Set<Long> ids = new HashSet<>();
                maximums.forEach(maximum -> {
                maximum.setCompetitions(Lists.newArrayList(competition));
                ids.add(grantClaimMaximumRepository.save(maximum).getId());
                competition.getGrantClaimMaximums().add(maximum);
            });
            return ids;
        });
    }
}
