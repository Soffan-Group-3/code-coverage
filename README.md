# Code coverage

## Janne
I worked on `gson.stream.JsonReader.peekNumber()` which tries to parse a number from the buffer and returns that number if it manages to read one, one of the if-statements wasn't covered by code coverage so I added a test case to increase branch coverage.

```java
	public void testDoubleNegativeReturnsUnquotedPeekNumber() {
		JsonReader reader = new JsonReader(reader("--"));
		reader.setStrictness(Strictness.LENIENT);
		int result = -1;
		try {
			result = reader.doPeek();
		} catch (IOException e) {
			fail("Unexpected exception: " + e);
		}
		
		final int PEEK_UNQUOTED = 10;
		
		assertEquals(PEEK_UNQUOTED, result);
		coverage.putAll(reader.coverageCheck);
	}
```

This succeeeded in increasing branch coverage and increased the coverage for the file from 88% to 88.2%.

Both intelliJs coverage report and my own (as seen in the last line of the test) showed that the branch coverage had increased.