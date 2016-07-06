package com.worth.ifs.category.resource;


public enum CategoryType {
    INNOVATION_SECTOR("innovation_sector"),
    INNOVATION_AREA("innovation_area"),
	RESEARCH_CATEGORY("research_category");

    private final String name;

    CategoryType(String innovation_area) {
        name = innovation_area;
    }

    public String getName() {
        return name;
    }

    public static CategoryType fromString(String type) {
        if(type!=null) {
            for(CategoryType categoryType : CategoryType.values()) {
                if(type.equalsIgnoreCase(categoryType.name)) {
                    return categoryType;
                }
            }
        }
        throw new IllegalArgumentException("Not a valid CategoryType : " + type);
    }
}
