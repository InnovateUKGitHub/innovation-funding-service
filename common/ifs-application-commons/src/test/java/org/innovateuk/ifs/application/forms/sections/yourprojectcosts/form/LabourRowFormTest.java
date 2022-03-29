package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LabourRowFormTest {

    @Test
    public void isBlank() {
        LabourRowForm labourRowForm = new LabourRowForm(false);
        labourRowForm.setRate(BigDecimal.ONE);

        assertTrue(labourRowForm.isBlank());
    }

    @Test
    public void isBlank_for_thirdPartyOfgem() {
        LabourRowForm labourRowForm = new LabourRowForm(true);
        labourRowForm.setGross(BigDecimal.ONE);

        assertTrue(labourRowForm.isBlank());
    }
}
