package dev.walgo.walib;

import java.io.File;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class PackageMetaInfoTest {
  
  /**
   * Test of getTitle method, of class MetaInfo.
   */
  @Test
  public void testReadAllFromJAR() {
    PackageMetaInfo instance = new PackageMetaInfo("test.jar");
    instance.read(new File("build/resources/test/test.jar"));
    assertAll(
            () -> assertEquals("Java common library", instance.getTitle()),
            () -> assertEquals("1.0.0", instance.getVersion()),
            () -> assertEquals("2020-07-26 21:56:58", instance.getBuiltDateStr()),
            () -> assertEquals("walery", instance.getAuthor())
    );
  }

}
