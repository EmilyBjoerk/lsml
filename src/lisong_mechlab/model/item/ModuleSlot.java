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
package lisong_mechlab.model.item;

/**
 * Enumerates all possible module slot types.
 * 
 * @author Emily Björk
 */
public enum ModuleSlot {
	CONSUMABLE("Consumable"), WEAPON("Weapon"), MECH("Mech"), HYBRID("Hybrid");

	public static ModuleSlot fromMwo(String aString) {
		switch (aString) {
			case "WeaponMod":
				return WEAPON;
			case "Consumable":
				return CONSUMABLE;
			case "Pilot":
				return MECH;
			default:
				throw new IllegalArgumentException("Unknown module type!: " + aString);
		}
	}

	private final String name;

	private ModuleSlot(String aName) {
		name = aName;
	}

	@Override
	public String toString() {
		return name;
	}
}
