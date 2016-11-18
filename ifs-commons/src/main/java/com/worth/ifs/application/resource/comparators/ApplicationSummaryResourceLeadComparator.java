package com.worth.ifs.application.resource.comparators;

import com.worth.ifs.application.resource.ApplicationSummaryResource;

import java.util.Comparator;

/**
 * Camporator, handling nulls, and using id if lead is equal.
 */
public class ApplicationSummaryResourceLeadComparator extends DualFieldComparator<String, Long> implements Comparator<ApplicationSummaryResource> {

	@Override
	public int compare(ApplicationSummaryResource resource1, ApplicationSummaryResource resource2) {
		
		String o1Lead = resource1.getLead();
		String o2Lead = resource2.getLead();
		
		Long o1Id = resource1.getId();
		Long o2Id = resource2.getId();
		
		return compare(o1Lead, o2Lead, o1Id, o2Id);
	}

}
