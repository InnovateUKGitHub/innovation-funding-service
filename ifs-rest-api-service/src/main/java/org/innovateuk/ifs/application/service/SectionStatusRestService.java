package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link SectionResource} related data.
 */
public interface SectionStatusRestService {
    RestResult<List<ValidationMessages>> markAsComplete(long sectionId, long applicationId, long markedAsCompleteById);
    RestResult<Void> markAsNotRequired(long sectionId, long applicationId, long markedAsCompleteById);
    RestResult<Void> markAsInComplete(long sectionId, long applicationId, long markedAsInCompleteById);
    RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(long applicationId);
    RestResult<List<Long>> getCompletedSectionIds(long applicationId, long organisationId);
    RestResult<List<Long>> getIncompletedSectionIds(long applicationId);
    RestResult<Boolean> allSectionsMarkedAsComplete(long applicationId);
}
