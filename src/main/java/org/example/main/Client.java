package org.example.main;

import java.lang.reflect.Method;

public class Client {

    public static void main(String[] args) throws Exception {
        final String simpleProgram = "public class SimpleProgram {" +
                " public static void main(String[] args) {" +
                "    System.out.println(\"Hello from SimpleProgram!\");}}";

        testSimpleProgram(simpleProgram);

        final String complexProgram = "package com.exaple;" +
                "import java.util.Random;" +
                "public class ComplexProgram {"+
                "  public static void main(String[] args) {" +
                "   System.out.println(\"'Sup from Fubar\");}" +
                "public int getRandomNumber() {" +
                "  return (new Random()).nextInt(100);}}";

        testComplexProgram(complexProgram);
    }

    private static void testSimpleProgram(String simpleProgram) throws Exception {

        Class<?> simpleClazz =
                CompilingClassLoader.getInstance().loadClassFromString(simpleProgram);

        if (simpleClazz != null) {
            Method main = simpleClazz.getDeclaredMethod("main", String[].class);

            main.invoke(null, (Object)null);
        }
    }

    private static void testComplexProgram(final String complexProgram) throws Exception {

        Class<?> complexClazz =
                CompilingClassLoader.getInstance().loadClassFromString(complexProgram);

        if (complexClazz != null) {
            Object obj = complexClazz.getConstructor().newInstance();
            Method main = complexClazz.getDeclaredMethod("main", String[].class);

            main.invoke(null, (Object)null);

            Method getRandomNumber = complexClazz.getDeclaredMethod("getRandomNumber");
            int n = (int)getRandomNumber.invoke(obj);
            System.out.format("Random number = %d\n", n);
        }
    }
}
