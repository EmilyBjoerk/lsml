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
package org.lisoft.lsml.command;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.export.Base64LoadoutCoder;
import org.lisoft.lsml.model.helpers.MockLoadoutContainer;
import org.lisoft.lsml.model.item.Ammunition;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.item.MissileWeapon;
import org.lisoft.lsml.model.loadout.EquipResult;
import org.lisoft.lsml.model.loadout.LoadoutBase;
import org.lisoft.lsml.model.loadout.LoadoutStandard;
import org.lisoft.lsml.model.upgrades.GuidanceUpgrade;
import org.lisoft.lsml.model.upgrades.UpgradeDB;
import org.lisoft.lsml.model.upgrades.Upgrades;
import org.lisoft.lsml.util.CommandStack;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test suite for {@link CmdSetGuidanceType}.
 * 
 * @author Emily Björk
 */
@RunWith(MockitoJUnitRunner.class)
public class CmdSetGuidanceTypeTest {
    MockLoadoutContainer mlc = new MockLoadoutContainer();

    @Mock
    GuidanceUpgrade oldGuidance;
    @Mock
    GuidanceUpgrade newGuidance;
    @Mock
    MessageXBar     xBar;

    /**
     * Apply shall change the {@link GuidanceUpgrade} of the {@link Upgrades}s object of the {@link LoadoutStandard}
     * given as argument.
     * @throws Exception 
     */
    @Test
    public void testApply() throws Exception {
        Mockito.when(mlc.upgrades.getGuidance()).thenReturn(oldGuidance);
        CommandStack stack = new CommandStack(0);
        Mockito.when(mlc.loadout.getFreeMass()).thenReturn(100.0);
        Mockito.when(mlc.loadout.getNumCriticalSlotsFree()).thenReturn(100);

        stack.pushAndApply(new CmdSetGuidanceType(xBar, mlc.loadout, newGuidance));

        Mockito.verify(mlc.upgrades).setGuidance(newGuidance);
    }

    /**
     * If apply fails, the changes shall have been rolled back completely.
     */
    @Test
    public void testApply_FailRollback() {
        Mockito.when(mlc.loadout.getFreeMass()).thenReturn(0.0);
        Mockito.when(newGuidance.getExtraTons(mlc.loadout)).thenReturn(1.0);
        Mockito.when(mlc.upgrades.getGuidance()).thenReturn(oldGuidance);

        try {
            (new CommandStack(0)).pushAndApply(new CmdSetGuidanceType(xBar, mlc.loadout, newGuidance));
        }
        catch (Throwable t) {
            /* No-Op */
        }

        Mockito.verify(mlc.upgrades, Mockito.never()).setGuidance(Matchers.any(GuidanceUpgrade.class));
    }

    /**
     * Apply shall delegate to the upgrades object to change all Missile Weapons and Ammunition types.
     * @throws Exception 
     */
    @Test
    public void testApply_changeMissileLaunchersAndAmmo() throws Exception {
        Mockito.when(mlc.upgrades.getGuidance()).thenReturn(oldGuidance);
        CommandStack stack = new CommandStack(0);
        Mockito.when(mlc.loadout.getFreeMass()).thenReturn(100.0);
        Mockito.when(mlc.loadout.getNumCriticalSlotsFree()).thenReturn(100);
        Mockito.when(mlc.loadout.canEquip(Matchers.any(Item.class))).thenReturn(EquipResult.SUCCESS);

        MissileWeapon lrm5 = Mockito.mock(MissileWeapon.class);
        MissileWeapon lrm5Artemis = Mockito.mock(MissileWeapon.class);
        MissileWeapon narc = Mockito.mock(MissileWeapon.class);
        Ammunition lrmAmmo = Mockito.mock(Ammunition.class);
        Ammunition lrmAmmoArtemis = Mockito.mock(Ammunition.class);
        Ammunition narcAmmo = Mockito.mock(Ammunition.class);

        List<Item> rlItems = Arrays.asList(lrm5, lrmAmmo);
        List<Item> ltItems = Arrays.asList(lrm5, narcAmmo, narc, lrmAmmo);

        Mockito.when(newGuidance.upgrade(lrm5)).thenReturn(lrm5Artemis);
        Mockito.when(newGuidance.upgrade(narc)).thenReturn(narc);
        Mockito.when(newGuidance.upgrade(lrmAmmo)).thenReturn(lrmAmmoArtemis);
        Mockito.when(newGuidance.upgrade(narcAmmo)).thenReturn(narcAmmo);
        Mockito.when(mlc.rl.canEquip(Matchers.any(Item.class))).thenReturn(EquipResult.SUCCESS);
        Mockito.when(mlc.lt.canEquip(Matchers.any(Item.class))).thenReturn(EquipResult.SUCCESS);
        Mockito.when(mlc.rl.getItemsEquipped()).thenReturn(rlItems);
        Mockito.when(mlc.lt.getItemsEquipped()).thenReturn(ltItems);
        Mockito.when(mlc.rl.canRemoveItem(Matchers.any(Item.class))).thenReturn(true);
        Mockito.when(mlc.lt.canRemoveItem(Matchers.any(Item.class))).thenReturn(true);

        stack.pushAndApply(new CmdSetGuidanceType(xBar, mlc.loadout, newGuidance));

        // FIXME: Verify... I can't gain access to verify this in any way...
        // assertEquals(2, rlItems.size());
        // assertEquals(4, ltItems.size());
        // assertTrue(rlItems.remove(lrm5Artemis));
        // assertTrue(rlItems.remove(lrmAmmoArtemis));
        // assertTrue(ltItems.remove(lrm5Artemis));
        // assertTrue(ltItems.remove(lrmAmmoArtemis));
        // assertTrue(ltItems.remove(narcAmmo));
        // assertTrue(ltItems.remove(narc));
    }

    @Test
    public void testUndo() throws Exception {
        Base64LoadoutCoder coder = new Base64LoadoutCoder();
        LoadoutBase<?> loadout = coder.parse("lsml://rR4AEURNB1QScQtNB1REvqCEj9P37332SAXGzly5WoqI0fyo");
        LoadoutBase<?> loadoutOriginal = coder.parse("lsml://rR4AEURNB1QScQtNB1REvqCEj9P37332SAXGzly5WoqI0fyo");
        CommandStack stack = new CommandStack(1);

        stack.pushAndApply(new CmdSetGuidanceType(xBar, loadout, UpgradeDB.STANDARD_GUIDANCE));
        stack.undo();

        assertEquals(loadoutOriginal, loadout);
    }
}
