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
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ValidationMessages> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    SectionResource getById(Long sectionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<Long> getCompleted(Long applicationId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<Long> getInCompleted(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Boolean allSectionsMarkedAsComplete(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<SectionResource> filterParentSections(List<SectionResource> sections);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<SectionResource> getAllByCompetitionId(Long competitionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void removeSectionsQuestionsWithType(SectionResource section, FormInputType type);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    SectionResource getSectionByQuestionId(Long questionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Set<Long> getQuestionsForSectionAndSubsections(Long sectionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<SectionResource> getSectionsForCompetitionByType(Long competitionId, SectionType type);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    SectionResource getFinanceSection(Long competitionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    SectionResource getOrganisationFinanceSection(Long competitionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<SectionResource> findResourceByIdInList(List<Long> ids, List<SectionResource> list);
}
