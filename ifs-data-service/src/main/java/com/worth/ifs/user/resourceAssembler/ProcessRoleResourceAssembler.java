package com.worth.ifs.user.resourceassembler;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resourceassembler.ApplicationResourceAssembler;
import com.worth.ifs.commons.resource.ExtendedLink;
import com.worth.ifs.user.controller.ProcessRoleController;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class ProcessRoleResourceAssembler extends ResourceAssemblerSupport<ProcessRole, ProcessRoleResource> {

    private Class<ProcessRoleController> controllerClass = ProcessRoleController.class;

    @Autowired
    private ApplicationResourceAssembler applicationResourceAssembler;

    public ProcessRoleResourceAssembler() {
        super(ProcessRoleController.class, ProcessRoleResource.class);
    }

    public ExtendedLink linkToSingleResource(ProcessRole role) {
        Link link = linkTo(methodOn(controllerClass).findOne(role.getId())).withSelfRel();
        return new ExtendedLink(link);
    }

    @Override
    public ProcessRoleResource toResource(ProcessRole role) {
        final ProcessRoleResource resource = createResourceWithId(role.getId(), role);
        Application application = role.getApplication();
        resource.add(applicationResourceAssembler.linkToSingleResource(application).withRel("application").withName("awesome name"));
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

    public Resources<ProcessRoleResource> toEmbeddedList(Iterable<ProcessRole> entities) {
        final List<ProcessRoleResource> resources = toResources(entities);
        return new Resources<>(resources, linkTo(controllerClass).withSelfRel());
    }
}
