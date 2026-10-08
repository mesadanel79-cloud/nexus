package application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifies that the whole application context (controllers,
 * domain services, output adapters, security and persistence) can be
 * composed by the {@code application.App} entry point.
 */
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
