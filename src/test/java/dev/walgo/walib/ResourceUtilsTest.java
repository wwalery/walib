package dev.walgo.walib;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceUtilsTest {

  @Test
  public void testFindResourceFiles_zip() {
    System.setProperty("loader.path", ResourceLoaderTest.TEST_JAR);
    String resourceRegex = "\\/MetaInfo\\.class";
    List<String> expResult = List.of("dev/walgo/walib/MetaInfo.class");
    List<String> result = ResourceUtils.findResourceFiles(resourceRegex);
    Assertions.assertEquals(expResult, result);
  }

  @Test
  public void testFindResourceFiles_dir() {
    String resourceRegex = "README\\.md";
    List<String> expResult = List.of("test.dir/sub.dir/README.md");
    List<String> result = ResourceUtils.findResourceFiles(resourceRegex);
    Assertions.assertEquals(expResult, result);
  }

  @Test
  public void testGetClass() {
    String className = "dev.walgo.walib.PackageMetaInfo";
    Class<?> expResult = PackageMetaInfo.class;
    Class<?> result = ResourceUtils.getClass(className);
    Assertions.assertEquals(expResult, result);
  }

  @Test
  public void testClassFromResource() {
    String resourceName = "dev/walgo/walib/PackageMetaInfo.class";
    Class<?> result = ResourceUtils.classFromResource(resourceName);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(
        resourceName.replace('/', '.').substring(0, resourceName.length() - ".class".length()),
        result.getName());
  }

  @Test
  public void testFindClassesFromResources_withPackage() {
    String packageName = "dev.walgo.walib";
    List<Class<?>> result = ResourceUtils.findClassesFromResources(packageName, Object.class);
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.stream().anyMatch(it -> it.equals(PackageMetaInfo.class)));
  }

  @Test
  public void testFindClassesFromResources_all() {
    List<Class<?>> result = ResourceUtils.findClassesFromResources(null, Object.class);
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.stream().anyMatch(it -> it.equals(PackageMetaInfo.class)));
  }
}
