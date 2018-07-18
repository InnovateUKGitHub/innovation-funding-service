package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.model.OrganisationSelectionChoiceViewModel;
import org.innovateuk.ifs.registration.model.OrganisationSelectionViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Component
public class OrganisationSelectionViewModelPopulator {

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionService competitionService;

    public OrganisationSelectionViewModel populate(UserResource userResource, Optional<Long> competitionId, Optional<String> inviteHash, String newOrganisationUrl) {
        Optional<List<Long>> leadApplicantTypes = competitionId.map(competitionService::getById)
                .map(CompetitionResource::getLeadApplicantTypes);
        Set<OrganisationSelectionChoiceViewModel> choices = organisationRestService.getAllUsersOrganisations(userResource.getId()).getSuccess()
                .stream()
                .map(choice(leadApplicantTypes))
                .collect(toSet());
        return new OrganisationSelectionViewModel(choices, !inviteHash.isPresent(), newOrganisationUrl);
    }

    private Function<OrganisationResource, OrganisationSelectionChoiceViewModel> choice(Optional<List<Long>> leadApplicantTypes) {
        return (organisation) ->
            new OrganisationSelectionChoiceViewModel(organisation.getId(),
                    organisation.getName(),
                    organisation.getOrganisationTypeName(),
                    isEligible(organisation, leadApplicantTypes));
    }

    private boolean isEligible(OrganisationResource organisation, Optional<List<Long>> leadApplicantTypes) {
        return leadApplicantTypes.isPresent() && leadApplicantTypes.get().contains(organisation.getOrganisationType());
    }


}
