package com.worth.ifs.application.resource.comparators;

import java.util.Comparator;

import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryResource;

/**
 * Camporator, handling nulls, and using id if number of partners is equal.
 */
public class ClosedCompetitionSubmittedApplicationSummaryNumberOfPartnersComparator extends DualFieldComparator<Integer, Long> implements Comparator<ClosedCompetitionSubmittedApplicationSummaryResource> {

	@Override
	public int compare(ClosedCompetitionSubmittedApplicationSummaryResource o1, ClosedCompetitionSubmittedApplicationSummaryResource o2) {
		
		Integer o1Lead = o1.getNumberOfPartners();
		Integer o2Lead = o2.getNumberOfPartners();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o1Lead, o2Lead, o1Id, o2Id);
	}
}