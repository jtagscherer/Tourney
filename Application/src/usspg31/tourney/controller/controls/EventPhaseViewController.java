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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.controller.MainMenuController;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.eventphases.EventSetupPhaseController;
import usspg31.tourney.controller.controls.eventphases.PreRegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.RegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.TournamentExecutionPhaseController;
import usspg31.tourney.controller.dialogs.PasswordDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.UserFlag;
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

    private ColorAdjust highlightedBreadcrumbEffect;

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

    // Event
    private Event loadedEvent;

    // UndoManager
    private UndoManager activeUndoManager;

    // PasswordDialog
    private ModalDialog<Object, Object> passwordDialog;

    @FXML
    private void initialize() throws IOException {
        this.phasePosition = new SimpleDoubleProperty(0);

        this.loadSubViews();
        this.initBreadcrumbs();

        // only activate the lock button if a password is set
        this.buttonLock.disableProperty().bind(
                PreferencesManager.getInstance().passwordSet().not());

        // add all event phase views to the event phase container
        this.eventPhaseContainer.getChildren().addAll(this.eventSetupPhase,
                this.preRegistrationPhase, this.registrationPhase,
                this.tournamentExecutionPhase);

        this.passwordDialog = new PasswordDialog().modalDialog();
    }

    private void initBreadcrumbs() {
        this.highlightedBreadcrumbEffect = new ColorAdjust();
        this.highlightedBreadcrumbEffect.setBrightness(-0.05);
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

    public void setActiveUndoManager(UndoManager undoManager) {
        this.activeUndoManager = undoManager;
        this.buttonUndo.disableProperty().bind(
                undoManager.undoAvailableProperty().not());
        this.buttonRedo.disableProperty().bind(
                undoManager.redoAvailableProperty().not());

        this.buttonSave.disableProperty().bind(
                undoManager.undoAvailableProperty().not());
    }

    public UndoManager getActiveUndoManager() {
        return this.activeUndoManager;
    }

    public void unsetUndoManager() {
        this.activeUndoManager = null;
        this.buttonUndo.disableProperty().unbind();
        this.buttonUndo.disableProperty().set(true);
        this.buttonRedo.disableProperty().unbind();
        this.buttonRedo.disableProperty().set(true);
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

            /*
             * Clean up if some values are still set from a previously opened
             * event
             */
            this.breadcrumbEventSetup.setEffect(null);
            this.breadcrumbEventSetup.setDisable(false);
            this.breadcrumbPreRegistration.setEffect(null);
            this.breadcrumbPreRegistration.setDisable(false);
            this.breadcrumbRegistration.setEffect(null);
            this.breadcrumbRegistration.setDisable(false);
            this.breadcrumbTournamentExecution.setEffect(null);
            this.breadcrumbTournamentExecution.setDisable(false);

            this.loadedEvent = event;
            switch (this.loadedEvent.getEventPhase()) {
            case EVENT_SETUP:
                this.breadcrumbEventSetup
                        .setEffect(this.highlightedBreadcrumbEffect);
                this.phasePosition.set(0);
                break;
            case PRE_REGISTRATION:
                this.breadcrumbPreRegistration
                        .setEffect(this.highlightedBreadcrumbEffect);
                this.phasePosition.set(1);
                break;
            case REGISTRATION:
                this.breadcrumbRegistration
                        .setEffect(this.highlightedBreadcrumbEffect);
                this.phasePosition.set(2);
                break;
            case TOURNAMENT_EXECUTION:
                this.breadcrumbTournamentExecution
                        .setEffect(this.highlightedBreadcrumbEffect);
                this.phasePosition.set(3);
                break;
            }
        } else if (event.getUserFlag() == UserFlag.REGISTRATION) {
            this.loadedEvent = event;
            this.registrationPhaseController.loadEvent(event);
            this.breadcrumbRegistration.setEffect(null);

            // TODO: Remove these lines after the undo manager works in this
            // view
            this.buttonSave.disableProperty().unbind();
            this.buttonSave.setDisable(false);

            this.phasePosition.set(2);
            this.breadcrumbEventSetup.setDisable(true);
            this.breadcrumbPreRegistration.setDisable(true);
            this.breadcrumbTournamentExecution.setDisable(true);

            this.registrationPhaseController.chooseRegistratorNumber(this);
        } else if (event.getUserFlag() == UserFlag.TOURNAMENT_EXECUTION) {
            this.loadedEvent = event;
            this.tournamentExecutionPhaseController.loadEvent(event);
            this.breadcrumbTournamentExecution.setEffect(null);

            // TODO: Remove these lines after the undo manager works in this
            // view
            this.buttonSave.disableProperty().unbind();
            this.buttonSave.setDisable(false);

            this.phasePosition.set(3);

            this.tournamentExecutionPhaseController
                    .showTournamentExecutionView(this.loadedEvent
                            .getExecutedTournament());

            this.breadcrumbEventSetup.setDisable(true);
            this.breadcrumbPreRegistration.setDisable(true);
            this.breadcrumbRegistration.setDisable(true);
        }
    }

    @Override
    public void unloadEvent() {
        // TODO unload any registered listeners on the event
        this.eventSetupPhaseController.unloadEvent();
        this.preRegistrationPhaseController.unloadEvent();
        this.registrationPhaseController.unloadEvent();

        this.loadedEvent = null;
    }

    public DialogResult saveEvent() {
        if (this.getLoadedEventFile() == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Eventdatei speichern");
            fileChooser.getExtensionFilters().add(
                    new ExtensionFilter("Tourney Eventdatei (*.tef)", "*.tef"));
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

            new SimpleDialog<>("Das Event konnte nicht gespeichert werden.\n"
                    + "Bitte stellen Sie sicher, dass Sie für die Zieldatei "
                    + "alle Berechtigungen besitzen.").modalDialog()
                    .title("Fehler").show();

            return DialogResult.CANCEL;
        }

        return DialogResult.OK;
    }

    @FXML
    private void onButtonCloseClicked(ActionEvent event) {
        log.fine("Close Button was clicked");

        if (this.activeUndoManager == null) {
            this.unloadEvent();
            MainWindow.getInstance().slideDown(
                    MainWindow.getInstance().getMainMenu());
            return;
        }

        if (this.activeUndoManager.undoAvailable()) {
            new SimpleDialog<>("Es sind ungesicherte Änderungen vorhanden.\n"
                    + "Möchten Sie diese vor dem Beenden speichern?")
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
        if (this.activeUndoManager != null) {
            this.activeUndoManager.undo();
        }
    }

    @FXML
    private void onButtonRedoClicked(ActionEvent event) {
        log.fine("Redo Button was clicked");
        if (this.activeUndoManager != null) {
            this.activeUndoManager.redo();
        }
    }

    @FXML
    private void onButtonSaveClicked(ActionEvent event) {
        log.fine("Save Button was clicked");
        this.saveEvent();
    }

    @FXML
    private void onButtonExportClicked(ActionEvent event) {
        log.fine("Export Button was clicked");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Event als PDF exportieren");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("PDF-Dokument (*.pdf)", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(EntryPoint
                .getPrimaryStage());
        if (selectedFile == null) {
            return;
        }
        if (!selectedFile.getName().endsWith(".pdf")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
        }

        try {
            PDFExporter.exportEventAsPdf(this.loadedEvent,
                    selectedFile.getAbsolutePath());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not export the event.", e);

            new SimpleDialog<>("Das Event konnte nicht exportiert werden.\n"
                    + "Bitte stellen Sie sicher, dass Sie für die Zieldatei "
                    + "alle Berechtigungen besitzen.").modalDialog()
                    .title("dialogs.titles.error")
                    .dialogButtons(DialogButtons.OK).show();
            return;
        }
    }

    @FXML
    private void onButtonLockClicked(ActionEvent event) {
        log.fine("Lock Button was clicked");

        this.passwordDialog.show();
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
        this.slideToPhase(0);
        this.loadedEvent.setEventPhase(Event.EventPhase.EVENT_SETUP);

        this.breadcrumbEventSetup.setEffect(this.highlightedBreadcrumbEffect);
        this.breadcrumbPreRegistration.setEffect(null);
        this.breadcrumbRegistration.setEffect(null);
        this.breadcrumbTournamentExecution.setEffect(null);
    }

    @FXML
    private void onBreadcrumbPreRegistrationClicked(ActionEvent event) {
        log.fine("Pre Registration Breadcrumb was clicked");
        this.slideToPhase(1);
        this.loadedEvent.setEventPhase(Event.EventPhase.PRE_REGISTRATION);

        this.breadcrumbEventSetup.setEffect(null);
        this.breadcrumbPreRegistration
                .setEffect(this.highlightedBreadcrumbEffect);
        this.breadcrumbRegistration.setEffect(null);
        this.breadcrumbTournamentExecution.setEffect(null);
    }

    @FXML
    private void onBreadcrumbRegistrationClicked(ActionEvent event) {
        log.fine("Registration Breadcrumb was clicked");
        this.slideToPhase(2);
        this.loadedEvent.setEventPhase(Event.EventPhase.REGISTRATION);

        this.breadcrumbEventSetup.setEffect(null);
        this.breadcrumbPreRegistration.setEffect(null);
        if (this.loadedEvent.getUserFlag() != UserFlag.REGISTRATION) {
            this.breadcrumbRegistration
                    .setEffect(this.highlightedBreadcrumbEffect);
        }
        this.breadcrumbTournamentExecution.setEffect(null);
    }

    @FXML
    private void onBreadcrumbTournamentExecutionClicked(ActionEvent event) {
        log.fine("Tournament Execution Breadcrumb was clicked");
        this.slideToPhase(3);
        this.loadedEvent.setEventPhase(Event.EventPhase.TOURNAMENT_EXECUTION);

        this.breadcrumbEventSetup.setEffect(null);
        this.breadcrumbPreRegistration.setEffect(null);
        this.breadcrumbRegistration.setEffect(null);
        if (this.loadedEvent.getUserFlag() != UserFlag.TOURNAMENT_EXECUTION) {
            this.breadcrumbTournamentExecution
                    .setEffect(this.highlightedBreadcrumbEffect);
        }
    }
}
