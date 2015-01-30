package usspg31.tourney.controller.controls;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import org.xml.sax.SAXException;

import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.controller.MainMenuController;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.eventphases.EventSetupPhaseController;
import usspg31.tourney.controller.controls.eventphases.PreRegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.RegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.TournamentExecutionPhaseController;
import usspg31.tourney.controller.dialogs.PasswordDialog;
import usspg31.tourney.controller.dialogs.PdfOutputConfiguration;
import usspg31.tourney.controller.dialogs.PdfOutputSelectionDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.EventPhase;
import usspg31.tourney.model.Event.UserFlag;
import usspg31.tourney.model.filemanagement.FileLoader;
import usspg31.tourney.model.filemanagement.FileSaver;
import usspg31.tourney.model.filemanagement.pdfexport.PDFExporter;
import usspg31.tourney.model.undo.UndoManager;

public class EventPhaseViewController implements EventUser {

    private static final Logger log = Logger.getLogger(MainMenuController.class
            .getName());

    // Loaded event file
    private File loadedEventFile;

    // Menu bar
    @FXML private Button buttonClose;
    @FXML private Button buttonSave;
    @FXML private Button buttonExport;

    @FXML private Button buttonUndo;
    @FXML private Button buttonRedo;

    @FXML private Button buttonLock;
    @FXML private Button buttonOptions;

    // Breadcrumb controls
    @FXML private HBox breadcrumbContainer;
    @FXML private Button breadcrumbEventSetup;
    @FXML private Button breadcrumbPreRegistration;
    @FXML private Button breadcrumbRegistration;
    @FXML private Button breadcrumbTournamentExecution;

    // Event phases
    @FXML private StackPane eventPhaseContainer;

    private Node eventSetupPhase;
    private EventSetupPhaseController eventSetupPhaseController;

    private Node preRegistrationPhase;
    private PreRegistrationPhaseController preRegistrationPhaseController;

    private Node registrationPhase;
    private RegistrationPhaseController registrationPhaseController;

    private Node tournamentExecutionPhase;
    private TournamentExecutionPhaseController tournamentExecutionPhaseController;

    // Animations
    private DoubleProperty phasePosition;
    private Timeline currentAnimation;

    private static final Duration transitionDuration = Duration.millis(300);
    private static Interpolator transitionInterpolator = Interpolator.SPLINE(
            .4, 0, 0, 1);

    // Bread crumb effects
    private static final String breadCrumbInactive = "-fx-background-color: #888, -t-button-color;";
    private static final String breadCrumbActive = "-fx-background-color: #888, derive(-t-button-color, -7%);";

    // Event
    private Event loadedEvent;

    // UndoManager
    private UndoManager undoManager;

    // PasswordDialog
    private ModalDialog<Object, Object> passwordDialog;

    // PdfOutputDialog
    private ModalDialog<PdfOutputConfiguration, PdfOutputConfiguration> pdfOutputDialog;

    @FXML
    private void initialize() throws IOException {
        this.phasePosition = new SimpleDoubleProperty(0);

        this.loadSubViews();

        // only activate the lock button if a password is set
        this.buttonLock.disableProperty().bind(
                PreferencesManager.getInstance().passwordSet().not());

        // add all event phase views to the event phase container
        this.eventPhaseContainer.getChildren().addAll(this.eventSetupPhase,
                this.preRegistrationPhase, this.registrationPhase,
                this.tournamentExecutionPhase);

        this.passwordDialog = new PasswordDialog().modalDialog();
        this.pdfOutputDialog = new PdfOutputSelectionDialog().modalDialog();

        this.undoManager = new UndoManager();

        this.buttonUndo.disableProperty().bind(
                this.undoManager.undoAvailableProperty().not());
        this.buttonRedo.disableProperty().bind(
                this.undoManager.redoAvailableProperty().not());

        ChangeListener<Boolean> saveAvailableListener = (ov, o, n) -> {
            if (n) {
                this.buttonSave.setDisable(false);
            }
        };
        this.undoManager.undoAvailableProperty().addListener(
                saveAvailableListener);
        this.undoManager.redoAvailableProperty().addListener(
                saveAvailableListener);
    }

    private void loadSubViews() throws IOException {
        FXMLLoader eventSetupPhaseLoader = new FXMLLoader(
                this.getClass().getResource(
                        "/ui/fxml/controls/eventphases/event-setup-phase.fxml"),
                PreferencesManager.getInstance().getSelectedLanguage()
                        .getLanguageBundle());
        this.eventSetupPhase = eventSetupPhaseLoader.load();
        this.eventSetupPhaseController = eventSetupPhaseLoader.getController();
        this.eventSetupPhase.setVisible(true);

        FXMLLoader preRegistrationPhaseLoader = new FXMLLoader(
                this.getClass()
                        .getResource(
                                "/ui/fxml/controls/eventphases/pre-registration-phase.fxml"),
                PreferencesManager.getInstance().getSelectedLanguage()
                        .getLanguageBundle());
        this.preRegistrationPhase = preRegistrationPhaseLoader.load();
        this.preRegistrationPhaseController = preRegistrationPhaseLoader
                .getController();
        this.preRegistrationPhase.setVisible(true);

        FXMLLoader registrationPhaseLoader = new FXMLLoader(
                this.getClass()
                        .getResource(
                                "/ui/fxml/controls/eventphases/registration-phase.fxml"),
                PreferencesManager.getInstance().getSelectedLanguage()
                        .getLanguageBundle());
        this.registrationPhase = registrationPhaseLoader.load();
        this.registrationPhaseController = registrationPhaseLoader
                .getController();
        this.registrationPhase.setVisible(true);

        FXMLLoader tournamentExecutionPhaseLoader = new FXMLLoader(
                this.getClass()
                        .getResource(
                                "/ui/fxml/controls/eventphases/tournament-execution-phase.fxml"),
                PreferencesManager.getInstance().getSelectedLanguage()
                        .getLanguageBundle());
        this.tournamentExecutionPhase = tournamentExecutionPhaseLoader.load();
        this.tournamentExecutionPhaseController = tournamentExecutionPhaseLoader
                .getController();
        this.tournamentExecutionPhase.setVisible(true);

        // bind the phase view's translateX property to the phasePosition
        // property, so the pages scroll all together when the phasePosition
        // gets changed. (e.g. phasePosition == 1 -> show phase 2)

        this.eventSetupPhase.translateXProperty().bind(
                this.eventPhaseContainer
                        .widthProperty()
                        .multiply(0)
                        .subtract(
                                this.eventPhaseContainer.widthProperty()
                                        .multiply(this.phasePosition)));

        this.preRegistrationPhase.translateXProperty().bind(
                this.eventPhaseContainer
                        .widthProperty()
                        .multiply(1)
                        .subtract(
                                this.eventPhaseContainer.widthProperty()
                                        .multiply(this.phasePosition)));

        this.registrationPhase.translateXProperty().bind(
                this.eventPhaseContainer
                        .widthProperty()
                        .multiply(2)
                        .subtract(
                                this.eventPhaseContainer.widthProperty()
                                        .multiply(this.phasePosition)));

        this.tournamentExecutionPhase.translateXProperty().bind(
                this.eventPhaseContainer
                        .widthProperty()
                        .multiply(3)
                        .subtract(
                                this.eventPhaseContainer.widthProperty()
                                        .multiply(this.phasePosition)));
    }

    private void slideToPhase(int phaseNumber) {
        if (this.currentAnimation != null) {
            this.currentAnimation.stop();
        }
        this.currentAnimation = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(this.phasePosition, this.phasePosition.get())),
                new KeyFrame(transitionDuration,
                        new KeyValue(this.phasePosition, phaseNumber,
                                transitionInterpolator)));
        this.currentAnimation.play();
    }

    public UndoManager getUndoManager() {
        return this.undoManager;
    }

    public File getLoadedEventFile() {
        return this.loadedEventFile;
    }

    public void setLoadedEventFile(File loadedEventFile) {
        this.loadedEventFile = loadedEventFile;
        this.registrationPhaseController.setLoadedEventFile(loadedEventFile);
    }

    @Override
    public void loadEvent(Event event) {
        if (this.loadedEvent != null) {
            this.unloadEvent();
        }

        if (event.getUserFlag() == UserFlag.ADMINISTRATION) {
            /*
             * Load the event as an administrator
             */
            this.eventSetupPhaseController.loadEvent(event);
            this.preRegistrationPhaseController.loadEvent(event);
            this.registrationPhaseController.loadEvent(event);
            this.tournamentExecutionPhaseController.loadEvent(event);

            this.loadedEvent = event;
            switch (this.loadedEvent.getEventPhase()) {
            case EVENT_SETUP:
                this.breadcrumbEventSetup
                        .setStyle(EventPhaseViewController.breadCrumbActive);
                this.phasePosition.set(0);
                break;
            case PRE_REGISTRATION:
                this.breadcrumbPreRegistration
                        .setStyle(EventPhaseViewController.breadCrumbActive);
                this.phasePosition.set(1);
                break;
            case REGISTRATION:
                this.breadcrumbRegistration
                        .setStyle(EventPhaseViewController.breadCrumbActive);
                this.phasePosition.set(2);
                break;
            case TOURNAMENT_EXECUTION:
                this.breadcrumbTournamentExecution
                        .setStyle(EventPhaseViewController.breadCrumbActive);
                this.phasePosition.set(3);
                break;
            }
        } else if (event.getUserFlag() == UserFlag.REGISTRATION) {
            this.loadedEvent = event;
            this.registrationPhaseController.loadEvent(event);
            this.breadcrumbRegistration
                    .setStyle(EventPhaseViewController.breadCrumbInactive);

            this.phasePosition.set(2);
            this.breadcrumbEventSetup.setDisable(true);
            this.breadcrumbPreRegistration.setDisable(true);
            this.breadcrumbTournamentExecution.setDisable(true);

            this.registrationPhaseController.chooseRegistratorNumber(this);
        } else if (event.getUserFlag() == UserFlag.TOURNAMENT_EXECUTION) {
            this.loadedEvent = event;
            this.tournamentExecutionPhaseController.loadEvent(event);
            this.breadcrumbTournamentExecution
                    .setStyle(EventPhaseViewController.breadCrumbInactive);

            this.phasePosition.set(3);

            this.tournamentExecutionPhaseController
                    .showTournamentExecutionView(this.loadedEvent
                            .getExecutedTournament());

            this.breadcrumbEventSetup.setDisable(true);
            this.breadcrumbPreRegistration.setDisable(true);
            this.breadcrumbRegistration.setDisable(true);
        }

        // register undo properties
        UndoManager undo = MainWindow.getInstance()
                .getEventPhaseViewController().getUndoManager();
        undo.registerUndoProperty(this.loadedEvent.getRegisteredPlayers());
    }

    @Override
    public void unloadEvent() {
        /*
         * Clean up if some values are still set from a previously opened event
         */
        this.breadcrumbEventSetup
                .setStyle(EventPhaseViewController.breadCrumbInactive);
        this.breadcrumbEventSetup.setDisable(false);
        this.breadcrumbPreRegistration
                .setStyle(EventPhaseViewController.breadCrumbInactive);
        this.breadcrumbPreRegistration.setDisable(false);
        this.breadcrumbRegistration
                .setStyle(EventPhaseViewController.breadCrumbInactive);
        this.breadcrumbRegistration.setDisable(false);
        this.breadcrumbTournamentExecution
                .setStyle(EventPhaseViewController.breadCrumbInactive);
        this.breadcrumbTournamentExecution.setDisable(false);

        /* clear the undo manager's history */
        this.undoManager.clearHistory();

        /* Unload the event from the specific phases */
        this.eventSetupPhaseController.unloadEvent();
        this.preRegistrationPhaseController.unloadEvent();
        this.registrationPhaseController.unloadEvent();

        // unregister undo properties
        UndoManager undo = MainWindow.getInstance()
                .getEventPhaseViewController().getUndoManager();
        undo.unregisterUndoProperty(this.loadedEvent.getRegisteredPlayers());

        undo.clearHistory();

        this.loadedEvent = null;
    }

    public DialogResult saveEvent() {
        if (this.getLoadedEventFile() == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(PreferencesManager.getInstance()
                    .localizeString("eventphaseview.saveevent.title"));
            fileChooser.getExtensionFilters().add(
                    new ExtensionFilter(PreferencesManager.getInstance()
                            .localizeString("dialogs.extensions.eventfile"),
                            "*.tef"));
            File selectedFile = fileChooser.showSaveDialog(EntryPoint
                    .getPrimaryStage());
            if (selectedFile == null) {
                return DialogResult.CANCEL;
            }
            if (!selectedFile.getName().endsWith(".tef")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".tef");
            }
            this.setLoadedEventFile(selectedFile);
        }

        try {
            FileSaver.saveEventToFile(this.loadedEvent, this
                    .getLoadedEventFile().getAbsolutePath());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not save the event.", e);

            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "dialogs.messages.couldnotsave")).modalDialog()
                    .title("dialogs.titles.error").show();

            return DialogResult.CANCEL;
        }

        return DialogResult.OK;
    }

    @FXML
    private void onButtonCloseClicked(ActionEvent event) {
        log.fine("Close Button was clicked");

        if (this.undoManager == null) {
            this.unloadEvent();
            MainWindow.getInstance().slideDown(
                    MainWindow.getInstance().getMainMenu());
            return;
        }

        if (this.undoManager.undoAvailable()) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "dialogs.messages.unsavedchanges"))
                    .modalDialog()
                    .title("dialogs.titles.warning")
                    .dialogButtons(DialogButtons.YES_NO_CANCEL)
                    .onResult((result, returnValue) -> {
                        switch (result) {
                        case YES:
                            this.saveEvent();
                        case NO: // fall-through: both yes and no will close the
                            // event
                            this.unloadEvent();
                            MainWindow.getInstance().slideDown(
                                    MainWindow.getInstance().getMainMenu());
                        default: // do nothing
                        }
                    }).show();
        } else {
            this.unloadEvent();
            MainWindow.getInstance().slideDown(
                    MainWindow.getInstance().getMainMenu());
        }
    }

    @FXML
    private void onButtonUndoClicked(ActionEvent event) {
        log.fine("Undo Button was clicked");
        this.undoManager.undo();
    }

    @FXML
    private void onButtonRedoClicked(ActionEvent event) {
        log.fine("Redo Button was clicked");
        this.undoManager.redo();
    }

    public void activateSaveButton() {
        this.buttonSave.setDisable(false);
    }

    public boolean saveAvailable() {
        return !this.buttonSave.isDisabled();
    }

    public boolean hasLoadedEvent() {
        return this.loadedEvent != null;
    }

    @FXML
    private void onButtonSaveClicked(ActionEvent event) {
        log.fine("Save Button was clicked");
        this.saveEvent();
        this.buttonSave.setDisable(true);
    }

    @FXML
    private void onButtonExportClicked(ActionEvent event) {
        log.fine("Export Button was clicked");

        PdfOutputConfiguration configuration = new PdfOutputConfiguration();
        configuration.exportPlayerList(true);
        configuration.exportTournaments(true);
        configuration.setTournaments(this.loadedEvent.getTournaments());
        this.pdfOutputDialog
                .properties(configuration)
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK) {
                                FileChooser fileChooser = new FileChooser();
                                fileChooser
                                        .setTitle(PreferencesManager
                                                .getInstance()
                                                .localizeString(
                                                        "eventphaseview.savepdf.title"));
                                fileChooser
                                        .getExtensionFilters()
                                        .add(new ExtensionFilter(
                                                PreferencesManager
                                                        .getInstance()
                                                        .localizeString(
                                                                "dialogs.extensions.pdffile"),
                                                "*.pdf"));
                                File selectedFile = fileChooser
                                        .showSaveDialog(EntryPoint
                                                .getPrimaryStage());
                                if (selectedFile == null) {
                                    return;
                                }
                                if (!selectedFile.getName().endsWith(".pdf")) {
                                    selectedFile = new File(selectedFile
                                            .getAbsolutePath() + ".pdf");
                                }

                                try {
                                    PDFExporter.exportEventAsPdf(
                                            this.loadedEvent,
                                            selectedFile.getAbsolutePath(),
                                            returnValue);
                                } catch (Exception e) {
                                    log.log(Level.SEVERE,
                                            "Could not export the event.", e);

                                    new SimpleDialog<>(
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "dialogs.messages.couldnotsave"))
                                            .modalDialog()
                                            .title("dialogs.titles.error")
                                            .dialogButtons(DialogButtons.OK)
                                            .show();
                                    return;
                                }
                            }
                        }).show();
    }

    @FXML
    private void onButtonLockClicked(ActionEvent event) {
        log.fine("Lock Button was clicked");

        EntryPoint.lockApplication();
        this.passwordDialog.onResult((result, value) -> {
            EntryPoint.unlockApplication();
        }).show();
    }

    @FXML
    private void onButtonOptionsClicked(ActionEvent event) {
        log.fine("Options Button was clicked");
        MainWindow mainWindow = MainWindow.getInstance();
        mainWindow.getOptionsViewController().setExitProperties(
                "settings.returnto.eventview",
                "settings.returnto.eventview.description",
                "settings.returnto.eventview.explanation", () -> {
                    mainWindow.slideDown(mainWindow.getEventPhaseView());
                });
        mainWindow.slideUp(mainWindow.getOptionsView());
    }

    @FXML
    private void onBreadcrumbEventSetupClicked(ActionEvent event) {
        log.fine("Event Setup Breadcrumb was clicked");
        if (this.loadedEvent.getEventPhase() == EventPhase.TOURNAMENT_EXECUTION) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "eventphaseview.warnings.tournamentexecution")
                    + "\n\n"
                    + PreferencesManager.getInstance().localizeString(
                            "eventphaseview.warnings.confirm.message"))
                    .modalDialog().title("dialogs.titles.warning")
                    .dialogButtons(DialogButtons.YES_NO)
                    .onResult((result, returnValue) -> {
                        if (result == DialogResult.YES) {
                            this.switchToEventPhase(EventPhase.EVENT_SETUP);
                        }
                    }).show();
        } else if (this.loadedEvent.getEventPhase() != EventPhase.EVENT_SETUP) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "eventphaseview.warnings.eventsetup")
                    + "\n\n"
                    + PreferencesManager.getInstance().localizeString(
                            "eventphaseview.warnings.confirm.message"))
                    .modalDialog().title("dialogs.titles.warning")
                    .dialogButtons(DialogButtons.YES_NO)
                    .onResult((result, returnValue) -> {
                        if (result == DialogResult.YES) {
                            this.switchToEventPhase(EventPhase.EVENT_SETUP);
                        }
                    }).show();
        } else {
            this.switchToEventPhase(EventPhase.EVENT_SETUP);
        }
    }

    @FXML
    private void onBreadcrumbPreRegistrationClicked(ActionEvent event) {
        log.fine("Pre Registration Breadcrumb was clicked");

        if (this.loadedEvent.getEventPhase() == EventPhase.REGISTRATION) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "eventphaseview.warnings.preregistration")
                    + "\n\n"
                    + PreferencesManager.getInstance().localizeString(
                            "eventphaseview.warnings.confirm.message"))
                    .modalDialog()
                    .title("dialogs.titles.warning")
                    .dialogButtons(DialogButtons.YES_NO)
                    .onResult(
                            (result, returnValue) -> {
                                if (result == DialogResult.YES) {
                                    this.switchToEventPhase(EventPhase.PRE_REGISTRATION);
                                }
                            }).show();
        } else if (this.loadedEvent.getEventPhase() == EventPhase.TOURNAMENT_EXECUTION) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "eventphaseview.warnings.tournamentexecution")
                    + "\n\n"
                    + PreferencesManager.getInstance().localizeString(
                            "eventphaseview.warnings.confirm.message"))
                    .modalDialog()
                    .title("dialogs.titles.warning")
                    .dialogButtons(DialogButtons.YES_NO)
                    .onResult(
                            (result, returnValue) -> {
                                if (result == DialogResult.YES) {
                                    this.switchToEventPhase(EventPhase.PRE_REGISTRATION);
                                }
                            }).show();
        } else {
            this.switchToEventPhase(EventPhase.PRE_REGISTRATION);
        }
    }

    @FXML
    private void onBreadcrumbRegistrationClicked(ActionEvent event) {
        log.fine("Registration Breadcrumb was clicked");

        if (this.loadedEvent.getEventPhase() == EventPhase.TOURNAMENT_EXECUTION) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "eventphaseview.warnings.tournamentexecution")
                    + "\n\n"
                    + PreferencesManager.getInstance().localizeString(
                            "eventphaseview.warnings.confirm.message"))
                    .modalDialog().title("dialogs.titles.warning")
                    .dialogButtons(DialogButtons.YES_NO)
                    .onResult((result, returnValue) -> {
                        if (result == DialogResult.YES) {
                            this.switchToEventPhase(EventPhase.REGISTRATION);
                        }
                    }).show();
        } else {
            this.switchToEventPhase(EventPhase.REGISTRATION);
        }
    }

    @FXML
    private void onBreadcrumbTournamentExecutionClicked(ActionEvent event) {
        log.fine("Tournament Execution Breadcrumb was clicked");
        this.switchToEventPhase(EventPhase.TOURNAMENT_EXECUTION);
    }

    private void switchToEventPhase(EventPhase phase) {
        // don't do anything if we already are in the given phase
        if (phase == this.loadedEvent.getEventPhase()) {
            return;
        }

        this.saveAvailable();

        // clear the history of the undoManager
        this.undoManager.clearHistory();

        switch (phase) {
        case EVENT_SETUP:
            this.slideToPhase(0);
            this.loadedEvent.setEventPhase(Event.EventPhase.EVENT_SETUP);

            this.breadcrumbEventSetup
                    .setStyle(EventPhaseViewController.breadCrumbActive);
            this.breadcrumbPreRegistration
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            this.breadcrumbRegistration
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            this.breadcrumbTournamentExecution
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            break;
        case PRE_REGISTRATION:
            this.slideToPhase(1);
            this.loadedEvent.setEventPhase(Event.EventPhase.PRE_REGISTRATION);

            this.breadcrumbEventSetup
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            this.breadcrumbPreRegistration
                    .setStyle(EventPhaseViewController.breadCrumbActive);
            this.breadcrumbRegistration
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            this.breadcrumbTournamentExecution
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            break;
        case REGISTRATION:
            this.slideToPhase(2);
            this.loadedEvent.setEventPhase(Event.EventPhase.REGISTRATION);

            this.breadcrumbRegistration
                    .setStyle("-fx-background-color: #888, derive(-t-button-color, -20%);");

            this.breadcrumbEventSetup
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            this.breadcrumbPreRegistration
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            if (this.loadedEvent.getUserFlag() != UserFlag.REGISTRATION) {
                this.breadcrumbRegistration
                        .setStyle(EventPhaseViewController.breadCrumbActive);
            }
            this.breadcrumbTournamentExecution
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            break;
        case TOURNAMENT_EXECUTION:
            this.slideToPhase(3);
            this.loadedEvent
                    .setEventPhase(Event.EventPhase.TOURNAMENT_EXECUTION);

            File tempEvent = new File(System.getProperty("user.dir")
                    + "/tmp.tef");
            FileSaver.saveEventToFile(this.loadedEvent,
                    tempEvent.getAbsolutePath());
            try {
                this.loadEvent(FileLoader.loadEventFromFile(tempEvent
                        .getAbsolutePath()));
            } catch (IOException | SAXException e) {
                log.log(Level.SEVERE, "Could not save the event.", e);
            }
            tempEvent.delete();

            this.breadcrumbEventSetup
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            this.breadcrumbPreRegistration
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            this.breadcrumbRegistration
                    .setStyle(EventPhaseViewController.breadCrumbInactive);
            if (this.loadedEvent.getUserFlag() != UserFlag.TOURNAMENT_EXECUTION) {
                this.breadcrumbTournamentExecution
                        .setStyle(EventPhaseViewController.breadCrumbActive);
            }
            break;
        }
    }
}
