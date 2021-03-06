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
package org.lisoft.lsml.model.modifiers;

import java.util.*;
import java.util.function.Predicate;

import javax.inject.Inject;

/**
 * This {@link Predicate} will return <code>true</code> if the tested {@link Modifier} will affect the performance of
 * any weapon.
 *
 * @author Emily Björk
 *
 */
public class AffectsWeaponPredicate implements Predicate<Modifier> {
    private final static Set<String> WEAPON_SPECIFIERS;
    private final static List<String> HEAT_SELECTORS;

    static {
        WEAPON_SPECIFIERS = new HashSet<>();
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_COOL_DOWN);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_ROF);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_HEAT);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_PROJECTILE_SPEED);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_JAMMED_TIME);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_JAMMING_CHANCE);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_JAM_RAMP_DOWN_TIME);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_LARGE_BORE);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_RANGE);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_SPREAD);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_TAG_DURATION);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_DAMAGE);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_DURATION);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_NARC_DURATION);
        WEAPON_SPECIFIERS.add(ModifierDescription.SPEC_WEAPON_MAX_FREE_ALPAHA);

        HEAT_SELECTORS = new ArrayList<>();
        HEAT_SELECTORS.addAll(ModifierDescription.SEL_HEAT_DISSIPATION);
        HEAT_SELECTORS.addAll(ModifierDescription.SEL_HEAT_LIMIT);
        HEAT_SELECTORS.addAll(ModifierDescription.SEL_HEAT_EXTERNALTRANSFER);
    }

    @Inject
    public AffectsWeaponPredicate() {
        // NOP
    }

    @Override
    public boolean test(Modifier aModifier) {
        final ModifierDescription description = aModifier.getDescription();
        final String specifier = description.getSpecifier();
        if (null != specifier) {
            // An attribute is specified, see if that attribute is of a weapon.
            // If it's not, then it can't affect weapons and if it is, then it will.
            return WEAPON_SPECIFIERS.contains(specifier);
        }

        // Otherwise see if it selects a heat affecting attribute of the 'Mech
        final Collection<String> selectors = description.getSelectors();
        for (final String h : HEAT_SELECTORS) {
            if (selectors.contains(h)) {
                return true;
            }
        }
        return false;
    }
}
