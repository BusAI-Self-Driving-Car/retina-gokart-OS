// code by jph
package ch.ethz.idsc.owl.car.math;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Tan;

// TODO JPH REL move to owl
public enum TurningGeometry {
  ;
  /** 0.01[rad] will result in ~100[m] offset for x_front == 1[m] */
  static final Chop CHOP = Chop._02;

  /** @param ratio with unit "m^-1"
   * @return radius with unit "m" */
  public static Optional<Scalar> offset_y(Scalar ratio) {
    return CHOP.allZero(ratio) //
        ? Optional.empty()
        : Optional.of(ratio.reciprocal());
  }

  /** inverse function of {@link ChassisGeometry#steerAngleForTurningRatio(Scalar)}
   * 
   * @param x_front distance from rear to front axle
   * @param angle of steering
   * @return center of circle of rotation of a vehicle on the y-axis
   * except when the vehicle is headed straight, in which case
   * Optional.empty() is returned */
  public static Optional<Scalar> offset_y(Scalar x_front, Scalar angle) {
    return CHOP.allZero(angle) //
        ? Optional.empty()
        : Optional.of(x_front.divide(Tan.FUNCTION.apply(angle)));
  }
}
