package com.worth.ifs.application.resource.comparators;

import java.math.BigDecimal;
import java.util.Comparator;

import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryResource;

/**
 * Camporator, handling nulls, and using id if grant requested is equal.
 */
public class ClosedCompetitionSubmittedApplicationSummaryGrantRequestedComparator extends DualFieldComparator<BigDecimal, Long> implements Comparator<ClosedCompetitionSubmittedApplicationSummaryResource> {

	@Override
	public int compare(ClosedCompetitionSubmittedApplicationSummaryResource o1, ClosedCompetitionSubmittedApplicationSummaryResource o2) {
		
		BigDecimal o1Lead = o1.getGrantRequested();
		BigDecimal o2Lead = o2.getGrantRequested();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o1Lead, o2Lead, o1Id, o2Id);
	}
}