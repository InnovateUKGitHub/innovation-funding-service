package org.innovateuk.ifs.application.forms.questions.heukar.populator;

import org.innovateuk.ifs.applicant.service.ApplicationHeukarLocationRestService;
import org.innovateuk.ifs.application.forms.questions.heukar.model.HeukarProjectLocationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Component
public class HeukarProjectLocationPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationHeukarLocationRestService heukarLocationRestService;

    public HeukarProjectLocationViewModel populate(long applicationId,
                                                   long questionId,
                                                   long userId,
                                                   HeukarLocation parentLocation,
                                                   Map<String, List<HeukarLocation>> readOnlyMap) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(userId, application.getId()).getSuccess();

        boolean readOnly = !readOnlyMap.isEmpty();

        String pageTitle = getPageTitleForParentLocation(parentLocation, readOnly);

        return new HeukarProjectLocationViewModel(application.getName(),
                applicationId,
                isComplete(application, organisation, questionId),
                true,
                true,
                pageTitle,
                readOnly,
                readOnlyMap
        );
    }

    private String getPageTitleForParentLocation(HeukarLocation parentLocation, boolean readOnly) {
        return readOnly ?
                Optional.ofNullable(parentLocation).map(HeukarLocation::getTitleDisplayFor).orElse("Confirm your locations") :
                Optional.ofNullable(parentLocation).map(HeukarLocation::getTitleDisplayFor).orElse("Project location");
    }

    private boolean isComplete(ApplicationResource application, OrganisationResource organisation, long questionId) {
        try {
            return questionStatusRestService.getMarkedAsComplete(application.getId(), organisation.getId()).get().contains(questionId);
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e);
        }
    }
}
