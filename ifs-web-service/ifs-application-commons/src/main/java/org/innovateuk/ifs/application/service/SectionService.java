package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilterNot;

/**
 * Interface for CRUD operations on {@link SectionResource} related data.
 */
public interface SectionService {
    ValidationMessages markAsComplete(long sectionId, long applicationId, long markedAsCompleteById);
    void markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById);
    void markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById);
    SectionResource getById(Long sectionId);
    List<Long> getCompleted(Long applicationId, Long organisationId);
    Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId);
    Boolean allSectionsMarkedAsComplete(Long applicationId);
    List<SectionResource> filterParentSections(List<SectionResource> sections);
    List<SectionResource> getAllByCompetitionId(long competitionId);
    default List<SectionResource> getAllByCompetitionIdExcludingTerms(long id) {
        return simpleFilterNot(getAllByCompetitionId(id), SectionResource::isTermsAndConditions);
    }
    SectionResource getSectionByQuestionId(long questionId);
    Set<Long> getQuestionsForSectionAndSubsections(long sectionId);
    List<SectionResource> getSectionsForCompetitionByType(long competitionId, SectionType type);
    SectionResource getFinanceSection(long competitionId);
    SectionResource getTermsAndConditionsSection(long competitionId);
    SectionResource getOrganisationFinanceSection(long competitionId);
    List<SectionResource> findResourceByIdInList(List<Long> ids, List<SectionResource> list);
}