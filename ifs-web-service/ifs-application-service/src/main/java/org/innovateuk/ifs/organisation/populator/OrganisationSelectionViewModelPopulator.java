package org.innovateuk.ifs.organisation.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationSelectionChoiceViewModel;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationSelectionViewModel;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.KNOWLEDGE_BASE;

@Component
public class OrganisationSelectionViewModelPopulator {

    private OrganisationRestService organisationRestService;

    private RegistrationCookieService registrationCookieService;

    private CompetitionRestService competitionRestService;

    @Autowired
    public OrganisationSelectionViewModelPopulator(OrganisationRestService organisationRestService, RegistrationCookieService registrationCookieService, CompetitionRestService competitionRestService) {
        this.organisationRestService = organisationRestService;
        this.registrationCookieService = registrationCookieService;
        this.competitionRestService = competitionRestService;
    }

    public OrganisationSelectionViewModel populate(UserResource userResource, HttpServletRequest request, CompetitionResource competitionResource, String newOrganisationUrl) {

        boolean isCollaboratorJourney = registrationCookieService.isCollaboratorJourney(request);

        Set<OrganisationTypeEnum> allowedTypes = EnumSet.allOf(OrganisationTypeEnum.class);

        if (!competitionResource.isKtp()) {
            allowedTypes.remove(KNOWLEDGE_BASE);
        } else {
            if (isCollaboratorJourney) {
                allowedTypes.removeIf(CollectionFunctions.negate(OrganisationTypeEnum::isKtpCollaborator));
            } else {
                allowedTypes.removeIf(CollectionFunctions.negate(OrganisationTypeEnum::isKnowledgeBase));
            }
        }

        Set<OrganisationSelectionChoiceViewModel> choices = getOrganisationResources(userResource.getId(), request)
                .stream()
                .filter(resource -> allowedTypes.contains(OrganisationTypeEnum.getFromId(resource.getOrganisationType())))
                .map(this::choice)
                .collect(toSet());

        return new OrganisationSelectionViewModel(choices,
                registrationCookieService.isCollaboratorJourney(request),
                registrationCookieService.isApplicantJourney(request),
                registrationCookieService.isInternationalJourney(request),
                newOrganisationUrl);
    }

    private List<OrganisationResource> getOrganisationResources(long userId, HttpServletRequest request) {
       final boolean international = registrationCookieService.isInternationalJourney(request);
       return organisationRestService.getOrganisations(userId, international).getSuccess();
    }

    private OrganisationSelectionChoiceViewModel choice(OrganisationResource organisation) {
        return new OrganisationSelectionChoiceViewModel(organisation.getId(),
                    organisation.getName(),
                    organisation.getOrganisationTypeName());
    }
}
