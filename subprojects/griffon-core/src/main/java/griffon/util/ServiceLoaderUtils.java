/*
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Scanner;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ServiceLoaderUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLoaderUtils.class);

    private ServiceLoaderUtils() {

    }

    public static interface LineProcessor {
        void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line);
    }

    public static boolean load(@Nonnull ClassLoader classLoader, @Nonnull String path, @Nonnull Class<?> type, @Nonnull LineProcessor processor) {
        requireNonNull(classLoader, "Argument 'classLoader' must not be null");
        requireNonBlank(path, "Argument 'path' must not be blank");
        requireNonNull(type, "Argument 'type' must not be null");
        requireNonNull(processor, "Argument 'processor' must not be null");
        // "The name of a resource is a /-separated path name that identifies the resource."
        String normalizedPath = path.endsWith("/") ? path : path + "/";

        Enumeration<URL> urls;

        try {
            urls = classLoader.getResources(normalizedPath + type.getName());
        } catch (IOException ioe) {
            LOG.error(ioe.getClass().getName() + " error loading resources of type \"" + type.getName() + "\" from \"" + normalizedPath + "\".");
            return false;
        }

        if (urls == null) return false;

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            LOG.debug("Reading {} definitions from {}", type.getName(), url);

            try (Scanner scanner = new Scanner(url.openStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("#") || isBlank(line)) continue;
                    processor.process(classLoader, type, line);
                }
            } catch (IOException e) {
                LOG.warn("Could not load " + type.getName() + " definitions from " + url, sanitize(e));
            }
        }

        return true;
    }
}
