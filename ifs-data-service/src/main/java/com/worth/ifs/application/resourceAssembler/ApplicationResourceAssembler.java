package com.worth.ifs.application.resourceAssembler;

import com.worth.ifs.application.controller.ApplicationController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class ApplicationResourceAssembler extends ResourceAssemblerSupport<Application, ApplicationResource> {

    public ApplicationResourceAssembler() {
        super(ApplicationController.class, ApplicationResource.class);
    }

    @Override
    public ApplicationResource toResource(Application entity) {
        ApplicationResource resource = createResourceWithId(
            entity.getId(),
            entity
        );
        resource.application = entity;
        return resource;
    }
}
