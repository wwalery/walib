package dev.walgo.walib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read meta information (MANIFEST.MF) from package, which contains given class.
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class PackageMetaInfo {

  /**
   * show stacktrace, when exception catched.
   */
  public static boolean SHOW_STACKTRACE = true;

  private static final Logger LOG = LoggerFactory.getLogger(PackageMetaInfo.class);
  private static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
  private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
  private static final String CLASS_EXTENSION = ".class";

  private String title;
  private String version;
  private String apiVersion;
  private String builtDate;
  private String author;

  private final String dateFormat;

  public PackageMetaInfo() {
    this.dateFormat = DEFAULT_DATE_FORMAT;
  }

  public PackageMetaInfo(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  /**
   * Read meta info from package, which contains given class.
   *
   * @param clazz
   */
  public void read(Class<?> clazz) {
    String className = clazz.getSimpleName() + CLASS_EXTENSION;
    String classPath = clazz.getResource(className).toString();
    LOG.debug("Class path = {}", classPath);
    String manifestPath;
    if (classPath.startsWith("jar")) {
      manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + MANIFEST_PATH;
    } else if (classPath.startsWith("war")) {
      manifestPath = MANIFEST_PATH;
    } else {
      // Class not from JAR/WAR
      String relativePath = clazz.getName().replace('.', File.separatorChar) + CLASS_EXTENSION;
      String classFolder = classPath.substring(0, classPath.length() - relativePath.length() - 1);
      manifestPath = classFolder + MANIFEST_PATH;
    }
    try {
      try (InputStream stream = new URL(manifestPath).openStream()) {
        read(new Manifest(stream));
      }
    } catch (IOException ex) {
      if (SHOW_STACKTRACE) {
        LOG.error(String.format("Can't load manifest from manifest: [%s]", manifestPath), ex);
      } else {
        LOG.error("Can't load manifest from manifest: [{}]", manifestPath);
      }
    }
  }

  /**
   * Read meta info from package.
   *
   * @param jarFile
   */
  public void read(File jarFile) {
    try {
      try (InputStream stream = new FileInputStream(jarFile)) {
        JarInputStream jar = new JarInputStream(stream);
        read(jar.getManifest());
      }
    } catch (IOException ex) {
      if (SHOW_STACKTRACE) {
        LOG.error(String.format("Can't load manifest from JAR: [%s]", jarFile), ex);
      } else {
        LOG.error("Can't load manifest from JAR: [{}]", jarFile);
      }
    }
  }

  private void read(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    title = attributes.getValue("Implementation-Title");
    version = attributes.getValue("Implementation-Version");
    builtDate = attributes.getValue("Implementation-Time");
    author = attributes.getValue("Built-By");
    apiVersion = attributes.getValue("Specification-Version");
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @return the builtDate
   */
  public String getBuiltDateStr() {
    return builtDate;
  }

  /**
   * @return the builtDate
   */
  public LocalDate getBuiltDate() {
    try {
      return LocalDate.parse(builtDate, DateTimeFormatter.ofPattern(dateFormat));
    } catch (Throwable ex) {
      if (SHOW_STACKTRACE) {
        LOG.error(
                String.format("Can't parse built date [%s] with format [%s]", builtDate, dateFormat), ex);
      } else {
        LOG.error("Can't parse built date [{}] with format [{}]", builtDate, dateFormat);
      }
      return LocalDate.now();
    }
  }

  /**
   * @return the author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * @return the apiVersion
   */
  public String getAPIVersion() {
    return apiVersion;
  }

  public String versionFullString() {
    String str = String.format(
                    "%s, version %s, built %s by %s",
                    getTitle(), getVersion(), getBuiltDate(), getAuthor());
    return str;
  }

  public static PackageMetaInfo build(Class<?> clazz) {
    PackageMetaInfo meta = new PackageMetaInfo();
    meta.read(clazz);
    return meta;
  }

  public static PackageMetaInfo build() {
    PackageMetaInfo meta = new PackageMetaInfo();
    meta.read(PackageMetaInfo.class);
    return meta;
  }

}
