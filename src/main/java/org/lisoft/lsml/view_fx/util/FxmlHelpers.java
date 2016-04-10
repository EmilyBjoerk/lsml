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
package org.lisoft.lsml.view_fx.util;

import static javafx.beans.binding.Bindings.equal;
import static javafx.beans.binding.Bindings.when;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.NamedObject;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.chassi.ChassisStandard;
import org.lisoft.lsml.model.chassi.HardPointType;
import org.lisoft.lsml.model.chassi.Location;
import org.lisoft.lsml.model.garage.GarageDirectory;
import org.lisoft.lsml.model.garage.GaragePath;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponent;
import org.lisoft.lsml.model.metrics.TopSpeed;
import org.lisoft.lsml.util.CommandStack;
import org.lisoft.lsml.view_fx.GarageTreeCell;
import org.lisoft.lsml.view_fx.GarageTreeItem;
import org.lisoft.lsml.view_fx.loadout.component.HardPointPane;
import org.lisoft.lsml.view_fx.loadout.equipment.EquipmentCategory;
import org.lisoft.lsml.view_fx.style.StyleManager;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Assorted helper methods for dealing with FXML.
 * 
 * @author Emily Björk
 */
public class FxmlHelpers {
    public static final DecimalFormat MASS_FMT = new DecimalFormat("#.## t");
    public static final DecimalFormat SPEED_FMT = new DecimalFormat("#.# km/h");
    public static final DecimalFormat DF2 = new DecimalFormat("#.##");

    public static final Comparator<String> NUMERICAL_ORDERING;

    static {
        NUMERICAL_ORDERING = new Comparator<String>() {
            Pattern p = Pattern.compile("((?:\\d+)?[.,]?\\d*).*");

            @Override
            public int compare(String aLHS, String aRHS) {
                Matcher matcherLHS = p.matcher(aLHS);
                Matcher matcherRHS = p.matcher(aRHS);
                if (matcherLHS.matches() && matcherRHS.matches()) {
                    String lhsValStr = matcherLHS.group(1);
                    String rhsValStr = matcherRHS.group(1);
                    if (!lhsValStr.isEmpty() && !rhsValStr.isEmpty()) {
                        double lhsVal = Double.parseDouble(lhsValStr.replace(',', '.'));
                        double rhsVal = Double.parseDouble(rhsValStr.replace(',', '.'));
                        return Double.compare(lhsVal, rhsVal);
                    }
                }
                return String.CASE_INSENSITIVE_ORDER.compare(aLHS, aRHS);
            }
        };
    }

    public static void loadFxmlControl(final Object aControl) {
        final String fxmlFile = "view/" + aControl.getClass().getSimpleName() + ".fxml";
        URL fxmlResource = ClassLoader.getSystemClassLoader().getResource(fxmlFile);
        if (null == fxmlResource) {
            throw new IllegalArgumentException("Unable to load FXML file: " + fxmlFile);
        }
        final FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
        fxmlLoader.setControllerFactory((aClass) -> aControl);
        fxmlLoader.setRoot(aControl);
        try {
            fxmlLoader.load();
        }
        catch (final IOException e) {
            // Failure to load XML is a program error and cannot be recovered from, promote to unchecked.
            throw new RuntimeException(e);
        }
    }

    public static void createStage(final Stage aStage, final Parent aRoot) {
        aStage.initStyle(StageStyle.TRANSPARENT);
        aStage.getIcons().add(new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("icon.png")));
        Scene scene = new Scene(aRoot);
        scene.setFill(Color.TRANSPARENT);
        aStage.setScene(scene);
        aStage.sizeToScene();
        aStage.show();
        aStage.toFront();

        final Orientation bias = aRoot.getContentBias();
        final double minWidth;
        final double minHeight;
        if (bias == Orientation.VERTICAL) {
            minHeight = aRoot.minHeight(-1);
            minWidth = aRoot.minWidth(minHeight);
        }
        else {
            minWidth = aRoot.minWidth(-1);
            minHeight = aRoot.minHeight(minWidth);
        }

        aStage.setMinWidth(minWidth);
        aStage.setMinHeight(minHeight);
    }

    public static void setToggleText(ToggleButton aButton, String aSelected, String aUnSelected) {
        StringBinding textBinding = Bindings.when(aButton.selectedProperty()).then(aSelected).otherwise(aUnSelected);
        aButton.textProperty().bind(textBinding);
    }

    /**
     * @param aCheckBox
     * @param aBooleanExpression
     * @param aSuccess
     */
    public static void bindTogglable(final CheckBox aCheckBox, final BooleanExpression aBooleanExpression,
            final Predicate<Boolean> aSuccess) {
        aCheckBox.setSelected(aBooleanExpression.get());
        aBooleanExpression.addListener((aObservable, aOld, aNew) -> {
            aCheckBox.setSelected(aNew);
        });

        aCheckBox.setOnAction(e -> {
            final boolean value = aCheckBox.isSelected();
            final boolean oldValue = aBooleanExpression.get();
            if (value != oldValue && !aSuccess.test(value)) {
                aCheckBox.setSelected(oldValue);
            }
        });
    }

    public static void bindTogglable(final ToggleButton aCheckBox, final BooleanExpression aBooleanExpression,
            final Predicate<Boolean> aSuccess) {
        aCheckBox.setSelected(aBooleanExpression.get());
        aBooleanExpression.addListener((aObservable, aOld, aNew) -> {
            aCheckBox.setSelected(aNew);
        });

        aCheckBox.setOnAction(e -> {
            final boolean value = aCheckBox.isSelected();
            final boolean oldValue = aBooleanExpression.get();
            if (value != oldValue && !aSuccess.test(value)) {
                aCheckBox.setSelected(oldValue);
            }
        });
    }

    @Deprecated // Use GaragePath
    public static String getTreePath(final TreeItem<?> aNode) {
        StringBuilder sb = new StringBuilder();
        TreeItem<?> node = aNode;
        do {
            sb.insert(0, "/").insert(1, node.getValue().toString());
            node = node.getParent();
        } while (node != null);
        return sb.toString();
    }

    @Deprecated // Use GaragePath
    public static <T> Optional<TreeItem<T>> resolveTreePath(final TreeItem<T> aRoot, final String aPath) {
        // FIXME: Clean this mess up and extract "/" to a constant and do proper escaping!
        List<String> pathParts = Arrays.asList(aPath.split("/"));

        final Iterator<String> pathIt = pathParts.iterator();
        if (!pathIt.hasNext() || !pathIt.next().equals(aRoot.getValue().toString())) {
            return Optional.empty();
        }

        TreeItem<T> node = aRoot;
        while (pathIt.hasNext()) {
            final String pathComponent = pathIt.next();
            boolean foundChild = false;
            for (final TreeItem<T> child : node.getChildren()) {
                if (child.getValue().toString().equals(pathComponent)) {
                    node = child;
                    foundChild = true;
                    break;
                }
            }
            if (!foundChild) {
                return Optional.empty();
            }
        }
        return Optional.of(node);
    }

    public static <T> void addPropertyColumn(TableView<T> aTable, String aName, String aStat) {
        TableColumn<T, String> col = makePropertyColumn(aName, aStat);
        aTable.getColumns().add(col);
    }

    public static <T> TableColumn<T, String> makePropertyColumn(String aName, String aProperty) {
        TableColumn<T, String> col = new TableColumn<>(aName);
        col.setCellValueFactory(new PropertyValueFactory<>(aProperty));
        col.setComparator(NUMERICAL_ORDERING);
        return col;
    }

    public static void resizeTableToFit(TableView<?> aTableView) {
        double width = 0.0;
        for (TableColumn<?, ?> col : aTableView.getColumns()) {
            width += col.getWidth();
        }

        final double tableWidth = aTableView.getWidth();
        if (tableWidth > width) {
            aTableView.setPrefWidth(width);
        }
    }

    public static void addStatColumn(TableView<Weapon> aTable, String aName, String aStat) {
        TableColumn<Weapon, String> col = new TableColumn<>(aName);
        col.setCellValueFactory(aFeatures -> {
            return formatDouble(aFeatures.getValue().getStat(aStat, null));
        });
        col.setComparator(NUMERICAL_ORDERING);
        aTable.getColumns().add(col);
    }

    public static <T> void addAttributeColumn(TableView<T> aTable, String aName, String aStat) {
        TableColumn<T, String> col = makeAttributeColumn(aName, aStat);
        aTable.getColumns().add(col);
    }

    public static <T> TableColumn<T, String> makeAttributeColumn(String aName, String aStat) {
        TableColumn<T, String> col = new TableColumn<>(aName);
        col.setCellValueFactory(aFeatures -> {
            Object obj = aFeatures.getValue();
            String[] bits = aStat.split("\\.");

            for (String bit : bits) {
                String methodName = "get" + Character.toUpperCase(bit.charAt(0)) + bit.substring(1);

                boolean found = false;
                for (Method method : obj.getClass().getMethods()) {
                    if (method.getName().equals(methodName)) {
                        try {
                            obj = method.invoke(obj, new Object[method.getParameterCount()]);
                            found = true;
                            break;
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (!found) {
                    throw new RuntimeException("Couldn't find property: " + aStat);
                }
            }

            if (obj instanceof Number)
                return formatDouble(((Number) obj).doubleValue());
            return new ReadOnlyStringWrapper(obj.toString());
        });
        col.setComparator(NUMERICAL_ORDERING);
        return col;
    }

    public static void addTopSpeedColumn(TableView<Loadout> aTable) {
        TableColumn<Loadout, String> col = new TableColumn<>("Speed");
        col.setCellValueFactory(aFeatures -> {
            Loadout loadout = aFeatures.getValue();
            Chassis chassis = loadout.getChassis();
            final int rating;
            if (chassis instanceof ChassisStandard) {
                ChassisStandard chassisStandard = (ChassisStandard) chassis;
                rating = chassisStandard.getEngineMax();

            }
            else {
                rating = loadout.getEngine().getRating();
            }

            double speed = TopSpeed.calculate(rating, loadout.getMovementProfile(), chassis.getMassMax(),
                    loadout.getModifiers());
            return new ReadOnlyStringWrapper(FxmlHelpers.SPEED_FMT.format(speed));
        });
        col.setComparator(FxmlHelpers.NUMERICAL_ORDERING);
        aTable.getColumns().add(col);
    }

    public static void addTotalHardpointsColumn(ObservableList<TableColumn<Loadout, ?>> aColumns,
            HardPointType aHardPointType) {
        TableColumn<Loadout, Integer> col = new TableColumn<>(aHardPointType.shortName());
        col.setCellValueFactory(
                aFeatures -> new ReadOnlyObjectWrapper<>(aFeatures.getValue().getHardpointsCount(aHardPointType)));
        col.setCellFactory(aView -> new TableCell<Loadout, Integer>() {
            @Override
            protected void updateItem(Integer aObject, boolean aEmpty) {
                if (null != aObject && !aEmpty) {
                    Label l = new Label(aObject.toString());
                    l.getStyleClass().add(StyleManager.CLASS_HARDPOINT);
                    StyleManager.changeStyle(l, EquipmentCategory.classify(aHardPointType));
                    setGraphic(l);
                }
                else {
                    setGraphic(null);
                }
                setText(null);
            }
        });
        aColumns.add(col);
    }

    /**
     * Creates an {@link ObjectBinding} of {@link Faction} type from two boolean expressions for either
     * {@link Faction#CLAN} or {@link Faction#INNERSPHERE}.
     * 
     * @param filterClan
     *            A {@link BooleanExpression} that is true if clan should be included.
     * @param filterInnerSphere
     *            A {@link BooleanExpression} that is true if inner sphere should be included.
     * @return A new {@link ObjectBinding} of {@link Faction}.
     */
    public static ObjectBinding<Faction> createFactionBinding(BooleanExpression filterClan,
            BooleanExpression filterInnerSphere) {

        return when(equal(filterClan, filterInnerSphere)).then(Faction.ANY)
                .otherwise(when(filterClan).then(Faction.CLAN).otherwise(Faction.INNERSPHERE));
    }

    public static void addHardpointsColumn(TableView<Loadout> aTable, Location aLocation) {
        TableColumn<Loadout, ConfiguredComponent> col = new TableColumn<>(aLocation.shortName());

        col.setCellValueFactory(aFeatures -> new ReadOnlyObjectWrapper<>(aFeatures.getValue().getComponent(aLocation)));

        col.setCellFactory(aView -> new TableCell<Loadout, ConfiguredComponent>() {
            @Override
            protected void updateItem(ConfiguredComponent aObject, boolean aEmpty) {
                setText(null);
                if (null != aObject && !aEmpty) {
                    setGraphic(new HardPointPane(aObject));
                }
                else {
                    setGraphic(null);
                }
            }
        });
        col.setSortable(false);

        aTable.getColumns().add(col);
    }

    public static ReadOnlyStringWrapper formatDouble(double aValue) {
        if (0 == aValue)
            return new ReadOnlyStringWrapper("-");
        return new ReadOnlyStringWrapper(DF2.format(aValue));
    }

    public static <T extends NamedObject> void prepareGarageTree(TreeView<GaragePath<T>> aTreeView,
            GarageDirectory<T> aRoot, MessageXBar aXBar, CommandStack aStack, boolean aShowValues) {
        aTreeView.setRoot(new GarageTreeItem<>(aXBar, new GaragePath<T>(null, aRoot), aShowValues));
        aTreeView.getRoot().setExpanded(true);
        aTreeView.setShowRoot(true);
        aTreeView.setCellFactory(aView -> new GarageTreeCell<>(aXBar, aStack, aTreeView));
        aTreeView.setEditable(true);
    }

    /**
     * @return The URI path to the base style sheet.
     */
    public static String getBaseStyleSheet() {
        return "view/BaseStyle.css";
    }

    /**
     * @return The URI path to the loadout style sheet.
     */
    public static String getLoadoutStyleSheet() {
        return "view/LoadoutStyle.css";
    }
}
