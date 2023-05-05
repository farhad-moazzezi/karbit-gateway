import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestUtils {
	@Test
	void test() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encode = encoder.encode("test");
		boolean test = encoder.matches("test", encode);
		Assertions.assertThat(test).isTrue();
	}
}
