/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.parser;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
class Messages
{
    /** Loads a string resource and formats it with specified arguments. */
    static String format( String property, Object... args ) {
        String text = ResourceBundle.getBundle(Messages.class.getPackage().getName() +".MessageBundle").getString(property);
        return MessageFormat.format(text,args);
    }
    
    
    
    static final String ERR_UNACKNOWLEDGED_CUSTOMIZATION =
        "CustomizationContextChecker.UnacknolwedgedCustomization"; // arg:1

    static final String WARN_INCORRECT_URI = // 1 args
        "IncorrectNamespaceURIChecker.WarnIncorrectURI";

    static final String WARN_UNABLE_TO_CHECK_CORRECTNESS = // 0 args
        "SchemaConstraintChecker.UnableToCheckCorrectness";
}
