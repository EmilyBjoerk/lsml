package lisong_mechlab.model.loadout.metrics;

import java.util.TreeMap;

import lisong_mechlab.model.item.Item;

public abstract class TableMetric{
   abstract public TreeMap<?, ?> calculate();
}