package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.repository.CompetitionCategoryLinkRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.innovateuk.ifs.category.resource.CategoryType.RESEARCH_CATEGORY;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    private static final Log LOG = LogFactory.getLog(CompetitionServiceImpl.class);

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitionCategoryLinkRepository competitionCategoryLinkRepository;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(Long id) {
        Competition competition = competitionRepository.findById(id);
        if (competition == null) {
            return serviceFailure(notFoundError(Competition.class, id));
        }

        addCategories(competition);
        return serviceSuccess(competitionMapper.mapToResource(competition));
    }

    @Override
    public Competition addCategories(Competition competition) {
        addInnovationSector(competition);
        addInnovationAreas(competition);
        addResearchCategories(competition);
        return competition;
    }

    private void addInnovationSector(Competition competition) {
        Category category =
                Optional.ofNullable(
                        competitionCategoryLinkRepository.findByCompetitionIdAndCategory_Type(competition.getId(), INNOVATION_SECTOR)
                ).map(CompetitionCategoryLink::getCategory).orElse(null);
        competition.setInnovationSector(category);
    }

    private void addInnovationAreas(Competition competition) {
        Set<Category> categories = competitionCategoryLinkRepository.findAllByCompetitionIdAndCategoryType(competition.getId(), INNOVATION_AREA)
                .stream()
                .map(CompetitionCategoryLink::getCategory)
                .collect(Collectors.toSet());

        competition.setInnovationAreas(categories);
    }

    private void addResearchCategories(Competition competition) {
        Set<Category> categories = competitionCategoryLinkRepository.findAllByCompetitionIdAndCategoryType(competition.getId(), RESEARCH_CATEGORY)
                .stream()
                .map(CompetitionCategoryLink::getCategory)
                .collect(Collectors.toSet());
        competition.setResearchCategories(categories);
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(
                competitionRepository.findAll().stream().filter(comp -> !comp.isTemplate()).map(this::addCategories).collect(Collectors.toList())
        ));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions() {
        List<Competition> competitions = competitionRepository.findLive().stream().map(this::addCategories).collect(Collectors.toList());
        return serviceSuccess(simpleMap(competitions, this::searchResultFromCompetition));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions() {
        List<Competition> competitions = competitionRepository.findProjectSetup().stream().map(this::addCategories).collect(Collectors.toList());
        return serviceSuccess(simpleMap(competitions, this::searchResultFromCompetition));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions() {
        List<Competition> competitions = competitionRepository.findUpcoming().stream().map(this::addCategories).collect(Collectors.toList());
        return serviceSuccess(simpleMap(competitions, this::searchResultFromCompetition));
    }

    @Override
    public ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
        String searchQueryLike = String.format("%%%s%%", searchQuery);
        PageRequest pageRequest = new PageRequest(page, size);
        Page<Competition> pageResult = competitionRepository.search(searchQueryLike, pageRequest);

        CompetitionSearchResult result = new CompetitionSearchResult();
        List<Competition> competitions = pageResult.getContent().stream().map(this::addCategories).collect(Collectors.toList());
        result.setContent(simpleMap(competitions, this::searchResultFromCompetition));
        result.setNumber(pageRequest.getPageNumber());
        result.setSize(size);
        result.setTotalElements(pageResult.getTotalElements());
        result.setTotalPages(pageResult.getTotalPages());

        return serviceSuccess(result);
    }

    private CompetitionSearchResultItem searchResultFromCompetition(Competition c) {
        return new CompetitionSearchResultItem(c.getId(),
                c.getName(),
                ofNullable(c.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream().map(Category::getName).collect(Collectors.toSet()),
                c.getApplications().size(),
                c.startDateDisplay(),
                c.getCompetitionStatus(),
                ofNullable(c.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                projectRepository.findByApplicationCompetitionId(c.getId()).size()
        );
    }

    @Override
    public ServiceResult<CompetitionCountResource> countCompetitions() {
        //TODO INFUND-3833 populate complete count
        return serviceSuccess(new CompetitionCountResource(competitionRepository.countLive(), competitionRepository.countProjectSetup(),
                competitionRepository.countUpcoming(), 0L));
    }

    @Override
    public ServiceResult<Void> closeAssessment(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.closeAssessment(LocalDateTime.now());
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> notifyAssessors(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.notifyAssessors(LocalDateTime.now());
        return serviceSuccess();
    }
}
