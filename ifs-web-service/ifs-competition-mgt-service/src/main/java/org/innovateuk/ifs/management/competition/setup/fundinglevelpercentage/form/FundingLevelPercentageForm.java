package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FundingLevelPercentageForm extends CompetitionSetupForm {

    private List<List<FundingLevelMaximumForm>> maximums = new ArrayList<>();

    public List<List<FundingLevelMaximumForm>> getMaximums() {
        return maximums;
    }

    public void setMaximums(List<List<FundingLevelMaximumForm>> maximums) {
        this.maximums = maximums;
    }

    public int indexForSize(OrganisationSize size) {
        return IntStream.range(0, maximums.size())
                .filter(index -> maximums.get(index).get(0).getOrganisationSize() == size)
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("No matching maximum on form"));
    }

    public int indexForCategory(ResearchCategoryResource category) {
        return IntStream.range(0, maximums.get(0).size())
                .filter(index -> maximums.get(0).get(index).getCategoryId().equals(category.getId()))
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("No matching maximum on form"));
    }
}
