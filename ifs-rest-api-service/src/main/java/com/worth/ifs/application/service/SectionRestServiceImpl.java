package com.worth.ifs.application.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.BaseRestService;

import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * SectionRestServiceImpl is a utility for CRUD operations on {@link SectionResource}.
 * This class connects to the {com.worth.ifs.application.controller.SectionController}
 * through a REST call.
 */
@Service
public class SectionRestServiceImpl extends BaseRestService implements SectionRestService {

    private String sectionRestURL = "/section";

    @Override
    public RestResult<List<ValidationMessages>> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        return getWithRestResult(sectionRestURL + "/markAsComplete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById, new ParameterizedTypeReference<List<ValidationMessages>>() {
        });
    }

    @Override
    public RestResult<Void> markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById) {
        return putWithRestResult(sectionRestURL + "/markAsInComplete/" + sectionId + "/" + applicationId + "/" + markedAsInCompleteById, Void.class);
    }

    @Override
    public RestResult<SectionResource> getById(Long sectionId) {
        return getWithRestResult(sectionRestURL + "/" + sectionId, SectionResource.class);
    }

    @Override public RestResult<List<SectionResource>> getByCompetition(final Long competitionId) {
        return getWithRestResult(sectionRestURL + "/getByCompetition/" + competitionId, new ParameterizedTypeReference<List<SectionResource>>() {
        });
    }

    @Override
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/getCompletedSectionsByOrganisation/" + applicationId, new ParameterizedTypeReference<Map<Long, Set<Long>>>() {
        });
    }

    @Override
    public RestResult<List<Long>> getCompletedSectionIds(Long applicationId, Long organisationId) {
        return getWithRestResult(sectionRestURL + "/getCompletedSections/" + applicationId + "/" + organisationId, ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<List<Long>> getIncompletedSectionIds(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/getIncompleteSections/" + applicationId, ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<Boolean> allSectionsMarkedAsComplete(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/allSectionsMarkedAsComplete/" + applicationId, Boolean.class);
    }

    @Override
    public Future<RestResult<SectionResource>> getPreviousSection(Long sectionId) {
        return getWithRestResultAsync(sectionRestURL + "/getPreviousSection/" + sectionId, SectionResource.class);
    }

    @Override
    public Future<RestResult<SectionResource>> getNextSection(Long sectionId) {
        return getWithRestResultAsync(sectionRestURL + "/getNextSection/" + sectionId, SectionResource.class);
    }

    @Override
    public RestResult<SectionResource> getSectionByQuestionId(Long questionId) {
        return getWithRestResult(sectionRestURL + "/getSectionByQuestionId/" + questionId, SectionResource.class);
    }

    @Override
    public RestResult<Set<Long>> getQuestionsForSectionAndSubsections(Long sectionId) {
        return getWithRestResult(sectionRestURL + "/getQuestionsForSectionAndSubsections/" + sectionId, ParameterizedTypeReferences.longsSetType());
    }

    @Override
    public RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type) {
        return getWithRestResult(sectionRestURL + "/getSectionsByCompetitionIdAndType/" + competitionId + "/" + type.name(), new ParameterizedTypeReference<List<SectionResource>>() {
        });
    }

    @Override
    public RestResult<SectionResource> getFinanceSectionForCompetition(Long competitionId) {
        return getWithRestResult(sectionRestURL + "/getFinanceSectionByCompetitionId/" + competitionId, SectionResource.class);

    }
}
