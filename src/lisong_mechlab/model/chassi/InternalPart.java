package lisong_mechlab.model.chassi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lisong_mechlab.model.item.Internal;
import lisong_mechlab.model.item.Item;
import lisong_mechlab.model.mwo_parsing.HardpointsXml;
import lisong_mechlab.model.mwo_parsing.helpers.MdfComponent;
import lisong_mechlab.model.mwo_parsing.helpers.MdfInternal;

public class InternalPart{
   private final int             criticalslots;
   private final Part            type;
   private final int             maxarmor;
   private final double          hitpoints;
   private final List<Item>      internals;
   private final List<Hardpoint> hardpoints;

   public InternalPart(MdfComponent aComponent, Part aPart, HardpointsXml aHardpoints, Chassi aChassi){
      criticalslots = aComponent.Slots;
      type = aPart;
      hitpoints = aComponent.HP;
      maxarmor = (type == Part.Head) ? 18 : (int)(hitpoints * 2);

      internals = new ArrayList<Item>();
      if( null != aComponent.internals ){
         for(MdfInternal internal : aComponent.internals){
            internals.add(new Internal(internal));
         }
      }

      hardpoints = new ArrayList<>();
      if( null != aComponent.hardpoints ){
         for(MdfComponent.Hardpoint hardpoint : aComponent.hardpoints){
            final HardpointType hardpointType = HardpointType.fromMwoType(hardpoint.Type);

            if( hardpointType == HardpointType.MISSILE ){
               List<Integer> tubes = aHardpoints.tubesForId(hardpoint.ID);
               for(Integer tube : tubes){
                  // FIXME: Hardcoded case for hbk-4j which has 2 LRM10s as an LRM20 but the data files are missleading
                  if( aChassi.getNameShort().equals("HBK-4J") && aPart == Part.RightTorso ){
                     tube = 10;
                  }
                  if( tube < 1 ){
                     hardpoints.add(HardpointCache.getHardpoint(hardpoint.ID, aChassi.getMwoName(), aPart));
                  }
                  else{
                     hardpoints.add(new Hardpoint(HardpointType.MISSILE, tube));
                  }
               }
            }
            else{
               for(int i = 0; i < aHardpoints.slotsForId(hardpoint.ID); ++i)
                  hardpoints.add(new Hardpoint(hardpointType));
            }
         }

         // For any mech with more than 2 missile hardpoints in CT, any launcher beyond the largest one can only
         // have 5 tubes (anything else is impossible to fit)
         if( type == Part.CenterTorso && getNumHardpoints(HardpointType.MISSILE) > 1 ){
            int maxTubes = 0;
            for(Hardpoint hardpoint : hardpoints){
               maxTubes = Math.max(hardpoint.getNumMissileTubes(), maxTubes);
            }

            boolean maxAdded = false;
            for(int i = 0; i < hardpoints.size(); ++i){
               if( hardpoints.get(i).getType() != HardpointType.MISSILE )
                  continue;
               int tubes = hardpoints.get(i).getNumMissileTubes();
               if( (tubes < maxTubes && tubes > 5) || (tubes == maxTubes && maxAdded == true && tubes > 5) ){
                  hardpoints.set(i, new Hardpoint(HardpointType.MISSILE, 5));
               }
               if( tubes == maxTubes )
                  maxAdded = true;
            }
         }
      }

      // Stupid PGI making hacks to put ECM on a hardpoint... now I have to change my code...
      if( aComponent.CanEquipECM == 1 )
         hardpoints.add(new Hardpoint(HardpointType.ECM));
   }

   @Override
   public String toString(){
      return getType().toString();
   }

   @Override
   public int hashCode(){
      final int prime = 31;
      int result = 1;
      result = prime * result + criticalslots;
      result = prime * result + ((hardpoints == null) ? 0 : hardpoints.hashCode());
      long temp;
      temp = Double.doubleToLongBits(hitpoints);
      result = prime * result + (int)(temp ^ (temp >>> 32));
      result = prime * result + ((internals == null) ? 0 : internals.hashCode());
      result = prime * result + maxarmor;
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj){
      if( this == obj )
         return true;
      if( obj == null )
         return false;
      if( !(obj instanceof InternalPart) )
         return false;
      InternalPart other = (InternalPart)obj;
      if( criticalslots != other.criticalslots )
         return false;
      if( hardpoints == null ){
         if( other.hardpoints != null )
            return false;
      }
      else if( !hardpoints.equals(other.hardpoints) )
         return false;
      if( Double.doubleToLongBits(hitpoints) != Double.doubleToLongBits(other.hitpoints) )
         return false;
      if( internals == null ){
         if( other.internals != null )
            return false;
      }
      else if( !internals.equals(other.internals) )
         return false;
      if( maxarmor != other.maxarmor )
         return false;
      if( type != other.type )
         return false;
      return true;
   }

   public Part getType(){
      return type;
   }

   public int getArmorMax(){
      return maxarmor;
   }

   public int getNumCriticalslots(){
      return criticalslots;
   }

   public int getNumHardpoints(HardpointType aHardpointType){
      int ans = 0;
      for(Hardpoint it : hardpoints){
         if( it.getType() == aHardpointType ){
            ans++;
         }
      }
      return ans;
   }

   public List<Item> getInternalItems(){
      return Collections.unmodifiableList(internals);
   }

   public double getHitpoints(){
      return hitpoints;
   }

   public Collection<Hardpoint> getHardpoints(){
      return hardpoints;
   }
}
