package builderb0y.fractallightning;

import org.junit.jupiter.api.Test;

/**
this class tricks gradle into actually running
the test task even when there are no tests to run.
this is important because I have a doFirst() {} block in the test task,
and I need *that* to run even when there are no junit tests.
*/
public class TheTest {

	@Test
	public void theTest() {}
}