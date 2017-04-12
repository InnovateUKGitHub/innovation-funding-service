package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationInnovationAreaRestService;
import org.innovateuk.ifs.application.viewmodel.InnovationAreaViewModel;
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
        innovationAreaViewModel.setAvailableInnovationAreas(applicationInnovationAreaRestService.getAvailableInnovationAreasForApplication(applicationResource.getId()).getSuccessObject());
        innovationAreaViewModel.setQuestionId(questionId);
        innovationAreaViewModel.setApplicationId(applicationResource.getId());
        innovationAreaViewModel.setCurrentCompetitionName(applicationResource.getCompetitionName());

        setInnovationAreaChoice(applicationResource, innovationAreaViewModel);


        return innovationAreaViewModel;
    }

    private void setInnovationAreaChoice(ApplicationResource applicationResource, InnovationAreaViewModel innovationAreaViewModel) {

        if(applicationResource.getNoInnovationAreaApplicable()) {
            innovationAreaViewModel.setNoInnovationAreaApplicable(true);
        }
        else if (applicationResource.getInnovationArea() != null && applicationResource.getInnovationArea().getId() != null) {
            innovationAreaViewModel.setSelectedInnovationAreaId(applicationResource.getInnovationArea().getId());
        }
    }
}
