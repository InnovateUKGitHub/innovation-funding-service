package com.worth.ifs.application.resourceAssembler;

import com.worth.ifs.application.controller.ApplicationController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.resource.EmbeddableResourceAssemblerSupport;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resourceAssembler.ProcessRoleResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.stereotype.Service;

@Service
public class ApplicationResourceAssembler extends EmbeddableResourceAssemblerSupport<Application, ApplicationResource, ApplicationController> {

    @Autowired
    private ProcessRoleResourceAssembler processRoleResourceAssembler;

    @Autowired
    public ApplicationResourceAssembler(final EntityLinks entityLinks, final RelProvider relProvider) {
        super(entityLinks, relProvider, ApplicationController.class, ApplicationResource.class);
    }

    @Override
    public ApplicationResource toResource(Application application) {
        final ApplicationResource resource = createResourceWithId(application.getId(), application);
        // Add (multiple) links to processRoles
        for(ProcessRole role : application.getProcessRoles()) {
            resource.add( processRoleResourceAssembler.linkToSingleResource(role).withRel("roles") );
        }

        return resource;
    }

    @Override
    public Link linkToSingleResource(Application application) {
        return entityLinks.linkToSingleResource(ApplicationResource.class, application.getId());
    }

    @Override
    protected ApplicationResource instantiateResource(Application application) {
        return new ApplicationResource(application.getId(),
            application.getName(),
            application.getStartDate(),
            application.getDurationInMonths(),
            application.getProcessRoles(),
            application.getApplicationStatus(),
            application.getCompetition()
        );
    }
}
