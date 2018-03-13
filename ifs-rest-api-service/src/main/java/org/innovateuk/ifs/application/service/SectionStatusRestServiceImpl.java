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

    private String sectionRestURL = "/sectionStatus";

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

}
