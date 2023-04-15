package dev.walgo.walib.db;

import org.immutables.value.Value;

/**
 * Database table column info.
 */
//CHECKSTYLE:OFF
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE, overshadowImplementation = true, jdkOnly = true)
public abstract class ColumnInfo extends AbstractFieldInfo {
//CHECKSTYLE:ON

  /**
   * Is column primary key?
   * 
   * @return TRUE/FALSE
   */
  public abstract boolean isPrimaryKey();

  /**
   * Column info builder.
   */
  public static class Builder extends ColumnInfoBuilder {
  }

}
