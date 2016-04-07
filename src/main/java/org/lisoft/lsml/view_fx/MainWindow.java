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
package org.lisoft.lsml.view_fx;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.export.Base64LoadoutCoder;
import org.lisoft.lsml.model.export.BatchImportExporter;
import org.lisoft.lsml.model.export.LsmlLinkProtocol;
import org.lisoft.lsml.model.export.SmurfyImportExport;
import org.lisoft.lsml.model.garage.GaragePath;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.util.CommandStack;
import org.lisoft.lsml.view_fx.util.FxmlHelpers;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * This class is the controller for the main window.
 * 
 * @author Emily Björk
 */
public class MainWindow extends StackPane {

    @FXML
    private StackPane                     block_content;
    private final CommandStack            cmdStack      = new CommandStack(100);
    private ObjectProperty<Faction>       factionFilter = new SimpleObjectProperty<>();
    @FXML
    private CheckBox                      filterClan;
    @FXML
    private CheckBox                      filterIS;
    @FXML
    private ListView<Loadout>             loadout_pills;
    @FXML
    private TreeView<GaragePath<Loadout>> loadout_tree;
    @FXML
    private Toggle                        nav_chassis;
    @FXML
    private Toggle                        nav_dropships;
    @FXML
    private ToggleGroup                   nav_group;
    @FXML
    private ToggleButton                  nav_imexport;
    @FXML
    private Toggle                        nav_loadouts;
    @FXML
    private ToggleButton                  nav_settings;
    @FXML
    private Toggle                        nav_weapons;
    @FXML
    private BorderPane                    overlayPane;
    private BorderPane                    page_chassis;
    @FXML
    private Pane                          page_dropships;
    private BorderPane                    page_imexport;
    @FXML
    private BorderPane                    page_loadouts;
    @FXML
    private ScrollPane                    page_settings;
    @FXML
    private ScrollPane                    page_weapons;
    private final Settings                settings      = Settings.getSettings();
    private final MessageXBar             xBar          = new MessageXBar();
    @FXML
    private BorderPane                    base;

    private final GlobalGarage            globalGarage  = GlobalGarage.instance;

    public MainWindow() {
        // This function will be called outside of the JavaFX thread, only do stuff that doesn't
        // require the JavaFX thread. Other work to be done in #prepareShow.
        FxmlHelpers.loadFxmlControl(this);
        setupFactionFilter();

        getChildren().remove(overlayPane);
    }

    /**
     * Selects a new garage file and creates an empty garage. Sets the {@link Settings#CORE_GARAGE_FILE} property to the
     * new file. If successful, the {@link Settings#CORE_GARAGE_FILE} property is updated.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    @FXML
    public void newGarage() throws FileNotFoundException, IOException {
        globalGarage.newGarage(getScene().getWindow());
    }

    @FXML
    public void openNewMechOverlay() {
        overlayPane.setCenter(new NewMechPane(() -> {
            getChildren().remove(overlayPane);
            overlayPane.setCenter(null);
            base.setDisable(false);
        }, xBar, settings));
        getChildren().add(overlayPane);
        base.setDisable(true);
    }

    /**
     * Opens a garage after confirming save with the user. If a new garage is loaded the
     * {@link Settings#CORE_GARAGE_FILE} property will be updated.
     * 
     * @throws IOException
     */
    @FXML
    public void openGarage() throws IOException {
        globalGarage.openGarage(getScene().getWindow());
    }

    /**
     * @param aCoder
     * @throws IOException
     * 
     */
    public void prepareShow(Base64LoadoutCoder aCoder) throws IOException {
        page_chassis = new ChassisPage(factionFilter, xBar);
        // FIXME: These really should be constructed through DI
        BatchImportExporter importer = new BatchImportExporter(aCoder, LsmlLinkProtocol.LSML,
                DefaultLoadoutErrorReporter.instance);
        SmurfyImportExport smurfyImportExport = new SmurfyImportExport(aCoder, DefaultLoadoutErrorReporter.instance);
        page_imexport = new ImportExportPage(xBar, importer, smurfyImportExport, cmdStack);
        setupNavigationBar();
        setupLoadoutPage();
        page_weapons.setContent(new WeaponsPage(factionFilter));
    }

    @FXML
    public void saveGarage() throws IOException {
        globalGarage.saveGarage();
    }

    /**
     * Will save the current garage as a new file. If successful, the {@link Settings#CORE_GARAGE_FILE} property is
     * updated.
     * 
     * @return <code>true</code> if the garage was written to a file, <code>false</code> otherwise.
     * @throws IOException
     */
    @FXML
    public boolean saveGarageAs() throws IOException {
        return globalGarage.saveGarageAs(getScene().getWindow());
    }

    private void setupFactionFilter() {
        InvalidationListener listener = (aObs) -> {
            if (filterClan.isSelected()) {
                if (filterIS.isSelected()) {
                    factionFilter.set(Faction.ANY);
                }
                else {
                    factionFilter.set(Faction.CLAN);
                }
            }
            else {
                if (filterIS.isSelected()) {
                    factionFilter.set(Faction.INNERSPHERE);
                }
                else {
                    factionFilter.set(Faction.ANY);
                }
            }
        };
        filterIS.selectedProperty().addListener(listener);
        filterClan.selectedProperty().addListener(listener);
        listener.invalidated(null);
    }

    private void setupLoadoutPage() {
        FxmlHelpers.prepareGarageTree(loadout_tree, globalGarage.getGarage().getLoadoutRoot(), xBar, cmdStack, false);
        loadout_tree.getSelectionModel().selectedItemProperty().addListener((aObservable, aOld, aNew) -> {
            if (null == aNew)
                loadout_pills.getItems().clear();
            else
                loadout_pills
                        .setItems(FXCollections.observableArrayList(aNew.getValue().getTopDirectory().getValues()));
        });
        loadout_pills.setCellFactory(aView -> new LoadoutPillCell(xBar, loadout_tree, aView));
        loadout_pills.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setupNavigationBar() {
        page_settings.setContent(new SettingsPage());

        nav_group.selectedToggleProperty().addListener((aObservable, aOld, aNew) -> {
            if (aNew == nav_loadouts) {
                block_content.getChildren().setAll(page_loadouts);
                page_loadouts.setVisible(true);
            }
            else if (aNew == nav_dropships) {
                block_content.getChildren().setAll(page_dropships);
                page_dropships.setVisible(true);
            }
            else if (aNew == nav_chassis) {
                block_content.getChildren().setAll(page_chassis);
                page_chassis.setVisible(true);
            }
            else if (aNew == nav_weapons) {
                block_content.getChildren().setAll(page_weapons);
                page_weapons.setVisible(true);
            }
            else if (aNew == nav_imexport) {
                block_content.getChildren().setAll(page_imexport);
                page_imexport.setVisible(true);
            }
            else if (aNew == nav_settings) {
                block_content.getChildren().setAll(page_settings);
                page_settings.setVisible(true);
            }
            else {
                throw new IllegalArgumentException("Unknown toggle value! " + aNew);
            }
        });
        nav_group.selectToggle(nav_loadouts);
    }

    /**
     * @return The global {@link MessageXBar}.
     */
    public MessageXBar getXBar() {
        return xBar;
    }
}
