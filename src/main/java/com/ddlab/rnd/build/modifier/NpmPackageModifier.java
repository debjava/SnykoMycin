package com.ddlab.rnd.build.modifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ddlab.rnd.exception.NoModificationRequiredException;

public class NpmPackageModifier implements BuildModifiable {

	@Override
	public void modifyBuild(String content, Map<String, String> fixedDependencyMap, Path destnPath) throws Exception {
        String newContent = new String();

		for (Map.Entry<String, String> entry : fixedDependencyMap.entrySet()) {
			String dependencyName = entry.getKey();
			String newVersion = entry.getValue();

			// Regex: match "dependencyName": "<prefix><version>"
			String regex = "(\"" + Pattern.quote(dependencyName) + "\"\\s*:\\s*\")([~^]?)([0-9][^\"]*)(\")";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);

			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String prefix = matcher.group(2); // keep ^ or ~ if present
				matcher.appendReplacement(sb, matcher.group(1) + prefix + newVersion + matcher.group(4));
				System.out.println("Updated " + dependencyName + " to " + prefix + newVersion);
			}
			matcher.appendTail(sb);
//			content = sb.toString();
            newContent = sb.toString();
		}
        if(newContent.equals(content)) {
            throw new NoModificationRequiredException("No modification required");
        }

//		Files.writeString(destnPath, content);
        Files.writeString(destnPath, newContent);
	}
}


