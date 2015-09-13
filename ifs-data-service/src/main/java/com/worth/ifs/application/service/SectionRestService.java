package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;
import java.util.List;

/**
 * UserRestService is a utility to use client-side to retrieve User data from the data-service controllers.
 */
public interface SectionRestService {
    public List<Long> getCompletedSectionIds(Long applicationId);
    public List<Long> getIncompletedSectionIds(Long applicationId);
    public Section getSection(String name);
}
