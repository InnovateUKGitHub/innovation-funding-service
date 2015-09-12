package com.worth.ifs.application.helper;

import com.worth.ifs.application.domain.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SectionHelper {

    public List<Section> getParentSections(List<Section> sections) {
        List<Section> childSections = new ArrayList<Section>();
        getChildSections(sections, childSections);
        sections = sections.stream()
                .filter(s -> !childSections.stream()
                        .anyMatch(c -> c.getId().equals(s.getId())))
                .sorted()
                .collect(Collectors.toList());
        sections.stream()
                .filter(s -> s.getChildSections()!=null)
                .forEach(s -> Collections.sort(s.getChildSections()));
        return sections;
    }

    private List<Section> getChildSections(List<Section> sections, List<Section>children) {
        for(Section section : sections) {
            if(section.getChildSections()!=null) {
                children.addAll(section.getChildSections());
                getChildSections(section.getChildSections(), children);
            }
        }
        return children;
    }

}
