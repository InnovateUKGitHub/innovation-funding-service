package org.innovateuk.ifs.eugrant.organisation.service;

import org.innovateuk.ifs.eugrant.organisation.form.OrganisationCreationForm;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationTypeForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class OrganisationCookieService {
    public Optional<OrganisationCreationForm> getOrganisationCreationCookieValue(HttpServletRequest request) {
    }

    public Optional<OrganisationTypeForm> getOrganisationTypeCookieValue(HttpServletRequest request) {
    }

    public void saveToOrganisationCreationCookie(OrganisationCreationForm organisationForm, HttpServletResponse response) {
    }

    public void saveToOrganisationTypeCookie(OrganisationTypeForm organisationTypeForm, HttpServletResponse response) {
    }
}
