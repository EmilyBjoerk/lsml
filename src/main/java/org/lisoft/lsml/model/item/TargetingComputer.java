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

import java.util.Collection;
import java.util.List;

import org.lisoft.lsml.model.chassi.ChassisClass;
import org.lisoft.lsml.model.chassi.HardPointType;
import org.lisoft.lsml.model.chassi.Location;
import org.lisoft.lsml.model.modifiers.Modifier;

/**
 * Models a targeting computer or command console.
 * <p>
 * XXX: Only takes range modifiers into account as of yet. We don't display crit and modified projectile speeds yet.
 *
 * @author Emily Björk
 */
public class TargetingComputer extends Module implements ModifierEquipment {
    private final Collection<Modifier> modifiers;

    public TargetingComputer(String aName, String aDesc, String aMwoName, int aMwoId, int aSlots, double aTons,
            HardPointType aHardpointType, double aHP, Faction aFaction, List<Location> aAllowedLocations,
            List<ChassisClass> aAllowedChassisClasses, Integer aAllowedAmount, Collection<Modifier> aModifiers) {
        super(aName, aDesc, aMwoName, aMwoId, aSlots, aTons, aHardpointType, aHP, aFaction, aAllowedLocations,
                aAllowedChassisClasses, aAllowedAmount);
        modifiers = aModifiers;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return modifiers;
    }
}
