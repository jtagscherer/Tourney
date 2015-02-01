package usspg31.tourney.controller.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import usspg31.tourney.controller.layout.IconPane;

public class MaterialTextField extends AnchorPane {

    /**
     * Class used in the input validation callback to display the desired
     * message (or no message at all).
     */
    public static final class ValidationResult {
        /** the actual message to display */
        private final String message;
        /** the color set for the underline of the text field */
        private final Color underlineColor;
        /** the color set for the info promt and the icon */
        private final Color messageColor;
        /** the style class applied to the icon */
        private final String[] iconStyleClasses;

        public ValidationResult(String message, String underlineColor,
                String messageColor, String... iconStyleClass) {
            this(message, Color.web(underlineColor), Color.web(messageColor), iconStyleClass);
        }
        public ValidationResult(String message, Color underlineColor,
                String messageColor, String... iconStyleClass) {
            this(message, underlineColor, Color.web(messageColor), iconStyleClass);
        }
        public ValidationResult(String message, String underlineColor,
                Color messageColor, String... iconStyleClass) {
            this(message, Color.web(underlineColor), messageColor, iconStyleClass);
        }
        public ValidationResult(String message, Color underlineColor, Color messageColor,
                String... iconStyleClass) {

            this.message = message;
            this.underlineColor = underlineColor;
            this.messageColor = messageColor;
            this.iconStyleClasses = iconStyleClass;
        }
        public static ValidationResult ok() {
            return new ValidationResult("", "#039ed3", Color.TRANSPARENT);
        }
        public static ValidationResult success() {
            return success("");
        }
        public static ValidationResult success(String text) {
            return new ValidationResult(text, "#3a0", "#3a0", "icon-success", "third");
        }
        public static ValidationResult info(String text) {
            return new ValidationResult(text, "#039ed3", "#039ed3", "icon-info", "third");
        }
        public static ValidationResult warning(String text) {
            return new ValidationResult(text, "#f80", "#f80", "icon-warning", "third");
        }
        public static ValidationResult error(String text) {
            return new ValidationResult(text, "#f00", "#f00", "icon-error", "third");
        }

        private Color getUnderlineColor() {
            return this.underlineColor;
        }
        private Color getMessageColor() {
            return this.messageColor;
        }
        private String[] getIconStyleClasses() {
            return this.iconStyleClasses;
        }
        private String getMessage() {
            return this.message;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ValidationResult)) {
                return false;
            }
            ValidationResult cmp = (ValidationResult) obj;
            if (cmp.iconStyleClasses.length != this.iconStyleClasses.length) {
                return false;
            }
            for (int i = 0; i < this.iconStyleClasses.length; i++) {
                if (!this.iconStyleClasses[i].equals(cmp.iconStyleClasses[i])) {
                    return false;
                }
            }
            return this.underlineColor.equals(cmp.underlineColor)
                    && this.message.equals(cmp.message)
                    && this.messageColor.equals(cmp.messageColor);
        }
    }

    private static final Logger log = Logger.getLogger(MaterialTextField.class.getName());

    @FXML private Label promptLabel;
    @FXML private TextField textField;
    @FXML private Pane defaultUnderline;
    @FXML private Pane activeUnderline;

    @FXML private HBox hintContainer;
    @FXML private Label hintLabel;
    @FXML private IconPane hintIcon;

    private BooleanProperty floatingPrompt;
    private BooleanProperty hasHint;
    private ObjectProperty<Callback<String, ValidationResult>> inputValidationCallback;

    private static final Duration promptAnimationDuration = Duration.millis(250);
    private static final Interpolator promptAnimationInterpolator = Interpolator.SPLINE(.7, 0, 0, 1);
    private Animation promptMoveAnimation;
    private Animation promptFadeAnimation;
    private boolean promptVisible;
    private boolean promptUp;

    private static final Duration underlineAnimationDuration = Duration.millis(200);
    private static final Interpolator underlineAnimationInterpolator = Interpolator.LINEAR;
    private Animation underlineAnimation;

    private static final Duration hintAnimationDuration = Duration.millis(150);
    private static final Interpolator hintAnimationInterpolator = Interpolator.SPLINE(.4, 0, 0, .4);
    private Animation hintAnimation;
    private Color previousBackgroundColor;
    private ValidationResult previousResult;

    public MaterialTextField() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/controls/material-text-field.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new Error(e);
        }

        this.initializeAnimations();

        this.validateInput();
        this.updateSize();
    }

    private void initializeAnimations() {
        this.initializePromptMoveTransition();
        this.initializePromptFadeTransition();

        this.initializeUnderlineAnimation();

        this.animateUnderlineOut();
    }

    private void initializePromptMoveTransition() {
        ScaleTransition promptScale = new ScaleTransition(promptAnimationDuration, this.promptLabel);
        promptScale.setFromX(1);
        promptScale.setFromY(1);
        promptScale.setToX(.7);
        promptScale.setToY(.7);
        promptScale.setInterpolator(promptAnimationInterpolator);

        TranslateTransition promptTranslate = new TranslateTransition(promptAnimationDuration, this.promptLabel);
        promptTranslate.setFromY(0);
        promptTranslate.setToY(-AnchorPane.getTopAnchor(this.promptLabel) - 4);
        promptTranslate.setInterpolator(promptAnimationInterpolator);

        this.promptLabel.translateXProperty().bind(
                this.promptLabel.widthProperty()
                .multiply(this.promptLabel.scaleXProperty()
                        .subtract(1)
                        .divide(2)));

        this.promptMoveAnimation = new ParallelTransition(promptScale, promptTranslate);

        this.promptUp = false;
    }

    private void initializePromptFadeTransition() {
        FadeTransition fade = new FadeTransition(promptAnimationDuration, this.promptLabel);
        fade.setFromValue(1);
        fade.setToValue(0);
        this.promptFadeAnimation = fade;
        this.promptVisible = true;
    }

    private void initializeUnderlineAnimation() {
        ScaleTransition scaleX = new ScaleTransition(underlineAnimationDuration, this.activeUnderline);
        scaleX.setFromX(0);
        scaleX.setToX(1);
        scaleX.setInterpolator(underlineAnimationInterpolator);
        FadeTransition fade = new FadeTransition(underlineAnimationDuration, this.activeUnderline);
        fade.setFromValue(0);
        fade.setToValue(1);
        this.underlineAnimation = new ParallelTransition(scaleX, fade);
    }

    @FXML
    private void initialize() {
        this.textField.focusedProperty().addListener(this::onFocusChanged);
        this.textField.textProperty().addListener((ov, o, n) -> this.validateInput());

        this.updateSize();

        this.widthProperty().addListener((ov, o, n) -> {
            this.updateSize();
        });
    }

    private void validateInput() {
        ValidationResult validation = this.getInputValidationCallback().call(this.getText());
        if (this.previousResult != null && this.previousResult.equals(validation)) {
            return;
        }
        this.previousResult = validation;
        this.hintLabel.setText(validation.getMessage());
        this.hintIcon.getStyleClass().clear();
        this.hintIcon.getStyleClass().addAll("icon-pane", "message-icon");
        if (validation.getIconStyleClasses().length > 0) {
            this.hintIcon.getStyleClass().addAll(validation.getIconStyleClasses());
        }
        this.hintIcon.setOpacity(validation.getIconStyleClasses().length > 0 ? 1 : 0);
        this.hintIcon.setStyle(String.format("-fx-background-color: #%02x%02x%02x%02x;",
                (int)(validation.getMessageColor().getRed() * 255),
                (int)(validation.getMessageColor().getGreen() * 255),
                (int)(validation.getMessageColor().getBlue() * 255),
                (int)(validation.getMessageColor().getOpacity() * 255)));

        this.hintLabel.setTextFill(validation.messageColor);

        // fade the secondary hint container in and slide it in from the top
        FadeTransition secondaryFadeIn = new FadeTransition(
                hintAnimationDuration, this.hintContainer);
        secondaryFadeIn.setFromValue(0);
        secondaryFadeIn.setToValue(1);
        secondaryFadeIn.setInterpolator(hintAnimationInterpolator);
        TranslateTransition secondaryTranslate = new TranslateTransition(
                hintAnimationDuration, this.hintContainer);
        secondaryTranslate.setFromY(-this.hintContainer.getHeight());
        secondaryTranslate.setToY(0);
        secondaryTranslate.setInterpolator(hintAnimationInterpolator);

        // create a smooth transition for the color of the active underline
        ObjectProperty<Color> highlightColor = new SimpleObjectProperty<>();
        highlightColor.addListener((ov, o, n) -> {
            this.activeUnderline.setStyle(String.format(
                    "-fx-background-color: #%02x%02x%02x%02x;",
                    (int)(n.getRed() * 255),
                    (int)(n.getGreen() * 255),
                    (int)(n.getBlue() * 255),
                    (int)(n.getOpacity() * 255)));
        });
        if (this.previousBackgroundColor == null) {
            this.previousBackgroundColor = validation.getUnderlineColor();
        }
        Timeline highlightTransition = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(highlightColor,
                        this.previousBackgroundColor)),
                new KeyFrame(hintAnimationDuration, new KeyValue(highlightColor,
                        validation.getUnderlineColor(),
                        hintAnimationInterpolator)));
        this.previousBackgroundColor = validation.getUnderlineColor();

        if (this.hintAnimation != null) {
            this.hintAnimation.stop();
        }

        this.hintAnimation = new ParallelTransition(
                secondaryFadeIn,
                secondaryTranslate,
                highlightTransition);

        this.hintAnimation.play();
    }

    private void onFocusChanged(ObservableValue<? extends Boolean> ov, Boolean o, Boolean n) {
        // scale the highlight underline in or out
        if (n) {
            this.animateUnderlineIn();
        } else {
            this.animateUnderlineOut();
        }

        this.updatePrompt();
    }

    private void updatePrompt() {
        if (this.textProperty().isNotEmpty().get() || this.textField.isFocused()) {
            this.animatePromptOut();
        } else {
            this.animatePromptIn();
        }
    }

    private void animatePromptOut() {
        if (this.isFloatingPrompt()) {
            if (!this.promptVisible) {
                this.animatePromptFadeIn();
            }
            if (!this.promptUp) {
                this.animatePromptUp();
            }
        } else {
            if (this.promptVisible) {
                this.animatePromptFadeOut();
            }
            if (this.promptUp) {
                this.animatePromptDown();
            }
        }
    }

    private void animatePromptIn() {
        if (!this.promptVisible) {
            this.animatePromptFadeIn();
        }
        if (this.promptUp) {
            this.animatePromptDown();
        }
    }

    private void animatePromptUp() {
        this.promptUp = true;
        this.promptMoveAnimation.stop();
        this.promptMoveAnimation.setRate(1);
        this.promptMoveAnimation.play();
    }

    private void animatePromptDown() {
        this.promptUp = false;
        this.promptMoveAnimation.stop();
        this.promptMoveAnimation.setRate(-1);
        this.promptMoveAnimation.play();
    }

    private void animatePromptFadeOut() {
        this.promptVisible = false;
        this.promptFadeAnimation.stop();
        this.promptFadeAnimation.setRate(1);
        this.promptFadeAnimation.play();
    }

    private void animatePromptFadeIn() {
        this.promptVisible = true;
        this.promptFadeAnimation.stop();
        this.promptFadeAnimation.setRate(-1);
        this.promptFadeAnimation.play();
    }

    private void animateUnderlineIn() {
        this.underlineAnimation.stop();
        this.underlineAnimation.setRate(1);
        this.underlineAnimation.play();
    }

    private void animateUnderlineOut() {
        this.underlineAnimation.stop();
        this.underlineAnimation.setRate(-1);
        this.underlineAnimation.play();
    }



    public BooleanProperty floatingPromptProperty() {
        if (this.floatingPrompt == null) {
            this.floatingPrompt = new SimpleBooleanProperty(false);
            this.floatingPrompt.addListener((ov, o, n) -> {
                this.updateSize();
            });
        }
        return this.floatingPrompt;
    }

    public boolean isFloatingPrompt() {
        return this.floatingPromptProperty().get();
    }

    public void setFloatingPrompt(boolean value) {
        this.floatingPromptProperty().set(value);
    }


    public BooleanProperty hasHintProperty() {
        if (this.hasHint == null) {
            this.hasHint = new SimpleBooleanProperty(false);
            this.hasHint.addListener((ov, o, n) -> {
                this.hintIcon.setVisible(n);
                this.validateInput();
            });
        }
        return this.hasHint;
    }

    private void updateSize() {
        double translateY = this.isFloatingPrompt() ? 0 : -AnchorPane.getTopAnchor(this.textField);
        double minHeight = 67;

        if (!this.isFloatingPrompt()) {
            minHeight -= AnchorPane.getTopAnchor(this.textField);
        }
        if (!this.isHasHint()) {
            minHeight -= 16;
        }

        this.setTranslateY(translateY);
        this.setMinHeight(minHeight);
        this.setMaxHeight(minHeight);
    }

    public boolean isHasHint() {
        return this.hasHintProperty().get();
    }

    public void setHasHint(boolean value) {
        this.hasHintProperty().set(value);
    }


    public ObjectProperty<Callback<String, ValidationResult>> inputValidationCallbackProperty() {
        if (this.inputValidationCallback == null) {
            this.inputValidationCallback =
                    new SimpleObjectProperty<Callback<String,ValidationResult>>(
                            input -> ValidationResult.ok());
            this.inputValidationCallback.addListener((ov, o, n) -> this.validateInput());
        }
        return this.inputValidationCallback;
    }

    private Callback<String, ValidationResult> getInputValidationCallback() {
        return this.inputValidationCallbackProperty().get();
    }

    public void setInputValidationCallback(Callback<String, ValidationResult> value) {
        this.inputValidationCallbackProperty().set(value);
    }


    public StringProperty textProperty() {
        return this.textField.textProperty();
    }

    public String getText() {
        return this.textProperty().get();
    }

    public void setText(String value) {
        this.textProperty().set(value);
    }


    public StringProperty promptTextProperty() {
        return this.promptLabel.textProperty();
    }

    public String getPromptText() {
        return this.promptTextProperty().get();
    }

    public void setPromptText(String value) {
        this.promptTextProperty().set(value);
    }


    public BooleanProperty showPromptProperty() {
        return this.promptLabel.visibleProperty();
    }

    public boolean isShowPrompt() {
        return this.showPromptProperty().get();
    }

    public void setShowPrompt(boolean value) {
        this.showPromptProperty().set(value);
    }


    public ObjectProperty<Pos> alignmentProperty() {
        return this.textField.alignmentProperty();
    }

    public void setAlignment(Pos value) {
        this.alignmentProperty().set(value);
    }

    public Pos getAlignment() {
        return this.alignmentProperty().get();
    }

}
