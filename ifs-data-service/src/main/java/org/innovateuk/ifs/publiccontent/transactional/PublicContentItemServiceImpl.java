package org.innovateuk.ifs.publiccontent.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class PublicContentItemServiceImpl extends BaseTransactionalService implements PublicContentItemService {

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Autowired
    private PublicContentMapper publicContentMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    public static Integer MAX_ALLOWED_KEYWORDS = 10;

    private static Log LOG = LogFactory.getLog(PublicContentItemServiceImpl.class);

    @Override
    public ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Integer pageSize) {
        Page<Competition> publicContentPage = getPublicContentPage(innovationAreaId, searchString, pageNumber, pageSize);


        if(null == publicContentPage) {
            return ServiceResult.serviceFailure(new Error(GENERAL_NOT_FOUND));
        }

        return ServiceResult.serviceSuccess(mapPageToPageItemResource(publicContentPage));
    }


    private Page<Competition> getPublicContentPage(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Integer pageSize) {
        Page<Competition> publicContentPage;

        if(innovationAreaId.isPresent() && searchString.isPresent()) {
            List<Long> competitionsIdsInInnovationArea = getFilteredCompetitionIds(innovationAreaId);
            Set<Long> keywordsFound = getFilteredPublicContentIds(searchString.get());
            if (!keywordsFound.isEmpty()) {
                publicContentPage = publicContentRepository.findAllPublishedForOpenCompetitionByKeywordsAndInnovationId(keywordsFound, competitionsIdsInInnovationArea, getPageable(pageNumber, pageSize));
            } else {
                publicContentPage = new PageImpl<Competition>(emptyList());
            }
        }
        else if(innovationAreaId.isPresent()) {
            List<Long> competitionsIdsInInnovationArea = getFilteredCompetitionIds(innovationAreaId);
            publicContentPage = publicContentRepository.findAllPublishedForOpenCompetitionByInnovationId(competitionsIdsInInnovationArea,getPageable(pageNumber, pageSize));
        }
        else if(searchString.isPresent())
        {
            Set<Long> keywordsFound = getFilteredPublicContentIds(searchString.get());
            if (!keywordsFound.isEmpty()) {
                publicContentPage = publicContentRepository.findAllPublishedForOpenCompetitionByKeywords(keywordsFound, getPageable(pageNumber, pageSize));
            } else {
                publicContentPage = new PageImpl<Competition>(emptyList());
            }
        }
        else {
            publicContentPage = publicContentRepository.findAllPublishedForOpenCompetition(getPageable(pageNumber, pageSize));
        }

        return publicContentPage;
    }

    @Override
    public ServiceResult<PublicContentItemResource> byCompetitionId(Long id) {
        Competition competition = competitionRepository.findById(id);
        PublicContent publicContent = publicContentRepository.findByCompetitionId(id);

        if(null == competition || null == publicContent) {
            return ServiceResult.serviceFailure(new Error(GENERAL_NOT_FOUND));
        }

        return ServiceResult.serviceSuccess(mapPublicContentToPublicContentItemResource(publicContent, competition));
    }

    private List<String> separateSearchStringToList(String searchString) {
        return asList(searchString.replaceAll("[^A-Za-z0-9]", " ").split("\\s"));
    }

    private List<Long> getFilteredCompetitionIds(Optional<Long> innovationAreaId) {
        List<Long> competitionIds = new ArrayList<>();

        innovationAreaId.ifPresent(id -> {
            InnovationArea innovationArea = innovationAreaRepository.findOne(id);

            if (innovationArea != null) {
                competitionIds.addAll(simpleMap(
                        competitionRepository.findByInnovationSectorCategoryId(innovationArea.getSector().getId()),
                        Competition::getId
                ));
            }
        });

        return competitionIds;
    }

    private Set<Long> getFilteredPublicContentIds(String searchString) {
        Set<Long> publicContentIds = new HashSet<>();

        Set<Keyword> keywords = new HashSet<>();

        Integer i = 0;
        try {
            for (String keyword: separateSearchStringToList(UriUtils.decode(searchString, "UTF8"))) {
                i++;
                keywords.addAll(keywordRepository.findByKeywordLike("%"+ keyword + "%"));


                if(i >= MAX_ALLOWED_KEYWORDS) {
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Unable to decode searchstring");
        }

        keywords.forEach(keyword -> publicContentIds.add(keyword.getPublicContent().getId()));

        return publicContentIds;
    }

    private Pageable getPageable(Optional<Integer> pageNumber, Integer pageSize) {
        Integer pageNumberUsing = 0;

        if(pageNumber.isPresent()) {
            pageNumberUsing = pageNumber.get();
        }

        return new PageRequest(pageNumberUsing, pageSize);
    }

    private PublicContentItemResource mapPublicContentToPublicContentItemResource(PublicContent publicContent, Competition competition) {
        PublicContentItemResource publicContentItemResource = new PublicContentItemResource();
        publicContentItemResource.setPublicContentResource(publicContentMapper.mapToResource(publicContent));
        publicContentItemResource.setCompetitionOpenDate(competition.getStartDate());
        publicContentItemResource.setCompetitionCloseDate(competition.getEndDate());
        publicContentItemResource.setCompetitionTitle(competition.getName());
        publicContentItemResource.setNonIfs(competition.isNonIfs());
        publicContentItemResource.setNonIfsUrl(competition.getNonIfsUrl());

        return publicContentItemResource;
    }

    private PublicContentItemPageResource mapPageToPageItemResource(Page<Competition> competitionList) {
        PublicContentItemPageResource publicContentItemPageResource = new PublicContentItemPageResource();

        List<PublicContentItemResource> publicContentItemResources = new ArrayList<>();

        competitionList.getContent().forEach(competition -> {
            PublicContent publicContent = publicContentRepository.findByCompetitionId(competition.getId());
            PublicContentItemResource publicContentItemResource = mapPublicContentToPublicContentItemResource(publicContent, competition);
            publicContentItemResources.add(publicContentItemResource);
        });

        publicContentItemPageResource.setTotalElements(competitionList.getTotalElements());
        publicContentItemPageResource.setContent(publicContentItemResources);
        publicContentItemPageResource.setTotalPages(competitionList.getTotalPages());
        publicContentItemPageResource.setNumber(competitionList.getNumber());
        publicContentItemPageResource.setSize(competitionList.getSize());

        return publicContentItemPageResource;
    }
}
