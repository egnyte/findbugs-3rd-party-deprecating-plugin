package com.egnyte.fbplugins.settings;

import static com.egnyte.fbplugins.deprecated3rdpartyrules.Deprecated3rdPartyDetector.DETECTOR_NAME;
import lombok.Getter;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.Deprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecatedListParseError;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettings;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettingsLoader;

import edu.umd.cs.findbugs.ba.AnalysisContext;

public class SetupChecker {

    private static final String DEPRECATED_LIST_RESOURCE_PATH = "/deprecated-list.txt";

    private static class SetupCheckerHolder {
        public static final SetupChecker instance;
        static {
            try {
                instance = new SetupChecker();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static SetupChecker getInstance() {
        return SetupCheckerHolder.instance;
    }

    @Getter
    private final Settings settings;

    private final DeprecationSettingsLoader settingsLoader;

    private SetupChecker() throws Exception {
        this.settingsLoader = new DeprecationSettingsLoader();
        this.settings = initSettings(settingsLoader);
    }

    public Settings initSettings(DeprecationSettingsLoader settingsLoader) throws Exception {
        Settings settings = new Settings();
        settings.setDeprecatedSettings(initDeprecatedSettings(settingsLoader));

        return settings;
    }

    private DeprecationSettings initDeprecatedSettings(DeprecationSettingsLoader settingsLoader) throws Exception {
        System.out.printf("[%s] Will try to load from [%s].%n", DETECTOR_NAME, DEPRECATED_LIST_RESOURCE_PATH);

        DeprecationSettings settings = null;
        settings = settingsLoader.settingsFromResource(DEPRECATED_LIST_RESOURCE_PATH);

        if (!checkAndLogErrors(settings)) {
            logFoundDeprecations(settings);
        }

        return settings;
    }

    private void logFoundDeprecations(DeprecationSettings settings) {
        System.out.printf("[%s] Searching for deprecated classes:%n", DETECTOR_NAME);

        for (Deprecation deprecation : settings.getDeprecations()) {
            System.out.printf("[%s] %s%n", DETECTOR_NAME, deprecation.toString());
        }
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
}
