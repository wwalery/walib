package dev.walgo.walib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read meta information (MANIFEST.MF) from package, which contains given class.
 *
 * @author Walery Wysotsky {@literal <dev@wysotsky.info>}
 */
public class PackageMetaInfo {

  /** show stacktrace, when exception caught. */
  public static boolean SHOW_STACKTRACE = true;

  private static final Logger LOG = LoggerFactory.getLogger(PackageMetaInfo.class);
  private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
  private static final String CLASS_EXTENSION = ".class";

  private String title;
  private String version;
  private String apiVersion;
  private String builtDate;
  private String author;

  private final String dateFormat;

  /**
   * Default constructor.
   */
  public PackageMetaInfo() {
    this.dateFormat = null;
  }

  /**
   * Constructor with defined date format
   * 
   * @param dateFormat build dat format (ISO by default)
   */
  public PackageMetaInfo(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  /**
   * Read meta info from package, which contains given class.
   *
   * @param clazz Base class for extract information
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
        LOG.error("Can't load manifest from [{}]", manifestPath);
      }
    }
  }

  /**
   * Read meta info from package.
   *
   * @param jarFile JAR file for read info
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
   * Gets title from manifest.
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets version from manifest.
   * 
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Gets build date and time as is (as string).
   * 
   * @return the builtDate
   */
  public String getBuiltDateStr() {
    return builtDate;
  }

  /**
   * Gets build date and time.
   * 
   * @return the builtDate
   */
  public ZonedDateTime getBuiltDate() {
    try {
      if (dateFormat != null) {
        return ZonedDateTime.parse(builtDate, DateTimeFormatter.ofPattern(dateFormat));
      } else {
        return ZonedDateTime.parse(builtDate);
      }
    } catch (Throwable ex) {
      if (SHOW_STACKTRACE) {
        LOG.error(
            String.format("Can't parse built date [%s] with format [%s]", builtDate, dateFormat),
            ex);
      } else {
        LOG.error("Can't parse built date [{}] with format [{}]", builtDate, dateFormat);
      }
      return ZonedDateTime.now(ZoneId.systemDefault());
    }
  }

  /**
   * Gets package author.
   * 
   * @return the author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Gets API version (if exists).
   * 
   * @return the apiVersion
   */
  public String getAPIVersion() {
    return apiVersion;
  }

  /**
   * Package version full info.
   * 
   * @return package version generated string
   */
  public String versionFullString() {
    String str = String.format(
        "%s, version %s, built %s by %s",
        getTitle(), getVersion(), getBuiltDate(), getAuthor());
    return str;
  }

  /**
   * Build package meta data based on specific class.
   * 
   * @param clazz Class for choose package
   * @return package meta info
   */
  public static PackageMetaInfo build(Class<?> clazz) {
    PackageMetaInfo meta = new PackageMetaInfo();
    meta.read(clazz);
    return meta;
  }

  /**
   * Build package meta data based on current class.
   * 
   * @return package meta info
   */
  public static PackageMetaInfo build() {
    PackageMetaInfo meta = new PackageMetaInfo();
    meta.read(PackageMetaInfo.class);
    return meta;
  }
}
