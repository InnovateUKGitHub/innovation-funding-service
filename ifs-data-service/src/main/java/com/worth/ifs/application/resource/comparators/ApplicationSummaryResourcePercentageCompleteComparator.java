package com.worth.ifs.application.resource.comparators;

import com.worth.ifs.application.resource.ApplicationSummaryResource;

import java.util.Comparator;

/**
 * Camporator, handling nulls, and using id if percentage complete is equal.
 */
public class ApplicationSummaryResourcePercentageCompleteComparator extends DualFieldComparator<Integer, Long> implements Comparator<ApplicationSummaryResource> {

	@Override
	public int compare(ApplicationSummaryResource o1, ApplicationSummaryResource o2) {
		
		Integer o1Lead = o1.getCompletedPercentage();
		Integer o2Lead = o2.getCompletedPercentage();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o2Lead, o1Lead, o1Id, o2Id);
	}
}