package com.ddlab.rnd.common.util;

import java.util.List;

public class Constants {

    public static final String SNYK_ISSUES = "Snyk Issues";

    public static final String SNYKOMYCIN_PROGRESS_TITLE = "Snykomycin";
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
    public static final String ERR_TITLE = "SnykoMycin Error";
    public static final String SNYKO_TITLE = "SnykMycin Success Title";

    public static final String DEFAULT_SNYK_URI = "https://snyk.io/api/v1";

    public static final String UPDATE_BUILD_SUCCESS_MSG = """
                            Build file updated successfully.  \
                        \nPlease build the application before you commit. \
                            \nIf there is any issue while building the application, \
                            \nplease revert it. You can find the file in Backup folder.\
                            \nIf it goes well, delete the Backup folder before you commit\
                            \n Disclaimer: Answer from AI may not be accurate. \
                            """;

    public static final List<String> APPLICABLE_FILE_TYPES = List.of("pom.xml", "build.gradle", "package.json");
    public static final String DATE_FMT = "dd-MM-yyyy-HH-mm-ss";
    public static final String BACKUP_DIR = "Backup";
    public static final String SNYK_FIX_INPUT_PROMPT = "snyk.fix.input.prompt";
    public static final String SAST = "sast";
    public static final String MODEL_PATH = "/models";
}
