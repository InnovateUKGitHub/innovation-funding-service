package com.worth.ifs.assessment.builder;

import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static org.junit.Assert.assertEquals;

public class ApplicationRejectionResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedRejectReason = "Reason";
        String expectedRejectComment = "Comment";

        ApplicationRejectionResource applicationRejectionResource = newApplicationRejectionResource()
                .withRejectReason(expectedRejectReason)
                .withRejectComment(expectedRejectComment)
                .build();

        assertEquals(expectedRejectReason, applicationRejectionResource.getRejectReason());
        assertEquals(expectedRejectComment, applicationRejectionResource.getRejectComment());
    }

    @Test
    public void buildMany() {
        String[] expectedRejectReasons = {"Reason 1", "Reason 2"};
        String[] expectedRejectComments = {"Comment 1", "Comment 2"};

        List<ApplicationRejectionResource> applicationRejectionResources = newApplicationRejectionResource()
                .withRejectReason(expectedRejectReasons)
                .withRejectComment(expectedRejectComments)
                .build(2);

        ApplicationRejectionResource first = applicationRejectionResources.get(0);

        assertEquals(expectedRejectReasons[0], first.getRejectReason());
        assertEquals(expectedRejectComments[0], first.getRejectComment());

        ApplicationRejectionResource second = applicationRejectionResources.get(1);

        assertEquals(expectedRejectReasons[1], second.getRejectReason());
        assertEquals(expectedRejectComments[1], second.getRejectComment());
    }

}