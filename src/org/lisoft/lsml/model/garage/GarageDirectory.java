/*
 * @formatter:off
 * Li Song Mechlab - A 'mech building tool for PGI's MechWarrior: Online.
 * Copyright (C) 2013  Emily Björk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//@formatter:on
package org.lisoft.lsml.model.garage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.util.ListArrayUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This class is a folder that can contain sub-folders and mechs.
 * 
 * @author Emily Björk
 * @param <T>
 */
@XStreamAlias("dir")
public class GarageDirectory<T> {
    private final List<GarageDirectory<T>> children = new ArrayList<>();
    private final List<T>                  values   = new ArrayList<>();
    @XStreamAsAttribute
    private String                         name;

    /**
     * Creates a directory with a given name.
     * 
     * @param aName
     *            The name of the folder.
     */
    public GarageDirectory(String aName) {
        name = aName;
    }

    /**
     * Creates a default unnamed directory.
     */
    public GarageDirectory() {
        this("Unnamed Directory");
    }

    /**
     * @return the directories
     */
    public List<GarageDirectory<T>> getDirectories() {
        return children;
    }

    /**
     * @return the name of this {@link GarageDirectory}.
     */
    public String getName() {
        return name;
    }

    /**
     * @param aName
     *            the name to set
     */
    public void setName(String aName) {
        name = aName;
    }

    @Override
    public boolean equals(Object aObj) {
        if (this == aObj)
            return true;
        if (aObj instanceof GarageDirectory) {
            @SuppressWarnings("unchecked")
            GarageDirectory<Object> that = (GarageDirectory<Object>) aObj;
            return ListArrayUtils.equalsUnordered(values, that.values)
                    && ListArrayUtils.equalsUnordered(children, that.children) && name.equals(that.name);
        }
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return A {@link List} of {@link Loadout} in this directory.
     */
    public List<T> getValues() {
        return values;
    }

    /**
     * Checks if this directory contains the given value recursively.
     * 
     * @param aValue
     *            The value to check if it is contained in this subtree.
     * @return <code>true</code> if this directory or any of its children contains the argument.
     */
    public boolean contains(T aValue) {
        if (values.contains(aValue)) {
            return true;
        }

        for (GarageDirectory<T> child : children) {
            if (child.contains(aValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recursively creates the given path of directories under this directory. Directories that already exist with those
     * names are re-used. Leading and tailing spaces are trimmed of path components.
     * 
     * @param aPath
     *            A path of directories to create. Each directory is separated by a forward slash, leading and tailing
     *            slashes are ignored.
     * @return The leaf directory created.
     */
    public GarageDirectory<T> makeDirsRecursive(String aPath) {
        return makeDirsRecursive(Arrays.asList(aPath.split("/")));
    }

    /**
     * Recursively creates the given path of directories under this directory. Directories that already exist with those
     * names are re-used. Leading and tailing spaces are trimmed of path components.
     * 
     * @param aPathComponents
     *            A path of directories to create. Leading and tailing slashes are ignored.
     * @return The leaf directory created.
     */
    public GarageDirectory<T> makeDirsRecursive(List<String> aPathComponents) {
        GarageDirectory<T> current = this;
        for (String pathComponent : aPathComponents) {
            pathComponent = pathComponent.trim();
            if (pathComponent.isEmpty())
                continue;

            boolean found = false;
            for (GarageDirectory<T> child : current.getDirectories()) {
                if (child.getName().equals(pathComponent)) {
                    found = true;
                    current = child;
                    break;
                }
            }
            if (!found) {
                GarageDirectory<T> newChild = new GarageDirectory<>(pathComponent);
                current.getDirectories().add(newChild);
                current = newChild;
            }
        }
        return current;
    }
}