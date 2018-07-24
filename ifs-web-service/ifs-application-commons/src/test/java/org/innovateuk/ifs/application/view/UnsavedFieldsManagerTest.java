package org.innovateuk.ifs.application.view;

import org.innovateuk.ifs.application.populator.finance.model.FinanceFormField;
import org.innovateuk.ifs.application.populator.finance.view.UnsavedFieldsManager;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnsavedFieldsManagerTest {

    private UnsavedFieldsManager unsavedFieldsManager;

    @Before
    public void setUp() {
        unsavedFieldsManager = new UnsavedFieldsManager();
    }

    @Test
    public void separateGroups() {
        List<FinanceFormField> fields = asList(field("a", "1"), field("b", "2"), field("c", "3"));

        Map<String, List<FinanceFormField>> map = unsavedFieldsManager.separateGroups(fields);

        assertEquals(3, map.size());
        assertThat(map, hasEntry("a", asList(field("a", "1"))));
        assertThat(map, hasEntry("b", asList(field("b", "2"))));
        assertThat(map, hasEntry("c", asList(field("c", "3"))));
    }

    @Test
    public void separateGroups_empty() {
        assertTrue(unsavedFieldsManager.separateGroups(emptyList()).isEmpty());
    }

    @Test
    public void separateGroups_nullValue() {
        List<FinanceFormField> fields = asList(field("a", "1"), field("b", null));

        Map<String, List<FinanceFormField>> map = unsavedFieldsManager.separateGroups(fields);

        assertThat(map, hasEntry("a", asList(field("a", "1"))));
        assertThat(map, not(hasKey("b")));
    }

    @Test
    public void separateGroups_blankValue() {
        List<FinanceFormField> fields = asList(field("a", "1"), field("b", ""));

        Map<String, List<FinanceFormField>> map = unsavedFieldsManager.separateGroups(fields);

        assertThat(map, hasEntry("a", asList(field("a", "1"))));
        assertThat(map, not(hasKey("b")));
    }

    @Test
    public void separateGroups_multiple() {
        List<FinanceFormField> fields = asList(field("a", "1"), field("b", "2"), field("a", "3"), field("b", "4"));

        Map<String, List<FinanceFormField>> map = unsavedFieldsManager.separateGroups(fields);

        assertEquals(2, map.size());
        assertThat(map, hasEntry("a", asList(field("a", "1"), field("a", "3"))));
        assertThat(map, hasEntry("b", asList(field("b", "2"), field("b", "4"))));
    }

    private FinanceFormField field(String fieldName, String value) {
        return new FinanceFormField(fieldName, value, null, null, null, null);
    }
}
