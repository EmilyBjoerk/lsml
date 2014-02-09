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
package lisong_mechlab.model.upgrades;

import lisong_mechlab.model.item.Item;
import lisong_mechlab.model.item.MissileWeapon;
import lisong_mechlab.model.loadout.Loadout;
import lisong_mechlab.model.loadout.part.LoadoutPart;
import lisong_mechlab.model.mwo_parsing.helpers.ItemStatsUpgradeType;

public class GuidanceUpgrade extends Upgrade{
   final private int    slots;
   final private double tons;

   public GuidanceUpgrade(ItemStatsUpgradeType aUpgradeType){
      super(aUpgradeType);
      slots = aUpgradeType.UpgradeTypeStats.slots;
      tons = aUpgradeType.UpgradeTypeStats.pointMultiplier;
   }

   public int getSlots(){
      return slots;
   }

   public double getTons(){
      return tons;
   }

   /**
    * Calculates how many extra slots are needed in total for the given upgrade.
    * 
    * @param aLoadout
    *           The loadout to calculate for.
    * @return A number of slots needed.
    */
   public int getExtraSlots(Loadout aLoadout){
      int ans = 0;
      for(LoadoutPart part : aLoadout.getPartLoadOuts()){
         ans += getExtraSlots(part);
      }
      return ans;
   }

   /**
    * Calculates how many extra slots are needed for the given {@link LoadoutPart} for the given upgrade.
    * 
    * @param aLoadoutPart
    *           The {@link LoadoutPart} to calculate for.
    * @return A number of slots needed.
    */
   public int getExtraSlots(LoadoutPart aLoadoutPart){
      int ans = 0;
      for(Item item : aLoadoutPart.getItems()){
         if( item instanceof MissileWeapon ){
            MissileWeapon weapon = (MissileWeapon)item;
            if( weapon.isArtemisCapable() ){
               ans += slots;
            }
         }
      }
      return ans;
   }

   /**
    * Calculates how many extra tons are needed in total for the given upgrade.
    * 
    * @param aLoadout
    *           The {@link Loadout} to calculate for.
    * @return A number of tons needed.
    */
   public double getExtraTons(Loadout aLoadout){
      double ans = 0;
      for(LoadoutPart part : aLoadout.getPartLoadOuts()){
         ans += getExtraTons(part);
      }
      return ans;
   }

   /**
    * Calculates how many extra tons are needed for the given {@link LoadoutPart} for the given upgrade.
    * 
    * @param aLoadoutPart
    *           The {@link LoadoutPart} to calculate for.
    * @return A number of tons needed.
    */
   public double getExtraTons(LoadoutPart aLoadoutPart){
      double ans = 0;
      for(Item item : aLoadoutPart.getItems()){
         if( item instanceof MissileWeapon ){
            MissileWeapon weapon = (MissileWeapon)item;
            if( weapon.isArtemisCapable() ){
               ans += tons;
            }
         }
      }
      return ans;
   }
}