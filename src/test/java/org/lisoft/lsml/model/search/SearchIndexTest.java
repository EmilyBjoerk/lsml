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
package org.lisoft.lsml.model.search;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Test;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.modifiers.*;

/**
 * Unit tests for {@link SearchIndex}.
 *
 * @author Emily Björk
 */
public class SearchIndexTest {
    SearchIndex cut = new SearchIndex();
    List<Modifier> modifiers = new ArrayList<>();

    Loadout makeLoadout() {
        final Loadout l = mock(Loadout.class);
        final Chassis c = mock(Chassis.class);
        when(l.getChassis()).thenReturn(c);
        when(l.getAllModifiers()).thenReturn(modifiers);
        return l;
    }

    @Test
    public void testModifiers() {
        final String keyword = "ENERGY HEAT 5%";
        final ModifierDescription description = mock(ModifierDescription.class);
        final Modifier modifier = mock(Modifier.class);
        modifiers.add(modifier);
        when(modifier.getDescription()).thenReturn(description);
        when(description.getUiName()).thenReturn(keyword);

        final Loadout l = makeLoadout();

        cut.merge(l);
        final Collection<Loadout> ans = cut.query("ENERGY HEAT");

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByFactionShort() {
        final Loadout l = makeLoadout();
        when(l.getChassis().getFaction()).thenReturn(Faction.INNERSPHERE);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query(Faction.INNERSPHERE.getUiShortName());

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByFaction() {
        final Loadout l = makeLoadout();
        when(l.getChassis().getFaction()).thenReturn(Faction.CLAN);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query(Faction.CLAN.getUiName());

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByChassisMassSpace() {
        final Loadout l = makeLoadout();
        when(l.getChassis().getMassMax()).thenReturn(95);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query("95 ton");

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByChassisMass() {
        final Loadout l = makeLoadout();
        when(l.getChassis().getMassMax()).thenReturn(95);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query("95ton");

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByChassisName() {
        final String keyword = "ILYA MUROMETS";
        final Loadout l = makeLoadout();
        when(l.getChassis().getName()).thenReturn(keyword);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query(keyword.toLowerCase());

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByChassisShort() {
        final String keyword = "CPLT-K2";
        final Loadout l = makeLoadout();
        when(l.getChassis().getShortName()).thenReturn(keyword);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query(keyword.toLowerCase());

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByChassisSeries() {
        final String keyword = "SERIES";
        final Loadout l = makeLoadout();
        when(l.getChassis().getSeriesName()).thenReturn(keyword);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query(keyword.toLowerCase());

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testQueryByName() {
        final String keyword = "arbitrary string";
        final Loadout l = makeLoadout();
        when(l.getName()).thenReturn(keyword);

        cut.merge(l);
        final Collection<Loadout> ans = cut.query(keyword);

        assertTrue(ans.contains(l));
        assertEquals(1, ans.size());
    }

    @Test
    public void testUpdate() {
        final Loadout l = makeLoadout();
        when(l.getName()).thenReturn("nope").thenReturn("hello");
        cut.merge(l);
        cut.update();

        assertFalse(cut.query("nope").contains(l));
        assertTrue(cut.query("hello").contains(l));
    }

    @Test
    public void testQueryByNamePrefix() {
        final String keyword = "abc";
        final Loadout l = makeLoadout();
        when(l.getName()).thenReturn(keyword);

        cut.merge(l);

        final Collection<Loadout> ans2 = cut.query("ab");
        assertTrue(ans2.contains(l));
        final Collection<Loadout> ans1 = cut.query("a");
        assertTrue(ans1.contains(l));
        final Collection<Loadout> ans0 = cut.query("");
        assertTrue(ans0.contains(l));
    }

    @Test
    public void testQueryByNameCaseInsensitive() {
        final String keyword = "abc";
        final Loadout l = makeLoadout();
        when(l.getName()).thenReturn(keyword);

        cut.merge(l);

        final Collection<Loadout> ans = cut.query("AB");
        assertTrue(ans.contains(l));
    }

    @Test
    public void testQueryMultipleHits() {
        final Loadout l1 = makeLoadout();
        when(l1.getName()).thenReturn("def abc");
        cut.merge(l1);

        final Loadout l2 = makeLoadout();
        when(l2.getName()).thenReturn("ghi abc");
        cut.merge(l2);

        final Collection<Loadout> ans = cut.query("abc");
        assertTrue(ans.contains(l1));
        assertTrue(ans.contains(l2));
    }

    @Test
    public void testQueryAND() {
        final Loadout l1 = makeLoadout();
        when(l1.getName()).thenReturn("def abc");
        cut.merge(l1);

        final Loadout l2 = makeLoadout();
        when(l2.getName()).thenReturn("ghi abc");
        cut.merge(l2);

        final Collection<Loadout> ans = cut.query("abc ghi");
        assertFalse(ans.contains(l1));
        assertTrue(ans.contains(l2));
    }

    @Test
    public void testRebuildEmpty() {
        cut.rebuild();
        final Collection<Loadout> ans = cut.query("");
        assertTrue(ans.isEmpty());
    }

    @Test
    public void testUnmergeEmptyIndex() {
        final Loadout l2 = makeLoadout();
        when(l2.getName()).thenReturn("abc");
        cut.unmerge(l2);

        final Collection<Loadout> ans = cut.query("abc");
        assertTrue(ans.isEmpty());
    }

    @Test
    public void testUnmerge() {
        final Loadout l1 = makeLoadout();
        when(l1.getName()).thenReturn("def abc");
        cut.merge(l1);

        final Loadout l2 = makeLoadout();
        when(l2.getName()).thenReturn("ghi abc");
        cut.merge(l2);

        cut.unmerge(l2);

        final Collection<Loadout> ans = cut.query("abc ghi");
        assertTrue(ans.isEmpty());
    }

    @Test
    public void testQueryNoModifyIndex() {
        final Loadout l1 = makeLoadout();
        when(l1.getName()).thenReturn("x b");
        cut.merge(l1);

        final Loadout l2 = makeLoadout();
        when(l2.getName()).thenReturn("x y");
        cut.merge(l2);

        final Loadout l3 = makeLoadout();
        when(l3.getName()).thenReturn("a y");
        cut.merge(l3);

        cut.query("x y");
        cut.query("x b");
        cut.query("a y");
        assertEquals(2, cut.query("x").size());
        assertEquals(2, cut.query("y").size());
        assertEquals(1, cut.query("a").size());
        assertEquals(1, cut.query("b").size());
    }
}
