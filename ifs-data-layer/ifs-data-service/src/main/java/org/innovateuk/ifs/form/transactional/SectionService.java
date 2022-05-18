package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * Transactional and secure service for Section processing work
 */
public interface SectionService {

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.form.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getById(final Long sectionId);

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.form.resource.SectionResource', 'READ')")
    ServiceResult<Set<Long>> getQuestionsForSectionAndSubsections(final Long sectionId);

    @PostAuthorize("hasPermission(filterObject, 'READ')")
	ServiceResult<List<SectionResource>> getSectionsByCompetitionIdAndType(long competitionId, SectionType type);

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.form.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getNextSection(final Long sectionId);

    @PreAuthorize("hasPermission(#section, 'READ')")
    ServiceResult<SectionResource> getNextSection(SectionResource section);

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.form.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getPreviousSection(final Long sectionId);

    @PreAuthorize("hasPermission(#section, 'READ')")
    ServiceResult<SectionResource> getPreviousSection(SectionResource section);

    @PreAuthorize("hasPermission(#questionId, 'org.innovateuk.ifs.form.resource.QuestionResource', 'READ')")
    ServiceResult<SectionResource> getSectionByQuestionId(final Long questionId);

    @NotSecured(value = "Everyone can see sections", mustBeSecuredByOtherServices = false)
    ServiceResult<List<SectionResource>> getByCompetitionId(final Long CompetitionId);

    @NotSecured(value = "Everyone can see sections", mustBeSecuredByOtherServices = false)
    ServiceResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(final Long competitionId);

    @PreAuthorize("hasPermission(#parentId, 'org.innovateuk.ifs.form.resource.SectionResource', 'READ')")
    ServiceResult<List<SectionResource>> getChildSectionsByParentId(long parentId);

    @SecuredBySpring(value = "UPDATE", description = "Only comp admin can update a section")
    @PreAuthorize("hasAnyAuthority('comp_admin')")
    ServiceResult<SectionResource> save(SectionResource sectionResource);
}
