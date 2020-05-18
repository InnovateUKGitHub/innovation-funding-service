package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.form.OrganisationInternationalForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public OrganisationCreationSelectTypeViewModel populate(HttpServletRequest request) {

        List<OrganisationTypeResource> organisationTypeResourceList = organisationTypeRestService.getAll().getSuccess();

        // change to get them back instead of filter
        Optional<OrganisationInternationalForm> organisationInternationalForm = registrationCookieService.getOrganisationInternationalCookieValue(request);
        if (organisationInternationalForm.isPresent()) {
            if (organisationInternationalForm.get().getInternational()) {
                organisationTypeResourceList = organisationTypeResourceList.stream()
                        .filter(resource -> !OrganisationTypeEnum.getFromId(resource.getId()).equals(OrganisationTypeEnum.RESEARCH))
                        .collect(Collectors.toList());
            }
        }

        return new OrganisationCreationSelectTypeViewModel(
                        simpleFilter(organisationTypeResourceList,
                                o -> OrganisationTypeEnum.getFromId(o.getId()) != null));
    }
}
