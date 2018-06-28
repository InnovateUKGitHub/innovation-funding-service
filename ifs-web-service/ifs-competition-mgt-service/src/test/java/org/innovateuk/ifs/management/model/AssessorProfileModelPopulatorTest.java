package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.assessor.populator.AssessorProfileModelPopulator;
import org.innovateuk.ifs.management.competition.viewmodel.InnovationSectorViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.AssessorsProfileViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessorProfileModelPopulatorTest {

    @InjectMocks
    private AssessorProfileModelPopulator assessorProfileModelPopulator;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private AssessorRestService assessorRestService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void populateModel() throws Exception {
        long competitionId = 7L;
        long assessorId = 11L;
        String expectedFirstName = "firstname";
        String expectedLastName = "lastname";
        String expectedEmail = "email";
        String expectedPhone = "phone";
        String expectedSkills = "skills";
        BusinessType expectedBusinessType = ACADEMIC;
        CompetitionResource expectedCompetition = newCompetitionResource().build();
        AddressResource expectedAddress = newAddressResource().build();

        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource()
                .withSector(1L, 2L, 1L)
                .withSectorName("sector 1", "sector 2", "sector 1")
                .withName("innovation area 1", "innovation area 2", "innovation area 3")
                .build(3);

        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(
                        newUserResource()
                                .withFirstName(expectedFirstName)
                                .withLastName(expectedLastName)
                                .withEmail(expectedEmail)
                                .withPhoneNumber(expectedPhone)
                                .build()
                )
                .withProfile(
                        newProfileResource()
                                .withBusinessType(ACADEMIC)
                                .withSkillsAreas(expectedSkills)
                                .withInnovationAreas(expectedInnovationAreas)
                                .withAddress(expectedAddress)
                                .build()
                )
                .build();

        when(competitionService.getById(competitionId)).thenReturn(expectedCompetition);
        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(assessorProfileResource));

        AssessorsProfileViewModel viewModel =
                assessorProfileModelPopulator.populateModel(assessorId, competitionId);

        InOrder inOrder = inOrder(competitionService, assessorRestService);
        inOrder.verify(competitionService).getById(competitionId);
        inOrder.verify(assessorRestService).getAssessorProfile(assessorId);
        inOrder.verifyNoMoreInteractions();

        assertEquals(expectedCompetition, viewModel.getCompetition());
        assertEquals(expectedFirstName + " " + expectedLastName, viewModel.getName());
        assertEquals(expectedEmail, viewModel.getEmail());
        assertEquals(expectedPhone, viewModel.getPhone());
        assertEquals(expectedSkills, viewModel.getSkills());
        assertEquals(expectedAddress, viewModel.getAddress());
        assertEquals(expectedBusinessType.getDisplayName(), viewModel.getBusinessType());
        assertEquals(2, viewModel.getInnovationSectors().size());

        InnovationSectorViewModel sector1 = viewModel.getInnovationSectors().get(0);
        assertEquals("sector 1", sector1.getName());
        assertEquals(2, sector1.getChildren().size());
        assertEquals(expectedInnovationAreas.get(0), sector1.getChildren().get(0));
        assertEquals(expectedInnovationAreas.get(2), sector1.getChildren().get(1));

        InnovationSectorViewModel sector2 = viewModel.getInnovationSectors().get(1);
        assertEquals("sector 2", sector2.getName());
        assertEquals(1, sector2.getChildren().size());
        assertEquals(expectedInnovationAreas.get(1), sector2.getChildren().get(0));
    }
}