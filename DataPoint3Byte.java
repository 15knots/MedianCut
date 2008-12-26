


/**
 * A data point holding three dimensions of type 'byte'.
 * 
 * @author Martin Weber
 */
public class DataPoint3Byte implements DataPoint<DataPoint3Byte>
{
  /**  */
  private static final int NUM_DIMENSIONS= 3;

  private final byte[] value;

  /**
   * Creates a new Datapoint that has no values for its dimensions.
   */
  DataPoint3Byte()
  {
    value= new byte[NUM_DIMENSIONS];
  }

  /**
   * Creates a new Datapoint with the specified values for its dimensions.
   */
  public DataPoint3Byte( byte[] values)
  {
    if (values.length != NUM_DIMENSIONS)
      throw new IllegalArgumentException( "value array too small");
    value= values;
  }

  /**
   * Gets the number of dimensions of this data point.
   */
  public short getDimensions()
  {
    return NUM_DIMENSIONS;
  }

  /**
   * Gets the value for the specified dimension.
   */
  public final byte getValue( short dimension)
  {
    return value[dimension];
  }

  /**
   * Sets the value for the specified dimension.
   */
  public final void setValue( short dimension, byte value)
  {
    this.value[dimension]= value;
  }

  /**
   * Sets the values for each dimension of this point to their minimum possible
   * value, for exampe {@code Byte#MIN_VALUE}.
   */
  public final void moveToMinimum()
  {
    for (short dim= 0; dim < getDimensions(); dim++) {
      value[dim]= Byte.MIN_VALUE;
    }
  }

  /**
   * Sets the values for each dimension of this point to their maximum possible
   * value, for exampe {@code Byte#MAX_VALUE}.
   */
  public final void moveToMaximum()
  {
    for (short dim= 0; dim < getDimensions(); dim++) {
      value[dim]= Byte.MAX_VALUE;
    }
  }

  /**
   * Gets the difference for the specifed dimension to the specified point.
   * Usually implemented as
   * 
   * <pre>
   * this.getValue( dimension) - rhs.getValue( dimension);
   * </pre>
   */
  public final int difference( short dimension, DataPoint3Byte rhs)
  {
    return this.value[dimension] - rhs.value[dimension];
  }

  /**
   * Sets the value for the specifed dimension to be the minimum of this point
   * and the specified point. Usually implemented as
   * 
   * <pre>
   * this.setValue(dimension, 
   *       Math.min( this.getValue( dimension), rhs.getValue( dimension))
   * </pre>
   */
  public final void setMin( short dimension, DataPoint3Byte rhs)
  {
    if (this.value[dimension] > rhs.value[dimension])
      this.value[dimension]= rhs.value[dimension];
  }

  /**
   * Sets the value for the specifed dimension to be the maximum of this point
   * and the specified point. Usually implemented as
   * 
   * <pre>
   * this.setValue(dimension, 
   *       Math.max( this.getValue( dimension), rhs.getValue( dimension))
   * </pre>
   */
  public final void setMax( short dimension, DataPoint3Byte rhs)
  {
    if (this.value[dimension] < rhs.value[dimension])
      this.value[dimension]= rhs.value[dimension];
  }
}