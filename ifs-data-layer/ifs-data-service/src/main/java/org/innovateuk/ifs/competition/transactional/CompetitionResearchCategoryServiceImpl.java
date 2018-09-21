package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionResearchCategoryLink;
import org.innovateuk.ifs.competition.mapper.CompetitionResearchCategoryMapper;
import org.innovateuk.ifs.competition.repository.CompetitionResearchCategoryLinkRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and research categories for a competition
 */
@Service
public class CompetitionResearchCategoryServiceImpl implements CompetitionResearchCategoryService {

    private CompetitionResearchCategoryLinkRepository competitionResearchCategoryLinkRepository;
    private CompetitionResearchCategoryMapper competitionResearchCategoryMapper;

    public CompetitionResearchCategoryServiceImpl(CompetitionResearchCategoryLinkRepository competitionResearchCategoryLinkRepository,
                                                  CompetitionResearchCategoryMapper competitionResearchCategoryMapper) {
        this.competitionResearchCategoryLinkRepository = competitionResearchCategoryLinkRepository;
        this.competitionResearchCategoryMapper = competitionResearchCategoryMapper;
    }

    @Override
    public ServiceResult<List<CompetitionResearchCategoryLinkResource>> findByCompetition(Long competitionId) {
        return find(competitionResearchCategoryLinkRepository.findAllByCompetitionId(competitionId), notFoundError(CompetitionResearchCategoryLink.class))
                .andOnSuccessReturn(competitionResearchCategoryMapper::mapToResource);
    }
}