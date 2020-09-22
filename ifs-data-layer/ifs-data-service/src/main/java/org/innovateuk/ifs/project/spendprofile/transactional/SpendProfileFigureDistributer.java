package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.project.financechecks.domain.Cost;

import java.util.List;

public interface SpendProfileFigureDistributer {

    List<List<Cost>> distributeCosts(SpendProfileCostCategorySummaries summaryPerCategory);
}
