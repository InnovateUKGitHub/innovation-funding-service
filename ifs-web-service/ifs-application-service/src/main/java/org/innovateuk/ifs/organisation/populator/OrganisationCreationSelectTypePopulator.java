package org.innovateuk.ifs.organisation.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Populator for Organisation creation lead applicant - choosing organisation type
 */
@Service
public class OrganisationCreationSelectTypePopulator {

    @Autowired
    protected RegistrationCookieService registrationCookieService;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    public OrganisationCreationSelectTypeViewModel populate(HttpServletRequest request, CompetitionResource competitionResource) {

        List<OrganisationTypeResource> organisationTypeResourceList = organisationTypeRestService.getAll().getSuccess();

        EnumSet<OrganisationTypeEnum> allowedTypes = EnumSet.allOf(OrganisationTypeEnum.class);

        if (registrationCookieService.isInternationalJourney(request)) {
            allowedTypes.remove(RESEARCH);
        }
        if (competitionResource.getFundingType() == FundingType.KTP) {
            allowedTypes.removeAll(asList(RESEARCH, RTO, KNOWLEDGE_BASE));
        } else {
            allowedTypes.remove(KNOWLEDGE_BASE);
        }
        organisationTypeResourceList = organisationTypeResourceList.stream()
                .filter(resource -> allowedTypes.contains(OrganisationTypeEnum.getFromId(resource.getId())))
                .collect(Collectors.toList());

        return new OrganisationCreationSelectTypeViewModel(
                        simpleFilter(organisationTypeResourceList,
                                o -> OrganisationTypeEnum.getFromId(o.getId()) != null),
                registrationCookieService.isLeadJourney(request));
    }
}
