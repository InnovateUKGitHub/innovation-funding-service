package com.worth.ifs.project.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ProjectResource;

import java.time.LocalDate;

public interface ProjectRestService {
    RestResult<ProjectResource> getProjectById(Long projectId);
    RestResult<Void> updateProjectStartDate(long projectId, LocalDate projectStartDate);
    RestResult<Void> updateProjectAddress(long projectId, AddressType addressType, AddressResource address);
}
