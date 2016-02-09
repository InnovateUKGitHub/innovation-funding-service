package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.longsListType;

/**
 * SectionRestServiceImpl is a utility for CRUD operations on {@link Section}.
 * This class connects to the {@link com.worth.ifs.application.controller.SectionController}
 * through a REST call.
 */
@Service
public class SectionRestServiceImpl extends BaseRestService implements SectionRestService {

    @Value("${ifs.data.service.rest.section}")
    String sectionRestURL;

    @Override
    public RestResult<Section> getById(Long sectionId) {
        return getWithRestResult(sectionRestURL + "/getById/" + sectionId, Section.class);
    }

    @Override
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsByOrganisation(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/getCompletedSectionsByOrganisation/" + applicationId, new ParameterizedTypeReference<Map<Long, Set<Long>>>() {});
    }

    @Override
    public RestResult<List<Long>> getCompletedSectionIds(Long applicationId, Long organisationId) {
        return getWithRestResult(sectionRestURL + "/getCompletedSections/" + applicationId + "/" + organisationId, longsListType());
    }

    @Override
    public RestResult<List<Long>> getIncompletedSectionIds(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/getIncompleteSections/" + applicationId, longsListType());
    }

    @Override
    public RestResult<Section> getSection(String name) {
        return getWithRestResult(sectionRestURL + "/findByName/" + name, Section.class);
    }

    @Override
    public RestResult<Boolean> allSectionsMarkedAsComplete(Long applicationId) {
        return getWithRestResult(sectionRestURL + "/allSectionsMarkedAsComplete/" + applicationId, Boolean.class);
    }

    @Override
    public ListenableFuture<RestResult<Section>> getPreviousSection(Long sectionId) {
        return getWithRestResultAsyc(sectionRestURL + "/getPreviousSection/" + sectionId, Section.class);
    }

    @Override
    public ListenableFuture<RestResult<Section>> getNextSection(Long sectionId) {
        return getWithRestResultAsyc(sectionRestURL + "/getNextSection/" + sectionId, Section.class);
    }

    @Override
    public RestResult<Section> getSectionByQuestionId(Long questionId) {
        return getWithRestResult(sectionRestURL + "/getSectionByQuestionId/" + questionId, Section.class);
    }
}
