package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;

import java.util.List;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link SectionResource} related data.
 */
public interface SectionRestService {
    RestResult<SectionResource> getById(Long sectionId);
    RestResult<List<SectionResource>> getByCompetition(Long competitionId);
    RestResult<SectionResource> getSectionByQuestionId(Long questionId);
    RestResult<Set<Long>> getQuestionsForSectionAndSubsections(Long sectionId);
    RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type);
    RestResult<SectionResource> getFinanceSectionForCompetition(Long competitionId);
    RestResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(Long competitionId);
}
