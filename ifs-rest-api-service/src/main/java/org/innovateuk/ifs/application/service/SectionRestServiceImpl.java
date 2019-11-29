package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsSetType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.sectionResourceListType;

/**
 * SectionRestServiceImpl is a utility for CRUD operations on {@link SectionResource}.
 * This class connects to the {org.innovateuk.ifs.application.controller.SectionController}
 * through a REST call.
 */
@Service
public class SectionRestServiceImpl extends BaseRestService implements SectionRestService {

    private String sectionRestURL = "/section";

    @Override
    public RestResult<SectionResource> getById(Long sectionId) {
        return getWithRestResult(sectionRestURL + "/" + sectionId, SectionResource.class);
    }

    @Override
    public RestResult<List<SectionResource>> getChildSectionsByParentId(Long parentId) {
        return getWithRestResult(sectionRestURL + "/get-child-sections/" + parentId, sectionResourceListType());
    }

    @Override
    public RestResult<List<SectionResource>> getByCompetition(final Long competitionId) {
        return getWithRestResult(sectionRestURL + "/get-by-competition/" + competitionId, sectionResourceListType());
    }

    @Override
    public RestResult<SectionResource> getSectionByQuestionId(Long questionId) {
        return getWithRestResult(sectionRestURL + "/get-section-by-question-id/" + questionId, SectionResource.class);
    }

    @Override
    public RestResult<Set<Long>> getQuestionsForSectionAndSubsections(Long sectionId) {
        return getWithRestResult(sectionRestURL + "/get-questions-for-section-and-subsections/" + sectionId, longsSetType());
    }

    @Override
    public RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type) {
        return getWithRestResult(sectionRestURL + "/get-sections-by-competition-id-and-type/" + competitionId + "/" + type.name(), sectionResourceListType());
    }

    @Override
    public RestResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(Long competitionId) {
        return getWithRestResult(sectionRestURL + "/get-by-competition-id-visible-for-assessment/" + competitionId, sectionResourceListType());
    }
}
