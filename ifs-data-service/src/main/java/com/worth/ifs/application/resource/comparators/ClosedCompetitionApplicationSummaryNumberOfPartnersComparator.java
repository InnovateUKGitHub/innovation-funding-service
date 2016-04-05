package com.worth.ifs.application.resource.comparators;

import java.util.Comparator;

import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryResource;

/**
 * Camporator, handling nulls, and using id if number of partners is equal.
 */
public class ClosedCompetitionApplicationSummaryNumberOfPartnersComparator extends DualFieldComparator<Integer, Long> implements Comparator<ClosedCompetitionApplicationSummaryResource> {

	@Override
	public int compare(ClosedCompetitionApplicationSummaryResource o1, ClosedCompetitionApplicationSummaryResource o2) {
		
		Integer o1Lead = o1.getNumberOfPartners();
		Integer o2Lead = o2.getNumberOfPartners();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o1Lead, o2Lead, o1Id, o2Id);
	}
}