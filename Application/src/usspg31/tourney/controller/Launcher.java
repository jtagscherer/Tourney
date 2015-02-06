package usspg31.tourney.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javafx.application.LauncherImpl;

/**
 * This class is only needed to set some VM arguments prior to executing the
 * actual application.
 */
public class Launcher {

    private static final Logger log = Logger
            .getLogger(Launcher.class.getName());

    public static void main(String[] args) {
        try {
            // Force the usage of GPU acceleration prior to launching the
            // application
            System.setProperty("prism.forceGPU", "true");
            log.info("Starting Application");
            log.info("Running JavaFX Version "
                    + System.getProperty("javafx.runtime.version") + " on "
                    + System.getProperty("os.name"));
            LauncherImpl.launchApplication(EntryPoint.class,
                    SplashScreen.class, args);
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
        }
    }
}
