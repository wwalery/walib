package dev.walgo.walib;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.junit.jupiter.api.Test;

public class PackageMetaInfoTest {

  /**
   * Test read all from jar.
   */
  @Test
  public void testReadAllFromJar() {
    PackageMetaInfo instance = new PackageMetaInfo("test.jar");
    instance.read(new File("build/resources/test/test.jar"));
    assertAll(
        () -> assertEquals("Java common library", instance.getTitle()),
        () -> assertEquals("1.0.0", instance.getVersion()),
        () -> assertEquals("2020-07-26 21:56:58", instance.getBuiltDateStr()),
        () -> assertEquals("walery", instance.getAuthor()));
  }
}
