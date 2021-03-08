package dev.walgo.walib;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various resource manipulations (based on REsourceLoader output).
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class ResourceUtils {

  public static final String CLASS_EXT = ".class";

  private static final Logger LOG = LoggerFactory.getLogger(ResourceUtils.class);
  private static final String REGEX_PATH_DELIMITER = "\\/";
  private static final int MODIFIER_MODULE = 0x8000;

  @SuppressWarnings("rawtypes")
  private static final Map<String, Class> CLASS_WEAK_CACHE = new WeakHashMap<>();

  private static Map<String, List<String>> resourceCache;

  private ResourceUtils() {
    // do nothing
  }

  /**
   * Lazy resource loader. See {@link ResourceLoader#loadFromClasspath() }
   *
   * @return cached resources from class path
   */
  public static Map<String, List<String>> getResourcesFromClasspath() {
    if (resourceCache == null) {
      resourceCache = ResourceLoader.loadFromClasspath();
    }
    return resourceCache;
  }

  /**
   * List resources with names corresponded to regex. E.g: ".*\.class" - all classes
   * "my\/package\/*\.class" - all classes from given package
   *
   * @param resourceRegex
   * @return
   */
  public static List<String> findResourceFiles(String resourceRegex) {
    Pattern pattern =
        (resourceRegex == null) || resourceRegex.isEmpty() ? null : Pattern.compile(resourceRegex);
    Map<String, List<String>> localResources = ResourceLoader.loadFromClasspath();
    List<String> files = new ArrayList<>();
    for (List<String> pathResources : localResources.values()) {
      for (String resource : pathResources) {
        if ((pattern == null) || (pattern.matcher(resource).find())) {
          files.add(resource);
        }
      }
    }
    return files;
  }

  private static String classNameFromResource(String resourceName) {
    if (!resourceName.endsWith(CLASS_EXT)) {
      return null;
    }
    String className =
        resourceName.replace('/', '.').substring(0, resourceName.length() - CLASS_EXT.length());
    return className;
  }

  public static Class<?> getClass(String className) {
    if (CLASS_WEAK_CACHE.containsKey(className)) {
      return CLASS_WEAK_CACHE.get(className);
    }
    try {
      final Class<?> clazz = Class.forName(className);
      CLASS_WEAK_CACHE.put(className, clazz);
      return clazz;
    } catch (Throwable ex) {
      LOG.warn("Class not found: {} - {}", className, ex.getMessage());
      return null;
    }
  }

  /**
   * Load class from resource name.
   *
   * @param resourceName
   * @return loaded class
   */
  @SuppressWarnings("rawtypes")
  public static Class classFromResource(String resourceName) {
    String className = classNameFromResource(resourceName);
    if (className == null) {
      return null;
    }
    return getClass(className);
  }

  /**
   * Load all classes from resources.
   *
   * @param <T> base class type
   * @param basePackage base package for classes - optional
   * @param baseClass - base class (interface) - only descendant classes will be returned.
   * @return list of found and loaded classes
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T> List<Class<? extends T>> findClassesFromResources(
      String basePackage, Class<T> baseClass) {
    List<Class<? extends T>> result = new ArrayList<>();
    String regex = "";
    if ((basePackage != null) && !basePackage.isEmpty()) {
      regex = basePackage.replace(".", REGEX_PATH_DELIMITER) + REGEX_PATH_DELIMITER;
    }
    List<String> resources = findResourceFiles(regex + ".+?\\" + CLASS_EXT);

    for (String fullName : resources) {

      Class clazz = classFromResource(fullName);

      if (clazz != null
          && baseClass.isAssignableFrom(clazz)
          && !clazz.isInterface()
          && !Modifier.isAbstract(clazz.getModifiers())
          && ((clazz.getModifiers() & MODIFIER_MODULE) == 0) // module
      ) {
        LOG.trace("Found class [{}] as instance of {}", clazz.getName(), baseClass.getName());
        Class<? extends T> clazzI = clazz.asSubclass(baseClass);
        result.add(clazzI);
      }
    }
    return result;
  }
}
