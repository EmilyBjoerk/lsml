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
package lisong_mechlab.model.loadout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import lisong_mechlab.util.MessageXBar;

/**
 * This class models an operation stack that can be used for undo etc. It will automatically reset the stack if a new
 * garage is loaded.
 * 
 * @author Emily Björk
 */
public class OperationStack{
   /**
    * The {@link Operation} class represents an action that can be (un)done. Undoing the action will restore the state
    * of affected object to that before the {@link Operation} was done.
    * 
    * @author Emily Björk
    */
   public static abstract class Operation{

      /**
       * @return A {@link String} containing a (short) human readable description of this action.
       */
      public abstract String describe();

      /**
       * Will 'do' this operation
       */
      protected abstract void apply();

      /**
       * Will undo this action.
       */
      protected abstract void undo();
   }

   /**
    * This class models an operation that should be considered as one but actually consists of many smaller operations
    * that are all performed simultaneously.
    * 
    * @author Emily Björk
    */
   public static class CompositeOperation extends Operation{
      private final List<Operation> operations = new ArrayList<>();
      private final String          desciption;

      public CompositeOperation(String aDescription){
         desciption = aDescription;
      }

      public void addOp(Operation anOperation){
         operations.add(anOperation);
      }

      @Override
      public String describe(){
         return desciption;
      }

      @Override
      protected void apply(){
         ListIterator<Operation> it = operations.listIterator();
         while( it.hasNext() ){
            it.next().apply();
         }
      }

      @Override
      protected void undo(){
         // Do it in the "right" i.e. backwards order
         ListIterator<Operation> it = operations.listIterator(operations.size());
         while( it.hasPrevious() ){
            it.previous().undo();
         }
      }
   }

   private final List<Operation> actions   = new LinkedList<>();
   private final int             depth;
   private int                   currentOp = -1;

   /**
    * Creates a new {@link OperationStack} that listens on the given {@link MessageXBar} for garage resets and has the
    * given undo depth.
    * 
    * @param anUndoDepth
    *           The number of undo levels allowed.
    */
   public OperationStack(int anUndoDepth){
      depth = anUndoDepth;
   }

   public void pushAndApply(Operation anOp){
      anOp.apply();
      while( currentOp < actions.size() - 1 ){
         // Previously undone actions in the list
         actions.remove(actions.size() - 1);
      }
      actions.add(anOp);
      currentOp = actions.size() - 1;

      while( actions.size() > depth ){
         actions.remove(0);
         currentOp--;
      }

   }

   public void undo(){
      Operation op = nextUndo();
      if( null != op ){
         op.undo();
         currentOp--;
      }
   }

   public void redo(){
      Operation op = nextRedo();
      if( null != op ){
         op.apply();
         currentOp++;
      }
   }

   public Operation nextRedo(){
      if( currentOp + 1 >= actions.size() )
         return null;
      return actions.get(currentOp + 1);
   }

   public Operation nextUndo(){
      if( currentOp < 0 )
         return null;
      return actions.get(currentOp);
   }
}
