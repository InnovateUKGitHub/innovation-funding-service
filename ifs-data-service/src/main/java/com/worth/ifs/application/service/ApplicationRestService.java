package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import java.util.List;

/**
 * ApplicationRestRestService is a utility to use client-side to retrieve Application data from the data-service controllers.
 */

public interface ApplicationRestService {
    public Application getApplicationById(Long applicationId);
    public List<Application> getApplicationsByUserId(Long userId);
    public void saveApplication(Application application);
    public void updateApplicationStatus(Long applicationId, Long statusId);
    public Double getCompleteQuestionsPercentage(Long applicationId);
}
