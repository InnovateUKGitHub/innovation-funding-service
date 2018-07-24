package org.innovateuk.ifs.profile.domain;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.junit.Assert.assertEquals;
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
    public void startOfCurrentFinancialYear() {
        assertEquals(LocalDate.of(2017, 04, 06), Profile.startOfCurrentFinancialYear(ZonedDateTime.parse("2018-04-05T11:59:59+00:00")));
        assertEquals(LocalDate.of(2018, 04, 06), Profile.startOfCurrentFinancialYear(ZonedDateTime.parse("2018-04-06T00:00:00+00:00")));
        assertEquals(LocalDate.of(2018, 04, 06), Profile.startOfCurrentFinancialYear(ZonedDateTime.parse("2018-04-06T00:00:01+00:00")));
    }
}