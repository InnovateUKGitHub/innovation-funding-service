package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

/**
 * SectionRestServiceImpl is a utility for CRUD operations on {@link SectionResource}.
 * This class connects to the {org.innovateuk.ifs.application.controller.SectionController}
 * through a REST call.
 */
@Service
public class SectionRestServiceImpl extends BaseRestService implements SectionRestService {

    private String sectionRestURL = "/section";

    @Override
    public RestResult<List<ValidationMessages>> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        return postWithRestResult(sectionRestURL + "/markAsComplete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById, validationMessagesListType());
    }

    @Override
    public RestResult<Void> markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        return postWithRestResult(sectionRestURL + "/markAsNotRequired/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById, Void.class);
    }

    @Override
    public RestResult<Void> markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById) {
        return postWithRestResult(sectionRestURL + "/markAsInComplete/" + sectionId + "/" + applicationId + "/" + markedAsInCompleteById, Void.class);
    }

    @Override
    public RestResult<SectionResource> getById(Long sectionId) {
        return getWithRestResult(sectionRestURL + "/" + sectionId, SectionResource.class);
    }

    @Override
    public RestResult<List<SectionResource>> getByCompetition(final Long competitionId) {
        return getWithRestResult(sectionRestURL + "/getByCompetition/" + competitionId, sectionResourceListType());
    }

    @Override
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/getCompletedSectionsByOrganisation/" + applicationId, mapOfLongToLongsSetType());
    }

    @Override
    public RestResult<List<Long>> getCompletedSectionIds(Long applicationId, Long organisationId) {
        return getWithRestResult(sectionRestURL + "/getCompletedSections/" + applicationId + "/" + organisationId, longsListType());
    }

    @Override
    public RestResult<List<Long>> getIncompletedSectionIds(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/getIncompleteSections/" + applicationId, longsListType());
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
        return getWithRestResult(sectionRestURL + "/getQuestionsForSectionAndSubsections/" + sectionId, longsSetType());
    }

    @Override
    public RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type) {
        return getWithRestResult(sectionRestURL + "/getSectionsByCompetitionIdAndType/" + competitionId + "/" + type.name(), sectionResourceListType());
    }

    @Override
    public RestResult<SectionResource> getFinanceSectionForCompetition(Long competitionId) {
        return getWithRestResult(sectionRestURL + "/getFinanceSectionByCompetitionId/" + competitionId, SectionResource.class);

    }

    @Override
    public RestResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(Long competitionId) {
        return getWithRestResult(sectionRestURL + "/getByCompetitionIdVisibleForAssessment/" + competitionId, sectionResourceListType());
    }
}
