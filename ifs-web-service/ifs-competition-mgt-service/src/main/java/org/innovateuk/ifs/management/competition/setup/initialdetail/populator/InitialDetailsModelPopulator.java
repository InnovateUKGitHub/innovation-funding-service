package org.innovateuk.ifs.management.competition.setup.initialdetail.populator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.core.util.CompetitionUtils;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.initialdetail.viewmodel.InitialDetailsViewModel;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;

/**
 * populates the model for the initial details competition setup section.
 */
@Service
public class
InitialDetailsModelPopulator implements CompetitionSetupSectionModelPopulator<InitialDetailsViewModel> {

    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private CompetitionSetupService competitionSetupService;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.INITIAL_DETAILS;
    }

    @Override
    public InitialDetailsViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new InitialDetailsViewModel(generalViewModel,
                userRestService.findByUserRole(COMP_ADMIN).getSuccess(),
                categoryRestService.getInnovationSectors().getSuccess(),
                addAllInnovationAreaOption(categoryRestService.getInnovationAreas().getSuccess()),
                competitionRestService.getCompetitionTypes().getSuccess(),
                userRestService.findByUserRoleAndUserStatus(INNOVATION_LEAD, ACTIVE).getSuccess(),
                competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionResource.getId()));
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