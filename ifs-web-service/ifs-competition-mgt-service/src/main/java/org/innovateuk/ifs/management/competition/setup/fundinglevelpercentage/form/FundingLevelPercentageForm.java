package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import java.util.ArrayList;
import java.util.List;

public class FundingLevelPercentageForm extends CompetitionSetupForm {

    private List<Integer> percentages = new ArrayList<>();

    public List<Integer> getPercentages() {
        return percentages;
    }

    public void setPercentages(List<Integer> percentages) {
        this.percentages = percentages;
    }
}
