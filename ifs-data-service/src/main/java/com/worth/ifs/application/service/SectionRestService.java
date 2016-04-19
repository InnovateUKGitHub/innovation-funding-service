package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link Section} related data.
 */
public interface SectionRestService {
    RestResult<SectionResource> getById(Long sectionId);
    RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(Long applicationId);
    RestResult<List<Long>> getCompletedSectionIds(Long applicationId, Long organisationId);
    RestResult<List<Long>> getIncompletedSectionIds(Long applicationId);
    RestResult<Boolean> allSectionsMarkedAsComplete(Long applicationId);
    Future<RestResult<SectionResource>> getPreviousSection(Long sectionId);
    Future<RestResult<SectionResource>> getNextSection(Long sectionId);
    RestResult<SectionResource> getSectionByQuestionId(Long questionId);
    RestResult<Set<Long>> getQuestionsForSectionAndSubsections(Long sectionId);
    RestResult<Long> getFinanceSectionForCompetition(Long competitionId);
}
