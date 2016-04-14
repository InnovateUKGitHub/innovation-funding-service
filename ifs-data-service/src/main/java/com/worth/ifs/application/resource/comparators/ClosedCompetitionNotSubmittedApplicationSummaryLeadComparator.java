package com.worth.ifs.application.resource.comparators;

import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryResource;

import java.util.Comparator;

public class ClosedCompetitionNotSubmittedApplicationSummaryLeadComparator extends DualFieldComparator<String, Long> implements Comparator<ClosedCompetitionNotSubmittedApplicationSummaryResource> {

	@Override
	public int compare(ClosedCompetitionNotSubmittedApplicationSummaryResource o1, ClosedCompetitionNotSubmittedApplicationSummaryResource o2) {
		
		String o1Lead = o1.getLead();
		String o2Lead = o2.getLead();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o1Lead, o2Lead, o1Id, o2Id);
	}

}
