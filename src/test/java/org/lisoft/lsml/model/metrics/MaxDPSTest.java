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
package org.lisoft.lsml.model.metrics;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.lisoft.lsml.model.helpers.MockLoadoutContainer;
import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Test suite for {@link MaxDPS} {@link Metric}.
 * 
 * @author Emily Björk
 */
public class MaxDPSTest {
    private MockLoadoutContainer mlc   = new MockLoadoutContainer();
    private MaxDPS               cut;
    private List<Weapon>         items = new ArrayList<>();
    private Collection<Modifier> modifiers;

    @Before
    public void setup() {
        modifiers = Mockito.mock(Collection.class);
        when(mlc.loadout.items(Weapon.class)).thenReturn(items);
        when(mlc.loadout.getModifiers()).thenReturn(modifiers);
        cut = new MaxDPS(mlc.loadout);
    }

    /**
     * Non-Offensive weapons should not contribute to DPS.
     */
    @Test
    public void testCalculate_NonOffensive() {
        Weapon weapon = Mockito.mock(Weapon.class);
        Mockito.when(weapon.isOffensive()).thenReturn(false);
        Mockito.when(weapon.getRangeEffectivity(Matchers.anyDouble(), Matchers.anyCollection())).thenReturn(1.0);
        Mockito.when(weapon.getStat(Matchers.anyString(), Matchers.anyCollection())).thenReturn(100.0);

        items.add(weapon);
        assertEquals(0.0, cut.calculate(0), 0.0);
    }

    /**
     * No weapons should return 0.
     */
    @Test
    public void testCalculate_NoItems() {
        assertEquals(0.0, cut.calculate(0), 0.0);
    }

    /**
     * {@link MaxDPS#calculate(double)} shall calculate the maximal DPS at a given range.
     */
    @Test
    public void testCalculate() {
        final double range = 300;

        Weapon weapon1 = Mockito.mock(Weapon.class);
        Mockito.when(weapon1.isOffensive()).thenReturn(true);
        Mockito.when(weapon1.getRangeEffectivity(range, modifiers)).thenReturn(0.8);
        Mockito.when(weapon1.getStat("d/s", modifiers)).thenReturn(1.0);

        Weapon weapon2 = Mockito.mock(Weapon.class);
        Mockito.when(weapon2.isOffensive()).thenReturn(true);
        Mockito.when(weapon2.getRangeEffectivity(range, modifiers)).thenReturn(1.0);
        Mockito.when(weapon2.getStat("d/s", modifiers)).thenReturn(3.0);

        Weapon weapon3 = Mockito.mock(Weapon.class);
        Mockito.when(weapon3.isOffensive()).thenReturn(true);
        Mockito.when(weapon3.getRangeEffectivity(range, modifiers)).thenReturn(0.9);
        Mockito.when(weapon3.getStat("d/s", modifiers)).thenReturn(5.0);

        items.add(weapon1);
        items.add(weapon2);
        items.add(weapon3);

        final double dps1 = 0.8 * 1.0;
        final double dps2 = 1.0 * 3.0;
        final double dps3 = 0.9 * 5.0;

        assertEquals(dps1 + dps2 + dps3, cut.calculate(range), 0.0);
    }

    /**
     * {@link MaxDPS#calculate(double)} shall calculate the maximal DPS at a given range.
     */
    @Test
    public void testCalculate_WeaponGroups() {
        final double range = 300;

        Weapon weapon1 = Mockito.mock(Weapon.class);
        Mockito.when(weapon1.isOffensive()).thenReturn(true);
        Mockito.when(weapon1.getRangeEffectivity(range, modifiers)).thenReturn(0.8);
        Mockito.when(weapon1.getStat("d/s", modifiers)).thenReturn(1.0);

        Weapon weapon2 = Mockito.mock(Weapon.class);
        Mockito.when(weapon2.isOffensive()).thenReturn(true);
        Mockito.when(weapon2.getRangeEffectivity(range, modifiers)).thenReturn(1.0);
        Mockito.when(weapon2.getStat("d/s", modifiers)).thenReturn(3.0);

        Weapon weapon3 = Mockito.mock(Weapon.class);
        Mockito.when(weapon3.isOffensive()).thenReturn(true);
        Mockito.when(weapon3.getRangeEffectivity(range, modifiers)).thenReturn(0.9);
        Mockito.when(weapon3.getStat("d/s", modifiers)).thenReturn(5.0);

        items.add(weapon1);
        items.add(weapon2);
        items.add(weapon3);

        final double dps2 = 1.0 * 3.0;
        final double dps3 = 0.9 * 5.0;

        final int group = 0;
        Collection<Weapon> groupWeapons = new ArrayList<>();
        groupWeapons.add(weapon2);
        groupWeapons.add(weapon3);
        Mockito.when(mlc.weaponGroups.getWeapons(group, mlc.loadout)).thenReturn(groupWeapons);

        cut = new MaxDPS(mlc.loadout, group);

        assertEquals(dps2 + dps3, cut.calculate(range), 0.0);
    }

}
