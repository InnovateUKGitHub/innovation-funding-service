package com.worth.ifs.user.builder;

import com.worth.ifs.user.domain.Affiliation;
import com.worth.ifs.user.resource.AffiliationType;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.AffiliationType.EMPLOYER;
import static com.worth.ifs.user.resource.AffiliationType.FAMILY_FINANCIAL;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;

public class AffiliationBuilderTest {

    @Test
    public void buildOne() {

        Long expectedId = 1L;
        User expectedUser = newUser().build();
        AffiliationType expectedAffiliationType = FAMILY_FINANCIAL;
        Boolean expectedExists = TRUE;
        String expectedRelation = "Relation";
        String expectedOrganisation = "Organisation";
        String expectedPosition = "Position";
        String expectedDescription = "Description";


        Affiliation affiliation = newAffiliation()
                .withId(expectedId)
                .withUser(expectedUser)
                .withAffiliationType(expectedAffiliationType)
                .withExists(expectedExists)
                .withRelation(expectedRelation)
                .withOrganisation(expectedOrganisation)
                .withPosition(expectedPosition)
                .withDescription(expectedDescription)
                .build();

        assertEquals(expectedId, affiliation.getId());
        assertEquals(expectedUser, affiliation.getUser());
        assertEquals(expectedAffiliationType, affiliation.getAffiliationType());
        assertEquals(expectedExists, affiliation.getExists());
        assertEquals(expectedRelation, affiliation.getRelation());
        assertEquals(expectedOrganisation, affiliation.getOrganisation());
        assertEquals(expectedPosition, affiliation.getPosition());
        assertEquals(expectedDescription, affiliation.getDescription());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        User[] expectedUsers = newUser().buildArray(2, User.class);
        AffiliationType[] expectedAffiliationTypes = {FAMILY_FINANCIAL, EMPLOYER};
        Boolean[] expectedExists = {TRUE, FALSE};
        String[] expectedRelations = {"Relation 1", "Relation 2"};
        String[] expectedOrganisations = {"Organisation 1", "Organisation 2"};
        String[] expectedPositions = {"Position 1", "Position 2"};
        String[] expectedDescriptions = {"Description 1", "Description 2"};

        List<Affiliation> affiliations = newAffiliation()
                .withId(expectedIds)
                .withUser(expectedUsers)
                .withAffiliationType(expectedAffiliationTypes)
                .withExists(expectedExists)
                .withRelation(expectedRelations)
                .withOrganisation(expectedOrganisations)
                .withPosition(expectedPositions)
                .withDescription(expectedDescriptions)
                .build(2);

        Affiliation first = affiliations.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedAffiliationTypes[0], first.getAffiliationType());
        assertEquals(expectedExists[0], first.getExists());
        assertEquals(expectedRelations[0], first.getRelation());
        assertEquals(expectedOrganisations[0], first.getOrganisation());
        assertEquals(expectedPositions[0], first.getPosition());
        assertEquals(expectedDescriptions[0], first.getDescription());

        Affiliation second = affiliations.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedAffiliationTypes[1], second.getAffiliationType());
        assertEquals(expectedExists[1], second.getExists());
        assertEquals(expectedRelations[1], second.getRelation());
        assertEquals(expectedOrganisations[1], second.getOrganisation());
        assertEquals(expectedPositions[1], second.getPosition());
        assertEquals(expectedDescriptions[1], second.getDescription());
    }

}