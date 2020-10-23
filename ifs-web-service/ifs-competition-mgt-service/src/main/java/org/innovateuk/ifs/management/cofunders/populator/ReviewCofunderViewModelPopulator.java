package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.ReviewSupporterViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReviewSupporterViewModelPopulator {

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    public ReviewSupporterViewModel populateModel(long applicationId) {

        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<SupporterAssignmentResource> supporterAssignmentResourceList = supporterAssignmentRestService.getAssignmentsByApplicationId(applicationId).getSuccess();

        Map<SupporterState, List<SupporterAssignmentResource>> assignments = supporterAssignmentResourceList.stream()
                .collect(Collectors.groupingBy(SupporterAssignmentResource::getState));

        return new ReviewSupporterViewModel(assignments, applicationResource);
    }
}
