package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link SectionResource} related data.
 */
public interface SectionService {
    @NotSecured("Not currently secured")
    List<ValidationMessages> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById);

    @NotSecured("Not currently secured")
    void markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById);

    @NotSecured("Not currently secured")
    void markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById);

    @NotSecured("Not currently secured")
    SectionResource getById(Long sectionId);

    @NotSecured("Not currently secured")
    List<Long> getCompleted(Long applicationId, Long organisationId);

    @NotSecured("Not currently secured")
    List<Long> getInCompleted(Long applicationId);

    @NotSecured("Not currently secured")
    Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId);

    @NotSecured("Not currently secured")
    Boolean allSectionsMarkedAsComplete(Long applicationId);

    @NotSecured("Not currently secured")
    List<SectionResource> filterParentSections(List<SectionResource> sections);

    @NotSecured("Not currently secured")
    List<SectionResource> getAllByCompetitionId(Long competitionId);

    @NotSecured("Not currently secured")
    void removeSectionsQuestionsWithType(SectionResource section, FormInputType type);

    @NotSecured("Not currently secured")
    SectionResource getSectionByQuestionId(Long questionId);

    @NotSecured("Not currently secured")
    Set<Long> getQuestionsForSectionAndSubsections(Long sectionId);

    @NotSecured("Not currently secured")
    List<SectionResource> getSectionsForCompetitionByType(Long competitionId, SectionType type);

    @NotSecured("Not currently secured")
    SectionResource getFinanceSection(Long competitionId);

    @NotSecured("Not currently secured")
    SectionResource getOrganisationFinanceSection(Long competitionId);

    @NotSecured("Not currently secured")
    List<SectionResource> findResourceByIdInList(List<Long> ids, List<SectionResource> list);
}
