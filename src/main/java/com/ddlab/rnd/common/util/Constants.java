package com.ddlab.rnd.common.util;

import java.util.List;
import java.util.Map;

import com.ddlab.rnd.build.modifier.BuildModifiable;
import com.ddlab.rnd.build.modifier.GradleBuildModifier;
import com.ddlab.rnd.build.modifier.MavenPomModifier;
import com.ddlab.rnd.build.modifier.NpmPackageModifier;

public class Constants {

    public static final String SNYK_ISSUES = "Snyk Issues";
    public static final String PROD_TITLE = "Snykomycin";

    public static final String SNYK_ISSUES_PROGRESS_MSG = "Fetching Snyk Issues, please wait a while";
    public static final String TOKEN_SPACE =  "token ";
    public static final String AI_CHAT_COMPLETIONS = "chat/completions";
    public static final String HASH ="#";
    public static final String ARTIFACT_NAME =  "Artifact Name";
    public static final String SEVERITY =  "Severity";
    public static final String FIXABLE =   "Fixable";
    public static final String CURRENT_VERSIONS = "Current Versions";
    public static final String FIXED_VERSIONS = "Fixed Versions";

    public static final String BEARER_SPC =  "Bearer ";
    public static final String BASIC_SPC = "Basic ";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String URL_ENCODED_TYPE = "application/x-www-form-urlencoded";
    public static final String JSON_TYPE = "application/json";
    public static final String CLIENT_CREDENTIALS = "grant_type=client_credentials";

    public static final String USER = "user";

    public static final String DEFAULT_SNYK_URI = "https://snyk.io/api/v1";
    public static final String SNYK_TGT_ID_PARAM = "?version=2025-11-05&display_name=";
    public static final String SNYK_PGT_ID_PARAM = "?version=2025-11-05&target_id=";

//    public static final String UPDATE_BUILD_SUCCESS_MSG = """
//                            Build file updated successfully.  \
//                        \nPlease build the application before you commit. \
//                            \nIf there is any issue while building the application, \
//                            \nplease revert it. You can find the file in Backup folder.\
//                            \nIf it goes well, delete the Backup folder before you commit\
//                            \n\n Disclaimer: Answer from AI may not be accurate. \
//                            """;

    public static final String UPDATE_BUILD_SUCCESS_MSG = """
                            Build file updated successfully.  \
                           \nBuild the application before you commit. \
                            \nIf there is any issue while building the application, \
                            revert it from Back folder.
                            If it goes well, delete the Backup folder before you commit.\
                            """;

    public static final String NO_FIXABLE_ISSUES_MSG = """
            There are no fixable issues to be updated in build file. \
            \nPlease rerun/rescan the application in Snyk. \
            """;
    public static final List<String> APPLICABLE_FILE_TYPES = List.of("pom.xml", "build.gradle", "package.json");
    public static final String DATE_FMT = "dd-MM-yyyy-HH-mm-ss";
    public static final String BACKUP_DIR = "Backup";
//    public static final String SNYK_FIX_INPUT_PROMPT = "snyk.fix.input.prompt";
//    public static final String SAST = "sast";
    public static final String MODEL_PATH = "/models";
    public static final String NO_SNYK_ISSUES_FOUND = "No Issues Found in Snyk, All Good.";
    public static final String NO_SNYK_PROJECT_FOUND_MSG = "Could not find project in Snyk System.";
    public static final String NO_FIXABLE_SNYK_ISSUE_MSG = """
                        While Snyk reported some issues, \nthey are classified as not fixable at this time.""";
    public static final String NO_PROJECT_CONFIGURATION_BUILD_TYPE = """
                        No project configured for build type in Snyk.\nPlease check in Snyk System.""";

    public static final Map<String, BuildModifiable> BUILD_MODIFIER_MAP = Map.of("pom.xml", new MavenPomModifier(),
            "build.gradle", new GradleBuildModifier(),
            "build.gradle.kts", new GradleBuildModifier(),
            "package.json", new NpmPackageModifier());

    public static final String DISCLAIMER_MSG = """
                        Snykomycin Disclaimer: Answer from AI may not be accurate and definitive.
                        \nPlease exercise discretion to proceed further.
                        """;

    public static final String NO_MODIFICATION_MSG = """
                        No modification required for this build type due to following reasons.\n \
                        1. Already build file has been already been updated dependency versions.\n \
                        2. No reported dependency version found.\n \
                        3. There may be transitive dependencies, you need to update manually. \
                        """;
}
