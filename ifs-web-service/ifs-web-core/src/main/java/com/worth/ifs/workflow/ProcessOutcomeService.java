package com.worth.ifs.workflow;

import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

/**
 * Interface for CRUD operations on {@link ProcessOutcomeResource} related data.
 */
public interface ProcessOutcomeService {

    ProcessOutcomeResource getById(Long id);

}