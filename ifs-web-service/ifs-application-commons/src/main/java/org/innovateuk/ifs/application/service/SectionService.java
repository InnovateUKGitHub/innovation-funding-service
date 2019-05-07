package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.resource.FormInputType;
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
    List<ValidationMessages> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById);

    void markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById);

    void markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById);

    SectionResource getById(Long sectionId);

    List<Long> getCompleted(Long applicationId, Long organisationId);

    List<Long> getInCompleted(Long applicationId);

    Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId);

    Boolean allSectionsMarkedAsComplete(Long applicationId);

    List<SectionResource> filterParentSections(List<SectionResource> sections);

    List<SectionResource> getAllByCompetitionId(long competitionId);

    default List<SectionResource> getAllByCompetitionIdExcludingTerms(long id) {
        return simpleFilterNot(getAllByCompetitionId(id), SectionResource::isTermsAndConditions);
    }

    void removeSectionsQuestionsWithType(SectionResource section, FormInputType type);

    SectionResource getSectionByQuestionId(Long questionId);

    Set<Long> getQuestionsForSectionAndSubsections(Long sectionId);

    List<SectionResource> getSectionsForCompetitionByType(Long competitionId, SectionType type);

    SectionResource getFinanceSection(long competitionId);

    SectionResource getOrganisationFinanceSection(Long competitionId);

    List<SectionResource> findResourceByIdInList(List<Long> ids, List<SectionResource> list);
}