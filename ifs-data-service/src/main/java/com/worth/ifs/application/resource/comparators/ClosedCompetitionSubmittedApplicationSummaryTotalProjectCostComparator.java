package com.worth.ifs.application.resource.comparators;

import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryResource;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Camporator, handling nulls, and using id if total project cost is equal.
 */
public class ClosedCompetitionSubmittedApplicationSummaryTotalProjectCostComparator extends DualFieldComparator<BigDecimal, Long> implements Comparator<ClosedCompetitionSubmittedApplicationSummaryResource> {

	@Override
	public int compare(ClosedCompetitionSubmittedApplicationSummaryResource o1, ClosedCompetitionSubmittedApplicationSummaryResource o2) {
		
		BigDecimal o1Lead = o1.getTotalProjectCost();
		BigDecimal o2Lead = o2.getTotalProjectCost();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o1Lead, o2Lead, o1Id, o2Id);
	}
}