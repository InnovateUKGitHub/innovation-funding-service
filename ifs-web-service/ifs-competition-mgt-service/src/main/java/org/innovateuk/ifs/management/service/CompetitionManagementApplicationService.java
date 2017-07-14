package org.innovateuk.ifs.management.service;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementApplicationController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.function.Function;

/**
 * Service for handling requests from the {@link CompetitionManagementApplicationController}
 */
public interface CompetitionManagementApplicationService {

    String validateApplicationAndCompetitionIds(Long applicationId,
                                                Long competitionId,
                                                Function<ApplicationResource, String> success);

    String displayApplicationOverview(UserResource user,
                                      long competitionId,
                                      ApplicationForm form,
                                      String origin,
                                      MultiValueMap<String, String> queryParams,
                                      Model model,
                                      ApplicationResource application);

    String markApplicationAsIneligible(long applicationId,
                                       long competitionId,
                                       String origin,
                                       MultiValueMap<String, String> queryParams,
                                       ApplicationForm applicationForm,
                                       UserResource user,
                                       Model model);

    public String PROJECT_SETUP_MANAGEMENT_SERVICE = "project-setup-management";

}
