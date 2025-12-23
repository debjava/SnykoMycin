package com.ddlab.rnd.build.modifier;

import java.nio.file.Path;
import java.util.Map;

public interface BuildModifiable {
    void modifyBuild(String content, Map<String, String> depMap, Path destnPath) throws Exception;
}
