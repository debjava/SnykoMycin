package com.ddlab.rnd.build.modifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ddlab.rnd.exception.NoModificationRequiredException;

public class GradleBuildModifier implements BuildModifiable {

    @Override
    public void modifyBuild(String content, Map<String, String> fixedDependencyMap, Path destnPath) throws Exception {

        String[] contents = new String[1];
        contents[0] = content;

        fixedDependencyMap.forEach((key, value) -> {
            String[] values = key.split(":");
            String groupId = values[0];
            String artifactId = values[1];
            String fixedVersion = value;

            contents[0] = getRegexReplacment(contents[0], groupId, artifactId, fixedVersion);

        });
        if (contents[0].equals(content)) {
            throw new NoModificationRequiredException("No modification required");
        }

        Files.writeString(destnPath, contents[0]);

    }

    private String getRegexReplacment(String content, String groupId, String artifactId, String fixedVersion) {
        String regex = "(implementation|api|compileOnly|testImplementation|annotationProcessor)" // group 1
                + "(\\s*)" // group 2: spaces
                + "(\\(?)" // group 3: optional (
                + "(['\"])" // group 4: opening quote
                + groupId + ":" + artifactId + ":" // fixed coordinates
                + "([^'\"]+)" // group 5: old version
                + "(['\"])" // group 6: closing quote
                + "(\\)?)"; // group 7: optional )

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        // $1 = keyword, $2 = spaces, $3 = optional (, $4 = opening quote, $5 = old
        // version, $6 = closing quote, $7 = optional )
        String replacement = "$1$2$3$4" + groupId + ":" + artifactId + ":" + fixedVersion + "$6$7";
        String replaceValue = matcher.replaceAll(replacement);

        return replaceValue;
    }

}
