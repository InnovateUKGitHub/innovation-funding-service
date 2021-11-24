package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;

import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;

public class ProjectDocs {

    @SuppressWarnings("unchecked")
    public static final ProjectResourceBuilder projectResourceBuilder = newProjectResource()
            .withId(1L)
            .with(name("Sample Project"))
            .withTargetStartDate(LocalDate.now())
            .withAddress(new AddressResource())
            .withDuration(1L)
            .withProjectUsers(asList(12L, 13L, 14L));
}
