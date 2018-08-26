// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageUtil;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartLookup;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

/** provides general parameters not specific to SLAM or object detection algorithms */
public class DavisConfig {
  // log file parameters
  /** must match name in LogFileLocations and be an extract of a recording */
  public LogFileLocations logFileLocations = LogFileLocations.DUBI16b;
  /** maxDuration */
  public final Scalar logFileDuration = Quantity.of(60, SI.SECOND);
  // general parameters
  /** width of image is required to be an integer */
  public final Scalar width = RealScalar.of(240);
  /** height of image is required to be an integer */
  public final Scalar height = RealScalar.of(180);
  public final Scalar unitConversion = RealScalar.of(1000);
  /** [us] for background activity filter */
  public Scalar filterConstant = Quantity.of(1000, NonSI.MICRO_SECOND);
  /** [-] for FAST corner filter */
  public final Scalar margin = RealScalar.of(4);

  public AbstractFilterHandler createBackgroundActivityFilter() {
    return new BackgroundActivityFilter( //
        Scalars.intValueExact(width), //
        Scalars.intValueExact(height), //
        Magnitude.MICRO_SECOND.toInt(filterConstant));
  }

  public String logFilename() {
    return logFileLocations.name();
  }

  /** @return file specified by parameter {@link #logFileName} */
  public File getLogFile() {
    LogFileLocations logFileLocations = LogFileLocations.valueOf(logFilename());
    if (Objects.isNull(logFileLocations))
      throw new RuntimeException("invalid logFileName: " + logFilename());
    return logFileLocations.getFile();
  }

  /** @return new instance of {@link ImageToGokartUtil} derived from parameters in pipelineConfig */
  public ImageToGokartUtil createImageToGokartUtil() {
    return ImageToGokartUtil.fromMatrix(logFileLocations.calibration(), unitConversion, Scalars.intValueExact(width));
  }

  /** @return new instance of {@link ImageToGokartLookup} derived from parameters in pipelineConfig */
  public ImageToGokartInterface createImageToGokartInterface() {
    return ImageToGokartLookup.fromMatrix( //
        logFileLocations.calibration(), //
        unitConversion, //
        Scalars.intValueExact(width), //
        Scalars.intValueExact(height));
  }

  /** @return new instance of {@link GokartToImageUtil} derived from parameters in pipelineConfig */
  public GokartToImageUtil createGokartToImageUtil() {
    return GokartToImageUtil.fromMatrix(logFileLocations.calibration(), unitConversion);
  }
}
