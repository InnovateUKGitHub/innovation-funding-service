package org.innovateuk.ifs.registration.form;

public class InviteAndIdCookie {
    private long id;
    private String hash;

    private InviteAndIdCookie () {}

    public InviteAndIdCookie(long id, String hash) {
        this.id = id;
        this.hash = hash;
    }

    public long getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }
}
