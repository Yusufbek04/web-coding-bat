package org.example.main;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

public class CompilingClassLoader extends ClassLoader {
    private static final CompilingClassLoader __instance
            = new CompilingClassLoader();

    private static final Logger logger =
            Logger.getLogger(CompilingClassLoader.class.getName());

    private final Pattern namePattern;
    private final Pattern packagePattern;

    private CompilingClassLoader() {
        this.namePattern =
                Pattern.compile(".*class[ ]+([a-zA-Z0-9$_]+).*");
        this.packagePattern =
                Pattern.compile(".*package[ ]+([a-zA-Z0-9$_.]+).*");
    }

    public static CompilingClassLoader getInstance() {
        return __instance;
    }

    // load the class file after compiling the code
    public Class<?> loadClassFromString(final String program) throws ClassNotFoundException {
        final String className = getClassName(program);
        final String packagePath = getPackagePath(program);

        final String fullClassName;
        if (packagePath != null) {
            fullClassName = packagePath + '.' + className;
        } else {
            fullClassName = className;
        }

        logger.info("Loading " + fullClassName);

        // compile it!
        boolean result =
                JavaStringCompiler.INSTANCE
                        .compileStringCode(fullClassName, program);

        if (result) {
            byte[] classBytes = getClassBytes(className);
            if (classBytes != null) {
                logger.info("Loaded " + fullClassName);
                return defineClass(fullClassName, classBytes, 0, classBytes.length);
            } else
                throw new ClassNotFoundException("Unable to load: " + fullClassName +
                        ". Reason = failed to load class bytes.");
        } else
            throw new ClassNotFoundException("Unable to load: " + fullClassName +
                    ". Reason = compilation failed.");
    }

    private String getClassName(final String program) {
        Matcher m = namePattern.matcher(program);

        if (m.matches() && (m.groupCount() == 1)) {
            return m.group(1);
        }
        throw new RuntimeException("Could not find main class to load!");
    }

    private String getPackagePath(final String program) {
        Matcher m = packagePattern.matcher(program);

        if (m.matches() && (m.groupCount() == 1)) {
            return m.group(1);
        }
        return null;
    }

    private byte[] getClassBytes(final String className) {
        final String classFilePath =
                className.replace('.', File.separatorChar) + ".class";

        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(classFilePath));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4 * 1024];
            int bytesRead = -1;

            while ((bytesRead = bin.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            // delete the class file before returning
            try {
                Files.deleteIfExists(Paths.get(classFilePath));
            } catch (IOException ex) {
                //
            }

            return baos.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }
}
