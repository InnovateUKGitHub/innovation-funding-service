package com.worth.ifs.application.resource.comparators;

public abstract class DualFieldComparator<T extends Comparable<T>, I extends Comparable<I>> {

	protected int compare(T o1Primary, T o2Primary, I o1Id, I o2Id) {
		if(o1Primary == null) {
			if(o2Primary == null) {
				return compareId(o1Id, o2Id);
			}
			return -1;
		}
		if(o2Primary == null) {
			return 1;
		}
		
		int leadComparison = o1Primary.compareTo(o2Primary);
		if(leadComparison == 0){
			return compareId(o1Id, o2Id);
		}
		return leadComparison;
	}

	private int compareId(I o1Id, I o2Id) {
		if(o1Id == null) {
			if(o2Id == null) {
				return 0;
			}
			return -1;
		}
		if(o2Id == null) {
			return 1;
		}
		return o1Id.compareTo(o2Id);
	}
}
