package com.worth.ifs.application.resource.comparators;

import java.util.Comparator;

import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryResource;

public class ClosedCompetitionNotSubmittedApplicationSummaryPercentageCompleteComparator extends DualFieldComparator<Integer, Long> implements Comparator<ClosedCompetitionNotSubmittedApplicationSummaryResource> {

	@Override
	public int compare(ClosedCompetitionNotSubmittedApplicationSummaryResource o1, ClosedCompetitionNotSubmittedApplicationSummaryResource o2) {
		
		Integer o1Lead = o1.getCompletedPercentage();
		Integer o2Lead = o2.getCompletedPercentage();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o2Lead, o1Lead, o1Id, o2Id);
	}
}