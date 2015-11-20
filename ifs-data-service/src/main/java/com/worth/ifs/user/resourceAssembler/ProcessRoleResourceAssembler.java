package com.worth.ifs.user.resourceAssembler;

import com.worth.ifs.commons.resource.EmbeddableResourceAssemblerSupport;
import com.worth.ifs.user.controller.ProcessRoleController;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.stereotype.Service;

@Service
public class ProcessRoleResourceAssembler extends EmbeddableResourceAssemblerSupport<ProcessRole, ProcessRoleResource, ProcessRoleController> {

    @Autowired
    public ProcessRoleResourceAssembler(final EntityLinks entityLinks, final RelProvider relProvider) {
        super(entityLinks, relProvider, ProcessRoleController.class, ProcessRoleResource.class);
    }

    @Override
    public Link linkToSingleResource(ProcessRole role) {
        return entityLinks.linkToSingleResource(ProcessRoleResource.class, role.getId());
    }

    @Override
    public ProcessRoleResource toResource(ProcessRole role) {
        final ProcessRoleResource resource = createResourceWithId(role.getId(), role);
        return resource;
    }

    @Override
    protected ProcessRoleResource instantiateResource(ProcessRole processRole) {
        return new ProcessRoleResource(processRole.getId(),
            processRole.getUser(),
            processRole.getApplication(),
            processRole.getRole(),
            processRole.getOrganisation()
        );
    }
}
