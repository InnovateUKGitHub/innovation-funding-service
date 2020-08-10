package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface KnowledgeBaseRestService {
    RestResult<List<String>> getKnowledgeBases();
}
