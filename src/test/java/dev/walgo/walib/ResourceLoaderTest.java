package dev.walgo.walib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** @author Walery Wysotsky <dev@wysotsky.info> */
public class ResourceLoaderTest {

  static final String TEST_JAR = "build/resources/test/test.jar";
  static final String TEST_ZIP = "build/resources/test/test.zip";
  static final String TEST_DIR = "build/resources/test/test.dir";

  /** Test of load method, of class ClasspathResourceUtil. */
  @Test
  public void testLoad() {
    Map<String, List<String>> result = ResourceLoader.loadFromClasspath();
    assertFalse(result.isEmpty());
    System.setProperty("loader.path", TEST_JAR);
    Map<String, List<String>> resultWithSpring = ResourceLoader.loadFromClasspath();
    assertEquals(result.size() + 1, resultWithSpring.size());
    assertNotNull(resultWithSpring.get(TEST_JAR));
    // TODO review the generated test code and remove the default call to fail.
  }

  /** Test of loadFromURL method, of class ClasspathResourceUtil. */
  @Test
  public void testLoadFromUrl_Jar() throws MalformedURLException {
    String testFile = TEST_JAR;
    List<String> expResult = List.of("META-INF/MANIFEST.MF", "dev/walgo/walib/MetaInfo.class");
    List<String> result = ResourceLoader.load(testFile);
    assertEquals(expResult, result);
  }

  /** Test of loadFromURL method, of class ClasspathResourceUtil. */
  @Test
  public void testLoadFromUrl_Zip() throws MalformedURLException {
    String testFile = TEST_ZIP;
    List<String> expResult = List.of("build.xml");
    List<String> result = ResourceLoader.load(testFile);
    assertEquals(expResult, result);
  }

  @Test
  public void testLoadFromUrl_Dir() throws MalformedURLException {
    String testDir = TEST_DIR;
    List<String> expResult = List.of("build.sh", "sub.dir/README.md");
    List<String> result = ResourceLoader.load(testDir);
    assertEquals(expResult, result);
  }
}
