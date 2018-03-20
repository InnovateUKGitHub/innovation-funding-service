package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
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
	ServiceResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type);

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

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<SectionResource>> getByCompetitionId(final Long CompetitionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(final Long competitionId);

}
