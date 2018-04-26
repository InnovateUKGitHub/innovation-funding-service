package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

/**
 * SectionStatusRestServiceImpl is a utility for applicant operations on {@link SectionResource}.
 * This class connects to the {org.innovateuk.ifs.application.controller.SectionStatusController}
 * through a REST call.
 */
@Service
public class SectionStatusRestServiceImpl extends BaseRestService implements SectionStatusRestService {

    private String sectionRestURL = "/section-status";

    @Override
    public RestResult<List<ValidationMessages>> markAsComplete(long sectionId, long applicationId, long markedAsCompleteById) {
        return postWithRestResult(sectionRestURL + "/mark-as-complete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById, validationMessagesListType());
    }

    @Override
    public RestResult<Void> markAsNotRequired(long sectionId, long applicationId, long markedAsCompleteById) {
        return postWithRestResult(sectionRestURL + "/mark-as-not-required/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById, Void.class);
    }

    @Override
    public RestResult<Void> markAsInComplete(long sectionId, long applicationId, long markedAsInCompleteById) {
        return postWithRestResult(sectionRestURL + "/mark-as-in-complete/" + sectionId + "/" + applicationId + "/" + markedAsInCompleteById, Void.class);
    }

    @Override
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(long applicationId) {
        return getWithRestResult(sectionRestURL + "/get-completed-sections-by-organisation/" + applicationId, mapOfLongToLongsSetType());
    }

    @Override
    public RestResult<List<Long>> getCompletedSectionIds(long applicationId, long organisationId) {
        return getWithRestResult(sectionRestURL + "/get-completed-sections/" + applicationId + "/" + organisationId, longsListType());
    }

    @Override
    public RestResult<List<Long>> getIncompletedSectionIds(long applicationId) {
        return getWithRestResult(sectionRestURL + "/get-incomplete-sections/" + applicationId, longsListType());
    }

    @Override
    public RestResult<Boolean> allSectionsMarkedAsComplete(long applicationId) {
        return getWithRestResult(sectionRestURL + "/all-sections-marked-as-complete/" + applicationId, Boolean.class);
    }

}
