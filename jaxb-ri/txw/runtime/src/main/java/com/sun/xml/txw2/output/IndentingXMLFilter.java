/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2.output;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.Stack;

/**
 * {@link XMLFilterImpl} that does indentation to SAX events.
 *
 * @author Kohsuke Kawaguchi
 */
public class IndentingXMLFilter extends XMLFilterImpl implements LexicalHandler {
    private LexicalHandler lexical;

    public IndentingXMLFilter() {
    }

    public IndentingXMLFilter(ContentHandler handler) {
        setContentHandler(handler);
    }

    public IndentingXMLFilter(ContentHandler handler, LexicalHandler lexical) {
        setContentHandler(handler);
        setLexicalHandler(lexical);
    }

    public LexicalHandler getLexicalHandler() {
        return lexical;
    }

    public void setLexicalHandler(LexicalHandler lexical) {
        this.lexical = lexical;
    }


    /**
     * Return the current indent step.
     *
     * <p>Return the current indent step: each start tag will be
     * indented by this number of spaces times the number of
     * ancestors that the element has.</p>
     *
     * @return The number of spaces in each indentation step,
     *         or 0 or less for no indentation.
     * @see #setIndentStep(int)
     *
     * @deprecated
     *      Only return the length of the indent string.
     */
    @Deprecated
    public int getIndentStep ()
    {
        return indentStep.length();
    }


    /**
     * Set the current indent step.
     *
     * @param indentStep The new indent step (0 or less for no
     *        indentation).
     * @see #getIndentStep()
     *
     * @deprecated
     *      Should use the version that takes string.
     */
    @Deprecated
    public void setIndentStep (int indentStep)
    {
        StringBuilder s = new StringBuilder();
        for( ; indentStep>0; indentStep-- )   s.append(' ');
        setIndentStep(s.toString());
    }

    public void setIndentStep(String s) {
        this.indentStep = s;
    }



    ////////////////////////////////////////////////////////////////////
    // Override methods from XMLWriter.
    ////////////////////////////////////////////////////////////////////

    /**
     * Write a start tag.
     *
     * <p>Each tag will begin on a new line, and will be
     * indented by the current indent step times the number
     * of ancestors that the element has.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @param atts The element's attribute list.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the start tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#startElement(String, String, String,Attributes)
     */
    @Override
    public void startElement (String uri, String localName,
                              String qName, Attributes atts)
        throws SAXException {
        stateStack.push(SEEN_ELEMENT);
        state = SEEN_NOTHING;
        if (depth > 0) {
            writeNewLine();
        }
        doIndent();
        super.startElement(uri, localName, qName, atts);
        depth++;
    }

    private void writeNewLine() throws SAXException {
        super.characters(NEWLINE,0,NEWLINE.length);
    }

    private static final char[] NEWLINE = {'\n'};


    /**
     * Write an end tag.
     *
     * <p>If the element has contained other elements, the tag
     * will appear indented on a new line; otherwise, it will
     * appear immediately following whatever came before.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the end tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#endElement(String, String, String)
     */
    @Override
    public void endElement (String uri, String localName, String qName)
        throws SAXException
    {
        depth--;
        if (state == SEEN_ELEMENT) {
            writeNewLine();
            doIndent();
        }
        super.endElement(uri, localName, qName);
        state = stateStack.pop();
    }


//    /**
//     * Write a empty element tag.
//     *
//     * <p>Each tag will appear on a new line, and will be
//     * indented by the current indent step times the number
//     * of ancestors that the element has.</p>
//     *
//     * <p>The newline and indentation will be passed on down
//     * the filter chain through regular characters events.</p>
//     *
//     * @param uri The element's Namespace URI.
//     * @param localName The element's local name.
//     * @param qName The element's qualified (prefixed) name.
//     * @param atts The element's attribute list.
//     * @exception org.xml.sax.SAXException If there is an error
//     *            writing the empty tag, or if a filter further
//     *            down the chain raises an exception.
//     * @see XMLWriter#emptyElement(String, String, String, Attributes)
//     */
//    public void emptyElement (String uri, String localName,
//                              String qName, Attributes atts)
//        throws SAXException
//    {
//        state = SEEN_ELEMENT;
//        if (depth > 0) {
//            super.characters("\n");
//        }
//        doIndent();
//        super.emptyElement(uri, localName, qName, atts);
//    }


    /**
     * Write a sequence of characters.
     *
     * @param ch The characters to write.
     * @param start The starting position in the array.
     * @param length The number of characters to use.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the characters, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#characters(char[], int, int)
     */
    @Override
    public void characters (char ch[], int start, int length)
        throws SAXException
    {
        state = SEEN_DATA;
        super.characters(ch, start, length);
    }

    @Override
    public void comment(char ch[], int start, int length) throws SAXException {
        if (depth > 0) {
            writeNewLine();
        }
        doIndent();
        if(lexical!=null)
            lexical.comment(ch,start,length);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        if(lexical!=null)
            lexical.startDTD(name, publicId, systemId);
    }

    @Override
    public void endDTD() throws SAXException {
        if(lexical!=null)
            lexical.endDTD();
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if(lexical!=null)
            lexical.startEntity(name);
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if(lexical!=null)
            lexical.endEntity(name);
    }

    @Override
    public void startCDATA() throws SAXException {
        if(lexical!=null)
            lexical.startCDATA();
    }

    @Override
    public void endCDATA() throws SAXException {
        if(lexical!=null)
            lexical.endCDATA();
    }

    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Print indentation for the current level.
     *
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the indentation characters, or if a filter
     *            further down the chain raises an exception.
     */
    private void doIndent ()
        throws SAXException
    {
        if (depth > 0) {
            char[] ch = indentStep.toCharArray();
            for( int i=0; i<depth; i++ )
                characters(ch, 0, ch.length);
        }
    }


    ////////////////////////////////////////////////////////////////////
    // Constants.
    ////////////////////////////////////////////////////////////////////

    private final static Object SEEN_NOTHING = new Object();
    private final static Object SEEN_ELEMENT = new Object();
    private final static Object SEEN_DATA = new Object();


    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    private Object state = SEEN_NOTHING;
    private Stack<Object> stateStack = new Stack<>();

    private String indentStep = "";
    private int depth = 0;
}
