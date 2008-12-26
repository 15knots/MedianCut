


/**
 * @author Martin Weber
 */
final class DataPoint3ByteFactory implements DataPointFactory<DataPoint3Byte>
{

  /**
   * Creates a new Datapoint that has no values for its dimensions.
   */
  public DataPoint3Byte createPoint()
  {
    return new DataPoint3Byte();
  }

}