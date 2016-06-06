package com.worth.ifs.competition.transactional;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.domain.CategoryType;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.transactional.CategoryLinkService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.mapper.CompetitionSetupSectionMapper;
import com.worth.ifs.competition.mapper.CompetitionSetupSectionStatusMapper;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.CompetitionSetupSectionRepository;
import com.worth.ifs.competition.repository.CompetitionSetupSectionStatusRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionStatusResource;
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
    CompetitionSetupSectionStatusRepository competitionSetupSectionStatusRepository;
    @Autowired
    CompetitionSetupSectionRepository competitionSetupSectionRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryLinkService categoryLinkService;
    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private CompetitionSetupSectionStatusMapper competitionSetupSectionStatusMapper;
    @Autowired
    private CompetitionSetupSectionMapper competitionSetupSectionMapper;

    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(Long id) {
        Competition competition = competitionRepository.findById(id);
        addCategories(competition);
        return serviceSuccess(competitionMapper.mapToResource(competition));
    }

    private void addCategories(Competition competition) {
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
    public ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource) {
        Competition competition = competitionMapper.mapToDomain(competitionResource);
        saveCategories(competitionResource);
        competition = competitionRepository.save(competition);
        addCategories(competition);
        return serviceSuccess(competitionMapper.mapToResource(competition));
    }

    private void saveCategories(CompetitionResource competitionResource) {
        saveInnovationArea(competitionResource);
        saveInnovationSector(competitionResource);
    }

    private void saveInnovationSector(CompetitionResource competitionResource) {
        Long sectorId = competitionResource.getInnovationSector();
        saveCategoryLink(competitionResource, sectorId, CategoryType.INNOVATION_SECTOR);
    }

    private void saveInnovationArea(CompetitionResource competitionResource) {
        Long areaId = competitionResource.getInnovationArea();
        saveCategoryLink(competitionResource, areaId, CategoryType.INNOVATION_AREA);
    }

    private void saveCategoryLink(CompetitionResource competitionResource, Long categoryId, CategoryType categoryType) {
        categoryLinkService.addOrUpdateOrDeleteLink(COMPETITION_CLASS_NAME, competitionResource.getId(), categoryType, categoryId);
    }

    @Override
    public ServiceResult<CompetitionResource> create() {
        Competition competition = new Competition();
        return serviceSuccess(competitionMapper.mapToResource(competitionRepository.save(competition)));
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(competitionRepository.findAll()));
    }

    @Override
    public ServiceResult<List<CompetitionSetupSectionStatusResource>> findAllCompetitionSectionsStatuses(Long competitionId) {
        return serviceSuccess((List) competitionSetupSectionStatusMapper.mapToResource(competitionSetupSectionStatusRepository.findByCompetitionId(competitionId)));
    }

    @Override
    public ServiceResult<List<CompetitionSetupSectionResource>> findAllCompetitionSections() {
        return serviceSuccess((List) competitionSetupSectionMapper.mapToResource(competitionSetupSectionRepository.findAllByOrderByPriorityAsc()));
    }
}
