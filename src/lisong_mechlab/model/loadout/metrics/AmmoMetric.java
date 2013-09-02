package lisong_mechlab.model.loadout.metrics;

import java.util.TreeMap;

import lisong_mechlab.model.item.Ammunition;
import lisong_mechlab.model.item.Item;

public abstract class AmmoMetric{
   abstract public TreeMap<Ammunition, Integer> calculate();
}
