package io.kazuki.v0.internal.helper;

import io.kazuki.v0.internal.v2schema.Attribute.Type;

public interface SqlTypeHelper {
  String getPrefix();

  String getSqlType(Type type);

  String getInsertIgnore();

  String getPKConflictResolve();

  String quote(String name);

  String getTableOptions();
}
