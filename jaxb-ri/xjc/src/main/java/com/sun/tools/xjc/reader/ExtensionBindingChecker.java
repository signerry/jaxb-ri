/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader;

import java.util.StringTokenizer;

import com.sun.tools.xjc.Options;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * This filter checks jaxb:extensionBindingPrefix and
 * pass/filter extension bindings.
 * 
 * <p>
 * This filter also remembers enabled extension namespaces
 * and filters out any extension namespaces that doesn't belong
 * to those. The net effect is that disabled customizations
 * will never pass through this filter.
 *
 * <p>
 * Note that we can't just filter out all foreign namespaces,
 * as we need to use user-defined tags in documentations to generate javadoc.
 * 
 * <p>
 * The class needs to know the list of extension binding namespaces
 * that the RI recognizes.
 * To add new URI, modify the isSupportedExtension method.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class ExtensionBindingChecker extends AbstractExtensionBindingChecker {

    /**
     * Number of the elements encountered. Used to detect the root element.
     */
    private int count=0;

    public ExtensionBindingChecker(String schemaLanguage, Options options, ErrorHandler handler) {
        super(schemaLanguage, options, handler);
    }

    /**
     * Returns true if the elements with the given namespace URI
     * should be blocked by this filter.
     */
    private boolean needsToBePruned( String uri ) {
        if( uri.equals(schemaLanguage) )
            return false;
        if( uri.equals(Const.JAXB_NSURI) )
            return false;
        if( enabledExtensions.contains(uri) )
            return false;
        
        // we don't need to prune something unless
        // the rest of the processor recognizes it as something special.
        // this allows us to send the documentation and other harmless
        // foreign XML fragments, which may be picked up as documents.
        return isRecognizableExtension(uri);
    }


    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        count=0;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
        throws SAXException {
        
        if(!isCutting()) {
            String v = atts.getValue(Const.JAXB_NSURI,"extensionBindingPrefixes");
            if(v!=null) {
                if(count!=0)
                    // the binding attribute is allowed only at the root level.
                    error( Messages.ERR_UNEXPECTED_EXTENSION_BINDING_PREFIXES.format() );

                if(!allowExtensions)
                    error( Messages.ERR_VENDOR_EXTENSION_DISALLOWED_IN_STRICT_MODE.format() );

                // then remember the associated namespace URIs.
                StringTokenizer tokens = new StringTokenizer(v);
                while(tokens.hasMoreTokens()) {
                    String prefix = tokens.nextToken();
                    String uri = nsSupport.getURI(prefix);
                    if( uri==null )
                        // undeclared prefix
                        error( Messages.ERR_UNDECLARED_PREFIX.format(prefix) );
                    else
                        checkAndEnable(uri);
                }
            }
            
            if( needsToBePruned(namespaceURI) ) {
                // start pruning the tree. Call the super class method directly.
                if( isRecognizableExtension(namespaceURI) ) {
                    // but this is a supported customization.
                    // isn't the user forgetting @jaxb:extensionBindingPrefixes?
                    warning( Messages.ERR_SUPPORTED_EXTENSION_IGNORED.format(namespaceURI) );
                }
                startCutting();
            } else
                verifyTagName(namespaceURI, localName, qName);
        }
        
        count++;
        super.startElement(namespaceURI, localName, qName, atts);
    }
}
