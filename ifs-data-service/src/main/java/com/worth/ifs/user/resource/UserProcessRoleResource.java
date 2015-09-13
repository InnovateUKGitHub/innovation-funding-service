package com.worth.ifs.user.resource;

import com.worth.ifs.user.controller.UserApplicationRoleController;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import lombok.NonNull;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserProcessRoleResource implements ResourceProcessor<Resource<User>> {

    @Override
    public Resource<User> process(Resource<User> resource) {
        User user = resource.getContent();
        resource.add(linkTo(methodOn(UserApplicationRoleController.class).findByUser(user.getId())).withRel("userApplicationRole"));
        return resource;
    }
}
