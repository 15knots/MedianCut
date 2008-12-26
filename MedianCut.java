// $Header$
// Copyright © 2008 Martin Weber



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


/**
 * Median cut algorithm, based on <a
 * href="http://en.literateprograms.org/Median_cut_algorithm_(C_Plus_Plus)"
 * >Median cut algorithm (C Plus Plus)</a>.
 * 
 * @author Martin Weber
 */
public class MedianCut<DP extends DataPoint<DP>>
{

  private final DataPointFactory<DP> pointFactory;

  /**
   */
  // @SuppressWarnings("unchecked")
  public MedianCut()
  {
    pointFactory= (DataPointFactory<DP>) new DataPoint3ByteFactory();
  }

  /**
   * Determines the most representative values on the range of the specifed
   * input data for the desired number of quantization level.
   * 
   * @return a list of representative points, one for each desired quantization
   *         level.
   */
  public List<DP> medianCut( DP[] inputData, int desiredQuantizationLevels)
  {
    /**
     * a queue with the Cluster having the longest side to have maximum priority
     */
    PriorityQueue<Cluster> blockQueue= new PriorityQueue<Cluster>();
    // create initial block
    Cluster block1= new Cluster( inputData);
    block1.shrink();
    blockQueue.offer( block1);
    // While the number of clusters is less than desired number...
    while (blockQueue.size() < desiredQuantizationLevels
      && blockQueue.peek().getPointCnt() > 1) {

      // Find the largest side length of any side of any cluster..
      Cluster longestBlock= blockQueue.poll();

      // split off block2 from longestBlock
      Cluster block2= longestBlock.split();
      // Shrink the two new clusters so that they are just large enough to
      // contain their points.
      longestBlock.shrink();
      block2.shrink();

      blockQueue.offer( longestBlock);
      blockQueue.offer( block2);
    }
    // find a representative point for each block and add it to the result...
    ArrayList<DP> result= new ArrayList<DP>();
    while ( !blockQueue.isEmpty()) {
      Cluster block= blockQueue.poll();
      DP averagePoint= block.getRepresentativePoint();
      result.add( averagePoint);
    }
    return result;
  }

  /**
   * A cluster containing a number of points in n-dimensional space. For
   * efficiency reasons, the cluster is implemented as a rectangular block (a
   * cuboidal).
   * 
   * @author Martin Weber
   */
  private class Cluster implements Comparable<Cluster>
  {
    /** value storage. necessary that we have random access to the points */
    private final DP[] points;

    /** number of dimensions in DataPoint3Byte */
    private final int numDimensions;

    /** The offset is the first index of the storage that is used. */
    private final int offset;

    /** The count is the number of points in the Cluster. */
    private int count;

    /** the points corresponding to two opposite corners of the block. */
    private DP minCorner;

    private DP maxCorner;

    /**
     * @param points
     */
    public Cluster( DP[] points)
    {
      this.points= points;
      numDimensions= points[0].getDimensions();
      offset= 0;
      count= points.length;
      initCorners();
    }

    /**
     * Private constructor which shares value array for speed.
     * 
     * @param offset
     * @param count
     * @param points
     */
    private Cluster( int offset, int count, DP[] points)
    {
      this.points= points;
      numDimensions= points[0].getDimensions();
      this.offset= offset;
      this.count= count;
      initCorners();
    }

    /**
     */
    private void initCorners()
    {
      minCorner= MedianCut.this.pointFactory.createPoint();
      minCorner.moveToMinimum();
      maxCorner= MedianCut.this.pointFactory.createPoint();
      maxCorner.moveToMaximum();
    }

    /**
     * Gets the number of points in this block.
     */
    public final int getPointCnt()
    {
      return this.count;
    }

    /**
     * figures out which side (dimension) of the block is longest.
     * 
     * @return the number of dimension with the longest side.
     */
    private short longestSideIndex()
    {
      int maxLen= maxCorner.difference( (short) 0, minCorner);
      short dimension= 0;
      for (short dim= 1; dim < numDimensions; dim++) {
        int diff= maxCorner.difference( dim, minCorner);
        if (diff > maxLen) {
          maxLen= diff;
          dimension= dim;
        }
      }
      return dimension;
    }

    /**
     * Gets the length of the longest side of the block.
     */
    private int longestSideLength()
    {
      short dim= longestSideIndex();
      return maxCorner.difference( dim, minCorner);
    }

    /**
     * Shrinks a block so that it just barely contains its points; that is, its
     * minimum and maximum coordinates are chosen according to the minimum and
     * maximum coordinates of its points.
     */
    public void shrink()
    {
      for (short dim= 0; dim < numDimensions; dim++) {
        final byte value= points[offset].getValue( dim);
        minCorner.setValue( dim, value);
        maxCorner.setValue( dim, value);
      }
      for (int i= 1; i < count; i++) {
        for (short dim= 0; dim < numDimensions; dim++) {
          minCorner.setMin( dim, points[offset + i]);
          maxCorner.setMax( dim, points[offset + i]);
        }
      }
    }

    /**
     * Partitions the points in this block into two sublists. Splitting is done
     * along the largest side in such a way that half the contained points fall
     * into a new cluster.
     * 
     * @return a newly created block with the splitted off points
     */
    public Cluster split()
    {
      // partition the points into two sublists
      final short longestSideIndex= longestSideIndex();
      Comparator<DP> pointComparator= new Comparator<DP>() {

        public int compare( DP lhs, DP rhs)
        {
          return lhs.difference( longestSideIndex, rhs);
        }

      };
      Arrays.sort( points, offset, offset + count, pointComparator);
      int median= (count + 1) / 2;
      Cluster block2= new Cluster( offset + median, count - median, points);
      this.count= median;
      this.initCorners();
      return block2;
    }

    /**
     * Finds a representative point for this block. Implemented to compute the
     * arithmetic mean (average) of all points in the cluster.
     * 
     * @return a representative point for this block
     */
    public DP getRepresentativePoint()
    {
      // To find a representative point for each block, we merely compute the
      // arithmetic mean (average) of all points in the cluster:
      long[] sum= new long[numDimensions];
      for (int i= 0; i < count; i++) {
        for (short dim= 0; dim < numDimensions; dim++) {
          sum[dim]+= points[offset + i].getValue( dim);
        }
      }
      DP averagePoint= MedianCut.this.pointFactory.createPoint();
      for (short dim= 0; dim < numDimensions; dim++) {
        averagePoint.setValue( dim, (byte) (sum[dim] / count));
      }
      return averagePoint;
    }

    /**
     * {@inheritDoc} Compares two blocks by the length of their longest side.
     */
    public int compareTo( Cluster rhs)
    {
      return rhs.longestSideLength() - this.longestSideLength();
    }
  }
}
