package org.innovateuk.ifs.project.pendingpartner.controller;

class Pair<A,B> { 
    final String key;
    final String value;
    
    public Pair(String a, String b) {
        this.key = a;
        this.value = b;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}