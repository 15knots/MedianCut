// $Header$
// Copyright © 2008 Martin Weber



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;


/**
 * Median cut algorithm, based on <a
 * href="http://en.literateprograms.org/Median_cut_algorithm_(C_Plus_Plus)"
 * >Median cut algorithm (C Plus Plus)</a>.<br>
 * Specialized for boat speed values.
 * 
 * @author Martin Weber
 */
public class MedianCut4BoatSpeed
{

  /**
   */
  // @SuppressWarnings("unchecked")
  public MedianCut4BoatSpeed()
  {}

  /**
   * Determines the most representative values on the range of the specifed
   * input data for the desired number of quantization level.
   * 
   * @return a list of clusters containing the input points, one for each
   *         desired quantization level.
   */
  public List<MedianCut4BoatSpeed.Cluster> medianCut( List<Float> inputData,
    int desiredQuantizationLevels)
  {
    /**
     * a queue with the ClusterImpl having the longest side to have maximum
     * priority
     */
    PriorityQueue<ClusterImpl> blockQueue= new PriorityQueue<ClusterImpl>();
    // create initial block
    ClusterImpl longestBlock=
      new ClusterImpl( inputData.toArray( new Float[inputData.size()]));
    longestBlock.shrink();
    blockQueue.offer( longestBlock);
    // While the number of clusters is less than desired number...
    while (blockQueue.size() < desiredQuantizationLevels
      && blockQueue.peek().getPointCnt() > 1) {

      // Find the largest side length of any side of any cluster..
      longestBlock= blockQueue.poll();

      // split off block2 from longestBlock
      ClusterImpl block2= longestBlock.split();
      // Shrink the two new clusters so that they are just large enough to
      // contain their points.
      longestBlock.shrink();
      block2.shrink();

      blockQueue.offer( longestBlock);
      blockQueue.offer( block2);
    }
    // for each block add it to the result...
    ArrayList<Cluster> result= new ArrayList<Cluster>(blockQueue.size());
    while ( !blockQueue.isEmpty()) {
      ClusterImpl block= blockQueue.poll();
      result.add( block);
    }
    return result;
  }

  // //////////////////////////////////////////////////////////////////
  // inner classes
  // //////////////////////////////////////////////////////////////////
  /**
   * A cluster containing a number of points.
   * 
   * @author Martin Weber
   */
  public interface Cluster
  {
    /**
     * Gets the point with thre minimum value in this cluster.
     */
    public Float getMinimumPoint();

    /**
     * Gets the point with thre maximum value in this cluster.
     */
    public Float getMaximumPoint();

    /**
     * Finds a representative point for this block. Implemented to compute the
     * arithmetic mean (average) of all points in the cluster.
     * 
     * @return a representative point for this block
     */
    public abstract Float getRepresentativePoint();

  }

  /**
   * A cluster containing a number of points in n-dimensional space. For
   * efficiency reasons, the cluster is implemented as a rectangular block (a
   * cuboidal).
   * 
   * @author Martin Weber
   */
  private class ClusterImpl implements Comparable<ClusterImpl>, Cluster
  {
    /** value storage. necessary that we have random access to the points */
    private final Float[] points;

    /** number of dimensions in DataPoint3Byte */
    // private final int numDimensions;
    /** The offset is the first index of the storage that is used. */
    private final int offset;

    /** The count is the number of points in the ClusterImpl. */
    private int count;

    /** the points corresponding to two opposite corners of the block. */
    private float minCorner;

    private float maxCorner;

    /**
     * @param points
     */
    public ClusterImpl( Float[] points)
    {
      this.points= points;

      // numDimensions= points[0].getDimensions();
      offset= 0;
      count= points.length;
      initCorners();
      // sort points for split()..
      Arrays.sort( points, offset, offset + count);
    }

    /**
     * Private constructor which shares value array for speed.
     * 
     * @param offset
     * @param count
     * @param points
     */
    private ClusterImpl( int offset, int count, Float[] points)
    {
      this.points= points;
      // numDimensions= points[0].getDimensions();
      this.offset= offset;
      this.count= count;
      initCorners();
    }

    /**
     */
    private void initCorners()
    {
      minCorner= Float.MIN_VALUE;
      maxCorner= Float.MAX_VALUE;
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
      return 0;
    }

    /**
     * Gets the length of the longest side of the block.
     */
    private float longestSideLength()
    {
      return maxCorner - minCorner;
    }

    /**
     * Shrinks a block so that it just barely contains its points; that is, its
     * minimum and maximum coordinates are chosen according to the minimum and
     * maximum coordinates of its points.
     */
    public void shrink()
    {
      float value= points[offset].floatValue();
      minCorner= value;
      maxCorner= value;
      for (int i= 0; i < count; i++) {
        value= points[offset + i].floatValue();
        minCorner= Math.min( minCorner, value);
        maxCorner= Math.max( maxCorner, value);
      }
    }

    /**
     * Partitions the points in this block into two sublists. Splitting is done
     * along the largest side in such a way that half the contained points fall
     * into a new cluster.
     * 
     * @return a newly created block with the splitted off points
     */
    public ClusterImpl split()
    {
      // partition the points into two sublists
      // Arrays.sort( points, offset, offset + count);
      int median= (count + 1) / 2;
      ClusterImpl block2=
        new ClusterImpl( offset + median, count - median, points);
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
    public Float getRepresentativePoint()
    {
      // To find a representative point for each block, we merely compute the
      // arithmetic mean (average) of all points in the cluster:
      double sum= 0.0;
      for (int i= 0; i < count; i++) {
        sum+= points[offset + i].floatValue();
      }
      Float averagePoint= new Float( sum / count);
      return averagePoint;
    }

    /**
     * {@inheritDoc} Compares two blocks by the length of their longest side.
     */
    public int compareTo( ClusterImpl rhs)
    {
      return Float.compare( rhs.longestSideLength(), this.longestSideLength());
    }

    /*-
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      return getClass().getSimpleName() + ", min=" + minCorner + ", max="
        + maxCorner + ", len=" + longestSideLength();
    }

    /*-
     * @see mediancut.MedianCut4BoatSpeed.Cluster#getMaximumPoint()
     */
    public Float getMaximumPoint()
    {
      return minCorner;
    }

    /*-
     * @see mediancut.MedianCut4BoatSpeed.Cluster#getMinimumPoint()
     */
    public Float getMinimumPoint()
    {
      return maxCorner;
    }

  }
}
