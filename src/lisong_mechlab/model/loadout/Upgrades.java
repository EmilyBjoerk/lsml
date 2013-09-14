package lisong_mechlab.model.loadout;

import lisong_mechlab.util.MessageXBar;

public class Upgrades{
   private boolean                     artemis;
   private boolean                     ferroFibrous;
   private boolean                     endoSteel;
   private boolean                     dhs;

   private transient final MessageXBar xBar;

   public static class Message implements MessageXBar.Message{
      public final ChangeMsg msg;
      public final Upgrades  source;

      @Override
      public boolean equals(Object obj){
         if( obj instanceof Message ){
            Message other = (Message)obj;
            return msg == other.msg && source == other.source;
         }
         return false;
      }

      Message(ChangeMsg aChangeMsg, Upgrades anUpgrades){
         msg = aChangeMsg;
         source = anUpgrades;
      }
   }

   public enum ChangeMsg{
      GUIDANCE, STRUCTURE, ARMOR, HEATSINKS
   }

   public Upgrades(MessageXBar anXBar){
      xBar = anXBar;
   }

   @Override
   public int hashCode(){
      final int prime = 31;
      int result = 1;
      result = prime * result + (artemis ? 1231 : 1237);
      result = prime * result + (dhs ? 1231 : 1237);
      result = prime * result + (endoSteel ? 1231 : 1237);
      result = prime * result + (ferroFibrous ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj){
      if( this == obj )
         return true;
      if( obj == null )
         return false;
      if( !(obj instanceof Upgrades) )
         return false;
      Upgrades other = (Upgrades)obj;
      if( artemis != other.artemis )
         return false;
      if( dhs != other.dhs )
         return false;
      if( endoSteel != other.endoSteel )
         return false;
      if( ferroFibrous != other.ferroFibrous )
         return false;
      return true;
   }

   public boolean hasArtemis(){
      return artemis;
   }

   public boolean hasDoubleHeatSinks(){
      return dhs;
   }

   public boolean hasEndoSteel(){
      return endoSteel;
   }

   public boolean hasFerroFibrous(){
      return ferroFibrous;
   }

   public void setArtemis(boolean anArtemis){
	   
      if( anArtemis != artemis ){
         artemis = anArtemis;
         if( xBar != null )
            xBar.post(new Message(ChangeMsg.GUIDANCE, this));
      }
   }

   public void setDoubleHeatSinks(boolean aDHS){
      if( aDHS != dhs ){
         dhs = aDHS;
         if( xBar != null )
            xBar.post(new Message(ChangeMsg.HEATSINKS, this));
      }
   }

   public void setEndoSteel(boolean anEndoSteel){
      if( anEndoSteel != endoSteel ){
         endoSteel = anEndoSteel;
         if( xBar != null )
            xBar.post(new Message(ChangeMsg.STRUCTURE, this));
      }
   }

   public void setFerroFibrous(boolean aFerroFibrous){
      if( aFerroFibrous != ferroFibrous ){
         ferroFibrous = aFerroFibrous;
         if( xBar != null )
            xBar.post(new Message(ChangeMsg.ARMOR, this));
      }
   }
}
