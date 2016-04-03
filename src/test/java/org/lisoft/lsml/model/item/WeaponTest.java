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
package org.lisoft.lsml.model.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.lisoft.lsml.model.datacache.ItemDB;
import org.lisoft.lsml.model.datacache.ModifiersDB;
import org.lisoft.lsml.model.datacache.PilotModuleDB;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.lisoft.lsml.model.modifiers.ModifierDescription;

public class WeaponTest {

    @Test
    public void testInequality() {
        MissileWeapon lrm10 = (MissileWeapon) ItemDB.lookup("LRM 10");
        MissileWeapon lrm15 = (MissileWeapon) ItemDB.lookup("LRM 15");
        assertFalse(lrm10.equals(lrm15));
    }

    @Test
    public void testGetDamagePerShot_lpl() throws Exception {
        Weapon weapon = (Weapon) ItemDB.lookup("LRG PULSE LASER");
        assertTrue(weapon.getDamagePerShot() > 8);
    }

    @Test
    public void testGetDamagePerShot_ml() throws Exception {
        Weapon weapon = (Weapon) ItemDB.lookup("MEDIUM LASER");
        assertTrue(weapon.getDamagePerShot() > 4);
    }

    /**
     * Make sure {@link Weapon#getDamagePerShot()} returns the volley damage and not projectile damage.
     */
    @Test
    public void testGetDamagePerShot_lb10x() {
        Weapon lb10xac = (Weapon) ItemDB.lookup("LB 10-X AC");
        assertTrue(lb10xac.getDamagePerShot() > 5);
    }

    @Test
    public void testGetShotsPerVolley_lb10x() {
        Weapon lb10xac = (Weapon) ItemDB.lookup("LB 10-X AC");
        assertEquals(1, lb10xac.getAmmoPerPerShot());
    }

    @Test
    public void testGetDamagePerShot_gauss() {
        Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        assertTrue(gauss.getDamagePerShot() > 10);
    }

    @Test
    public void testGetHeat_gauss() {
        Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(1.0, gauss.getHeat(null), 0.0);
    }

    @Test
    public void testGetSecondsPerShot_gauss() {
        Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(gauss.getCoolDown(null) + 0.75, gauss.getSecondsPerShot(null), 0.0);
    }

    @Test
    public void testGetSecondsPerShot_mg() {
        Weapon mg = (Weapon) ItemDB.lookup("MACHINE GUN");
        assertTrue(mg.getSecondsPerShot(null) > 0.05);
    }

    @Test
    public void testGetRangeMin_ppc() {
        Weapon ppc = (Weapon) ItemDB.lookup("PPC");
        assertEquals(90.0, ppc.getRangeMin(null), 0.0);
    }

    @Test
    public void testGetRangeMax_ppc() {
        Weapon ppc = (Weapon) ItemDB.lookup("PPC");
        assertEquals(1080.0, ppc.getRangeMax(null), 0.0);
    }

    @Test
    public void testGetRangeLong_ppc() {
        Weapon ppc = (Weapon) ItemDB.lookup("PPC");
        assertEquals(540.0, ppc.getRangeLong(null), 0.0);
    }

    @Test
    public void testGetRangeEffectivity_mg() {
        BallisticWeapon mg = (BallisticWeapon) ItemDB.lookup("MACHINE GUN");
        assertEquals(1.0, mg.getRangeEffectivity(0, null), 0.0);
        assertEquals(1.0, mg.getRangeEffectivity(mg.getRangeLong(null), null), 0.1); // High spread on MG
        assertTrue(0.5 >= mg.getRangeEffectivity((mg.getRangeLong(null) + mg.getRangeMax(null)) / 2, null)); // Spread +
                                                                                                             // falloff
        assertEquals(0.0, mg.getRangeEffectivity(mg.getRangeMax(null), null), 0.0);
    }

    @Test
    public void testGetRangeEffectivity_clrm() {
        MissileWeapon lrm = (MissileWeapon) ItemDB.lookup("C-LRM 20");
        assertEquals(0.0, lrm.getRangeEffectivity(0, null), 0.0);
        assertEquals(0.444, lrm.getRangeEffectivity(120, null), 0.001);
        assertEquals(1.0, lrm.getRangeEffectivity(180, null), 0.0);
        assertEquals(1.0, lrm.getRangeEffectivity(1000, null), 0.0);
        assertEquals(0.0, lrm.getRangeEffectivity(1000 + Math.ulp(2000), null), 0.0);
    }

    @Test
    public void testGetRangeEffectivity_gaussrifle() {
        BallisticWeapon gauss = (BallisticWeapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(1.0, gauss.getRangeEffectivity(0, null), 0.0);
        assertEquals(1.0, gauss.getRangeEffectivity(gauss.getRangeLong(null), null), 0.0);
        assertEquals(0.5, gauss.getRangeEffectivity((gauss.getRangeLong(null) + gauss.getRangeMax(null)) / 2, null),
                0.0);
        assertEquals(0.0, gauss.getRangeEffectivity(gauss.getRangeMax(null), null), 0.0);

        assertTrue(gauss.getRangeEffectivity(750, null) < 0.95);
        assertTrue(gauss.getRangeEffectivity(750, null) > 0.8);
    }

    @Test
    public void testGetStat_gauss() {
        BallisticWeapon gauss = (BallisticWeapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(gauss.getDamagePerShot() / gauss.getHeat(null), gauss.getStat("d/h", null), 0.0);
    }

    @Test
    public void testRangeModifiers() {
        Weapon llas = (Weapon) ItemDB.lookup("LARGE LASER");
        WeaponModule rangeModule = (WeaponModule) PilotModuleDB.lookup("LARGE LASER RANGE 5");
        ModifierDescription rangeQuirk1 = ModifiersDB.lookup("islargelaser_range_multiplier");
        ModifierDescription rangeQuirk2 = ModifiersDB.lookup("energy_range_multiplier");
        Modifier range1 = new Modifier(rangeQuirk1, 0.125);
        Modifier range2 = new Modifier(rangeQuirk2, 0.125);

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.addAll(rangeModule.getModifiers());
        modifiers.add(range1);
        modifiers.add(range2);

        double expected_range = (llas.getRangeLong(null) + 0.0) * (1.0 + 0.125 + 0.125 + 0.1);

        assertEquals(expected_range, llas.getRangeLong(modifiers), 0.0);
    }

    @Test
    public void testIsLargeBore() {
        assertTrue(((Weapon) ItemDB.lookup("C-ER PPC")).isLargeBore());
        assertFalse(((Weapon) ItemDB.lookup("LARGE LASER")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("AC/10")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("LB 10-X AC")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("GAUSS RIFLE")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("C-LB5-X AC")).isLargeBore());
        assertFalse(((Weapon) ItemDB.lookup("MACHINE GUN")).isLargeBore());
        assertFalse(((Weapon) ItemDB.lookup("AMS")).isLargeBore());
    }

}