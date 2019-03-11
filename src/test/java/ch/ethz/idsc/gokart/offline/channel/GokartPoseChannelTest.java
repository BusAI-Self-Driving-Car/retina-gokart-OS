// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapterTest;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class GokartPoseChannelTest extends TestCase {
  public void testSimple() throws IOException {
    GokartLogInterface gokartLogInterface = GokartLogAdapterTest.FULL;
    // ---
    OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPoseChannel.INSTANCE);
    OfflineLogPlayer.process(gokartLogInterface.file(), offlineTableSupplier);
    Tensor tensor = offlineTableSupplier.getTable().map(CsvFormat.strict());
    assertEquals(Dimensions.of(tensor), Arrays.asList(36, 5));
    // check that pose quality is in the interval [0, 1]
    long count = tensor.get(Tensor.ALL, 4).stream() //
        .map(Scalar.class::cast) //
        .filter(Clips.unit()::isOutside) //
        .count();
    assertEquals(count, 0);
  }
}
