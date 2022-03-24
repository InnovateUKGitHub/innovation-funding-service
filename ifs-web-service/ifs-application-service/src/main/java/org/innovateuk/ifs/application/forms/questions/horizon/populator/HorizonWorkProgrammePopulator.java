package org.innovateuk.ifs.application.forms.questions.horizon.populator;

import org.innovateuk.ifs.application.forms.questions.horizon.model.HorizonWorkProgrammeViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class HorizonWorkProgrammePopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public HorizonWorkProgrammeViewModel populate(long applicationId,
                                                  long questionId,
                                                  long userId,
                                                  String pageTitle,
                                                  boolean isCallId,
                                                  Set<HorizonWorkProgramme> workProgrammes,
                                                  Map<String, List<HorizonWorkProgramme>> readOnlyMap) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(userId, application.getId()).getSuccess();

        boolean readOnly = !readOnlyMap.isEmpty();

        return new HorizonWorkProgrammeViewModel(
                application.getName(),
                applicationId,
                pageTitle,
                isCallId,
                isComplete(application, organisation, questionId),
                true,
                true,
                workProgrammes,
                readOnly,
                readOnlyMap
        );
    }

    private boolean isComplete(ApplicationResource application, OrganisationResource organisation, long questionId) {
        try {
            return questionStatusRestService.getMarkedAsComplete(application.getId(), organisation.getId()).get().contains(questionId);
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e);
        }
    }
}
