package org.innovateuk.ifs.publiccontent.transactional;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.CompetitionCategoryLinkRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
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
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;

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

    public static Integer MAX_ALLOWED_KEYWORDS = 10;

    public static Integer DEFAULT_PAGE_SIZE = 20;

    private static Log LOG = LogFactory.getLog(PublicContentItemServiceImpl.class);

    @Override
    public ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        Optional<List<Long>> competitionIds = getInnovationAreaId(innovationAreaId);
        Optional<Set<Long>> publicContentIds = getSearchString(searchString);

        Page<PublicContent> publicContentPage = getPublicContentPage(competitionIds, publicContentIds, pageNumber, pageSize);

        if(null == publicContentPage) {
            return ServiceResult.serviceFailure(new Error(GENERAL_NOT_FOUND));
        }

        return ServiceResult.serviceSuccess(mapPageToPageItemResource(publicContentPage));
    }

    private Page<PublicContent> getPublicContentPage(Optional<List<Long>> competitionIds, Optional<Set<Long>> publicContentIds, Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        Page<PublicContent> publicContentPage;

        if(competitionIds.isPresent() && publicContentIds.isPresent()) {
            publicContentPage = publicContentRepository.findByCompetitionIdInAndIdIn(competitionIds.get(), publicContentIds.get(), getPageable(pageNumber, pageSize));
        } else if(competitionIds.isPresent()) {
            publicContentPage = publicContentRepository.findByCompetitionIdIn(competitionIds.get(), getPageable(pageNumber, pageSize));
        } else if(publicContentIds.isPresent()) {
            publicContentPage = publicContentRepository.findByIdIn(publicContentIds.get(), getPageable(pageNumber, pageSize));
        } else {
            publicContentPage = publicContentRepository.findAll(getPageable(pageNumber, pageSize));
        }

        return publicContentPage;
    }

    @Override
    public ServiceResult<PublicContentItemResource> byCompetitionId(Long id) {
        //TODO : INFUND-7484
        throw new NotImplementedException("Not implemented yet");
    }

    private List<String> separateSearchStringToList(String searchString) {
        return asList(searchString.replaceAll("[^A-Za-z0-9]", " ").split("\\s"));
    }

    private Optional<List<Long>> getInnovationAreaId(Optional<Long> innovationAreaId) {
        List<Long> competitionIds = new ArrayList<>();

        innovationAreaId.ifPresent(id -> {
            InnovationArea innovationArea = innovationAreaRepository.findOne(id);
            if(null != innovationArea) {
                competitionIds.addAll(competitionCategoryLinkRepository.findByCategoryId(innovationArea.getSector().getId()).stream()
                        .map(competitionCategoryLink -> competitionCategoryLink.getEntity().getId())
                        .collect(Collectors.toList()));
            }
        });

        return innovationAreaId.isPresent() ? Optional.of(competitionIds) : Optional.empty();
    }

    private Optional<Set<Long>> getSearchString(Optional<String> searchString) {
        Set<Long> publicContentIds = new HashSet<>();

        searchString.ifPresent(s -> {
            Set<Keyword> keywords = new HashSet<>();

            Integer i = 0;
            try {
                for (String keyword: separateSearchStringToList(UriUtils.decode(s, "UTF8"))) {
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
        });

        return searchString.isPresent() ? Optional.of(publicContentIds) : Optional.empty();
    }

    private Pageable getPageable(Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        Integer pageNumberUsing = 0;
        Integer pageSizeUsing = DEFAULT_PAGE_SIZE;


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

        publicContentList.getContent().forEach(publicContent -> {
            PublicContentItemResource publicContentItemResource = new PublicContentItemResource();

            Competition competition = competitionRepository.findById(publicContent.getCompetitionId());

            if(CompetitionStatus.OPEN.equals(competition.getCompetitionStatus()) && null != competition.getEndDate()) {
                publicContentItemResource.setPublicContentResource(publicContentMapper.mapToResource(publicContent));
                publicContentItemResource.setCompetitionOpenDate(competition.getStartDate());
                publicContentItemResource.setCompetitionCloseDate(competition.getEndDate());
                publicContentItemResource.setCompetitionTitle(competition.getName());

                publicContentItemResources.add(publicContentItemResource);
            }
        });

        publicContentItemResources.sort((o1, o2) -> o1.getCompetitionCloseDate().compareTo(o2.getCompetitionCloseDate()));

        publicContentItemPageResource.setContent(publicContentItemResources);
        publicContentItemPageResource.setTotalPages(publicContentList.getTotalPages());
        publicContentItemPageResource.setTotalElements(publicContentList.getTotalElements());
        publicContentItemPageResource.setNumber(publicContentList.getNumber());
        publicContentItemPageResource.setSize(publicContentList.getSize());

        return publicContentItemPageResource;
    }
}
