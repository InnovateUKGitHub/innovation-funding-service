package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class AvailableAssessorResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedFirstName = "firstName";
        String expectedLastName = "lastName";
        CategoryResource expectedInnovationArea = newCategoryResource().build();
        Boolean expectedCompliant = FALSE;
        String expectedEmail = "email";
        BusinessType expectedBusinessType = BUSINESS;
        Boolean expectedAdded = TRUE;

        AvailableAssessorResource availableAssessorResource = newAvailableAssessorResource()
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withInnovationArea(expectedInnovationArea)
                .withCompliant(expectedCompliant)
                .withEmail(expectedEmail)
                .withBusinessType(expectedBusinessType)
                .withAdded(expectedAdded)
                .build();

        assertEquals(expectedFirstName, availableAssessorResource.getFirstName());
        assertEquals(expectedLastName, availableAssessorResource.getLastName());
        assertEquals(expectedInnovationArea, availableAssessorResource.getInnovationArea());
        assertEquals(expectedCompliant, availableAssessorResource.isCompliant());
        assertEquals(expectedEmail, availableAssessorResource.getEmail());
        assertEquals(expectedBusinessType, availableAssessorResource.getBusinessType());
        assertEquals(expectedAdded, availableAssessorResource.isAdded());
    }

    @Test
    public void buildMany() {
        String[] expectedFirstNames = {"firstName1", "firstName2"};
        String[] expectedLastNames = {"lastName1", "lastName2"};
        CategoryResource[] expectedInnovationAreas = newCategoryResource().buildArray(2, CategoryResource.class);
        Boolean[] expectedCompliants = {TRUE, FALSE};
        String[] expectedEmails = {"email1", "email2"};
        BusinessType[] expectedBusinessTypes = {BUSINESS, ACADEMIC};
        Boolean[] expectedAddeds = {TRUE, FALSE};

        List<AvailableAssessorResource> availableAssessorResources = newAvailableAssessorResource()
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withInnovationArea(expectedInnovationAreas)
                .withCompliant(expectedCompliants)
                .withEmail(expectedEmails)
                .withBusinessType(expectedBusinessTypes)
                .withAdded(expectedAddeds)
                .build(2);

        AvailableAssessorResource first = availableAssessorResources.get(0);
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedInnovationAreas[0], first.getInnovationArea());
        assertEquals(expectedCompliants[0], first.isCompliant());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());
        assertEquals(expectedAddeds[0], first.isAdded());

        AvailableAssessorResource second = availableAssessorResources.get(1);
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedInnovationAreas[1], second.getInnovationArea());
        assertEquals(expectedCompliants[1], second.isCompliant());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
        assertEquals(expectedAddeds[1], second.isAdded());
    }
}
