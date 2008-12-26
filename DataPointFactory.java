// $Header$
// Copyright © 2008 Martin Weber



/**
 * @param <T>
 *        the type of the data point objects created by the factory
 * @author Martin Weber
 */
interface DataPointFactory<T extends DataPoint<T>>
{

  /**
   * Creates a new Datapoint that has no values for its dimensions.
   */
  public abstract T createPoint();

}