/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.gen.config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Runtime Engine for RELAXNGCC execution.
 *
 * This class has the following functionalities:
 *
 * <ol>
 *  <li>Managing a stack of NGCCHandler objects and
 *      switching between them appropriately.
 *
 *  <li>Keep track of all Attributes.
 *
 *  <li>manage mapping between namespace URIs and prefixes.
 *
 *  <li>TODO: provide support for interleaving.
 * </ol>
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 * @version $Id: NGCCRuntime.java,v 1.15 2002/09/29 02:55:48 okajima Exp $
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class NGCCRuntime implements ContentHandler, NGCCEventSource {

    public NGCCRuntime() {
        reset();
    }

    /**
     * Sets the root handler, which will be used to parse the
     * root element.
     * <p>
     * This method can be called right after the object is created
     * or the reset method is called. You can't replace the root
     * handler while parsing is in progress.
     * <p>
     * Usually a generated class that corresponds to the {@code <start>}
     * pattern will be used as the root handler, but any NGCCHandler
     * can be a root handler.
     *
     * @param rootHandler new root handler
     * @exception IllegalStateException
     *      If this method is called but it doesn't satisfy the
     *      pre-condition stated above.
     */
    public void setRootHandler( NGCCHandler rootHandler ) {
        if(currentHandler!=null)
            throw new IllegalStateException();
        currentHandler = rootHandler;
    }


    /**
     * Cleans up all the data structure so that the object can be reused later.
     * Normally, applications do not need to call this method directly,
     *
     * as the runtime resets itself after the endDocument method.
     */
    public void reset() {
        attStack.clear();
        currentAtts = null;
        currentHandler = null;
        indent=0;
        locator = null;
        namespaces.clear();
        needIndent = true;
        redirect = null;
        redirectionDepth = 0;
        text = new StringBuffer();

        // add a dummy attributes at the bottom as a "centinel."
        attStack.push(new AttributesImpl());
    }

    // current content handler can be acccessed via set/getContentHandler.

    private Locator locator;
    @Override
    public void setDocumentLocator(Locator _loc ) { this.locator=_loc; }
    /**
     * Gets the source location of the current event.
     *
     * <p>
     * One can call this method from RelaxNGCC handlers to access
     * the line number information. Note that to
     * @return the source location of the current event
     */
    public Locator getLocator() { return locator; }


    /** stack of {@link Attributes}. */
    private final Stack<AttributesImpl> attStack = new Stack<>();
    /** current attributes set. always equal to attStack.peek() */
    private AttributesImpl currentAtts;

    /**
     * Attributes that belong to the current element.
     * <p>
     * It's generally not recommended for applications to use
     * this method. RelaxNGCC internally removes processed attributes,
     * so this doesn't correctly reflect all the attributes an element
     * carries.
     * @return Attributes that belong to the current element
     */
    public Attributes getCurrentAttributes() {
        return currentAtts;
    }

    /** accumulated text. */
    private StringBuffer text = new StringBuffer();




    /** The current NGCCHandler. Always equals to handlerStack.peek() */
    private NGCCEventReceiver currentHandler;

    @Override
    public int replace(NGCCEventReceiver o, NGCCEventReceiver n ) {
        if(o!=currentHandler)
            throw new IllegalStateException();  // bug of RelaxNGCC
        currentHandler = n;

        return 0;   // we only have one thread.
    }

    /**
     * Processes buffered text.
     *
     * This method will be called by the start/endElement event to process
     * buffered text as a text event.
     *
     * <p>
     * Whitespace handling is a tricky business. Consider the following
     * schema fragment:
     *
     * <pre>{@code
     * <element name="foo">
     *   <choice>
     *     <element name="bar"><empty/></element>
     *     <text/>
     *   </choice>
     * </element>
     * }</pre>
     *
     * Assume we hit the following instance:
     * <pre>{@code
     * <foo> <bar/></foo>
     * }</pre>
     *
     * Then this first space needs to be ignored (for otherwise, we will
     * end up treating this space as the match to &lt;text/> and won't
     * be able to process &lt;bar>.)
     *
     * Now assume the following instance:
     * <pre>{@code
     * <foo/>
     * }</pre>
     *
     * This time, we need to treat this empty string as a text, for
     * otherwise we won't be able to accept this instance.
     *
     * <p>
     * This is very difficult to solve in general, but one seemingly
     * easy solution is to use the type of next event. If a text is
     * followed by a start tag, it follows from the constraint on
     * RELAX NG that that text must be either whitespaces or a match
     * to &lt;text/>.
     *
     * <p>
     * On the contrary, if a text is followed by a end tag, then it
     * cannot be whitespace unless the content model can accept empty,
     * in which case sending a text event will be harmlessly ignored
     * by the NGCCHandler.
     *
     * <p>
     * Thus this method take one parameter, which controls the
     * behavior of this method.
     *
     * <p>
     * TODO: according to the constraint of RELAX NG, if characters
     * follow an end tag, then they must be either whitespaces or
     * must match to &lt;text/>.
     *
     * @param   ignorable
     *      True if the buffered character can be ignorable. False if
     *      it needs to be consumed.
     */
    private void processPendingText(boolean ignorable) throws SAXException {
        if(ignorable && text.toString().trim().length()==0)
            ; // ignore. See the above javadoc comment for the description
        else
            currentHandler.text(text.toString());   // otherwise consume this token

        // truncate StringBuffer, but avoid excessive allocation.
        if(text.length()>1024)  text = new StringBuffer();
        else                    text.setLength(0);
    }

    public void processList( String str ) throws SAXException {
        StringTokenizer t = new StringTokenizer(str, " \t\r\n");
        while(t.hasMoreTokens())
            currentHandler.text(t.nextToken());
    }

    @Override
    public void startElement(String uri, String localname, String qname, Attributes atts)
            throws SAXException {

        if(redirect!=null) {
            redirect.startElement(uri,localname,qname,atts);
            redirectionDepth++;
        } else {
            processPendingText(true);
            //        System.out.println("startElement:"+localname+"->"+_attrStack.size());
            currentHandler.enterElement(uri, localname, qname, atts);
        }
    }

    /**
     * Called by the generated handler code when an enter element
     * event is consumed.
     *
     * <p>
     * Pushes a new attribute set.
     *
     * <p>
     * Note that attributes are NOT pushed at the startElement method,
     * because the processing of the enterElement event can trigger
     * other attribute events and etc.
     * <p>
     * This method will be called from one of handlers when it truly
     * consumes the enterElement event.
     * @param uri Parameter passed to the element event.
     * @param localName Parameter passed to the element event.
     * @param qname Parameter passed to the element event.
     * @param atts Parameter passed to the element event.
     * @throws SAXException for errors
     * 
     */
    public void onEnterElementConsumed(
            String uri, String localName, String qname,Attributes atts) throws SAXException {
        attStack.push(currentAtts=new AttributesImpl(atts));
        nsEffectiveStack.push(nsEffectivePtr);
        nsEffectivePtr = namespaces.size();
    }

    /**
     * @param uri Parameter passed to the element event.
     * @param localName Parameter passed to the element event.
     * @param qname Parameter passed to the element event.
     * @throws SAXException for errors
     */
    public void onLeaveElementConsumed(String uri, String localName, String qname) throws SAXException {
        attStack.pop();
        if(attStack.isEmpty())
            currentAtts = null;
        else
            currentAtts = attStack.peek();
        nsEffectivePtr = nsEffectiveStack.pop();
    }

    @Override
    public void endElement(String uri, String localname, String qname)
            throws SAXException {

        if(redirect!=null) {
            redirect.endElement(uri,localname,qname);
            redirectionDepth--;

            if(redirectionDepth!=0)
                return;

            // finished redirection.
            for( int i=0; i<namespaces.size(); i+=2 )
                redirect.endPrefixMapping(namespaces.get(i));
            redirect.endDocument();

            redirect = null;
            // then process this element normally
        }

        processPendingText(false);

        currentHandler.leaveElement(uri, localname, qname);
//        System.out.println("endElement:"+localname);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(redirect!=null)
            redirect.characters(ch,start,length);
        else
            text.append(ch,start,length);
    }
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if(redirect!=null)
            redirect.ignorableWhitespace(ch,start,length);
        else
            text.append(ch,start,length);
    }

    public int getAttributeIndex(String uri, String localname) {
        return currentAtts.getIndex(uri, localname);
    }
    public void consumeAttribute(int index) throws SAXException {
        final String uri    = currentAtts.getURI(index);
        final String local  = currentAtts.getLocalName(index);
        final String qname  = currentAtts.getQName(index);
        final String value  = currentAtts.getValue(index);
        currentAtts.removeAttribute(index);

        currentHandler.enterAttribute(uri,local,qname);
        currentHandler.text(value);
        currentHandler.leaveAttribute(uri,local,qname);
    }


    @Override
    public void startPrefixMapping(String prefix, String uri ) throws SAXException {
        if(redirect!=null)
            redirect.startPrefixMapping(prefix,uri);
        else {
            namespaces.add(prefix);
            namespaces.add(uri);
        }
    }

    @Override
    public void endPrefixMapping(String prefix ) throws SAXException {
        if(redirect!=null)
            redirect.endPrefixMapping(prefix);
        else {
            namespaces.remove(namespaces.size()-1);
            namespaces.remove(namespaces.size()-1);
        }
    }

    @Override
    public void skippedEntity(String name ) throws SAXException {
        if(redirect!=null)
            redirect.skippedEntity(name);
    }

    @Override
    public void processingInstruction(String target, String data ) throws SAXException {
        if(redirect!=null)
            redirect.processingInstruction(target,data);
    }

    /** Impossible token. This value can never be a valid XML name. */
    static final String IMPOSSIBLE = "\u0000";

    @Override
    public void endDocument() throws SAXException {
        // consume the special "end document" token so that all the handlers
        // currently at the stack will revert to their respective parents.
        //
        // this is necessary to handle a grammar like
        // <start><ref name="X"/></start>
        // <define name="X">
        //   <element name="root"><empty/></element>
        // </define>
        //
        // With this grammar, when the endElement event is consumed, two handlers
        // are on the stack (because a child object won't revert to its parent
        // unless it sees a next event.)

        // pass around an "impossible" token.
        currentHandler.leaveElement(IMPOSSIBLE,IMPOSSIBLE,IMPOSSIBLE);

        reset();
    }
    @Override
    public void startDocument() {}




//
//
// event dispatching methods
//
//

    @Override
    public void sendEnterAttribute(int threadId,
                                   String uri, String local, String qname) throws SAXException {

        currentHandler.enterAttribute(uri,local,qname);
    }

    @Override
    public void sendEnterElement(int threadId,
                                 String uri, String local, String qname, Attributes atts) throws SAXException {

        currentHandler.enterElement(uri,local,qname,atts);
    }

    @Override
    public void sendLeaveAttribute(int threadId,
                                   String uri, String local, String qname) throws SAXException {

        currentHandler.leaveAttribute(uri,local,qname);
    }

    @Override
    public void sendLeaveElement(int threadId,
                                 String uri, String local, String qname) throws SAXException {

        currentHandler.leaveElement(uri,local,qname);
    }

    @Override
    public void sendText(int threadId, String value) throws SAXException {
        currentHandler.text(value);
    }


//
//
// redirection of SAX2 events.
//
//
    /** When redirecting a sub-tree, this value will be non-null. */
    private ContentHandler redirect = null;

    /**
     * Counts the depth of the elements when we are re-directing
     * a sub-tree to another ContentHandler.
     */
    private int redirectionDepth = 0;

    /**
     * This method can be called only from the enterElement handler.
     * The sub-tree rooted at the new element will be redirected
     * to the specified ContentHandler.
     *
     * <p>
     * Currently active NGCCHandler will only receive the leaveElement
     * event of the newly started element.
     *
     * @param child the new ContentHandler
     * @param uri
     *      Parameter passed to the enter element event. Used to
     *      simulate the startElement event for the new ContentHandler.
     * @param local
     *      Parameter passed to the enter element event. Used to
     *      simulate the startElement event for the new ContentHandler.
     * @param qname
     *      Parameter passed to the enter element event. Used to
     *      simulate the startElement event for the new ContentHandler.
     * @throws SAXException for errors
     */
    public void redirectSubtree( ContentHandler child,
                                 String uri, String local, String qname ) throws SAXException {

        redirect = child;
        redirect.setDocumentLocator(locator);
        redirect.startDocument();

        // TODO: when a prefix is re-bound to something else,
        // the following code is potentially dangerous. It should be
        // modified to report active bindings only.
        for( int i=0; i<namespaces.size(); i+=2 )
            redirect.startPrefixMapping(
                    namespaces.get(i),
                    namespaces.get(i+1)
            );

        redirect.startElement(uri,local,qname,currentAtts);
        redirectionDepth=1;
    }

//
//
// validation context implementation
//
//
    /** in-scope namespace mapping.
     * namespaces[2n  ] := prefix
     * namespaces[2n+1] := namespace URI */
    private final List<String> namespaces = new ArrayList<>();
    /**
     * Index on the namespaces array, which points to
     * the top of the effective bindings. Because of the
     * timing difference between the startPrefixMapping method
     * and the execution of the corresponding actions,
     * this value can be different from {@code namespaces.size()}.
     * <p>
     * For example, consider the following schema:
     * <pre>{@code
     *  <oneOrMore>
     *   <element name="foo"><empty/></element>
     *  </oneOrMore>
     *  code fragment X
     *  <element name="bob"/>
     * }</pre>
     * Code fragment X is executed after we see a startElement event,
     * but at this time the namespaces variable already include new
     * namespace bindings declared on "bob".
     */
    private int nsEffectivePtr=0;

    /**
     * Stack to preserve old nsEffectivePtr values.
     */
    private final Stack<Integer> nsEffectiveStack = new Stack<>();

    public String resolveNamespacePrefix( String prefix ) {
        for( int i = nsEffectivePtr-2; i>=0; i-=2 )
            if( namespaces.get(i).equals(prefix) )
                return namespaces.get(i+1);

        // no binding was found.
        if(prefix.equals(""))   return "";  // return the default no-namespace
        if(prefix.equals("xml"))    // pre-defined xml prefix
            return "http://www.w3.org/XML/1998/namespace";
        else    return null;    // prefix undefined
    }


    // error reporting
    protected void unexpectedX(String token) throws SAXException {
        throw new SAXParseException(MessageFormat.format(
                "Unexpected {0} appears at line {1} column {2}",
                new Object[]{
                        token,
                    getLocator().getLineNumber(),
                    getLocator().getColumnNumber() }),
                getLocator());
    }




    //
//
// trace functions
//
//
    private int indent=0;
    private boolean needIndent=true;
    private void printIndent() {
        for( int i=0; i<indent; i++ )
            System.out.print("  ");
    }
    public void trace( String s ) {
        if(needIndent) {
            needIndent=false;
            printIndent();
        }
        System.out.print(s);
    }
    public void traceln( String s ) {
        trace(s);
        trace("\n");
        needIndent=true;
    }
}
