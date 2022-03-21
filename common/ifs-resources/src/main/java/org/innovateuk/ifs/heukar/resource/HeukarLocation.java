package org.innovateuk.ifs.heukar.resource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public enum HeukarLocation {

    UK("UK"),
    OVERSEAS("British Overseas Territory"),
    CROWN("Crown Dependency"),

    ENGLAND("England", UK.getDisplay()),
    NORTH_EAST("North East", ENGLAND, UK.getDisplay()),
    NORTH_WEST("North West", ENGLAND, UK.getDisplay()),
    YORKSHIRE_AND_HUMBER("Yorkshire and the Humber", ENGLAND, UK.getDisplay()),
    EAST_MIDLANDS("East Midlands", ENGLAND, UK.getDisplay()),
    WEST_MIDLANDS("West Midlands", ENGLAND, UK.getDisplay()),
    EAST_OF_ENGLAND("East of England", ENGLAND, UK.getDisplay()),
    GREATER_LONDON("Greater London", ENGLAND, UK.getDisplay()),
    SOUTH_EAST("South East", ENGLAND, UK.getDisplay()),
    SOUTH_WEST("South West", ENGLAND, UK.getDisplay()),

    WALES("Wales", UK.getDisplay()),
    SCOTLAND("Scotland", UK.getDisplay()),
    NORTHERN_IRELAND("Northern Ireland", UK.getDisplay()),

    BRITISH_OVERSEAS_TERRITORY("British Overseas Territory", OVERSEAS.getDisplay()),
    ANGUILA("Anguila", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    BERMUDA("Bermuda", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    BRITISH_ANTARCTIC_TERRITORY("British Antarctic Territory", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    BRITISH_INDIAN_OCEAN_TERRITORY("British Indian Ocean Territory", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    BRITISH_VIRGIN_ISLANDS("British Virgin Islands", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    CAYMAN_ISLANDS("Cayman Islands", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    SOVEREIGN_BASE_AREAS_OF_AKROTIRI_AND_DHEKELIA("Sovereign Base Areas of Akrotiri and Dhekelia in Cyprus", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    FALKLAND_ISLANDS("Falkland Islands", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    GIBRALTAR("Gibraltar", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    MONTSERRAT("Montserrat", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    PITCAIRN_ISLANDS("Pitcairn Islands", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    SAINT_HELENA("Saint Helena, Ascension and Tristan da Cunha", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    SOUTH_GEORGIA("South Georgia and the South Sandwich Islands", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),
    TURKS_AND_CAICOS_ISLANDS("Turks and Caicos Islands", BRITISH_OVERSEAS_TERRITORY, OVERSEAS.getDisplay()),

    CROWN_DEPENDENCY("Crown Dependency", CROWN.getDisplay()),
    JERSEY("Jersey", CROWN_DEPENDENCY, CROWN.getDisplay()),
    GUERNSEY("Guernsey", CROWN_DEPENDENCY, CROWN.getDisplay()),
    ISLE_OF_MAN("Isle of Man", CROWN_DEPENDENCY, CROWN.getDisplay());

    HeukarLocation(String display) {
        this.display = display;
    }

    HeukarLocation(String display, HeukarLocation parent, String region) {
        this.display = display;
        this.parent = parent;
        this.region = region;
    }

    HeukarLocation(String display, String region) {
        this(display, null, region);
    }

    private String display;
    private HeukarLocation parent;
    private String region;

    public static final Set<HeukarLocation> parentLocations =
            EnumSet.of(ENGLAND, WALES, SCOTLAND, NORTHERN_IRELAND, BRITISH_OVERSEAS_TERRITORY, CROWN_DEPENDENCY);

    public static final Set<HeukarLocation> ukLocations = Stream.of(EnumSet.of(ENGLAND, WALES, SCOTLAND, NORTHERN_IRELAND), findChildrenOf(ENGLAND))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    public static List<HeukarLocation> findChildrenOf(HeukarLocation parent) {
        return Arrays.stream(HeukarLocation.values())
                .filter(location -> location.parent == parent)
                .collect(toList());
    }

    public static String getTitleDisplayFor(HeukarLocation parent) {
        switch (parent) {
            case ENGLAND:
                return "English regions";
            case BRITISH_OVERSEAS_TERRITORY:
                return "Overseas";
            case CROWN_DEPENDENCY:
                return "Crown dependency";
            default:
                return "Project location";
        }
    }

    public String getDisplay() {
        return display;
    }

    public HeukarLocation getParent() {
        return parent;
    }

    public String getRegion() {
        return region;
    }
}
