package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

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
    public List<Long> getCompletedSectionIds(Long applicationId, Long organisationId) {
        return asList(restGet(sectionRestURL + "/getCompletedSections/"+applicationId+"/"+organisationId, Long[].class));
    }

    @Override
    public List<Long> getIncompletedSectionIds(Long applicationId) {
        return asList(restGet(sectionRestURL + "/getIncompleteSections/"+applicationId, Long[].class));
    }

    @Override
    public Section getSection(String name) {
        return restGet(sectionRestURL + "/findByName/" + name, Section.class);
    }
}
