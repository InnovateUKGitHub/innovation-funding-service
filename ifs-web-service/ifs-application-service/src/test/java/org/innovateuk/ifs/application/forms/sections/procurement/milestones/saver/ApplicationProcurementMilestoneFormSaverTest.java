package org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationProcurementMilestoneFormSaverTest {

    @InjectMocks
    private ApplicationProcurementMilestoneFormSaver saver;

    @Mock
    private ApplicationProcurementMilestoneRestService restService;

    @Test
    public void save() {
        long applicationId = 1L;
        long organisationId = 2L;

        ProcurementMilestonesForm form = new ProcurementMilestonesForm();
        ProcurementMilestoneForm unsavedMilestone = new ProcurementMilestoneForm();
        unsavedMilestone.setMonth(1);
        unsavedMilestone.setPayment(new BigInteger("100"));
        unsavedMilestone.setDescription("Unsaved milestone");
        unsavedMilestone.setDeliverable("Unsaved deliverable");
        unsavedMilestone.setSuccessCriteria("Unsaved success");
        unsavedMilestone.setTaskOrActivity("Unsaved task");

        ProcurementMilestoneForm savedMilestone = new ProcurementMilestoneForm();
        savedMilestone.setId(4L);
        savedMilestone.setMonth(2);
        savedMilestone.setPayment(new BigInteger("200"));
        savedMilestone.setDescription("saved milestone");
        savedMilestone.setDeliverable("saved deliverable");
        savedMilestone.setSuccessCriteria("saved success");
        savedMilestone.setTaskOrActivity("saved task");

        form.getMilestones().put(generateUnsavedRowId(), unsavedMilestone);
        form.getMilestones().put(String.valueOf(savedMilestone.getId()), unsavedMilestone);

        when(restService.create(any())).thenReturn(restSuccess(new ApplicationProcurementMilestoneResource()));
        when(restService.update(any())).thenReturn(restSuccess());

        ServiceResult<Void> result = saver.save(form, applicationId, organisationId);

        assertThat(result.isSuccess(), is(true));

        verify(restService).create(argThat(lambdaMatches(milestone -> {
            assertThat(milestone.getId(), is(nullValue()));
            assertThat(milestone.getMonth(), is(equalTo(unsavedMilestone.getMonth())));
            assertThat(milestone.getPayment(), is(equalTo(unsavedMilestone.getPayment())));
            assertThat(milestone.getDescription(), is(equalTo(unsavedMilestone.getDescription())));
            assertThat(milestone.getDeliverable(), is(equalTo(unsavedMilestone.getDeliverable())));
            assertThat(milestone.getSuccessCriteria(), is(equalTo(unsavedMilestone.getSuccessCriteria())));
            assertThat(milestone.getTaskOrActivity(), is(equalTo(unsavedMilestone.getTaskOrActivity())));
            return true;
        })));

        verify(restService).update(argThat(lambdaMatches(milestone -> {
            assertThat(milestone.getId(), is(milestone.getId()));
            assertThat(milestone.getMonth(), is(equalTo(unsavedMilestone.getMonth())));
            assertThat(milestone.getPayment(), is(equalTo(unsavedMilestone.getPayment())));
            assertThat(milestone.getDescription(), is(equalTo(unsavedMilestone.getDescription())));
            assertThat(milestone.getDeliverable(), is(equalTo(unsavedMilestone.getDeliverable())));
            assertThat(milestone.getSuccessCriteria(), is(equalTo(unsavedMilestone.getSuccessCriteria())));
            assertThat(milestone.getTaskOrActivity(), is(equalTo(unsavedMilestone.getTaskOrActivity())));
            return true;
        })));
    }
}