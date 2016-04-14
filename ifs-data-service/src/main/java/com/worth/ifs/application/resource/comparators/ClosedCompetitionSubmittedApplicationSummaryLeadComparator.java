package com.worth.ifs.application.resource.comparators;

import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryResource;

import java.util.Comparator;

/**
 * Camporator, handling nulls, and using id if lead is equal.
 */
public class ClosedCompetitionSubmittedApplicationSummaryLeadComparator extends DualFieldComparator<String, Long> implements Comparator<ClosedCompetitionSubmittedApplicationSummaryResource> {

	@Override
	public int compare(ClosedCompetitionSubmittedApplicationSummaryResource o1, ClosedCompetitionSubmittedApplicationSummaryResource o2) {
		
		String o1Lead = o1.getLead();
		String o2Lead = o2.getLead();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o1Lead, o2Lead, o1Id, o2Id);
	}
}