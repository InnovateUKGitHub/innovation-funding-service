package com.worth.ifs.organisation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public interface CompanyHouseRestService {
    public List<CompanyHouseBusiness> searchOrganisationsByName(String name);
    public CompanyHouseBusiness getOrganisationById(String organisationId);
}
