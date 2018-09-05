package org.innovateuk.ifs.competitionsetup.initialdetail.populator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.util.CompetitionUtils;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.competitionsetup.initialdetail.viewmodel.InitialDetailsViewModel;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;

/**
 * populates the model for the initial details competition setup section.
 */
@Service
public class
InitialDetailsModelPopulator implements CompetitionSetupSectionModelPopulator {

    private CompetitionRestService competitionRestService;
    private UserRestService userRestService;
    private CategoryRestService categoryRestService;

    public InitialDetailsModelPopulator(CompetitionRestService competitionRestService,
                                        UserRestService userRestService,
                                        CategoryRestService categoryRestService) {
        this.competitionRestService = competitionRestService;
        this.userRestService = userRestService;
        this.categoryRestService = categoryRestService;
    }

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.INITIAL_DETAILS;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new InitialDetailsViewModel(generalViewModel,
                userRestService.findByUserRole(COMP_ADMIN).getSuccess(),
                categoryRestService.getInnovationSectors().getSuccess(),
                addAllInnovationAreaOption(categoryRestService.getInnovationAreas().getSuccess()),
                competitionRestService.getCompetitionTypes().getSuccess(),
                userRestService.findByUserRole(INNOVATION_LEAD).getSuccess());
    }

    private List<InnovationAreaResource> addAllInnovationAreaOption(List<InnovationAreaResource> innovationAreas) {
        List<InnovationAreaResource> returnList = new ArrayList<>(innovationAreas);

        InnovationAreaResource innovationAreaResource = new InnovationAreaResource();
        innovationAreaResource.setId(CompetitionUtils.ALL_INNOVATION_AREAS);
        innovationAreaResource.setName("All");

        returnList.add(0, innovationAreaResource);

        return returnList;
    }

}
