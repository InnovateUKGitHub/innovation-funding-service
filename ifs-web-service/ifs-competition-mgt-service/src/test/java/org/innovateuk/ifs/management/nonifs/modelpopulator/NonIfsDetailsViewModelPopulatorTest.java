package org.innovateuk.ifs.management.nonifs.modelpopulator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.nonifs.viewmodel.NonIfsDetailsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NonIfsDetailsViewModelPopulatorTest {

    @InjectMocks
    private NonIfsDetailsViewModelPopulator target;

    @Mock
    private CategoryRestService categoryRestService;

    @Test
    public void testPopulate() {
        List<InnovationAreaResource> innovationAreas = new ArrayList<>();
        List<InnovationSectorResource> innovationSectors = new ArrayList<>();
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreas));
        when(categoryRestService.getInnovationSectors()).thenReturn(restSuccess(innovationSectors));
        CompetitionResource competition = newCompetitionResource().build();

        NonIfsDetailsViewModel viewModel = target.populate(competition);

        assertThat(viewModel.getInnovationAreas(), equalTo(innovationAreas));
        assertThat(viewModel.getInnovationSectors(), equalTo(innovationSectors));
        assertThat(viewModel.getCompetition(), equalTo(competition));
    }

}
