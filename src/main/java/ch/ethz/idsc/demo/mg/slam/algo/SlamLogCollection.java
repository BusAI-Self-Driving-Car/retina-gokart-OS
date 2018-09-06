// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.util.io.CsvIO;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** save CSV logs when testing the SLAM algorithm offline */
/* package */ class SlamLogCollection extends PeriodicSlamStep implements StartAndStoppable {
  private final GokartPoseInterface gokartLidarPose;
  private final String filename;
  private final List<double[]> logData;

  protected SlamLogCollection(SlamContainer slamContainer, SlamConfig slamConfig, GokartPoseInterface gokartPoseInterface) {
    super(slamContainer, slamConfig.logCollectionUpdateRate);
    this.gokartLidarPose = gokartPoseInterface;
    filename = slamConfig.davisConfig.logFilename();
    logData = new ArrayList<>();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamLogCollectionUtil.savePoseEstimates(currentTimeStamp, gokartLidarPose.getPose(), //
        slamContainer.getPoseUnitless(), logData);
  }

  @Override // from StartAndStoppable
  public void start() {
    // ---
  }

  @Override // from StartAndStoppable
  public void stop() {
    CsvIO.saveToCSV(SlamFileLocations.OFFLINELOGS.inFolder(filename), logData);
    System.out.println("log data successfully saved");
  }
}
