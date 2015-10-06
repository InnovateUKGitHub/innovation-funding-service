package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * ApplicationRestServiceImpl is a utility for CRUD operations on {@link Section}.
 * This class connects to the {@link com.worth.ifs.application.controller.SectionController}
 * through a REST call.
 */
@Service
public class SectionRestServiceImpl extends BaseRestServiceProvider implements SectionRestService {
    @Value("${ifs.data.service.rest.section}")
    String sectionRestURL;


    public List<Long> getCompletedSectionIds(Long applicationId, Long organisationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Long[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + sectionRestURL + "/getCompletedSections/"+applicationId+"/"+organisationId, Long[].class);
        Long[] sections =responseEntity.getBody();
        return Arrays.asList(sections);
    }

    public List<Long> getIncompletedSectionIds(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Long[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + sectionRestURL + "/getIncompleteSections/"+applicationId, Long[].class);
        Long[] sections =responseEntity.getBody();
        return Arrays.asList(sections);
    }

    public Section getSection(String name) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Section> responseEntity = restTemplate.getForEntity(dataRestServiceURL + sectionRestURL + "/findByName/"+name, Section.class);
        return responseEntity.getBody();
    }
}
