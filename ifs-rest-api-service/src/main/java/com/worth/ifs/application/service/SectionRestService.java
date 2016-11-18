package com.worth.ifs.application.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;

/**
 * Interface for CRUD operations on {@link SectionResource} related data.
 */
public interface SectionRestService {
    RestResult<List<ValidationMessages>> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById);
    RestResult<Void> markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById);
    RestResult<SectionResource> getById(Long sectionId);
    RestResult<List<SectionResource>> getByCompetition(Long competitionId);
    RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(Long applicationId);
    RestResult<List<Long>> getCompletedSectionIds(Long applicationId, Long organisationId);
    RestResult<List<Long>> getIncompletedSectionIds(Long applicationId);
    RestResult<Boolean> allSectionsMarkedAsComplete(Long applicationId);
    Future<RestResult<SectionResource>> getPreviousSection(Long sectionId);
    Future<RestResult<SectionResource>> getNextSection(Long sectionId);
    RestResult<SectionResource> getSectionByQuestionId(Long questionId);
    RestResult<Set<Long>> getQuestionsForSectionAndSubsections(Long sectionId);
    RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type);
    RestResult<SectionResource> getFinanceSectionForCompetition(Long competitionId);
}
