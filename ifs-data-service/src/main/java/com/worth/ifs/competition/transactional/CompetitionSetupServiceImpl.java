package com.worth.ifs.competition.transactional;

import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.transactional.CategoryLinkService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.CompetitionSetupCompletedSection;
import com.worth.ifs.competition.domain.CompetitionSetupSection;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.mapper.CompetitionSetupCompletedSectionMapper;
import com.worth.ifs.competition.mapper.CompetitionSetupSectionMapper;
import com.worth.ifs.competition.mapper.CompetitionTypeMapper;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.CompetitionSetupCompletedSectionRepository;
import com.worth.ifs.competition.repository.CompetitionSetupSectionRepository;
import com.worth.ifs.competition.repository.CompetitionTypeRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.transactional.CompetitionServiceImpl.COMPETITION_CLASS_NAME;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionSetupServiceImpl extends BaseTransactionalService implements CompetitionSetupService {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);
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
    CategoryLinkService categoryLinkService;
    @Autowired
    CompetitionService competitionService;
    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private CompetitionTypeMapper competitionTypeMapper;
    @Autowired
    private CompetitionSetupCompletedSectionMapper competitionSetupSectionStatusMapper;
    @Autowired
    private CompetitionSetupSectionMapper competitionSetupSectionMapper;
    @Autowired
    CompetitionTypeRepository competitionTypeRepository;


    protected Supplier<ServiceResult<CompetitionSetupSection>> competitionSetupSection(final Long id) {
        return () -> getCompetitionSetupSection(id);
    }

    protected ServiceResult<CompetitionSetupSection> getCompetitionSetupSection(final Long id) {
        return find(competitionSetupSectionRepository.findOne(id), notFoundError(CompetitionSetupSection.class, id));
    }

    @Override
    public ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource) {
        Competition competition = competitionMapper.mapToDomain(competitionResource);
        saveCategories(competitionResource);
        competition = competitionRepository.save(competition);
        competitionService.addCategories(competition);
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
    public ServiceResult<List<CompetitionSetupCompletedSectionResource>> findAllCompetitionSectionsStatuses(Long competitionId) {
        return serviceSuccess((List) competitionSetupSectionStatusMapper.mapToResource(competitionSetupSectionStatusRepository.findByCompetitionId(competitionId)));
    }

    @Override
    public ServiceResult<List<CompetitionSetupSectionResource>> findAllCompetitionSections() {
        return serviceSuccess((List) competitionSetupSectionMapper.mapToResource(competitionSetupSectionRepository.findAllByOrderByPriorityAsc()));
    }

    @Override
    public ServiceResult<Void> markSectionComplete(Long competitionId, Long sectionId) {
        Optional<CompetitionSetupCompletedSection> alreadyComplete = competitionSetupSectionStatusRepository.findByCompetitionIdAndCompetitionSetupSectionId(competitionId, sectionId);
        if (!alreadyComplete.isPresent()) {
            find(competition(competitionId), competitionSetupSection(sectionId)).andOnSuccess((competition, section) -> saveCompletedSection(competition, section));
        }
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> markSectionInComplete(Long competitionId, Long sectionId) {
        Optional<CompetitionSetupCompletedSection> alreadyComplete = competitionSetupSectionStatusRepository.findByCompetitionIdAndCompetitionSetupSectionId(competitionId, sectionId);
        if (alreadyComplete.isPresent()) {
            competitionSetupSectionStatusRepository.delete(alreadyComplete.get());
        }
        return serviceSuccess();
    }

    private <T> ServiceResult<Void> saveCompletedSection(Competition competition, CompetitionSetupSection section) {
        CompetitionSetupCompletedSection completedSection = new CompetitionSetupCompletedSection(section, competition);
        competitionSetupSectionStatusRepository.save(completedSection);
        return serviceSuccess();
    }


    @Override
    public ServiceResult<List<CompetitionTypeResource>> findAllTypes() {
        return serviceSuccess((List) competitionTypeMapper.mapToResource(competitionTypeRepository.findAll()));
    }
}
