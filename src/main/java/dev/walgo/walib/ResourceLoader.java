package dev.walgo.walib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load resource list from various sources.
 *
 * @author Walery Wysotsky {@literal <dev@wysotsky.info>}
 */
public class ResourceLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

    private ResourceLoader() {
    }

    /**
     * Load list of all resources (classes too) from classpath. Also supports Spring classloader.
     *
     * @return Map of classpath entry -> resource list classpath entry could be jar/war file or directory
     */
    public static final Map<String, List<String>> loadFromClasspath() {

        Map<String, List<String>> result = new HashMap<>();

        // read entries from system classpath
        String classPath = System.getProperty("java.class.path");

        if (classPath != null && !classPath.isEmpty()) {
            String pathSeparator = System.getProperty("path.separator");
            String[] classPathArray = StringUtils.split(classPath, pathSeparator);
            for (String entry : classPathArray) {
                Map<String, List<String>> resources = load(entry);
                result.putAll(resources);
            }
        }

        // Spring classpath
        classPath = System.getProperty("loader.path");
        if (classPath != null && !classPath.isEmpty()) {
            String[] classPathArray = StringUtils.split(classPath, ",");
            for (String entry : classPathArray) {
                Map<String, List<String>> resources = load(entry);
                result.putAll(resources);
            }
        }

        return result;
    }

    /**
     * Extract list of resources from given path. Supported JAR, ZIP and directory as resource container.
     *
     * @param path resource container location
     * @return list of all container resources
     */
    public static Map<String, List<String>> load(String path) {
        if (path.endsWith(".jar")) {
            return loadJar(path);
        } else if (path.endsWith(".zip")) {
            return Map.of(path, loadZip(path));
        } else {
            return Map.of(path, loadDirectory(path));
            // } else {
            // LOG.error("Protocol [{}] for url [{}] not supported", url.getProtocol(), url);
            // return new ArrayList<>();
            // throw new IllegalArgumentException(String.format("Protocol [%s] for url [%s] not
            // supported",
            // url.getProtocol(), url));
        }
    }

    /**
     * Gets resource names from directory.
     *
     * @param path path to resources
     * @return list of resource names
     */
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

    /**
     * Load resources from JAR.
     *
     * @param path JAR file name
     * @return List of resource names
     */
    public static Map<String, List<String>> loadJar(String path) {
        // It does not work with the filesystem: we must
        // be in the case of a package contained in a jar file.
        LOG.debug("Process JAR: [{}]", path);
        Map<String, List<String>> result = new HashMap<>();
        List<String> items = new ArrayList<>();
        result.put(path, items);
        try {
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                if (!entry.isDirectory()) {
                    String entryname = entry.getName();
                    items.add(entryname);
                }
            }
// check classpath in manifest.mf
            Manifest manifest = jarFile.getManifest();
            if (manifest != null) {
                String classPath = manifest.getMainAttributes().getValue("Class-Path");
                if (classPath != null) {
                    for (String subPath : StringUtils.split(classPath, ' ')) {
                        try {
                            URI uri = new URI(subPath);
                            String fileName = uri.toURL().getFile();
                            Map<String, List<String>> subData = load(fileName);
                            result.putAll(subData);
                        } catch (Exception ex) {
                            LOG.error("Manifest: error on load content from JAR: [{}]: {}", subPath, ex.getMessage());
                        }
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error("Error on load content from JAR: [{}]", path, ex);
        }
        return result;
    }

    /**
     * Load resources from ZIP.
     *
     * @param path JAR file name
     * @return List of resource names
     */
    public static List<String> loadZip(String path) {
        LOG.debug("Process ZIP: [{}]", path);
        List<String> result = new ArrayList<>();
        try {

            ZipFile zip = new ZipFile(path);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                // LOG.trace("Process ZIP entry: {}", entry.getName());
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

    /**
     * List resources from given class loader.
     * 
     * @param classLoader class loader
     * @param path        search path
     * @return list of resources
     */
    public static List<String> listFromClassLoader(ClassLoader classLoader, String path) {
        List<String> result = new ArrayList<>();
        try (InputStream in = classLoader.getResourceAsStream(path)) {
            if (in == null) {
                LOG.debug("Resource [{}] not found in classloader", path);
                return result;
            }
            try (InputStreamReader reader = new InputStreamReader(in, Charset.defaultCharset())) {
                try (BufferedReader br = new BufferedReader(reader)) {
                    String resource;
                    while ((resource = br.readLine()) != null) {
                        result.add(path + "/" + resource);
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error("Error on list content from classloader: [{}]", path, ex.getMessage());
        }
        return result;
    }

    /**
     * List resources from current class loader.
     * 
     * @param path search path
     * @return list of resources
     */
    public static List<String> listFromClassLoader(String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return listFromClassLoader(classLoader, path);
    }

}
