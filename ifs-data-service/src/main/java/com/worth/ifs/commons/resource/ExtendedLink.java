package com.worth.ifs.commons.resource;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;

import javax.xml.bind.annotation.XmlAttribute;

public class ExtendedLink extends Link {

    @XmlAttribute
    private String title = "";

    @XmlAttribute
    private String name = "";

    @XmlAttribute
    private String docs = "";



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

    public ExtendedLink(String href, String rel, String title, String name, String docs) {
        this(href, rel);
        this.title = title;
        this.name = name;
        this.docs = docs;
    }

    public ExtendedLink withTitle(String title){
        return new ExtendedLink(this.getHref(),this.getRel(), title, name, docs);
    }

    public ExtendedLink withName(String name){
        return new ExtendedLink(this.getHref(),this.getRel(), title, name, docs);
    }

    public ExtendedLink withDocs(String docs){
        return new ExtendedLink(this.getHref(),this.getRel(), title, name, docs);
    }

    public String getTitle(){
        return title;
    }

    public String getName(){
        return name;
    }

    public String getDocs(){
        return docs;
    }
}
