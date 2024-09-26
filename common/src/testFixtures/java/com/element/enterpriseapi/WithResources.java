package com.element.enterpriseapi;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface WithResources {

    default String readFileContents(String filename) {
        return readFileContents(getClasspathFile(filename));
    }

    @SneakyThrows
    default String readFileContents(File file) {
        return FileUtils.readFileToString(file, UTF_8);
    }

    @SneakyThrows
    default File getClasspathFile(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(filename);
        return new File(resource.getFile());
    }
}
