package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.CompetitionCategoryLinkRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class PublicContentItemServiceImpl extends BaseTransactionalService implements PublicContentItemService {

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private CompetitionCategoryLinkRepository competitionCategoryLinkRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Autowired
    private PublicContentMapper publicContentMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    private static Integer MAX_ALLOWED_KEYWORDS = 10;

    private static Integer DEFAULT_PAGESIZE = 20;

    @Override
    public ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        List<Long> competitionIds = handleInnovationAreaId(innovationAreaId);
        Set<Long> publicContentIds = handleSearchString(searchString);

        Page<PublicContent> publicContentList;

        if(innovationAreaId.isPresent() && searchString.isPresent()) {
            publicContentList = publicContentRepository.findByCompetitionIdInAndIdIn(competitionIds, publicContentIds, getPageable(pageNumber, pageSize));
        } else if(innovationAreaId.isPresent()) {
            publicContentList = publicContentRepository.findByCompetitionIdIn(competitionIds, getPageable(pageNumber, pageSize));
        } else if(searchString.isPresent()) {
            publicContentList = publicContentRepository.findByIdIn(publicContentIds, getPageable(pageNumber, pageSize));
        } else {
            publicContentList = publicContentRepository.findAll(getPageable(pageNumber, pageSize));
        }

        return ServiceResult.serviceSuccess(mapPageToPageItemResource(publicContentList));
    }

    @Override
    public ServiceResult<PublicContentItemResource> byCompetitionId(Long id) {
        //TODO : Implement Method
        return null;
    }

    private List<String> separateSearchStringToList(String searchString) {
        return asList(searchString.replaceAll("[^A-Za-z0-9]", " ").split("\\s"));
    }


    private List<Long> handleInnovationAreaId(Optional<Long> innovationAreaId) {
        List<Long> competitionIds = new ArrayList<>();

        innovationAreaId.ifPresent(id -> {
            InnovationArea innovationArea = innovationAreaRepository.findOne(innovationAreaId.get());
            competitionIds.addAll(competitionCategoryLinkRepository.findByCategoryId(innovationArea.getSector().getId()).stream()
                    .map(competitionCategoryLink -> competitionCategoryLink.getEntity().getId())
                    .collect(Collectors.toList()));
        });

        return competitionIds;
    }

    private Set<Long> handleSearchString(Optional<String> searchString) {
        Set<Long> publicContentIds = new HashSet<>();

        searchString.ifPresent(s -> {
            Set<Keyword> keywords = new HashSet<>();

            Integer i = 0;
            for (String keyword: separateSearchStringToList(s)) {
                i++;

                keywords.addAll(keywordRepository.findByKeywordLike(keyword));

                if(i >= MAX_ALLOWED_KEYWORDS) {
                    break;
                }
            }

            keywords.forEach(keyword -> publicContentIds.add(keyword.getPublicContent().getId()));
        });

        return publicContentIds;
    }

    private Pageable getPageable(Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        Integer pageNumberUsing = 0;
        Integer pageSizeUsing = DEFAULT_PAGESIZE;


        if(pageSize.isPresent()) {
            pageSizeUsing = pageSize.get();
        }
        if(pageNumber.isPresent()) {
            pageNumberUsing = pageNumber.get();
        }

        return new PageRequest(pageNumberUsing, pageSizeUsing);
    }

    private PublicContentItemPageResource mapPageToPageItemResource(Page<PublicContent> publicContentList) {
        PublicContentItemPageResource publicContentItemPageResource = new PublicContentItemPageResource();

        List<PublicContentItemResource> publicContentItemResources = new ArrayList<>();

        publicContentList.forEach(publicContent -> {
            PublicContentItemResource publicContentItemResource = new PublicContentItemResource();

            Competition competition = competitionRepository.findById(publicContent.getCompetitionId());

            publicContentItemResource.setPublicContentResource(publicContentMapper.mapToResource(publicContent));
            publicContentItemResource.setCompetitionOpenDate(competition.getStartDate());
            publicContentItemResource.setCompetitionCloseDate(competition.getEndDate());
            publicContentItemResource.setCompetitionTitle(competition.getName());

            publicContentItemResources.add(publicContentItemResource);
        });

        publicContentItemPageResource.setContent(publicContentItemResources);
        publicContentItemPageResource.setTotalPages(publicContentItemPageResource.getTotalPages());
        publicContentItemPageResource.setTotalElements(publicContentItemPageResource.getTotalElements());
        publicContentItemPageResource.setNumber(publicContentItemPageResource.getNumber());
        publicContentItemPageResource.setSize(publicContentItemPageResource.getSize());

        return publicContentItemPageResource;
    }
}
