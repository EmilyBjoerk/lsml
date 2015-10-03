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
package org.lisoft.lsml.view.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jfree.chart.JFreeChart;
import org.lisoft.lsml.model.chassi.HardPoint;
import org.lisoft.lsml.model.chassi.HardPointType;
import org.lisoft.lsml.model.item.Ammunition;
import org.lisoft.lsml.model.item.ECM;
import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.HeatSink;
import org.lisoft.lsml.model.item.Internal;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.item.ItemDB;
import org.lisoft.lsml.model.item.JumpJet;
import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponentBase;
import org.lisoft.lsml.view.ProgramInit;
import org.lisoft.lsml.view.preferences.FontPreferences.FontSize;

public class StyleManager {
    private static final Insets PADDING        = new Insets(2, 5, 2, 5);
    private static final Insets THIN_PADDING   = new Insets(1, 2, 1, 2);
    private static final int    RADII          = ItemRenderer.RADII;
    private static final int    MARGIN         = 1;
    private static final Border thinItemBorder = new RoundedBorders(new Insets(0, MARGIN, 0, MARGIN), THIN_PADDING,
            RADII, false, false);
    private static final Border topBorder      = new RoundedBorders(new Insets(MARGIN, MARGIN, 0, MARGIN), PADDING,
            RADII, false, true);
    private static final Border middleBorder   = new RoundedBorders(new Insets(0, MARGIN, 0, MARGIN), PADDING, RADII,
            true, true);
    private static final Border bottomBorder   = new RoundedBorders(new Insets(0, MARGIN, MARGIN, MARGIN), PADDING,
            RADII, true, false);
    private static final Border singleBorder   = new RoundedBorders(new Insets(MARGIN, MARGIN, MARGIN, MARGIN), PADDING,
            RADII, false, false);

    // Weapons
    private static final Color COLOR_FG_ENERGY         = Color.WHITE;
    private static final Color COLOR_BG_ENERGY         = new Color(0x9b8c16);
    private static final Color COLOR_FG_ENERGY_ALT     = Color.WHITE;
    private static final Color COLOR_BG_ENERGY_ALT     = COLOR_BG_ENERGY;
    private static final Color COLOR_FG_MISSILE        = Color.WHITE;
    private static final Color COLOR_BG_MISSILE        = new Color(0x008b76);
    private static final Color COLOR_FG_MISSILE_AMMO   = Color.WHITE;
    private static final Color COLOR_BG_MISSILE_AMMO   = new Color(0x005c4f);
    private static final Color COLOR_FG_BALLISTIC      = Color.WHITE;
    private static final Color COLOR_BG_BALLISTIC      = new Color(0x6719cd);
    private static final Color COLOR_FG_BALLISTIC_AMMO = Color.WHITE;
    private static final Color COLOR_BG_BALLISTIC_AMMO = new Color(0x451189);

    // Engine/Propulsion
    private static final Color COLOR_FG_JJ     = Color.WHITE;
    private static final Color COLOR_BG_JJ     = new Color(0x0067a5);
    private static final Color COLOR_FG_ENGINE = Color.WHITE;
    private static final Color COLOR_BG_ENGINE = new Color(0x0067a5);
    private static final Color COLOR_FG_HS     = Color.WHITE;
    private static final Color COLOR_BG_HS     = new Color(0x004069);

    // Structure/Internal
    private static final Color COLOR_FG_DYNAMIC  = (new Color(0xe1e6dd)).darker();
    private static final Color COLOR_BG_DYNAMIC  = new Color(0xe1e6dd);
    private static final Color COLOR_FG_INTERNAL = Color.GRAY.darker();
    private static final Color COLOR_BG_INTERNAL = new Color(0xd3d7cf);

    // Counter measures
    private static final Color COLOR_FG_AMS      = Color.WHITE;
    private static final Color COLOR_BG_AMS      = new Color(0x78a62d);
    private static final Color COLOR_FG_AMS_AMMO = Color.WHITE;
    private static final Color COLOR_BG_AMS_AMMO = new Color(0x606f1e);
    private static final Color COLOR_FG_ECM      = Color.WHITE;
    private static final Color COLOR_BG_ECM      = new Color(0xcb5d01);

    // Others
    private static final Color COLOR_BG_MISC = new Color(0x0067a5);

    private static final Icon MISSILE_BAY_DOOR_ICON = new ImageIcon(
            StyleManager.class.getResource("/resources/mbd.png"),
            "This hard point has missile bay doors. While closed the component receives 10% less damage");

    private static final Border INNER_BORDER  = BorderFactory.createEmptyBorder(0, 4, 4, 4);
    private static final Color  BAR_PRIMARY   = new Color(0xb7ceeb);
    private static final Color  BAR_SECONDARY = new Color(0xd9e1eb);

    public static Border sectionBorder(String sectionTitle) {
        return BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(sectionTitle), INNER_BORDER);
    }

    public static void styleHardpointLabel(JLabel aLabel, ConfiguredComponentBase aComponentBase,
            HardPointType aHardPointType) {
        styleHardpointLabel(aLabel, aHardPointType, aComponentBase.getHardPoints());
    }

    public static Color getColourBarPrimary() {
        return BAR_PRIMARY;
    }

    public static Color getColourBarSecondary() {
        return BAR_SECONDARY;
    }

    private static int countHardPoints(HardPointType aHardPointType, Collection<HardPoint> aHardPoints) {
        int ans = 0;
        for (HardPoint hardPoint : aHardPoints) {
            if (hardPoint.getType() == aHardPointType)
                ans++;
        }
        return ans;
    }

    private static boolean hasMissileBayDoors(Collection<HardPoint> aHardPoints) {
        for (HardPoint hardPoint : aHardPoints) {
            if (hardPoint.hasMissileBayDoor())
                return true;
        }
        return false;
    }

    public static void styleHardpointLabel(JLabel aLabel, HardPointType aHardPointType,
            Collection<HardPoint> aHardPoints) {
        int hardPoints = countHardPoints(aHardPointType, aHardPoints);
        if (hardPoints < 1) {
            aLabel.setVisible(false);
            return;
        }

        aLabel.setVisible(true);
        styleThinItem(aLabel, aHardPointType);

        if (aHardPointType == HardPointType.MISSILE) {
            aLabel.setText(formatMissileHardpointText(aHardPoints));

            if (hasMissileBayDoors(aHardPoints)) {
                aLabel.setIcon(MISSILE_BAY_DOOR_ICON);
                aLabel.setToolTipText(
                        "This component has missile bay doors. While the doors are closed the component takes 10% less damage.");
            }
            else {
                aLabel.setIcon(null);
                aLabel.setToolTipText(null);
            }
        }
        else if (hardPoints == 1) {
            aLabel.setText(aHardPointType.shortName());
        }
        else {
            aLabel.setText(hardPoints + " " + aHardPointType.shortName());
        }
    }

    public static String formatMissileHardpointText(ConfiguredComponentBase aComponentBase) {
        return formatMissileHardpointText(aComponentBase.getHardPoints());
    }

    public static String formatMissileHardpointText(Collection<HardPoint> aHardPoints) {
        Map<Integer, Integer> tubecounts = new TreeMap<>();
        for (HardPoint hp : aHardPoints) {
            if (hp.getType() == HardPointType.MISSILE) {
                final int tubes = hp.getNumMissileTubes();
                if (tubecounts.containsKey(tubes))
                    tubecounts.put(tubes, tubecounts.get(tubes) + 1);
                else
                    tubecounts.put(tubes, 1);
            }
        }

        String ans = countHardPoints(HardPointType.MISSILE, aHardPoints) + " M (";
        boolean first = true;
        for (Entry<Integer, Integer> it : tubecounts.entrySet()) {
            if (!first)
                ans += ", ";

            if (it.getValue() == 1) {
                ans += it.getKey();
            }
            else {
                ans += it.getKey() + "x" + it.getValue();
            }

            first = false;
        }

        return ans + ")";
    }

    public static void styleItem(JComponent aComponent) {
        Item item = null;
        styleItem(aComponent, item);
    }

    static public void styleItem(JComponent aComponent, Item anItem) {
        aComponent.setOpaque(true);
        aComponent.setBorder(singleBorder);
        aComponent.setBackground(getBgColorFor(anItem));
        aComponent.setForeground(getFgColorFor(anItem));
    }

    public static void styleThinItem(JComponent aComponent, Item anItem) {
        aComponent.setOpaque(true);
        aComponent.setBorder(thinItemBorder);
        aComponent.setBackground(getBgColorFor(anItem));
        aComponent.setForeground(getFgColorFor(anItem));
    }

    static public void styleItemTop(JComponent aComponent, Item anItem) {
        aComponent.setOpaque(true);
        aComponent.setBorder(topBorder);
        aComponent.setBackground(getBgColorFor(anItem));
        aComponent.setForeground(getFgColorFor(anItem));
    }

    static public void styleItemMiddle(JComponent aComponent, Item anItem) {
        aComponent.setOpaque(true);
        aComponent.setBorder(middleBorder);
        aComponent.setBackground(getBgColorFor(anItem));
        aComponent.setForeground(getFgColorFor(anItem));
    }

    static public void styleItemBottom(JComponent aComponent, Item anItem) {
        aComponent.setOpaque(true);
        aComponent.setBorder(bottomBorder);
        aComponent.setBackground(getBgColorFor(anItem));
        aComponent.setForeground(getFgColorFor(anItem));
    }

    public static void styleThinItem(JComponent aComponent, HardPointType aType) {
        aComponent.setOpaque(true);
        aComponent.setBorder(thinItemBorder);
        aComponent.setBackground(getBgColorFor(aType));
        aComponent.setForeground(getFgColorFor(aType));
    }

    public static void styleDynamicEntry(JComponent aComponent) {
        aComponent.setOpaque(true);
        aComponent.setBorder(singleBorder);
        aComponent.setBackground(COLOR_BG_DYNAMIC);
        aComponent.setForeground(COLOR_FG_DYNAMIC);
    }

    public static void styleItem(JComponent aComponent, HardPointType aType) {
        aComponent.setOpaque(true);
        aComponent.setBorder(singleBorder);
        aComponent.setBackground(getBgColorFor(aType));
        aComponent.setForeground(getFgColorFor(aType));
    }

    static public void colour(JComponent aComponent, HardPointType aType) {
        aComponent.setBackground(getBgColorFor(aType));
        aComponent.setForeground(getFgColorFor(aType));
    }

    static public void colour(JComponent aComponent, Item anItem) {
        aComponent.setBackground(getBgColorFor(anItem));
        aComponent.setForeground(getFgColorFor(anItem));
    }

    static public void colourInvalid(JComponent aComponent) {
        aComponent.setBackground(getBgColorInvalid());
        aComponent.setForeground(getFgColorInvalid());
    }

    static public Color getBgColorFor(HardPointType aType) {
        switch (aType) {
            case AMS:
                return COLOR_BG_AMS;
            case BALLISTIC:
                return COLOR_BG_BALLISTIC;
            case ECM:
                return COLOR_BG_ECM;
            case ENERGY:
                return COLOR_BG_ENERGY;
            case MISSILE:
                return COLOR_BG_MISSILE;
            case NONE:
            default:
                return Color.WHITE;
        }
    }

    static public Color getBgColorFor(Item anItem) {
        if (anItem == null) {
            return Color.WHITE;
        }
        else if (anItem instanceof Internal) {
            return COLOR_BG_INTERNAL;
        }
        else if (anItem instanceof Ammunition) {
            switch (((Ammunition) anItem).getWeaponHardpointType()) {
                case AMS:
                    return COLOR_BG_AMS_AMMO;
                case BALLISTIC:
                    return COLOR_BG_BALLISTIC_AMMO;
                case MISSILE:
                    return COLOR_BG_MISSILE_AMMO;
                default:
                    break;
            }
        }
        else if (anItem instanceof HeatSink) {
            return COLOR_BG_HS;
        }
        else if (anItem instanceof Engine) {
            return COLOR_BG_ENGINE;
        }
        else if (anItem instanceof JumpJet) {
            return COLOR_BG_JJ;
        }
        else if (anItem instanceof ECM) {
            return COLOR_BG_ECM;
        }
        else if (anItem.getName().contains("TAG")) {
            return COLOR_BG_ENERGY_ALT;
        }
        else if (anItem instanceof Weapon || anItem == ItemDB.ECM) {
            return getBgColorFor(anItem.getHardpointType());
        }
        return COLOR_BG_MISC;
    }

    static public Color getBgColorInvalid() {
        return Color.GRAY.brighter();
    }

    static public Color getFgColorFor(HardPointType aType) {
        switch (aType) {
            case AMS:
                return COLOR_FG_AMS;
            case BALLISTIC:
                return COLOR_FG_BALLISTIC;
            case ECM:
                return COLOR_FG_ECM;
            case ENERGY:
                return COLOR_FG_ENERGY;
            case MISSILE:
                return COLOR_FG_MISSILE;
            case NONE:
            default:
                return Color.BLACK;
        }
    }

    static public Color getFgColorFor(Item anItem) {
        if (anItem == null) {
            return Color.BLACK;
        }
        else if (anItem instanceof Internal) {
            return COLOR_FG_INTERNAL;
        }
        else if (anItem instanceof Ammunition) {
            switch (((Ammunition) anItem).getWeaponHardpointType()) {
                case AMS:
                    return COLOR_FG_AMS_AMMO;
                case BALLISTIC:
                    return COLOR_FG_BALLISTIC_AMMO;
                case MISSILE:
                    return COLOR_FG_MISSILE_AMMO;
                default:
                    break;
            }
        }
        else if (anItem instanceof HeatSink) {
            return COLOR_FG_HS;
        }
        else if (anItem instanceof Engine) {
            return COLOR_FG_ENGINE;
        }
        else if (anItem instanceof JumpJet) {
            return COLOR_FG_JJ;
        }
        else if (anItem instanceof ECM) {
            return COLOR_FG_ECM;
        }
        else if (anItem.getName().contains("TAG")) {
            return COLOR_FG_ENERGY_ALT;
        }
        else if (anItem instanceof Weapon || anItem == ItemDB.ECM) {
            return getFgColorFor(anItem.getHardpointType());
        }
        return Color.WHITE;
    }

    static public Color getFgColorInvalid() {
        return Color.GRAY.darker();
    }

    static public void styleSmallGraph(JFreeChart aChart, Color aBackground) {
        aChart.setBackgroundPaint(aBackground);

        Font labelFont = UIManager.getFont("Label.font");
        FontSize fontSize = ProgramInit.lsml().preferences.fontPreferences.getFontSize();
        Font titleSize = labelFont.deriveFont(1.4f * fontSize.getSizeFactor() * labelFont.getSize());
        Font axisSize = labelFont.deriveFont(1.2f * fontSize.getSizeFactor() * labelFont.getSize());
        Font labelSize = labelFont.deriveFont(1.0f * fontSize.getSizeFactor() * labelFont.getSize());

        aChart.getTitle().setFont(titleSize);
        aChart.getXYPlot().getDomainAxis().setLabelFont(axisSize);
        aChart.getXYPlot().getDomainAxis().setTickLabelFont(labelSize);
        aChart.getXYPlot().getRangeAxis().setLabelFont(axisSize);
        aChart.getXYPlot().getRangeAxis().setTickLabelFont(labelSize);
    }
}