package dev.walgo.walib.db;

import org.immutables.value.Value;

/**
 * Callable parameter type.
 */
//CHECKSTYLE:OFF
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE, overshadowImplementation = true, jdkOnly = true)
public abstract class ParameterInfo extends AbstractFieldInfo {
//CHECKSTYLE:ON

  /**
   * Kind of column/parameter.
   * 
   * @return kind of column/parameter
   */
  public abstract ColumnType columnType();

  /**
   * Parameter info builder.
   */
  public static class Builder extends ParameterInfoBuilder {
  }

}
