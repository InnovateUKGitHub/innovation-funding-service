package com.worth.ifs.application.resource.comparators;

import com.worth.ifs.application.resource.ApplicationSummaryResource;

import java.util.Comparator;

/**
 * Camporator, handling nulls, and using id if percentage complete is equal.
 */
public class ApplicationSummaryResourcePercentageCompleteComparator extends DualFieldComparator<Integer, Long> implements Comparator<ApplicationSummaryResource> {

	@Override
	public int compare(ApplicationSummaryResource resource1, ApplicationSummaryResource resource2) {
		
		Integer o1Lead = resource1.getCompletedPercentage();
		Integer o2Lead = resource2.getCompletedPercentage();
		
		Long o1Id = resource1.getId();
		Long o2Id = resource2.getId();
		
		return compare(o2Lead, o1Lead, o1Id, o2Id);
	}
}