package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.eugrant.organisation.service.OrganisationCookieService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides a base class for each of the organisation registration controllers.
 */
public abstract class AbstractOrganisationController {

    protected static final String BASE_URL = "/organisation";
    protected static final String ORGANISATION_TYPE = "type";
    protected static final String FIND_ORGANISATION = "find";
    protected static final String ORGANISATION_FORM = "organisationForm";
    protected static final String TEMPLATE_PATH = "organisation";

    @Autowired
    protected OrganisationCookieService organisationCookieService;



}
