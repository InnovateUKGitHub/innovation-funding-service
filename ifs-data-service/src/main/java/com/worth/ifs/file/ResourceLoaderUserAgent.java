package com.worth.ifs.file;

import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import java.io.InputStream;

/**
 *
 **/
@Component
public class ResourceLoaderUserAgent extends ITextUserAgent {


    public ResourceLoaderUserAgent(ITextOutputDevice outputDevice) {
        super(outputDevice);
    }

    protected InputStream resolveAndOpenStream(String uri) {
        InputStream is = super.resolveAndOpenStream(uri);
        System.out.println("IN resolveAndOpenStream() " + uri);
        return is;
    }

}
