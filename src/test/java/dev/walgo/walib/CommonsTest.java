package dev.walgo.walib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** @author Walery Wysotsky <dev@wysotsky.info> */
public class CommonsTest {

  /** Test of getTitle method, of class MetaInfo. */
  @Test
  public void testCoalesceFirst() {
    String result = Commons.coalesce("first", null, "second");
    assertEquals("first", result);
  }

  @Test
  public void testCoalesceLast() {
    String result = Commons.coalesce(null, null, "second");
    assertEquals("second", result);
  }

  @Test
  public void testCoalesceNull() {
    String result = Commons.coalesce(null, null);
    assertEquals(null, result);
  }


}
