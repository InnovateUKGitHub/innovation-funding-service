package com.worth.ifs.competition.transactional;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.transactional.BaseTransactionalService;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {
    
	private static final Log LOG = LogFactory.getLog(CompetitionServiceImpl.class);
	
	public static final String COMPETITION_CLASS_NAME = Competition.class.getName();
    
    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
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
        addResearchCategories(competition);
    }

    private void addInnovationSector(Competition competition) {
        Category category = categoryRepository.findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType.INNOVATION_SECTOR, COMPETITION_CLASS_NAME, competition.getId());
        competition.setInnovationSector(category);
    }

    private void addInnovationArea(Competition competition) {
        Category category = categoryRepository.findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType.INNOVATION_AREA, COMPETITION_CLASS_NAME, competition.getId());
        competition.setInnovationArea(category);
    }
    
    private void addResearchCategories(Competition competition) {
        Set<Category> categories = categoryRepository.findAllByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType.RESEARCH_CATEGORY, COMPETITION_CLASS_NAME, competition.getId());
        competition.setResearchCategories(categories);
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(competitionRepository.findAll()));
    }

}
