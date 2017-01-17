package org.innovateuk.ifs.invite.builder;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class AvailableAssessorResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedName = "name";
        Set<CategoryResource> expectedInnovationAreas = Sets.newHashSet(newInnovationAreaResource().build());
        Boolean expectedCompliant = FALSE;
        String expectedEmail = "email";
        BusinessType expectedBusinessType = BUSINESS;
        Boolean expectedAdded = TRUE;

        AvailableAssessorResource availableAssessorResource = newAvailableAssessorResource()
                .withName(expectedName)
                .withInnovationArea(expectedInnovationAreas)
                .withCompliant(expectedCompliant)
                .withEmail(expectedEmail)
                .withBusinessType(expectedBusinessType)
                .withAdded(expectedAdded)
                .build();

        assertEquals(expectedName, availableAssessorResource.getName());
        assertEquals(expectedInnovationAreas, availableAssessorResource.getInnovationAreas());
        assertEquals(expectedCompliant, availableAssessorResource.isCompliant());
        assertEquals(expectedEmail, availableAssessorResource.getEmail());
        assertEquals(expectedBusinessType, availableAssessorResource.getBusinessType());
        assertEquals(expectedAdded, availableAssessorResource.isAdded());
    }

    @Test
    public void buildMany() {
        String[] expectedNames = {"name1", "name2"};
        List<Set<CategoryResource>> expectedInnovationAreas = new ArrayList<>();
        expectedInnovationAreas.add(Sets.newHashSet(newInnovationAreaResource().build()));
        expectedInnovationAreas.add(Sets.newHashSet(newInnovationAreaResource().build()));
        Boolean[] expectedCompliants = {TRUE, FALSE};
        String[] expectedEmails = {"email1", "email2"};
        BusinessType[] expectedBusinessTypes = {BUSINESS, ACADEMIC};
        Boolean[] expectedAddeds = {TRUE, FALSE};

        List<AvailableAssessorResource> availableAssessorResources = newAvailableAssessorResource()
                .withName(expectedNames)
                .withInnovationArea(expectedInnovationAreas.get(0), expectedInnovationAreas.get(1))
                .withCompliant(expectedCompliants)
                .withEmail(expectedEmails)
                .withBusinessType(expectedBusinessTypes)
                .withAdded(expectedAddeds)
                .build(2);

        AvailableAssessorResource first = availableAssessorResources.get(0);
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedInnovationAreas.get(0), first.getInnovationAreas());
        assertEquals(expectedCompliants[0], first.isCompliant());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());
        assertEquals(expectedAddeds[0], first.isAdded());

        AvailableAssessorResource second = availableAssessorResources.get(1);
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedInnovationAreas.get(1), second.getInnovationAreas());
        assertEquals(expectedCompliants[1], second.isCompliant());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
        assertEquals(expectedAddeds[1], second.isAdded());
    }
}
