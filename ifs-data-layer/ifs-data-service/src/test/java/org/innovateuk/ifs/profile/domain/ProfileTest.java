package org.innovateuk.ifs.profile.domain;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProfileTest {

    @Test
    public void testAddInnovationAreas() {
        InnovationArea expectedInnovationArea1 = newInnovationArea().withName("Innovation Area A").build();
        InnovationArea expectedInnovationArea2 = newInnovationArea().withName("Innovation Area B").build();

        Profile profile = newProfile().build();

        profile.addInnovationAreas(newHashSet(expectedInnovationArea1, expectedInnovationArea2));

        Set<InnovationArea> innovationAreas = profile.getInnovationAreas();

        assertEquals(2, innovationAreas.size());
        assertTrue(innovationAreas.contains(expectedInnovationArea1));
        assertTrue(innovationAreas.contains(expectedInnovationArea2));
    }

    @Test
    public void isAffiliationsComplete_none() {
        User user = newUser().build();
        assertFalse(Profile.isAffiliationsComplete(user));
    }

    @Test
    public void isAffiliationsComplete_completed() {
        User user = newUser().withAffiliations(newAffiliation().withModifiedOn(ZonedDateTime.now()).build(1)).build();
        assertTrue(Profile.isAffiliationsComplete(user));
    }

    @Test
    public void isAffiliationsComplete_expired() {
        User user = newUser().withAffiliations(newAffiliation().withModifiedOn(ZonedDateTime.now().minusYears(1)).build(1)).build();
        assertFalse(Profile.isAffiliationsComplete(user));
    }

    @Test
    public void startOfCurrentFinancialYear() {
        assertEquals(LocalDate.of(2017, 04, 06), Profile.startOfCurrentFinancialYear(ZonedDateTime.parse("2018-04-05T11:59:59+00:00")));
        assertEquals(LocalDate.of(2018, 04, 06), Profile.startOfCurrentFinancialYear(ZonedDateTime.parse("2018-04-06T00:00:00+00:00")));
        assertEquals(LocalDate.of(2018, 04, 06), Profile.startOfCurrentFinancialYear(ZonedDateTime.parse("2018-04-06T00:00:01+00:00")));
    }
}