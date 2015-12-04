package com.worth.ifs.commons.resource;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;

import javax.xml.bind.annotation.XmlAttribute;

public class ExtendedLink extends Link {

    @XmlAttribute
    private String title;

    @XmlAttribute
    private String name;

    public ExtendedLink(String href) {
        super(href);
    }

    public ExtendedLink(String href, String rel) {
        super(href, rel);
    }

    public ExtendedLink(UriTemplate template, String rel) {
        super(template, rel);
    }

    public ExtendedLink(Link link){
        this(link.getHref(), link.getRel());
    }

    public ExtendedLink(String href, String rel, String title, String name) {
        this(href, rel);
        this.title = title;
        this.name = name;
    }

    @Override
    public ExtendedLink withRel(String rel){
        return new ExtendedLink(this.getHref(),rel, title, name);
    }

    public ExtendedLink withTitle(String title){
        return new ExtendedLink(this.getHref(),this.getRel(), title, name);
    }

    public ExtendedLink withName(String name){
        return new ExtendedLink(this.getHref(),this.getRel(), title, name);
    }

    public String getTitle(){
        return title;
    }

    public String getName(){
        return name;
    }
}
