package lisong_mechlab.model.item;

import lisong_mechlab.model.chassi.HardpointType;
import lisong_mechlab.model.mwo_parsing.helpers.ItemStatsWeapon;

public class MissileWeapon extends AmmoWeapon{
   private static final String ARTEMIS = " + ARTEMIS";
   protected final double      flightSpeed;

   public MissileWeapon(ItemStatsWeapon aStatsWeapon){
      super(aStatsWeapon, HardpointType.MISSILE);
      flightSpeed = aStatsWeapon.WeaponStats.speed;
   }

   @Override
   public double getRangeMax(){
      // Missile fall off is a bit different from other weapons because long = max.
      // Emulate a steep fall off by nudging max ever so slightly
      return super.getRangeMax() + Math.ulp(super.getRangeMax())*4; 
   }
   
   @Deprecated
   public int getNumCriticalSlots(boolean hasArtemis){
      if( hasArtemis && isArtemisCapable() )
         return super.getNumCriticalSlots() + 1;
      return super.getNumCriticalSlots();
   }

   @Deprecated
   public double getMass(boolean hasArtemis){
      if( hasArtemis && isArtemisCapable() )
         return super.getMass() + 1.0;
      return super.getMass();
   }

   @Deprecated
   public String getName(boolean hasArtemis){
      if( hasArtemis && isArtemisCapable() )
         return super.getName() + ARTEMIS;
      return super.getName();
   }

   @Override
   public double getRangeEffectivity(double range){
      // Assume linear fall off
      if( range < getRangeMin() )
         return 0;
      else
         return super.getRangeEffectivity(range);
   }
   
   /**
    * Canonizes an item name with respect to MissileWeapon specifics.
    * 
    * @param name
    *           The item name to canonize
    * @return A canonized version of the argument.
    */
   static public String canonize(String name){
      if( name.endsWith(ARTEMIS) )
         return name.replace(ARTEMIS, "");
      return name;
   }

   public boolean isArtemisCapable(){
      return (getName().contains("LRM") || getName().contains("SRM") && !getName().contains("STREAK"));
   }

}
