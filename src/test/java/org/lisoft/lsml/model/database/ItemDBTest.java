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
package org.lisoft.lsml.model.database;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.lisoft.lsml.model.item.EnergyWeapon;
import org.lisoft.lsml.model.item.TargetingComputer;
import org.lisoft.lsml.model.modifiers.Modifier;

/**
 * This test suite doesn't as much test the behaviour of ItemDB but rather performs checks on the data stored in the
 * ItemDB.
 *
 * @author Emily Björk
 */
public class ItemDBTest {

    @Test
    public void testBug505() throws Exception {
        final double expectedMaxRangeMod = 1.04;

        final TargetingComputer tc1 = (TargetingComputer) ItemDB.lookup("TARGETING COMP. MK I");
        final EnergyWeapon erllas = (EnergyWeapon) ItemDB.lookup("ER LARGE LASER");

        final Collection<Modifier> modifiers = tc1.getModifiers();
        final double maxRangeMod = erllas.getRangeMax(modifiers) / erllas.getRangeMax(null);

        assertEquals(expectedMaxRangeMod, maxRangeMod, 0.00001);
    }
}
