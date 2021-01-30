package dev.walgo.walib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load resource list from various sources.
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class ResourceLoader {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

  private ResourceLoader() {}

  /**
   * Load list of all resources (classes too) from classpath. Also supports Spring classloader.
   *
   * @return Map of classpath entry -> resource list classpath entry could be jar/war file or
   *     directory
   */
  public static final Map<String, List<String>> loadFromClasspath() {

    Map<String, List<String>> result = new HashMap<>();

    // read entries from system classpath
    String classPath = System.getProperty("java.class.path");

    if ((classPath != null) && !classPath.isEmpty()) {
      String pathSeparator = System.getProperty("path.separator");
      String[] classPathArray = classPath.split(pathSeparator);
      for (String entry : classPathArray) {
        result.put(entry, null);
      }
    }

    // Spring classpath
    classPath = System.getProperty("loader.path");
    if ((classPath != null) && !classPath.isEmpty()) {
      String[] classPathArray = classPath.split(",");
      for (String entry : classPathArray) {
        result.put(entry, null);
      }
    }

    for (Map.Entry<String, List<String>> entry : result.entrySet()) {
      List<String> content = load(entry.getKey());
      entry.setValue(content);
    }
    return result;
  }

  /**
   * Extract list of resources from given path. Supported JAR, ZIP and directory as resource
   * container.
   *
   * @param path resource container location
   * @return list of all container resources
   */
  public static List<String> load(String path) {
    if (path.endsWith(".jar")) {
      return loadJar(path);
    } else if (path.endsWith(".zip")) {
      return loadZip(path);
    } else {
      return loadDirectory(path);
      //      } else {
      //        LOG.error("Protocol [{}] for url [{}] not supported", url.getProtocol(), url);
      //        return new ArrayList<>();
      //        throw new IllegalArgumentException(String.format("Protocol [%s] for url [%s] not
      // supported",
      //                url.getProtocol(), url));
    }
  }

  public static List<String> loadDirectory(String path) {
    // Get a File object for the package
    File directory = new File(path);
    LOG.debug("Process directory: [{}]", path);
    // System.out.println ("\tlooking in " + directory);
    if (!directory.exists()) {
      LOG.warn("Path [{}] not found or it's not directory", path);
      return new ArrayList<>();
    }
    return loadDirectory(directory, new ArrayList<>(), "");
  }

  private static List<String> loadDirectory(File directory, List<String> content, String baseDir) {
    File[] fileList = directory.listFiles();
    if (fileList != null) {
      for (File file : fileList) {
        if (file.isFile()) {
          content.add(baseDir + file.getName());
        } else if (file.isDirectory()) {
          loadDirectory(file, content, baseDir + file.getName() + "/");
        } else {
          LOG.info("Ignore [{}] because of unsupported type", directory.getPath());
        }
      }
    }
    return content;
  }

  public static List<String> loadJar(String path) {
    // It does not work with the filesystem: we must
    // be in the case of a package contained in a jar file.
    List<String> result = new ArrayList<>();
    try {
      JarFile jarFile = new JarFile(path);
      Enumeration<JarEntry> e = jarFile.entries();
      while (e.hasMoreElements()) {
        JarEntry entry = e.nextElement();
        if (!entry.isDirectory()) {
          String entryname = entry.getName();
          result.add(entryname);
        }
      }
    } catch (IOException ex) {
      LOG.error("Error on load content from JAR: [{}]", path, ex);
    }
    return result;
  }

  public static List<String> loadZip(String path) {
    List<String> result = new ArrayList<>();
    try {

      ZipFile zip = new ZipFile(path);
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
        //    LOG.trace("Process ZIP entry: {}", entry.getName());
        ZipEntry entry = entries.nextElement();
        if (!entry.isDirectory()) {
          result.add(entry.getName());
        }
      }
    } catch (IOException ex) {
      LOG.error("Error on load content from ZIP: [{}]", path, ex);
    }
    return result;
  }
}
