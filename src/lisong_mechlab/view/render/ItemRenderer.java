package lisong_mechlab.view.render;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import lisong_mechlab.model.item.Engine;
import lisong_mechlab.model.item.EngineType;
import lisong_mechlab.model.item.Item;
import lisong_mechlab.model.loadout.Upgrades;

/**
 * This class can render any {@link Item} to an {@link Image}.
 * 
 * @author Emily Björk
 */
public class ItemRenderer{
   public static final int                    ITEM_BASE_HEIGHT = 20;                                           // [px]
   public static final int                    ITEM_BASE_WIDTH  = 120;                                          // [px]

   private static final GraphicsConfiguration configuration    = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                                                                                    .getDefaultConfiguration();
   private static final float                 PADDING          = 3;
   private static final float                 RADII            = 5;

   public static Image render(Item item, Upgrades aUpgrades){
      if( item instanceof Engine && ((Engine)item).getType() == EngineType.XL ){
         // Draw side torsos too
         return null;
      }
      final int slots = item.getNumCriticalSlots(aUpgrades);
      final int h = ITEM_BASE_HEIGHT * slots;
      final int w = ITEM_BASE_WIDTH;
      BufferedImage image = configuration.createCompatibleImage(ITEM_BASE_WIDTH, ITEM_BASE_HEIGHT * slots, Transparency.TRANSLUCENT);
      Graphics2D g = image.createGraphics();

      RoundRectangle2D.Float rect = new RoundRectangle2D.Float(PADDING, PADDING, w - PADDING, h - PADDING, RADII, RADII);
      g.setColor(StyleManager.getBgColorFor(item));
      g.draw(rect);
      g.dispose();
      return image;
   }
}
