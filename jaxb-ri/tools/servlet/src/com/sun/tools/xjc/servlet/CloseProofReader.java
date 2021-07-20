/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet;

import java.io.FilterReader;
import java.io.Reader;

/**
 * {@link Reader} implementation that ignores the close method.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class CloseProofReader extends FilterReader {

    public CloseProofReader(Reader in) {
        super(in);
    }

    public void close() {}
}
