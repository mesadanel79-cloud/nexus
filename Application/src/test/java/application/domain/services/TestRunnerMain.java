package application.domain.services;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestRunnerMain {
    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;
        Class<?>[] testClasses = new Class<?>[] {
            AuthorizationServiceTest.class,
            OperationAuditServiceTest.class
        };

        for (Class<?> clazz : testClasses) {
            System.out.println("Running " + clazz.getSimpleName() + "...");
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
                    try {
                        Object instance = clazz.getDeclaredConstructor().newInstance();
                        // run @BeforeEach
                        for (Method bm : clazz.getDeclaredMethods()) {
                            if (bm.isAnnotationPresent(org.junit.jupiter.api.BeforeEach.class)) {
                                bm.setAccessible(true);
                                bm.invoke(instance);
                            }
                        }
                        m.setAccessible(true);
                        m.invoke(instance);
                        System.out.println("  [PASS] " + m.getName());
                        passed++;
                    } catch (Throwable t) {
                        System.out.println("  [FAIL] " + m.getName() + ": " + t.getCause());
                        t.printStackTrace(System.out);
                        failed++;
                    }
                }
            }
        }
        System.out.println("\nRESULTS: " + passed + " passed, " + failed + " failed.");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
