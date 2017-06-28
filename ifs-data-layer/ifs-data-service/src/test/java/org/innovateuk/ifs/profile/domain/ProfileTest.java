package org.innovateuk.ifs.profile.domain;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.junit.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ProfileTest {

    @Test
    public void testAddInnovationAreas() throws Exception {
        InnovationArea expectedInnovationArea1 = newInnovationArea().withName("Innovation Area A").build();
        InnovationArea expectedInnovationArea2 = newInnovationArea().withName("Innovation Area B").build();

        Profile profile = newProfile().build();

        profile.addInnovationAreas(newHashSet(expectedInnovationArea1, expectedInnovationArea2));

        Set<InnovationArea> innovationAreas = profile.getInnovationAreas();

        assertEquals(2, innovationAreas.size());
        assertTrue(innovationAreas.contains(expectedInnovationArea1));
        assertTrue(innovationAreas.contains(expectedInnovationArea2));
    }
}
