package com.egnyte.fbplugins.settings;

import static com.egnyte.fbplugins.deprecated3rdpartyrules.Deprecated3rdPartyDetector.DETECTOR_NAME;

import java.io.File;
import java.io.FileNotFoundException;

import lombok.Getter;

import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecatedListParseError;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettings;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettingsLoader;

import edu.umd.cs.findbugs.ba.AnalysisContext;

public class SetupChecker {
    private static final String DEPRECATED_LIST_NAME = "deprecated-list.txt";

    @Getter
    private final Settings settings;

    private final DeprecationSettingsLoader settingsLoader;

    public SetupChecker() throws Exception {
        this.settingsLoader = new DeprecationSettingsLoader();
        this.settings = initSettings(settingsLoader);
    }

    public Settings initSettings(DeprecationSettingsLoader settingsLoader) throws Exception {
        Settings settings = new Settings();
        settings.setDeprecatedSettings(initDeprecatedSettings(settingsLoader));

        return settings;
    }

    private DeprecationSettings initDeprecatedSettings(DeprecationSettingsLoader settingsLoader) throws Exception {
        DeprecationSettings settings = settingsLoader.settingsFromTxtFile(getPathToDeprecatedList());
        checkAndLogErrors(settings);

        return settings;
    }

    private boolean checkAndLogErrors(DeprecationSettings settings) {
        if (!settings.getErrors().isEmpty()) {
            AnalysisContext.logError(String.format("[%s] Errors in deprecated list:%n", DETECTOR_NAME));
            for (DeprecatedListParseError error : settings.getErrors()) {
                AnalysisContext.logError(String.format("[%s] %s (line %d): %s%n", DETECTOR_NAME, error.getMessage(),
                        error.getLineNo(), error.getSourceCode()));
            }

            return true;
        }
        return false;
    }

    private String getPathToDeprecatedList() throws FileNotFoundException {
        File parentDirectory = new File(SetupChecker.class.getProtectionDomain().getCodeSource()
                .getLocation().getFile()).getParentFile();
        File deprecatedList = new File(parentDirectory, DEPRECATED_LIST_NAME);
        if (!deprecatedList.exists()) {
            AnalysisContext.logError(String.format(
                    "[%s] deprecated-list.txt file does not exist. It should be placed in the same directory as the plugin.",
                    DETECTOR_NAME));
        }
        return deprecatedList.getPath();
    }
}
