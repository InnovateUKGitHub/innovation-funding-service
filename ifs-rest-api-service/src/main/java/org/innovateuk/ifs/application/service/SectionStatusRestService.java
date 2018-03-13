package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link SectionResource} related data.
 */
public interface SectionStatusRestService {
    RestResult<List<ValidationMessages>> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById);
    RestResult<Void> markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById);
    RestResult<Void> markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById);
    RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(Long applicationId);
    RestResult<List<Long>> getCompletedSectionIds(Long applicationId, Long organisationId);
    RestResult<List<Long>> getIncompletedSectionIds(Long applicationId);
    RestResult<Boolean> allSectionsMarkedAsComplete(Long applicationId);
}
