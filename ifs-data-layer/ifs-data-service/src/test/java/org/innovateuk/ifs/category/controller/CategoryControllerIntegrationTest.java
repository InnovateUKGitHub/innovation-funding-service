package org.innovateuk.ifs.category.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@Rollback
@Transactional
public class CategoryControllerIntegrationTest extends BaseControllerIntegrationTest<CategoryController> {

    private static final int EXPECTED_INNOVATION_SECTOR_TOTAL = 7;
    private static final int EXPECTED_INNOVATION_AREA_TOTAL = 54;
    private static final int EXPECTED_RESEARCH_CATEGORY_TOTAL = 3;

    @Override
    @Autowired
    protected void setControllerUnderTest(CategoryController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Test
    public void findInnovationAreas() {
        List<InnovationAreaResource> innovationAreas = controller.findInnovationAreas().getSuccess();

        assertThat(innovationAreas, hasSize(EXPECTED_INNOVATION_AREA_TOTAL));
        assertThat(innovationAreas, hasItem(hasProperty("name", equalTo("None"))));
        assertThat(innovationAreas, everyItem(hasProperty("sector", notNullValue())));
    }

    @Test
    public void findInnovationAreasExcludingNone() {
        List<InnovationAreaResource> innovationAreas = controller.findInnovationAreasExcludingNone().getSuccess();

        assertThat(innovationAreas, hasSize(EXPECTED_INNOVATION_AREA_TOTAL - 1));
        assertThat(innovationAreas, not(hasItem(hasProperty("name", equalTo("None")))));
        assertThat(innovationAreas, everyItem(hasProperty("sector", notNullValue())));
    }

    @Test
    public void findInnovationSectors() {
        List<InnovationSectorResource> innovationSectors = controller.findInnovationSectors().getSuccess();

        assertThat(innovationSectors, hasSize(EXPECTED_INNOVATION_SECTOR_TOTAL));
        assertThat(innovationSectors, everyItem(hasProperty("children", notNullValue())));
    }

    @Test
    public void findResearchCategories() {
        List<ResearchCategoryResource> researchCategories = controller.findResearchCategories().getSuccess();

        assertThat(researchCategories, hasSize(EXPECTED_RESEARCH_CATEGORY_TOTAL));
    }

    @Test
    public void findInnovationAreasBySector() {
        List<InnovationAreaResource> innovationAreas = controller.findInnovationAreasBySector(1L).getSuccess();

        assertThat(innovationAreas, hasSize(10));
        assertThat(innovationAreas, everyItem(hasProperty("sector", equalTo(1L))));
        assertThat(innovationAreas, containsInAnyOrder(asList(
                hasProperty("name", equalTo("Advanced therapies")),
                hasProperty("name", equalTo("Agricultural productivity")),
                hasProperty("name", equalTo("Biosciences")),
                hasProperty("name", equalTo("Diagnostics, medical technology and devices")),
                hasProperty("name", equalTo("Digital health")),
                hasProperty("name", equalTo("Enhancing food quality")),
                hasProperty("name", equalTo("Independent living and wellbeing")),
                hasProperty("name", equalTo("Precision medicine")),
                hasProperty("name", equalTo("Preclinical technologies and drug target discovery")),
                hasProperty("name", equalTo("Therapeutic and medicine development"))
        )));
    }
}