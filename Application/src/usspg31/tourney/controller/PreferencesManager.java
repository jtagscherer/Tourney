package usspg31.tourney.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.xml.sax.SAXException;

import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.filemanagement.FileLoader;
import usspg31.tourney.model.filemanagement.FileSaver;

/**
 * Central class used to store and restore user settings, as well as to load and
 * set available languages.
 */
public class PreferencesManager {

    private static final Logger log = Logger.getLogger(PreferencesManager.class
            .getName());

    /**
     * Represents an available language in the program. Consists of the language
     * specific {@link Locale} and the actual {@link ResourceBundle} used to
     * resolve localizable keys.
     */
    public static class Language {
        private final Locale locale;
        private final ResourceBundle languageBundle;

        private Language(Locale locale, ResourceBundle languageBundle) {
            this.locale = locale;
            this.languageBundle = languageBundle;
        }

        /**
         * @return the language specific {@link Locale}
         */
        public Locale getLocale() {
            return this.locale;
        }

        /**
         * Returns a string describing the language's locale, localized in the
         * language of the locale itself. E.g. for the locale de_DE this returns
         * "Deutsch (Deutschland)" whereas for en_UK "English (United Kingdom)"
         * is returned.
         *
         * @return a string describing the language's locale, localized in the
         *         language of the locale itself
         */
        public String getLocaleLocalized() {
            return this.locale.getDisplayLanguage(this.locale) + " ("
                    + this.locale.getDisplayCountry(this.locale) + ")";
        }

        /**
         * @return the {@link ResourceBundle} associated with this language
         */
        public ResourceBundle getLanguageBundle() {
            return this.languageBundle;
        }
    }

    private static final String languageFilePath = "/ui/language/";
    private static final String languageFilePrefix = "language_";
    private static final String languageFileSuffix = ".properties";
    private static final String availableLanguagesFile = "available_languages";

    private static final Pattern localeCodePattern = Pattern
            .compile("^(?<language>[a-z]+)_(?<country>[A-Z]+)$");

    private static final String defaultPreferencesFile = "defaultPreferences.properties";
    private static final String preferencesFolder = System
            .getProperty("user.home") + "/Tourney/preferences";
    private static final String preferencesFile = "preferences.properties";

    private static final String tournamentModuleFolder = PreferencesManager.preferencesFolder
            + "/tournament-modules/";
    private static final String standardTournamentModuleFolder = "/standard-tournament-modules/";
    private static final String availableTournamentModulesFile = "available-tournament-modules";

    private static PreferencesManager instance;

    /**
     * @return the instance of the PreferencesManager
     */
    public static PreferencesManager getInstance() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private ObservableList<Language> availableLanguages;

    private ObjectProperty<Language> selectedLanguage;

    private StringProperty passwordHash;
    private ReadOnlyBooleanWrapper passwordSet;

    private Properties preferences;

    /**
     * Initializes a new PreferencesManager
     */
    private PreferencesManager() {
        log.info("Initializing PreferencesManager");

        this.loadLanguages();
        this.loadPreferences();
    }

    /**
     * Load all tournament modules from the preferences folder
     * 
     * @return All tournament modules that have been saved previously
     */
    public ObservableList<TournamentModule> loadTournamentModules() {
        ObservableList<TournamentModule> savedModules = FXCollections
                .observableArrayList();

        /*
         * No folder for the tournament modules exists, therefore initialize the
         * folder and fill it with the standard modules
         */
        if (!Files.exists(Paths.get(tournamentModuleFolder))) {
            log.log(Level.INFO,
                    "Initializing the tournament modules with the standard modules...");
            this.saveTournamentModules(this.getStandardTournamentModules());
            return this.loadTournamentModules();
        }

        File tournamentModuleFolder = new File(
                PreferencesManager.tournamentModuleFolder);

        File[] moduleFiles = tournamentModuleFolder
                .listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".ttm");
                    }
                });

        for (File moduleFile : moduleFiles) {
            try {
                savedModules.add(FileLoader
                        .loadTournamentModuleFromFile(moduleFile
                                .getAbsolutePath()));
            } catch (SAXException | IOException e) {
                log.log(Level.SEVERE, "Could not load the tournament module \""
                        + moduleFile.getName() + "\".", e);
            }
        }

        return savedModules;
    }

    /**
     * Save all tournament modules to the preferences folder replacing possible
     * existing ones
     * 
     * @param modules
     *            List of tournament modules to save
     */
    public void saveTournamentModules(ObservableList<TournamentModule> modules) {
        /*
         * Create the needed folder infrastructure to save the tournament
         * modules
         */
        if (!Files.exists(Paths.get(tournamentModuleFolder))) {
            try {
                Files.createDirectories(Paths.get(tournamentModuleFolder));
            } catch (IOException e) {
                log.log(Level.SEVERE,
                        "Could not create the folders needed to save tournament modules.",
                        e);
            }
        }

        /* Save the actual modules overwriting existing ones */
        for (TournamentModule module : modules) {
            File moduleFile = new File(
                    PreferencesManager.tournamentModuleFolder
                            + module.getName() + ".ttm");
            if (moduleFile.exists()) {
                moduleFile.delete();
            }

            FileSaver.saveTournamentModuleToFile(module,
                    moduleFile.getAbsolutePath());
        }
    }

    /**
     * Get the standard tournament modules that are saved in the resources
     * 
     * @return A list of all standard tournament modules
     */
    private ObservableList<TournamentModule> getStandardTournamentModules() {
        ObservableList<TournamentModule> standardModules = FXCollections
                .observableArrayList();

        // get the path to the avaliableLanguagesFile
        String moduleFile = standardTournamentModuleFolder
                + availableTournamentModulesFile;
        URL availableModulesFile = this.getClass().getResource(moduleFile);

        if (availableModulesFile == null) {
            throw new Error("AvailableTournamentModuleFile not found! ("
                    + moduleFile + ")");
        }

        List<String> availableModules = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    availableModulesFile.openStream()));

            String input = null;
            while ((input = reader.readLine()) != null) {
                availableModules.add(input);
            }

            if (availableModules.size() == 0) {
                // must never happen, hence, throw an error
                throw new Error(
                        "AvailableTournamentModuleFile does not contain any "
                                + "valid languages!");
            }
        } catch (IOException e) {
            // must never happen, hence, throw an error
            throw new Error(e);
        }

        URL standardModuleUrl = this.getClass().getResource(
                PreferencesManager.standardTournamentModuleFolder);

        if (standardModuleUrl == null) {
            return standardModules;
        } else {
            for (String nextFile : availableModules) {
                try {
                    String path = this
                            .getClass()
                            .getResource(
                                    standardTournamentModuleFolder + nextFile)
                            .toExternalForm();
                    TournamentModule standardModule = FileLoader
                            .loadTournamentModuleFromFile(path, true);
                    standardModules.add(standardModule);
                } catch (SAXException | IOException e) {
                    log.log(Level.SEVERE,
                            "Could not load a standard tournament module.", e);
                }
            }
        }

        return standardModules;
    }

    /**
     * Remove a tournament file from the preferences folder
     * 
     * @param tournamentName
     *            Name of the tournament module to be removed
     */
    public void removeTournamentFile(String tournamentName) {
        if (!Files.exists(Paths.get(tournamentModuleFolder))) {
            return;
        }

        File tournamentModuleFolder = new File(
                PreferencesManager.tournamentModuleFolder);

        File[] moduleFiles = tournamentModuleFolder
                .listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".ttm");
                    }
                });

        for (File moduleFile : moduleFiles) {
            if (moduleFile.getName().equals(tournamentName + ".ttm")
                    || moduleFile.getName().equals(tournamentName + ".TTM")) {
                moduleFile.delete();
            }
        }
    }

    /**
     * Loads all available languages at the language directory and adds them to
     * the availableLanguages list.
     */
    private void loadLanguages() {
        log.info("Loading available languages...");
        this.availableLanguages = FXCollections.observableArrayList();

        List<String> availableLanguages = this.findAvailableLanguages();

        for (String language : availableLanguages) {
            try {
                URL languageFile = this.getClass().getResource(
                        languageFilePath + languageFilePrefix + language
                                + languageFileSuffix);

                if (languageFile == null) {
                    log.warning("LanguageFile " + languageFile
                            + " wasn't found");
                }

                Matcher languageMatcher = localeCodePattern.matcher(language);
                languageMatcher.find();

                Locale locale = new Locale(languageMatcher.group("language"),
                        languageMatcher.group("country"));
                ResourceBundle languageBundle = new PropertyResourceBundle(
                        languageFile.openStream());

                this.availableLanguages
                        .add(new Language(locale, languageBundle));
            } catch (Exception e) {
                log.log(Level.WARNING,
                        "Error loading language: " + e.getMessage(), e);
            }
        }

        if (this.availableLanguages.size() == 0) {
            throw new Error("No languages could be loaded successfully!");
        }
    }

    /**
     * Looks up available languages in the availableLanguages file.
     */
    private List<String> findAvailableLanguages() {
        // get the path to the avaliableLanguagesFile
        String langFile = languageFilePath + availableLanguagesFile;
        URL availableLangaugesFile = this.getClass().getResource(langFile);

        if (availableLangaugesFile == null) {
            throw new Error("AvailableLanguagesFile not found! (" + langFile
                    + ")");
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    availableLangaugesFile.openStream()));

            List<String> availableLanguages = new ArrayList<>();

            // read the availableLanguagesFile, there should be one locale code
            // in every line
            String input = null;
            while ((input = reader.readLine()) != null) {
                if (localeCodePattern.matcher(input).matches()) {
                    availableLanguages.add(input);
                }
            }

            if (availableLanguages.size() == 0) {
                // must never happen, hence, throw an error
                throw new Error("AvailableLanguagesFile does not contain any "
                        + "valid languages!");
            }

            return availableLanguages;
        } catch (IOException e) {
            // must never happen, hence, throw an error
            throw new Error(e);
        }
    }

    /**
     * Loads the configuration file and sets all properties according to the
     * file's content.
     */
    private void loadPreferences() {
        log.info("Loading user preferences...");

        // load the default preferences file
        Properties defaultPreferences = new Properties();
        try {
            defaultPreferences.load(this.getClass().getClassLoader()
                    .getResourceAsStream(defaultPreferencesFile));
        } catch (IOException e) {
            // must never happen if the project setup is correct
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new Error(e);
        }

        // load the preferences file
        Path preferencesPath = Paths.get(preferencesFolder, preferencesFile);
        this.preferences = new Properties(defaultPreferences);
        try {
            this.preferences.load(Files.newInputStream(preferencesPath));
        } catch (IOException e) {
            log.info("No preferences file found. Using default settings.");
        }

        // load the set language
        String[] localeStrings = this.preferences.getProperty(
                "application.language").split("_");
        Locale locale = new Locale(localeStrings[0], localeStrings[1]);

        this.selectedLanguage = new SimpleObjectProperty<>();
        for (Language language : this.availableLanguages) {
            if (language.getLocale().equals(locale)) {
                this.selectedLanguage.set(language);
                break;
            }
        }

        // if the language specified in the preferences isn't currently loaded,
        // set at least any language
        if (this.selectedLanguage.isNull().get()) {
            this.selectedLanguage.set(this.getAvailableLanguages().get(0));
        }

        // whenever the selectedLanguage changes, update the preferences file
        this.selectedLanguage.addListener((ov, o, n) -> {
            this.preferences.setProperty("application.language", n.getLocale()
                    .toString());
            this.savePreferences();
        });

        // load the set password hash (we wouldn't use un-hashed passwords,
        // would we?)
        String passwordHash = this.preferences.getProperty(
                "application.password", "");
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.passwordSet = new ReadOnlyBooleanWrapper();
        this.passwordSet.bind(this.passwordHash.isEmpty().not());

        // whenever the passwordHash changes, update the preferences file
        this.passwordHash.addListener((ov, o, n) -> {
            this.preferences.setProperty("application.password", n);
            this.savePreferences();
        });
    }

    /**
     * Saves all changed properties to the configuration file.
     */
    private void savePreferences() {
        Path preferencesPath = Paths.get(preferencesFolder, preferencesFile);
        try {
            if (!Files.exists(Paths.get(preferencesFolder))) {
                Files.createDirectories(Paths.get(preferencesFolder));
            }
            this.preferences
                    .store(Files.newOutputStream(preferencesPath),
                            "Configuration File for Tourney\nDo not change any of these values manually!");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save preferences file", e);
        }
    }

    /**
     * @return an ObservableList containing all supported languages
     */
    public ObservableList<Language> getAvailableLanguages() {
        return this.availableLanguages;
    }

    /**
     * @return the currently selected language
     */
    public Language getSelectedLanguage() {
        return this.selectedLanguage.get();
    }

    /**
     * Sets the preferred language. See {@link #getAvailableLanguages()} to
     * obtain available languages.
     *
     * @param language
     *            the language to set as the preferred one
     */
    public void setSelectedLanguage(Language language) {
        this.selectedLanguage.set(language);
    }

    /**
     * @return the ObjectProperty storing the currently selected language
     */
    public ObjectProperty<Language> selectedLanguageProperty() {
        if (this.selectedLanguage == null) {
            this.selectedLanguage = new SimpleObjectProperty<>();
        }
        return this.selectedLanguage;
    }

    /**
     * Helper method to localize the given key using the currently selected
     * language.
     *
     * @param key
     *            the key to look up
     * @return the localized string
     */
    public String localizeString(String key) {
        if (this.getSelectedLanguage().getLanguageBundle().containsKey(key)) {
            return this.getSelectedLanguage().getLanguageBundle()
                    .getString(key);
        } else {
            log.log(Level.SEVERE, "The key \"" + key
                    + "\" does not exist in the language bundle \""
                    + this.getSelectedLanguage().getLocale().getLanguage()
                    + "\".");
            return key;
        }
    }

    /**
     * Returns true if the given password is valid
     *
     * @param password
     * @return
     */
    public boolean isPasswordCorrect(String password) {
        if (this.passwordHash.get().isEmpty()) {
            return true;
        }
        return this.passwordHash.get().equals(this.getHash(password));
    }

    /**
     * Sets the new password for the application. The old password will be
     * validated and if it is correct, the new one will be set.
     *
     * @param oldPassword
     *            the old password
     * @param newPassword
     *            the new password
     * @return true if the new password was set, false if the old password
     *         wasn't valid
     */
    public boolean setPassword(String oldPassword, String newPassword) {
        if (this.isPasswordCorrect(oldPassword)) {
            if (newPassword.isEmpty()) {
                this.passwordHash.set("");
            } else {
                this.passwordHash.set(this.getHash(newPassword));
            }
            return true;
        }
        return false;
    }

    /**
     * @return true if a password was set for the application
     */
    public boolean isPasswordSet() {
        return this.passwordSet.get();
    }

    public ReadOnlyBooleanWrapper passwordSetProperty() {
        return this.passwordSet;
    }

    /**
     * @return a read-only property indicating if a password is set for the
     *         application
     */
    public ReadOnlyBooleanProperty passwordSet() {
        return this.passwordSet.getReadOnlyProperty();
    }

    /**
     * Calculates a hash value for the given input string, used to store and
     * validate the password.
     * 
     * @param input
     *            the string to get the hash of
     * @return the hash of the input string
     */
    private String getHash(String input) {
        try {
            // use SHA-256 to calculate the hash for the input
            MessageDigest stringDigest;
            stringDigest = MessageDigest.getInstance("SHA-256");
            stringDigest.update(input.getBytes());
            return new String(stringDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            // SHA-265 just HAS to be there, otherwise all this wouldn't work
            throw new Error(e);
        }
    }
}
