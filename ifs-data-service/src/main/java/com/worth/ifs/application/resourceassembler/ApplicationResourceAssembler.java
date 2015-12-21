package com.worth.ifs.application.resourceassembler;

import com.worth.ifs.application.controller.ApplicationController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResourceHateoas;
import com.worth.ifs.commons.resource.ExtendedLink;
import com.worth.ifs.competition.resourceassembler.CompetitionResourceAssembler;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class ApplicationResourceAssembler extends ResourceAssemblerSupport<Application, ApplicationResourceHateoas> {

    private final Log log = LogFactory.getLog(getClass());

    private Class<ApplicationController> controllerClass = ApplicationController.class;

    @Autowired
    private com.worth.ifs.user.resourceassembler.ProcessRoleResourceAssembler processRoleResourceAssembler;

    @Autowired
    private CompetitionResourceAssembler competitionResourceAssembler;

    public ApplicationResourceAssembler() {
        super(ApplicationController.class, ApplicationResourceHateoas.class);
    }

    @Override
    public ApplicationResourceHateoas toResource(Application application) {
        final ApplicationResourceHateoas resource = createResourceWithId(application.getId(), application);
        // Add (multiple) links to processRoles
        try{
            for (ProcessRole role : application.getProcessRoles()) {
                resource.add(processRoleResourceAssembler.linkToSingleResource(role).withRel("roles"));
            }
        }catch(NullPointerException e){
            log.error(e);
        }

        resource.add(competitionResourceAssembler.linkToSingleResource(application.getCompetition()).withRel("competition"));

        return resource;
    }

    public ExtendedLink linkToSingleResource(Application application) {
        Link link = linkTo(methodOn(controllerClass).getApplicationByIdHateoas(application.getId())).withSelfRel();
        return new ExtendedLink(link).withTitle(application.getName());
    }

    @Override
    protected ApplicationResourceHateoas instantiateResource(Application application) {
        return new ApplicationResourceHateoas(application.getId(),
            application.getName(),
            application.getStartDate(),
            application.getDurationInMonths(),
            application.getProcessRoles(),
            application.getApplicationStatus(),
            application.getCompetition()
        );
    }

    public Resources<ApplicationResourceHateoas> toEmbeddedList(Iterable<Application> entities) {
        final List<ApplicationResourceHateoas> resources = toResources(entities);
        return new Resources<>(resources, linkTo(controllerClass).withSelfRel());
    }
}
