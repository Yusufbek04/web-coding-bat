package org.example.main;


import java.net.URI;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.logging.Logger;

import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import javax.tools.DiagnosticCollector;
import javax.tools.Diagnostic;
import javax.tools.StandardJavaFileManager;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject;

public enum JavaStringCompiler {
    INSTANCE;

    private final JavaCompiler compiler;
    private final DiagnosticCollector<JavaFileObject> collector;
    private final StandardJavaFileManager manager;

    private static final Logger logger =
            Logger.getLogger(JavaStringCompiler.class.getName());

    JavaStringCompiler() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.collector  = new DiagnosticCollector<>();
        this.manager = compiler.getStandardFileManager(collector, null, null);
    }

    // class to represent a string object as a source file
    static class StringCodeObject extends SimpleJavaFileObject {
        private final String code;

        StringCodeObject(final String name, final String code) {
            super(URI.create("string:///" + name.replace('.', File.separatorChar) +
                            Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return this.code;
        }
    }

    // Compile the Java code stored inside the string
    public boolean compileStringCode(final String name, final String code) {
        logger.info("Compiling: " + name);

        boolean result;
        StringCodeObject source = new StringCodeObject(name, code);

        result = compiler.getTask(null, manager, null, null, null,
                List.of(source)).call();

        // display errors, if any
        for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
            System.err.format("Error at line: %d, in file: %s\n",
                    d.getLineNumber(),
                    d.getSource().toUri());
        }

        try {
            manager.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("XXX");
        }

        logger.info("Finished compiling: " + name);

        return result;
    }
}

