package net.jomura.blog;

import junit.framework.TestCase;

import org.junit.Test;

public class StringUtilTest extends TestCase  {

	@Test
	public void testUnescapeSql() {
		String input = "a\\\"b";
		String result = StringUtil.unescapeSql(input);
		
		assertEquals("a\"b", result);

		input = "c\\'d";
		result = StringUtil.unescapeSql(input);
		
		assertEquals("c'd", result);
}

}
