package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;

public class AvailableAssessorResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedUserId = 1L;
        String expectedFirstName = "firstName";
        String expectedLastName = "lastName";
        String expectedEmail = "email";
        BusinessType expectedBusinessType = BUSINESS;
        CategoryResource expectedInnovationArea = newCategoryResource().build();
        Boolean expectedCompliant = FALSE;
        Boolean expectedAdded = TRUE;

        AvailableAssessorResource availableAssessorResource = newAvailableAssessorResource()
                .withUserId(expectedUserId)
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withEmail(expectedEmail)
                .withBusinessType(expectedBusinessType)
                .withInnovationArea(expectedInnovationArea)
                .withCompliant(expectedCompliant)
                .withAdded(expectedAdded)
                .build();

        assertEquals(expectedUserId, availableAssessorResource.getUserId());
        assertEquals(expectedFirstName, availableAssessorResource.getFirstName());
        assertEquals(expectedLastName, availableAssessorResource.getLastName());
        assertEquals(expectedEmail, availableAssessorResource.getEmail());
        assertEquals(expectedBusinessType, availableAssessorResource.getBusinessType());
        assertEquals(expectedInnovationArea, availableAssessorResource.getInnovationArea());
        assertEquals(expectedCompliant, availableAssessorResource.isCompliant());
        assertEquals(expectedAdded, availableAssessorResource.isAdded());
    }

    @Test
    public void buildMany() {
        Long[] expectedUserIds = {1L, 2L};
        String[] expectedFirstNames = {"firstName1", "firstName2"};
        String[] expectedLastNames = {"lastName1", "lastName2"};
        String[] expectedEmails = {"email1", "email2"};
        BusinessType[] expectedBusinessTypes = {BUSINESS, ACADEMIC};
        CategoryResource[] expectedInnovationAreas = newCategoryResource().buildArray(2, CategoryResource.class);
        Boolean[] expectedCompliants = {TRUE, FALSE};
        Boolean[] expectedAddeds = {TRUE, FALSE};

        List<AvailableAssessorResource> availableAssessorResources = newAvailableAssessorResource()
                .withUserId(expectedUserIds)
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withEmail(expectedEmails)
                .withBusinessType(expectedBusinessTypes)
                .withInnovationArea(expectedInnovationAreas)
                .withCompliant(expectedCompliants)
                .withAdded(expectedAddeds)
                .build(2);

        AvailableAssessorResource first = availableAssessorResources.get(0);
        assertEquals(expectedUserIds[0], first.getUserId());
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());
        assertEquals(expectedInnovationAreas[0], first.getInnovationArea());
        assertEquals(expectedCompliants[0], first.isCompliant());
        assertEquals(expectedAddeds[0], first.isAdded());

        AvailableAssessorResource second = availableAssessorResources.get(1);
        assertEquals(expectedUserIds[1], second.getUserId());
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
        assertEquals(expectedInnovationAreas[1], second.getInnovationArea());
        assertEquals(expectedCompliants[1], second.isCompliant());
        assertEquals(expectedAddeds[1], second.isAdded());
    }
}
