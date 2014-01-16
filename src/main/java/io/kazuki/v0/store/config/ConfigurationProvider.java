package io.kazuki.v0.store.config;

import io.kazuki.v0.internal.helper.EncodingHelper;
import io.kazuki.v0.internal.helper.ResourceHelper;
import io.kazuki.v0.internal.helper.StringHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.inject.Provider;

public class ConfigurationProvider<T> implements Provider<T> {
  private final String propertyPrefix;
  private final Class<T> configClass;
  private final String propertiesPath;
  private final boolean includeSystemProperties;

  public ConfigurationProvider(String propertyPrefix, Class<T> configClass) {
    this(propertyPrefix, configClass, null, true);
  }

  public ConfigurationProvider(String propertyPrefix, Class<T> configClass,
      @Nullable String propertiesPath, boolean includeSystemProperties) {
    Preconditions.checkNotNull(propertyPrefix, "propertyPrefix must not be null");
    Preconditions.checkNotNull(propertyPrefix, "configClass must not be null");

    if (!propertyPrefix.endsWith(".")) {
      propertyPrefix += ".";
    }

    this.propertyPrefix = propertyPrefix;
    this.configClass = configClass;
    this.propertiesPath = propertiesPath;
    this.includeSystemProperties = includeSystemProperties;
  }

  @Override
  public T get() {
    Map<String, Object> properties = new LinkedHashMap<String, Object>();

    if (propertiesPath != null) {
      addProperties(ResourceHelper.loadProperties(propertiesPath), properties);
    }

    if (includeSystemProperties) {
      addProperties(System.getProperties(), properties);
    }

    if (properties.isEmpty()) {
      throw new IllegalArgumentException("unable to configure instance - no properties set");
    }

    try {
      return EncodingHelper.asValue(properties, configClass);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private void addProperties(Properties inProperties, Map<String, Object> properties) {
    for (Entry<Object, Object> entry : inProperties.entrySet()) {
      String key = (String) entry.getKey();

      if (!key.startsWith(propertyPrefix)) {
        continue;
      }

      String prefixFreeKey = key.substring(propertyPrefix.length());
      String camelCaseKey = StringHelper.toCamelCase(prefixFreeKey);

      properties.put(camelCaseKey, (String) entry.getValue());
    }
  }
}