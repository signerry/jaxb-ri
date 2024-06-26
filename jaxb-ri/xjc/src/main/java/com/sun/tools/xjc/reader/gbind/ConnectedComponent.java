/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.gbind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents one strongly-connected component
 * of the {@link Element} graph.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ConnectedComponent implements Iterable<Element> {
    /**
     * {@link Element}s that belong to this component.
     */
    private final List<Element> elements = new ArrayList<>();

    /*package*/ boolean isRequired;

    /**
     * Returns true iff this {@link ConnectedComponent}
     * can match a substring whose length is greater than 1.
     *
     * <p>
     * That means this property will become a collection property.
     */
    public final boolean isCollection() {
        assert !elements.isEmpty();

        // a strongly connected component by definition has a cycle,
        // so if its size is bigger than 1 there must be a cycle.
        if(elements.size()>1)
            return true;

        // if size is 1, it might be still forming a self-cycle
        Element n = elements.get(0);
        return n.hasSelfLoop();
    }

    /**
     * Returns true iff this {@link ConnectedComponent}
     * forms a cut set of a graph.
     *
     * <p>
     * That means any valid element sequence must have at least
     * one value for this property.
     */
    public final boolean isRequired() {
        return isRequired;
    }

    /*package*/void add(Element e) {
        assert !elements.contains(e);
        elements.add(e);
    }

    @Override
    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    /**
     * Just produces debug representation
     */
    @Override
    public String toString() {
        String s = elements.toString();
        if(isRequired())
            s += '!';
        if(isCollection())
            s += '*';
        return s;
    }
}
