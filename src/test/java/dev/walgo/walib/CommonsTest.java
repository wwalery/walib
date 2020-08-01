package dev.walgo.walib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** @author Walery Wysotsky <dev@wysotsky.info> */
public class CommonsTest {

  private static final String TEST_FIRST = "first";
  private static final String TEST_SECOND = "second";

  /** Test of getTitle method, of class MetaInfo. */
  @Test
  public void testCoalesceFirst() {
    String result = Commons.coalesce(TEST_FIRST, null, TEST_SECOND);
    assertEquals(TEST_FIRST, result);
  }

  @Test
  public void testCoalesceLast() {
    String result = Commons.coalesce(null, null, TEST_SECOND);
    assertEquals(TEST_SECOND, result);
  }

  @Test
  public void testCoalesceNull() {
    String result = Commons.coalesce(null, null);
    assertNull(result);
  }
}
