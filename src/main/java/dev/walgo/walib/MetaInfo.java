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
public class MetaInfo {

  private static final Logger LOG = LoggerFactory.getLogger(MetaInfo.class);
  private static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
  private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
  private static final String CLASS_EXTENSION = ".class";

  private String title;
  private String version;
  private String apiVersion;
  private String builtDate;
  private String builtBy;
  private final String dateFormat;

  public MetaInfo() {
    this(MetaInfo.class, DEFAULT_DATE_FORMAT);
  }

  public MetaInfo(Class<?> clazz) {
    this(clazz, DEFAULT_DATE_FORMAT);
  }

  public MetaInfo(Class<?> clazz, String dateFormat) {
    this.dateFormat = dateFormat;
    init(clazz);
  }

  public MetaInfo(File jarFile) {
    this.dateFormat = DEFAULT_DATE_FORMAT;
    init(jarFile);
  }

  public MetaInfo(InputStream manifestStream) {
    this.dateFormat = DEFAULT_DATE_FORMAT;
    init(manifestStream);
  }

  private void init(Class<?> clazz) {
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
        init(stream);
      }
    } catch (IOException ex) {
      LOG.error(String.format("Can't load manifest from manifest: [%s]", manifestPath), ex);
    }
  }

  private void init(File jarFile) {
    try {
      try (InputStream stream = new FileInputStream(jarFile)) {
        JarInputStream jar = new JarInputStream(stream);
        init(jar.getManifest());
      }
    } catch (IOException ex) {
      LOG.error(String.format("Can't load manifest from JAR: [%s]", jarFile), ex);
    }
  }

  private void init(InputStream manifestStream) {
    try {
      init(new Manifest(manifestStream));
    } catch (IOException ex) {
      LOG.error("Can't load manifest", ex);
    }
  }

  private void init(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    title = attributes.getValue("Implementation-Title");
    version = attributes.getValue("Implementation-Version");
    builtDate = attributes.getValue("Implementation-Time");
    builtBy = attributes.getValue("Built-By");
    apiVersion = attributes.getValue("Specification-Version");
  }

  /** @return the title */
  public String getTitle() {
    return title;
  }

  /** @return the version */
  public String getVersion() {
    return version;
  }

  /** @return the builtDate */
  public String getBuiltDateStr() {
    return builtDate;
  }

  /** @return the builtDate */
  public LocalDate getBuiltDate() {
    try {
      return LocalDate.parse(builtDate, DateTimeFormatter.ofPattern(dateFormat));
    } catch (Throwable ex) {
      LOG.error(
          String.format("Can't parse built date [%s] with format [%s]", builtDate, dateFormat), ex);
      return LocalDate.now();
    }
  }

  /** @return the builtBy */
  public String getBuiltBy() {
    return builtBy;
  }

  /** @return the apiVersion */
  public String getAPIVersion() {
    return apiVersion;
  }

  public static final String version() {
    return versionString(MetaInfo.class);
  }

  public static final String versionString(Class<?> clazz) {
    MetaInfo info = new MetaInfo(clazz);
    String str =
        String.format(
            "%s, version %s, built %s by %s",
            info.getTitle(), info.getVersion(), info.getBuiltDate(), info.getBuiltBy());
    //  log.info(str);
    return str;
  }

  public static final void print() {
    print(MetaInfo.class);
  }

  public static final void print(Class<?> clazz) {
    String str = versionString(clazz);
    //  log.info(str);
    System.out.println(str);
  }
}
