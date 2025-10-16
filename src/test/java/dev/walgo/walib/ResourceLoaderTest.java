package dev.walgo.walib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceLoaderTest {

    static final String TEST_JAR = "build/resources/test/test.jar";
    static final String TEST_ZIP = "build/resources/test/test.zip";
    static final String TEST_DIR = "build/resources/test/test.dir";

    @Test
    public void testLoad() {
        Map<String, List<String>> result = ResourceLoader.loadFromClasspath();
        assertFalse(result.isEmpty());
        System.setProperty("loader.path", TEST_JAR);
        Map<String, List<String>> resultWithSpring = ResourceLoader.loadFromClasspath();
        assertEquals(result.size() + 1, resultWithSpring.size());
        assertNotNull(resultWithSpring.get(TEST_JAR));
    }

    @Test
    public void testLoadFromUrl_Jar() {
        String testFile = TEST_JAR;
        List<String> expResult = List.of("META-INF/MANIFEST.MF", "dev/walgo/walib/MetaInfo.class");
        Map<String, List<String>> result = ResourceLoader.load(testFile);
        assertTrue(result.containsKey(testFile));
        assertEquals(expResult, result.get(testFile));
    }

    @Test
    public void testLoadFromUrl_Zip() {
        String testFile = TEST_ZIP;
        List<String> expResult = List.of("build.xml");
        Map<String, List<String>> result = ResourceLoader.load(testFile);
        assertTrue(result.containsKey(testFile));
        assertEquals(expResult, result.get(testFile));
    }

    @Test
    public void testLoadFromUrl_Dir() {
        String testDir = TEST_DIR;
        List<String> expResult = List.of("build.sh", "sub.dir/README.md");
        Map<String, List<String>> result = ResourceLoader.load(testDir);
        Assertions.assertThat(result).containsKey(testDir);
        Assertions.assertThat(result.get(testDir)).containsAll(expResult);
    }

    @Test
    public void testListFromClassloader_test() {
        List<String> expResult = List.of("dev/walgo/walib/CommonsTest.class", "dev/walgo/walib/db",
                "dev/walgo/walib/PackageMetaInfoTest.class", "dev/walgo/walib/ResourceLoaderTest.class",
                "dev/walgo/walib/ResourceUtilsTest.class");
        List<String> result = ResourceLoader.listFromClassLoader("dev/walgo/walib");
        assertEquals(expResult, result);
    }

}
