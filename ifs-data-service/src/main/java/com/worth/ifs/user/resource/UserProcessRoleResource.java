package com.worth.ifs.user.resource;

import com.worth.ifs.user.controller.ProcessRoleController;
import com.worth.ifs.user.domain.User;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * This class is used convert domain object to a resource object,
 * that is used to send data through REST calls.
 */
public class UserProcessRoleResource implements ResourceProcessor<Resource<User>> {

    @Override
    public Resource<User> process(Resource<User> resource) {
        User user = resource.getContent();
        resource.add(linkTo(methodOn(ProcessRoleController.class).findByUser(user.getId())).withRel("userApplicationRole"));
        return resource;
    }
}
