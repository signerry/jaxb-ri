/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/* this file is generated by RelaxNGCC */
package com.sun.xml.xsom.impl.parser.state;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import com.sun.xml.xsom.impl.parser.NGCCRuntimeEx;

    import com.sun.xml.xsom.*;
    import com.sun.xml.xsom.parser.*;
    import com.sun.xml.xsom.impl.*;
    import com.sun.xml.xsom.impl.parser.*;
    import org.xml.sax.Locator;
    import org.xml.sax.ContentHandler;
    import org.xml.sax.helpers.*;
    import java.util.*;
    import java.math.BigInteger;
  


class SimpleType_List extends NGCCHandler {
    private Locator locator;
    private AnnotationImpl annotation;
    private String name;
    private UName itemTypeName;
    private Set finalSet;
    private ForeignAttributesImpl fa;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;

    public final NGCCRuntime getRuntime() {
        return($runtime);
    }

    public SimpleType_List(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
        super(source, parent, cookie);
        $runtime = runtime;
        this.annotation = _annotation;
        this.locator = _locator;
        this.fa = _fa;
        this.name = _name;
        this.finalSet = _finalSet;
        $_ngcc_current_state = 10;
    }

    public SimpleType_List(NGCCRuntimeEx runtime, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
        this(null, runtime, runtime, -1, _annotation, _locator, _fa, _name, _finalSet);
    }

    private void action0()throws SAXException {
        
    	result = new ListSimpleTypeImpl(
    		$runtime.document, annotation, locator, fa,
    		name, name==null, finalSet, itemType );
    
}

    private void action1()throws SAXException {
        
          	itemType = new DelayedRef.SimpleType(
          		$runtime, lloc, $runtime.currentSchema, itemTypeName);
          
}

    private void action2()throws SAXException {
        lloc=$runtime.copyLocator();
}

    public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
        int $ai;
        $uri = $__uri;
        $localName = $__local;
        $qname = $__qname;
        switch($_ngcc_current_state) {
        case 9:
            {
                if((($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) || ((($ai = $runtime.getAttributeIndex("","itemType"))>=0 && (($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) || ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")))) || ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType"))))) {
                    NGCCHandler h = new foreignAttributes(this, super._source, $runtime, 266, fa);
                    spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
                }
                else {
                    unexpectedEnterElement($__qname);
                }
            }
            break;
        case 7:
            {
                if(($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation"))) {
                    NGCCHandler h = new annotation(this, super._source, $runtime, 264, annotation,AnnotationContext.SIMPLETYPE_DECL);
                    spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
                }
                else {
                    $_ngcc_current_state = 2;
                    $runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                }
            }
            break;
        case 10:
            {
                if(($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("list"))) {
                    $runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    action2();
                    $_ngcc_current_state = 9;
                }
                else {
                    unexpectedEnterElement($__qname);
                }
            }
            break;
        case 2:
            {
                if(($ai = $runtime.getAttributeIndex("","itemType"))>=0) {
                    $runtime.consumeAttribute($ai);
                    $runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                }
                else {
                    if(($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType"))) {
                        NGCCHandler h = new simpleType(this, super._source, $runtime, 258);
                        spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
                    }
                    else {
                        unexpectedEnterElement($__qname);
                    }
                }
            }
            break;
        case 0:
            {
                revertToParentFromEnterElement(result, super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
        default:
            {
                unexpectedEnterElement($__qname);
            }
            break;
        }
    }

    public void leaveElement(String $__uri, String $__local, String $__qname) throws SAXException {
        int $ai;
        $uri = $__uri;
        $localName = $__local;
        $qname = $__qname;
        switch($_ngcc_current_state) {
        case 9:
            {
                if((($ai = $runtime.getAttributeIndex("","itemType"))>=0 && ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("list")))) {
                    NGCCHandler h = new foreignAttributes(this, super._source, $runtime, 266, fa);
                    spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
                }
                else {
                    unexpectedLeaveElement($__qname);
                }
            }
            break;
        case 7:
            {
                $_ngcc_current_state = 2;
                $runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
        case 2:
            {
                if(($ai = $runtime.getAttributeIndex("","itemType"))>=0) {
                    $runtime.consumeAttribute($ai);
                    $runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                }
                else {
                    unexpectedLeaveElement($__qname);
                }
            }
            break;
        case 0:
            {
                revertToParentFromLeaveElement(result, super._cookie, $__uri, $__local, $__qname);
            }
            break;
        case 1:
            {
                if(($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("list"))) {
                    $runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    $_ngcc_current_state = 0;
                    action0();
                }
                else {
                    unexpectedLeaveElement($__qname);
                }
            }
            break;
        default:
            {
                unexpectedLeaveElement($__qname);
            }
            break;
        }
    }

    public void enterAttribute(String $__uri, String $__local, String $__qname) throws SAXException {
        int $ai;
        $uri = $__uri;
        $localName = $__local;
        $qname = $__qname;
        switch($_ngcc_current_state) {
        case 9:
            {
                if(($__uri.equals("") && $__local.equals("itemType"))) {
                    NGCCHandler h = new foreignAttributes(this, super._source, $runtime, 266, fa);
                    spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
                }
                else {
                    unexpectedEnterAttribute($__qname);
                }
            }
            break;
        case 7:
            {
                $_ngcc_current_state = 2;
                $runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
        case 2:
            {
                if(($__uri.equals("") && $__local.equals("itemType"))) {
                    $_ngcc_current_state = 5;
                }
                else {
                    unexpectedEnterAttribute($__qname);
                }
            }
            break;
        case 0:
            {
                revertToParentFromEnterAttribute(result, super._cookie, $__uri, $__local, $__qname);
            }
            break;
        default:
            {
                unexpectedEnterAttribute($__qname);
            }
            break;
        }
    }

    public void leaveAttribute(String $__uri, String $__local, String $__qname) throws SAXException {
        int $ai;
        $uri = $__uri;
        $localName = $__local;
        $qname = $__qname;
        switch($_ngcc_current_state) {
        case 7:
            {
                $_ngcc_current_state = 2;
                $runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
        case 0:
            {
                revertToParentFromLeaveAttribute(result, super._cookie, $__uri, $__local, $__qname);
            }
            break;
        case 4:
            {
                if(($__uri.equals("") && $__local.equals("itemType"))) {
                    $_ngcc_current_state = 1;
                }
                else {
                    unexpectedLeaveAttribute($__qname);
                }
            }
            break;
        default:
            {
                unexpectedLeaveAttribute($__qname);
            }
            break;
        }
    }

    public void text(String $value) throws SAXException {
        int $ai;
        switch($_ngcc_current_state) {
        case 9:
            {
                if(($ai = $runtime.getAttributeIndex("","itemType"))>=0) {
                    NGCCHandler h = new foreignAttributes(this, super._source, $runtime, 266, fa);
                    spawnChildFromText(h, $value);
                }
            }
            break;
        case 7:
            {
                $_ngcc_current_state = 2;
                $runtime.sendText(super._cookie, $value);
            }
            break;
        case 2:
            {
                if(($ai = $runtime.getAttributeIndex("","itemType"))>=0) {
                    $runtime.consumeAttribute($ai);
                    $runtime.sendText(super._cookie, $value);
                }
            }
            break;
        case 0:
            {
                revertToParentFromText(result, super._cookie, $value);
            }
            break;
        case 5:
            {
                NGCCHandler h = new qname(this, super._source, $runtime, 260);
                spawnChildFromText(h, $value);
            }
            break;
        }
    }

    public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__)throws SAXException {
        switch($__cookie__) {
        case 266:
            {
                fa = ((ForeignAttributesImpl)$__result__);
                $_ngcc_current_state = 7;
            }
            break;
        case 264:
            {
                annotation = ((AnnotationImpl)$__result__);
                $_ngcc_current_state = 2;
            }
            break;
        case 258:
            {
                itemType = ((SimpleTypeImpl)$__result__);
                $_ngcc_current_state = 1;
            }
            break;
        case 260:
            {
                itemTypeName = ((UName)$__result__);
                action1();
                $_ngcc_current_state = 4;
            }
            break;
        }
    }

    public boolean accepted() {
        return(($_ngcc_current_state == 0));
    }

    
  		/** computed simple type object */
  		private ListSimpleTypeImpl result;
  		
  		// reference to the base type
  		private Ref.SimpleType itemType;
  		
  		// locator of <list>
  		private Locator lloc;
  	
}

