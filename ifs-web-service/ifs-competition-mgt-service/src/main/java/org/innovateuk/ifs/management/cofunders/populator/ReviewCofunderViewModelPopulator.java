package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.ReviewCofunderViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReviewCofunderViewModelPopulator {

    @Autowired
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    public ReviewCofunderViewModel populateModel(long applicationId) {

        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<CofunderAssignmentResource> cofunderAssignmentResourceList = cofunderAssignmentRestService.getAssignmentsByApplicationId(applicationId).getSuccess();

        Map<CofunderState, List<CofunderAssignmentResource>> assignments = cofunderAssignmentResourceList.stream()
                .collect(Collectors.groupingBy(CofunderAssignmentResource::getState));

        return new ReviewCofunderViewModel(assignments, applicationResource);
    }
}
