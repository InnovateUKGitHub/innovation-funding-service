package com.worth.ifs.competition.resourceassembler;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resourceassembler.ApplicationResourceAssembler;
import com.worth.ifs.commons.resource.ExtendedLink;
import com.worth.ifs.competition.controller.CompetitionController;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResourceHateoas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class CompetitionResourceAssembler extends ResourceAssemblerSupport<Competition, CompetitionResourceHateoas> {
    private final Log log = LogFactory.getLog(getClass());

    private Class<CompetitionController> controllerClass = CompetitionController.class;

    @Autowired
    private ApplicationResourceAssembler applicationResourceAssembler;

    public CompetitionResourceAssembler() {
        super(CompetitionController.class, CompetitionResourceHateoas.class);
    }

    public ExtendedLink linkToSingleResource(Competition competition) {
        Link link = linkTo(methodOn(controllerClass).getCompetitionByIdHateoas(competition.getId())).withSelfRel();
        return new ExtendedLink(link).withName(competition.getName());
    }

    @Override
    public CompetitionResourceHateoas toResource(Competition entity) {
        final CompetitionResourceHateoas resource = createResourceWithId(entity.getId(), entity);
        // Add (multiple) links to processRoles
        try{
            for (Application application : entity.getApplications()) {
                resource.add(applicationResourceAssembler.linkToSingleResource(application).withRel("applications"));
            }
        }catch(NullPointerException e){
            log.error(e);
        }

        return resource;
    }

    @Override
    protected CompetitionResourceHateoas instantiateResource(Competition competition) {
        return new CompetitionResourceHateoas(competition.getId(),
            competition.getApplications(),
            competition.getQuestions(),
            competition.getSections(),
            competition.getName(),
            competition.getDescription(),
            competition.getStartDate(),
            competition.getEndDate(),
            competition.getAssessmentStartDate(),
            competition.getAssessmentEndDate()
        );
    }
}
