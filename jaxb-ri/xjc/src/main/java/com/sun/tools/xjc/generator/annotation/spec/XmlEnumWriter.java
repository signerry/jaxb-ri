/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.annotation.spec;

import jakarta.xml.bind.annotation.XmlEnum;
import com.sun.codemodel.JAnnotationWriter;
import com.sun.codemodel.JType;

/**
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 */
public interface XmlEnumWriter
    extends JAnnotationWriter<XmlEnum>
{


    XmlEnumWriter value(Class<?> value);

    XmlEnumWriter value(JType value);

}
