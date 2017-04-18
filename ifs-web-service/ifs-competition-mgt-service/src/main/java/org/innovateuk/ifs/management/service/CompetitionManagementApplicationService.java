package org.innovateuk.ifs.management.service;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementApplicationController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;

import java.util.function.Function;

/**
 * Service for handling requests from the {@link CompetitionManagementApplicationController}
 */
public interface CompetitionManagementApplicationService {

    String validateApplicationAndCompetitionIds(Long applicationId,
                                                Long competitionId,
                                                Function<ApplicationResource,
                                                        String> success);

    String displayApplicationFinances(long applicationId,
                                      long organisationId,
                                      ApplicationForm form,
                                      Model model,
                                      BindingResult bindingResult,
                                      ApplicationResource application);

    String displayApplicationOverview(UserResource user,
                                      long applicationId,
                                      long competitionId,
                                      ApplicationForm form,
                                      String origin,
                                      MultiValueMap<String, String> queryParams,
                                      Model model,
                                      ApplicationResource application);
}
