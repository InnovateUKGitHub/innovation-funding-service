package com.worth.ifs.competition.transactional;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.CompetitionSetupCompletedSectionRepository;
import com.worth.ifs.competition.repository.CompetitionSetupSectionRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {
    public static final String COMPETITION_CLASS_NAME = Competition.class.getName();
    private static final Log LOG = LogFactory.getLog(CompetitionServiceImpl.class);
    @Autowired
    CategoryLinkRepository categoryLinkRepository;
    @Autowired
    CompetitionSetupCompletedSectionRepository competitionSetupSectionStatusRepository;
    @Autowired
    CompetitionSetupSectionRepository competitionSetupSectionRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private CompetitionMapper competitionMapper;


    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(Long id) {
        Competition competition = competitionRepository.findById(id);
        addCategories(competition);
        return serviceSuccess(competitionMapper.mapToResource(competition));
    }

    @Override
    public void addCategories(Competition competition) {
        addInnovationSector(competition);
        addInnovationArea(competition);
    }

    private void addInnovationSector(Competition competition) {
        Category category = categoryRepository.findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType.INNOVATION_SECTOR, COMPETITION_CLASS_NAME, competition.getId());
        competition.setInnovationSector(category);
    }

    private void addInnovationArea(Competition competition) {
        Category category = categoryRepository.findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType.INNOVATION_AREA, COMPETITION_CLASS_NAME, competition.getId());
        competition.setInnovationArea(category);
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(competitionRepository.findAll()));
    }

}
