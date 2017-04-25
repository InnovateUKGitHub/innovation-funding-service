package org.innovateuk.ifs.nonifs.modelpopulator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.nonifs.viewmodel.NonIfsDetailsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

        NonIfsDetailsViewModel viewModel = target.populate();

        assertThat(viewModel.getInnovationAreas(), equalTo(innovationAreas));
        assertThat(viewModel.getInnovationSectors(), equalTo(innovationSectors));
    }

}
