package com.worth.ifs.service;

import com.worth.ifs.domain.Section;
import com.worth.ifs.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * UserService is a utility to use client-side to retrieve User data from the data-service controllers.
 */

@Service
public class SectionService extends BaseServiceProvider {
    @Value("${ifs.data.service.rest.section}")
    String sectionRestURL;


    public List<Long> getCompletedSectionIds(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Long[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + sectionRestURL + "/getCompletedSections/"+applicationId, Long[].class);
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
