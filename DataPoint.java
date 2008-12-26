// $Header$
// Copyright © 2008 Martin Weber



/**
 * A data point for the MedianCut to operate on.
 * 
 * @author Martin Weber
 */
public interface DataPoint<T extends DataPoint<T>>
{

  /**
   * Gets the number of dimensions of this data point.
   */
  public abstract short getDimensions();

  /**
   * Gets the value for the specified dimension.
   */
  public abstract byte getValue( short dimension);

  /**
   * Sets the value for the specified dimension.
   */
  public abstract void setValue( short dimension, byte value);

  /**
   * Sets the values for each dimension of this point to their minimum possible
   * value, for exampe {@code Byte#MIN_VALUE}.
   */
  public abstract void moveToMinimum();

  /**
   * Sets the values for each dimension of this point to their maximum possible
   * value, for exampe {@code Byte#MAX_VALUE}.
   */
  public abstract void moveToMaximum();

  /**
   * Gets the difference for the specifed dimension to the specified point.
   * Usually implemented as
   * 
   * <pre>
   * this.getValue( dimension) - rhs.getValue( dimension);
   * </pre>
   */
  public abstract int difference( short dimension, T rhs);

  /**
   * Sets the value for the specifed dimension to be the minimum of this point
   * and the specified point. Usually implemented as
   * 
   * <pre>
   * this.setValue(dimension, 
   *       Math.min( this.getValue( dimension), rhs.getValue( dimension))
   * </pre>
   */
  public abstract void setMin( short dimension, T rhs);

  /**
   * Sets the value for the specifed dimension to be the maximum of this point
   * and the specified point. Usually implemented as
   * 
   * <pre>
   * this.setValue(dimension, 
   *       Math.max( this.getValue( dimension), rhs.getValue( dimension))
   * </pre>
   */
  public abstract void setMax( short dimension, T rhs);

}