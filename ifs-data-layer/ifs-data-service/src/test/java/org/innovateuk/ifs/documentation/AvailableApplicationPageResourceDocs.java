package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.AvailableApplicationPageResourceBuilder;

import static org.innovateuk.ifs.invite.builder.AvailableApplicationPageResourceBuilder.newAvailableApplicationPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableApplicationResourceBuilder.newAvailableApplicationResource;

public class AvailableApplicationPageResourceDocs {

        public static final AvailableApplicationPageResourceBuilder availableApplicationPageResourceBuilder =
                newAvailableApplicationPageResource()
                        .withContent(newAvailableApplicationResource().build(2))
                        .withSize(20)
                        .withTotalPages(5)
                        .withTotalElements(100L)
                        .withNumber(0);
    }