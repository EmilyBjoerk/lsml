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
package org.lisoft.lsml.model.chassi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.lisoft.lsml.model.datacache.ChassisDB;
import org.lisoft.lsml.model.datacache.ItemDB;
import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.EngineType;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.item.HeatSink;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.item.JumpJet;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.lisoft.lsml.model.upgrades.ArmourUpgrade;
import org.lisoft.lsml.model.upgrades.HeatSinkUpgrade;
import org.lisoft.lsml.model.upgrades.StructureUpgrade;
import org.lisoft.lsml.model.upgrades.Upgrades;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * A test suite for {@link ChassisOmniMech}.
 *
 * @author Emily Björk
 */
public class ChassisOmniMechTest extends ChassisTest {
    private ArmourUpgrade armourType;
    private ComponentOmniMech[] components;
    private Engine engine;
    private HeatSinkUpgrade heatSinkType;
    private Map<Location, List<Item>> items;
    private StructureUpgrade structureType;

    @Override
    @Before
    public void setup() {
        super.setup();

        engine = Mockito.mock(Engine.class);
        Mockito.when(engine.getFaction()).thenReturn(faction);
        Mockito.when(engine.getHardpointType()).thenReturn(HardPointType.NONE);
        Mockito.when(engine.getRating()).thenReturn(250);
        Mockito.when(engine.getType()).thenReturn(EngineType.XL);
        Mockito.when(engine.isCompatible(Matchers.any(Upgrades.class))).thenReturn(true);

        structureType = Mockito.mock(StructureUpgrade.class);
        armourType = Mockito.mock(ArmourUpgrade.class);
        heatSinkType = Mockito.mock(HeatSinkUpgrade.class);

        items = new HashMap<>();
        components = new ComponentOmniMech[Location.values().length];
        for (final Location location : Location.values()) {
            items.put(location, new ArrayList<Item>());
            components[location.ordinal()] = Mockito.mock(ComponentOmniMech.class);
            Mockito.when(components[location.ordinal()].isAllowed(Matchers.any(Item.class))).thenReturn(true);
            Mockito.when(components[location.ordinal()].isAllowed(Matchers.any(Item.class), Matchers.any(Engine.class)))
                    .thenReturn(true);
            Mockito.when(components[location.ordinal()].getFixedItems()).thenReturn(items.get(location));
        }
        componentBases = components;

        items.get(Location.CenterTorso).add(engine);

        // Mockito.when(components[Location.Head.ordinal()].getArmourMax()).thenReturn(18);
        // Mockito.when(components[Location.LeftArm.ordinal()].getArmourMax()).thenReturn(48);
        // Mockito.when(components[Location.LeftTorso.ordinal()].getArmourMax()).thenReturn(64);
        // Mockito.when(components[Location.LeftLeg.ordinal()].getArmourMax()).thenReturn(64);
        // Mockito.when(components[Location.CenterTorso.ordinal()].getArmourMax()).thenReturn(92);
        // Mockito.when(components[Location.RightArm.ordinal()].getArmourMax()).thenReturn(48);
        // Mockito.when(components[Location.RightTorso.ordinal()].getArmourMax()).thenReturn(64);
        // Mockito.when(components[Location.RightLeg.ordinal()].getArmourMax()).thenReturn(64);

        Mockito.when(components[Location.Head.ordinal()].getDynamicArmourSlots()).thenReturn(1);
        Mockito.when(components[Location.CenterTorso.ordinal()].getDynamicArmourSlots()).thenReturn(1);
        Mockito.when(components[Location.LeftTorso.ordinal()].getDynamicArmourSlots()).thenReturn(3);
        Mockito.when(components[Location.RightArm.ordinal()].getDynamicArmourSlots()).thenReturn(2);

        Mockito.when(components[Location.LeftLeg.ordinal()].getDynamicStructureSlots()).thenReturn(1);
        Mockito.when(components[Location.RightTorso.ordinal()].getDynamicStructureSlots()).thenReturn(1);
        Mockito.when(components[Location.LeftArm.ordinal()].getDynamicStructureSlots()).thenReturn(3);
        Mockito.when(components[Location.RightLeg.ordinal()].getDynamicStructureSlots()).thenReturn(2);

        Mockito.when(structureType.getExtraSlots()).thenReturn(7);
        Mockito.when(armourType.getExtraSlots()).thenReturn(7);
    }

    @SuppressWarnings("unused")
    // Expecting exception
    @Test(expected = IllegalArgumentException.class)
    public final void testCtor_BadDynArmour() {
        Mockito.when(components[Location.Head.ordinal()].getDynamicArmourSlots()).thenReturn(13);
        new ChassisOmniMech(mwoID, mwoName, series, name, shortName, maxTons, variant, baseVariant, movementProfile,
                faction, components, maxPilotModules, maxConsumableModules, maxWeaponModules, structureType, armourType,
                heatSinkType, mascCapable);
    }

    @SuppressWarnings("unused")
    // Expecting exception
    @Test(expected = IllegalArgumentException.class)
    public final void testCtor_BadDynStructure() {
        Mockito.when(components[Location.Head.ordinal()].getDynamicStructureSlots()).thenReturn(13);
        new ChassisOmniMech(mwoID, mwoName, series, name, shortName, maxTons, variant, baseVariant, movementProfile,
                faction, components, maxPilotModules, maxConsumableModules, maxWeaponModules, structureType, armourType,
                heatSinkType, mascCapable);
    }

    @Test
    public final void testGetFixedArmourType() {
        assertSame(armourType, makeDefaultCUT().getFixedArmourType());
    }

    @Test
    public final void testGetFixedEngine() {
        assertSame(engine, makeDefaultCUT().getFixedEngine());
    }

    @Test(expected = IllegalStateException.class)
    public final void testGetFixedEngine_noEngine() {
        final Item item1 = Mockito.mock(Item.class);
        final HeatSink hs1 = Mockito.mock(HeatSink.class);

        items.get(Location.LeftArm).add(item1);
        items.get(Location.RightTorso).add(hs1);
        items.get(Location.RightTorso).add(item1);
        items.get(Location.CenterTorso).clear(); // Remove engine.
        items.get(Location.CenterTorso).add(hs1);
        items.get(Location.CenterTorso).add(item1);

        makeDefaultCUT().getFixedEngine();
    }

    /**
     * {@link ChassisOmniMech#getFixedMass()} shall return the mass of the chassis with all non fixed items and armour
     * removed.
     */
    @Test
    public final void testGetFixedHeatSinks() {
        final ChassisOmniMech cut = makeDefaultCUT();

        final List<Item> fixed1 = new ArrayList<>();
        final List<Item> fixed2 = new ArrayList<>();
        final List<Item> fixed3 = new ArrayList<>();

        final Item item1 = Mockito.mock(Item.class);
        final HeatSink hs1 = Mockito.mock(HeatSink.class);
        final HeatSink hs2 = Mockito.mock(HeatSink.class);

        Mockito.when(item1.getMass()).thenReturn(1.0);
        Mockito.when(hs1.getMass()).thenReturn(2.0);
        Mockito.when(hs2.getMass()).thenReturn(3.0);

        fixed1.add(item1);
        fixed1.add(hs1);
        fixed2.add(hs1);
        fixed2.add(hs1);
        fixed3.add(hs2);

        Mockito.when(components[2].getFixedItems()).thenReturn(fixed1);
        Mockito.when(components[3].getFixedItems()).thenReturn(fixed2);
        Mockito.when(components[5].getFixedItems()).thenReturn(fixed3);

        assertEquals(4, cut.getFixedHeatSinks());
    }

    @Test
    public final void testGetFixedHeatSinkType() {
        assertSame(heatSinkType, makeDefaultCUT().getFixedHeatSinkType());
    }

    @Test
    public final void testGetFixedJumpJets() {
        final List<Item> fixed1 = new ArrayList<>();
        final List<Item> fixed2 = new ArrayList<>();
        final List<Item> fixed3 = new ArrayList<>();

        final Item item1 = Mockito.mock(Item.class);
        final JumpJet jj1 = Mockito.mock(JumpJet.class);
        final JumpJet jj2 = Mockito.mock(JumpJet.class);

        fixed1.add(item1);
        fixed1.add(jj1);
        fixed2.add(jj1);
        fixed2.add(jj1);
        fixed3.add(jj2);

        Mockito.when(components[2].getFixedItems()).thenReturn(fixed1);
        Mockito.when(components[3].getFixedItems()).thenReturn(fixed2);
        Mockito.when(components[5].getFixedItems()).thenReturn(fixed3);

        assertSame(4, makeDefaultCUT().getFixedJumpJets());
    }

    /**
     * {@link ChassisOmniMech#getFixedMass()} shall return the mass of the chassis with all non fixed items and armour
     * removed.
     */
    @Test
    public final void testGetFixedMass() {
        final ChassisOmniMech cut = makeDefaultCUT();

        final List<Item> fixed1 = new ArrayList<>();
        final List<Item> fixed2 = new ArrayList<>();
        final List<Item> fixed3 = new ArrayList<>();

        final Item item1 = Mockito.mock(Item.class);
        final Item item2 = Mockito.mock(Item.class);
        final Item item3 = Mockito.mock(Item.class);

        Mockito.when(item1.getMass()).thenReturn(1.0);
        Mockito.when(item2.getMass()).thenReturn(2.0);
        Mockito.when(item3.getMass()).thenReturn(3.0);

        fixed1.add(item1);
        fixed1.add(item2);
        fixed2.add(item2);
        fixed2.add(item2);
        fixed3.add(item3);

        Mockito.when(components[2].getFixedItems()).thenReturn(fixed1);
        Mockito.when(components[3].getFixedItems()).thenReturn(fixed2);
        Mockito.when(components[5].getFixedItems()).thenReturn(fixed3);

        Mockito.when(structureType.getStructureMass(cut)).thenReturn(3.0);

        final double expected = 1 * 1 + 3 * 2 + 1 * 3 + 3;

        assertEquals(expected, cut.getFixedMass(), 0.0);

        Mockito.verify(armourType, Mockito.never()).getArmourMass(Matchers.anyInt());
    }

    @Test
    public final void testGetFixedStructureType() {
        assertSame(structureType, makeDefaultCUT().getFixedStructureType());
    }

    @Test
    public final void testGetMovementProfiles() {
        final ChassisOmniMech mech = (ChassisOmniMech) ChassisDB.lookup("kfx-prime");

        final MovementProfile baseProfile = mech.getMovementProfileBase();

        final MovementProfile max = mech.getMovementProfileMax();
        final MovementProfile min = mech.getMovementProfileMin();
        final Collection<Modifier> stock = mech.getStockModifiers();

        assertTrue(baseProfile.getTorsoYawSpeed(null) < baseProfile.getTorsoYawSpeed(stock));
        assertTrue(baseProfile.getTorsoYawMax(null) < baseProfile.getTorsoYawMax(stock));

        assertTrue(baseProfile.getTorsoYawSpeed(null) < min.getTorsoYawSpeed(null));
        assertTrue(baseProfile.getTorsoYawSpeed(null) < max.getTorsoYawSpeed(null));
    }

    @Test
    public final void testIsAllowed_CASE() {
        final Item item = ItemDB.CASE;

        final ChassisOmniMech cut = makeDefaultCUT();
        assertFalse(cut.isAllowed(item));
    }

    @Test
    public final void testIsAllowed_Engine() {
        assertFalse(makeDefaultCUT().isAllowed(engine));
    }

    @Test
    public final void testIsAllowed_NoComponentSupport() {
        final Item item = Mockito.mock(Item.class);
        Mockito.when(item.getHardpointType()).thenReturn(HardPointType.NONE);
        Mockito.when(item.getFaction()).thenReturn(Faction.CLAN);
        Mockito.when(item.isCompatible(Matchers.any(Upgrades.class))).thenReturn(true);

        final ChassisOmniMech cut = makeDefaultCUT();
        assertTrue(cut.isAllowed(item)); // Item in it self is allowed

        // But no component supports it.
        for (final Location location : Location.values()) {
            Mockito.when(components[location.ordinal()].isAllowed(item, null)).thenReturn(false);
        }
        assertFalse(cut.isAllowed(item));
    }

    @Override
    protected ChassisOmniMech makeDefaultCUT() {
        return new ChassisOmniMech(mwoID, mwoName, series, name, shortName, maxTons, variant, baseVariant,
                movementProfile, faction, components, maxPilotModules, maxConsumableModules, maxWeaponModules,
                structureType, armourType, heatSinkType, mascCapable);
    }
}
