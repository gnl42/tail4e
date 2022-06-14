package info.ballroomdancemusic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ActivatorTest {

	@Test
	public void testActivatorId() {
		Assertions.assertNotNull(Tail4e.PLUGIN_ID);
	}

}
