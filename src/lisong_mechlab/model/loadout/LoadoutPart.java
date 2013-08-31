package lisong_mechlab.model.loadout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lisong_mechlab.model.MessageXBar;
import lisong_mechlab.model.chassi.ArmorSide;
import lisong_mechlab.model.chassi.HardpointType;
import lisong_mechlab.model.chassi.InternalPart;
import lisong_mechlab.model.chassi.Part;
import lisong_mechlab.model.item.Ammunition;
import lisong_mechlab.model.item.Engine;
import lisong_mechlab.model.item.EngineType;
import lisong_mechlab.model.item.HeatSink;
import lisong_mechlab.model.item.Internal;
import lisong_mechlab.model.item.Item;
import lisong_mechlab.model.item.ItemDB;
import lisong_mechlab.model.item.JumpJet;
import lisong_mechlab.model.item.MissileWeapon;
import lisong_mechlab.model.loadout.LoadoutPart.Message.Type;
import lisong_mechlab.model.loadout.Upgrades.ChangeMsg;

public class LoadoutPart implements MessageXBar.Reader{
   public static class Message implements MessageXBar.Message{
      public Message(LoadoutPart aPart, Type aType){
         part = aPart;
         type = aType;
      }

      @Override
      public boolean equals(Object obj){
         if( obj instanceof Message ){
            Message other = (Message)obj;
            return part == other.part && type == other.type;
         }
         return false;
      }

      public enum Type{
         ItemAdded, ItemRemoved, ArmorChanged
      };

      final public LoadoutPart part;
      final public Type        type;
   }

   public final static double            ARMOR_PER_TON   = 32.0;

   public final Internal                 ENGINE_INTERNAL = new Internal("mdf_Engine", "mdf_EngineDesc", 3);

   private final InternalPart            internalPart;
   private final Loadout                 loadout;
   private final List<Item>              items;
   private final Map<ArmorSide, Integer> armor;
   private int                           engineHeatsinks = 0;

   private final MessageXBar             xBar;

   LoadoutPart(Loadout aLoadOut, InternalPart anInternalPart, MessageXBar aXBar){
      internalPart = anInternalPart;
      items = new ArrayList<Item>(internalPart.getInternalItems());
      loadout = aLoadOut;
      armor = new TreeMap<ArmorSide, Integer>();
      xBar = aXBar;
      xBar.attach(this);

      if( internalPart.getType().isTwoSided() ){
         armor.put(ArmorSide.FRONT, 0);
         armor.put(ArmorSide.BACK, 0);
      }
      else{
         armor.put(ArmorSide.ONLY, 0);
      }
   }

   public InternalPart getInternalPart(){
      return internalPart;
   }

   public int getNumCriticalSlotsFree(){
      return internalPart.getNumCriticalslots() - getNumCriticalSlotsUsed();
   }

   public int getNumCriticalSlotsUsed(){
      int crits = 0;
      int engineHsLeft = getNumEngineHeatsinksMax();
      for(Item item : items){
         if( item instanceof MissileWeapon )
            crits += ((MissileWeapon)item).getNumCriticalSlots(loadout.getUpgrades().hasArtemis());
         else if( item instanceof HeatSink ){
            if( engineHsLeft > 0 )
               engineHsLeft--;
            else
               crits += item.getNumCriticalSlots();
         }
         else
            crits += item.getNumCriticalSlots();
      }
      return crits;
   }

   public int getNumItemsOfHardpointType(HardpointType aHardpointType){
      int hardpoints = 0;
      for(Item it : items){
         if( it.getHardpointType() == aHardpointType ){
            hardpoints++;
         }
      }
      return hardpoints;
   }

   public List<Item> getItems(){
      return Collections.unmodifiableList(items);
   }

   public void addItem(String aString){
      addItem(ItemDB.lookup(aString));
   }

   public void addItem(Item anItem){
      if( !canAddItem(anItem) ){
         throw new IllegalArgumentException("Can't add " + anItem + "!");
      }
      if( anItem instanceof Engine ){
         Engine engine = (Engine)anItem;
         if( engine.getType() == EngineType.XL ){
            loadout.getPart(Part.LeftTorso).items.add(ENGINE_INTERNAL);
            loadout.getPart(Part.RightTorso).items.add(ENGINE_INTERNAL);
         }
      }
      items.add(anItem);
      xBar.post(new Message(this, Type.ItemAdded));
   }

   public boolean canAddItem(Item anItem){
      if( anItem instanceof Internal ){
         return false; // Can't add internals!
      }
      else if( anItem instanceof HeatSink ){
         return checkHeatsinkRules((HeatSink)anItem);
      }
      else if( anItem instanceof Engine ){
         return checkEngineRules((Engine)anItem);
      }
      else if( anItem instanceof JumpJet ){
         return checkJumpJetRules((JumpJet)anItem);
      }
      else{
         // Case can only be put in side torsi
         if( anItem == ItemDB.lookup("C.A.S.E.") ){
            if( internalPart.getType() != Part.LeftTorso && internalPart.getType() != Part.RightTorso ){
               return false;
            }
         }
         return checkCommonRules(anItem);
      }
   }

   public void removeItem(Item anItem){
      if( internalPart.getInternalItems().contains(anItem) || anItem instanceof Internal ){
         return; // Don't remove internals!
      }

      if( anItem instanceof Engine ){
         Engine engine = (Engine)anItem;
         if( !items.contains(engine) )
            return; // Don't remove anything we don't have (only dangerous if we accidentally remove LT/RT engine
                    // sides).

         if( engine.getType() == EngineType.XL ){
            loadout.getPart(Part.LeftTorso).items.remove(ENGINE_INTERNAL);
            loadout.getPart(Part.RightTorso).items.remove(ENGINE_INTERNAL);
         }
      }
      if( items.remove(anItem) )
         xBar.post(new Message(this, Type.ItemRemoved));
   }

   public void removeAllItems(){
      items.clear();
      items.addAll(internalPart.getInternalItems());
   }

   public void setArmor(ArmorSide anArmorSide, int anArmorAmount){
      if( anArmorAmount > getArmorMax(anArmorSide) ){
         throw new IllegalArgumentException("Exceeded max armor! Max allowed: " + getArmorMax(anArmorSide) + " Was: " + anArmorAmount);
      }
      armor.put(anArmorSide, anArmorAmount);
      xBar.post(new Message(this, Type.ArmorChanged));
   }

   public int getArmorTotal(){
      int sum = 0;
      for(Integer i : armor.values()){
         sum += i;
      }
      return sum;
   }

   public int getArmor(ArmorSide anArmorSide){
      return armor.get(anArmorSide);
   }

   public int getArmorMax(ArmorSide anArmorSide){
      // TODO: Take free tonnage into consideration!
      if( anArmorSide == ArmorSide.ONLY ){
         return internalPart.getArmorMax();
      }
      else if( anArmorSide == ArmorSide.FRONT ){
         return internalPart.getArmorMax() - getArmor(ArmorSide.BACK);
      }
      else if( anArmorSide == ArmorSide.BACK ){
         return internalPart.getArmorMax() - getArmor(ArmorSide.FRONT);
      }
      throw new UnsupportedOperationException("Unknown side!");
   }

   public double getItemMass(){
      double ans = engineHeatsinks * 1.0;
      for(Item item : items){
         if( item instanceof MissileWeapon )
            ans += ((MissileWeapon)item).getMass(loadout.getUpgrades().hasArtemis());
         else
            ans += item.getMass();
      }
      return ans;
   }

   public int getNumEngineHeatsinksMax(){
      for(Item item : items){
         if( item instanceof Engine ){
            return ((Engine)item).getNumHeatsinkSlots();
         }
      }
      return 0;
   }

   public int getNumEngineHeatsinks(){
      int ans = 0;
      for(Item i : items){
         if( i instanceof HeatSink )
            ans++;
      }
      return Math.min(ans, getNumEngineHeatsinksMax());
   }

   @Override
   public void receive(MessageXBar.Message aMsg){
      if( aMsg instanceof Upgrades.Message ){
         Upgrades.Message msg = (Upgrades.Message)aMsg;
         if( msg.source != loadout.getUpgrades() ){
            return;
         }

         if( msg.msg == ChangeMsg.HEATSINKS ){
            if( msg.source.hasDoubleHeatSinks() )
               while( items.remove(ItemDB.lookup("STD HEAT SINK")) );
            else
               while( items.remove(ItemDB.lookup("DOUBLE HEAT SINK")) );
         }
      }
   }

   public String getItemDisplayName(int index){
      Item item = items.get(index);
      if( item instanceof MissileWeapon ){
         MissileWeapon missileWeapon = (MissileWeapon)item;
         return missileWeapon.getName(loadout.getUpgrades().hasArtemis());
      }
      if( item instanceof Ammunition ){
         Ammunition missileWeapon = (Ammunition)item;
         return missileWeapon.getName(loadout.getUpgrades().hasArtemis());
      }
      return items.get(index).getName(loadout.getUpgrades());
   }

   public int getItemCriticalSlots(int index){
      Item item = items.get(index);
      if( item instanceof MissileWeapon ){
         MissileWeapon missileWeapon = (MissileWeapon)item;
         return missileWeapon.getNumCriticalSlots(loadout.getUpgrades().hasArtemis());
      }
      return items.get(index).getNumCriticalSlots();
   }

   private boolean checkCommonRules(Item anItem){
      // Check enough free mass
      if( loadout.getMass() + anItem.getMass() > loadout.getChassi().getMassMax() ){
         return false;
      }

      // Check enough free critical slots
      if( getNumCriticalSlotsFree() < anItem.getNumCriticalSlots() ){
         return false;
      }

      // Check enough free hardpoints
      if( anItem.getHardpointType() != HardpointType.NONE
          && getNumItemsOfHardpointType(anItem.getHardpointType()) >= getInternalPart().getNumHardpoints(anItem.getHardpointType()) ){
         return false; // Not enough hard points!
      }
      return true;
   }

   private boolean checkJumpJetRules(JumpJet aItem){
      Part type = getInternalPart().getType();
      switch( type ){
         case RightTorso:
         case CenterTorso:
         case LeftTorso:
         case RightLeg:
         case LeftLeg:
            return loadout.getJumpJetCount() + 1 <= loadout.getChassi().getMaxJumpJets() && checkCommonRules(aItem);
         default:
            return false;
      }
   }

   private boolean checkEngineRules(Engine engine){
      if( getInternalPart().getType() != Part.CenterTorso ){
         return false; // Engines only in CT!
      }

      // XL engines need 3 additional slots in RT/LT
      if( engine.getType() == EngineType.XL ){
         if( loadout.getPart(Part.LeftTorso).getNumCriticalSlotsFree() < 3 ){
            return false;
         }
         if( loadout.getPart(Part.RightTorso).getNumCriticalSlotsFree() < 3 ){
            return false;
         }
      }

      if( engine.getRating() > loadout.getChassi().getEngineMax() || engine.getRating() < loadout.getChassi().getEngineMin() ){
         return false; // Too low/high engine rating!
      }
      return checkCommonRules(engine);
   }

   private boolean checkHeatsinkRules(HeatSink anItem){
      // Don't allow standard heat sinks when double heat sinks are upgraded etc.
      if( loadout.getUpgrades().hasDoubleHeatSinks() && anItem != ItemDB.lookup(3001) ){
         return false;
      }
      if( !loadout.getUpgrades().hasDoubleHeatSinks() && anItem != ItemDB.lookup(3000) ){
         return false;
      }

      // Allow engine slot heat sinks even if there are no critical slots
      if( getNumEngineHeatsinks() < getNumEngineHeatsinksMax() && loadout.getMass() + anItem.getMass() <= loadout.getChassi().getMassMax() ){
         return true;
      }
      return checkCommonRules(anItem);
   }
}
