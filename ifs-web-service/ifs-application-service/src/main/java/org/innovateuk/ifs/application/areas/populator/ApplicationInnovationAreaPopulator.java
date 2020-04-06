package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.application.areas.viewmodel.InnovationAreaViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationInnovationAreaRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populates the Innovation Area selection viewmodel.
 */
@Component
public class ApplicationInnovationAreaPopulator {
    @Autowired
    private ApplicationInnovationAreaRestService applicationInnovationAreaRestService;


    public InnovationAreaViewModel populate(ApplicationResource applicationResource, Long questionId) {

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();
        innovationAreaViewModel.setAvailableInnovationAreas(applicationInnovationAreaRestService.getAvailableInnovationAreasForApplication(applicationResource.getId()).getSuccess());
        innovationAreaViewModel.setQuestionId(questionId);
        innovationAreaViewModel.setApplicationId(applicationResource.getId());
        innovationAreaViewModel.setCompetitionName(applicationResource.getCompetitionName());
        innovationAreaViewModel.setApplicationName(applicationResource.getName());

        setInnovationAreaChoice(applicationResource, innovationAreaViewModel);

        return innovationAreaViewModel;
    }

    private static void setInnovationAreaChoice(ApplicationResource applicationResource, InnovationAreaViewModel innovationAreaViewModel) {

        if(applicationResource.getNoInnovationAreaApplicable()) {
            innovationAreaViewModel.setNoInnovationAreaApplicable(true);
        }
        else if (applicationResource.getInnovationArea() != null && applicationResource.getInnovationArea().getId() != null) {
            innovationAreaViewModel.setSelectedInnovationAreaId(applicationResource.getInnovationArea().getId());
        }
    }
}
