package usspg31.tourney.controller.controls;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.PreferencesManager.Language;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class LanguageSelectionDialog extends VBox
        implements IModalDialogProvider<ObservableList<Language>, Language> {

    private HBox languageButtonContainer;
    private ModalDialog<ObservableList<Language>, Language> modalDialog;

    private Language selectedLanguage;

    public LanguageSelectionDialog() {
        super(5);

        this.setFillWidth(true);

        PreferencesManager preferences = PreferencesManager.getInstance();
        this.languageButtonContainer = new HBox(5);

        Label labelDescription = new Label(preferences.localizeString(
                "dialogs.languageselection.description"));

        this.getChildren().addAll(
                labelDescription,
                this.languageButtonContainer);
    }

    @Override
    public void setProperties(ObservableList<Language> properties) {
        this.languageButtonContainer.getChildren().clear();

        for (Language language : properties) {
            Button languageButton = new Button(language.getLocaleLocalized());
            HBox.setHgrow(languageButton, Priority.ALWAYS);
            languageButton.setOnAction(event -> {
                this.selectedLanguage = language;
                this.modalDialog.exitWith(DialogResult.OK);
            });
            this.languageButtonContainer.getChildren().add(languageButton);
        }
    }

    @Override
    public Language getReturnValue() {
        return this.selectedLanguage;
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<Language>, Language> modalDialog) {
        this.modalDialog = modalDialog;
        modalDialog.title("dialogs.languageselection");
        modalDialog.dialogButtons(DialogButtons.CANCEL);

        this.selectedLanguage = null;
    }

}
