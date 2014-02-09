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
package lisong_mechlab.model.loadout.export;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lisong_mechlab.model.chassi.Chassi;
import lisong_mechlab.model.chassi.ChassiDB;
import lisong_mechlab.model.loadout.Loadout;
import lisong_mechlab.model.loadout.RenameOperation;
import lisong_mechlab.util.Base64;
import lisong_mechlab.util.MessageXBar;
import lisong_mechlab.util.OperationStack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests the {@link LoadoutCoderV1}
 * 
 * @author Emily Björk
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadoutCoderV1Test{
   @Mock
   private MessageXBar    xBar;
   @InjectMocks
   private LoadoutCoderV1 cut;

   /**
    * The coder shall handle the artemis change.
    * 
    * @throws Exception
    */
   @Test
   public void testArtemis() throws Exception{

      Base64 base64 = new Base64();
      String line = "[CENTURION CN9-D]=lsml://rJAAHSAaDCASJA4aDCAg9D7+/hCK32zHWw==";
      Pattern pat = Pattern.compile("\\[([^\\]]*)\\]\\s*=\\s*lsml://(\\S*).*");
      Matcher m = pat.matcher(line);
      m.matches();
      Chassi chassi = ChassiDB.lookup(m.group(1));
      String lsml = m.group(2);
      Loadout reference = new Loadout(chassi.getName(), xBar);
      
      // Execute
      Loadout decoded = cut.decode(base64.decode(lsml.toCharArray()));

      // Name is not encoded
      OperationStack stack = new OperationStack(0);
      stack.pushAndApply(new RenameOperation(decoded, xBar, reference.getName()));

      // Verify
      assertEquals(reference, decoded);

   }

   /**
    * The coder shall be able to decode all stock mechs.
    * 
    * @throws Exception
    */
   @Test
   public void testAllStock() throws Exception{
      InputStream is = LoadoutCoderV1.class.getResourceAsStream("/resources/lsmlv1stock.txt");
      Scanner sc = new Scanner(is);
      Base64 base64 = new Base64();

      // [JENNER JR7-D(F)]=lsml://rQAD5AgQCAwOFAYQCAwIuipmzMO3aIExIyk9jt2DMA==
      while( sc.hasNextLine() ){
         String line = sc.nextLine();
         Pattern pat = Pattern.compile("\\[([^\\]]*)\\]\\s*=\\s*lsml://(\\S*).*");
         Matcher m = pat.matcher(line);
         m.matches();
         Chassi chassi = ChassiDB.lookup(m.group(1));
         String lsml = m.group(2);
         Loadout reference = new Loadout(chassi.getName(), xBar);
         Loadout decoded = cut.decode(base64.decode(lsml.toCharArray()));

         // Name is not encoded
         OperationStack stack = new OperationStack(0);
         stack.pushAndApply(new RenameOperation(decoded, xBar, reference.getName()));

         // Verify
         assertEquals(reference, decoded);
      }

      sc.close();
   }
}