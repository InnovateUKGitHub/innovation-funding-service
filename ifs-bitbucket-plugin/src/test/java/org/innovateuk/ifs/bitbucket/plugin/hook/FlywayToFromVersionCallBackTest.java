package org.innovateuk.ifs.bitbucket.plugin.hook;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class FlywayToFromVersionCallBackTest {
    @Test
    public void testNoPatchingError() {
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack();
        callback.onTo(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__Patch.sql", asList(1, 2, 4))));
        callback.onFrom(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__Patch.sql", asList(1, 2, 4)), of("V1_2_5__NewPatch.sql", asList(1, 2, 5))));

        List<String> errors = callback.getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testPatchingErrorSameLevel() {
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack();
        callback.onTo(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__NewToPatch.sql", asList(1, 2, 4))));
        callback.onFrom(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__NewFromPatch.sql", asList(1, 2, 4))));

        List<String> errors = callback.getErrors();

        assertEquals(1, errors.size());
        assertEquals("Flyway patch level error, please increase the level of the flyway patches in the incoming branch to be greater than the patch V1_2_4__NewToPatch.sql. Currently the lowest incoming patch is V1_2_4__NewFromPatch.sql", errors.get(0));
    }

    @Test
    public void testPatchingErrorLowerThanCurrent() {
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack();
        callback.onTo(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_5__NewToPatch.sql", asList(1, 2, 5))));
        callback.onFrom(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__NewFromPatch.sql", asList(1, 2, 4))));

        List<String> errors = callback.getErrors();

        assertEquals(1, errors.size());
        assertEquals("Flyway patch level error, please increase the level of the flyway patches in the incoming branch to be greater than the patch V1_2_5__NewToPatch.sql. Currently the lowest incoming patch is V1_2_4__NewFromPatch.sql", errors.get(0));
    }

}
