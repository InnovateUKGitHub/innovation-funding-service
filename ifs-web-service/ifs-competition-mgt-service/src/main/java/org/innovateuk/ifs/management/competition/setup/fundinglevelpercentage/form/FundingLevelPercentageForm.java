package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FundingLevelPercentageForm extends CompetitionSetupForm {

    private List<FundingLevelMaximumForm> maximums = new ArrayList<>();

    public List<FundingLevelMaximumForm> getMaximums() {
        return maximums;
    }

    public void setMaximums(List<FundingLevelMaximumForm> maximums) {
        this.maximums = maximums;
    }

    public int indexFor(OrganisationSize size, ResearchCategoryResource cat) {
        return IntStream.range(0, maximums.size())
                .filter(index -> maximums.get(index).getCategoryId().equals(cat.getId()) && maximums.get(index).getOrganisationSize() == size)
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("No matching maximum on form"));
    }
}
