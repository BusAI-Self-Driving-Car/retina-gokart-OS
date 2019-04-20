// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedPredictiveTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.owl.car.math.BicycleAngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ class MPCAggressiveTorqueVectoringPower extends MPCPower {
  private final BicycleAngularSlip bicycleAngularSlip = ChassisGeometry.GLOBAL.getBicycleAngularSlip();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final ImprovedNormalizedTorqueVectoring torqueVectoring = //
      new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  private final MPCSteering mpcSteering;
  // ---
  private final MPCStateEstimationProvider mpcStateEstimationProvider;

  public MPCAggressiveTorqueVectoringPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    this.mpcStateEstimationProvider = Objects.requireNonNull(mpcStateEstimationProvider);
    this.mpcSteering = mpcSteering;
  }

  @Override // from MPCPower
  Optional<Tensor> getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Optional.empty();
    Optional<Tensor> optional = mpcSteering.getSteering(time);
    if (!optional.isPresent())
      return Optional.empty();
    Tensor steering = optional.get();
    Scalar theta = steerMapping.getAngleFromSCE(steering.Get(0)); // steering angle of imaginary front wheel
    Scalar tangentialSpeed = mpcStateEstimationProvider.getState().getUx();
    // compute (negative) angular slip
    Scalar gyroZ = mpcStateEstimationProvider.getState().getdotPsi(); // unit s^-1
    Scalar wantedAcceleration = cnsStep.gokartControl().getaB();
    // get midpoint of powered acceleration range
    // Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    // Scalar midpoint = (Scalar) Mean.of(minmax);
    // more tame version
    AngularSlip angularSlip = bicycleAngularSlip.getAngularSlip(theta, tangentialSpeed, gyroZ);
    return Optional.of(torqueVectoring.getMotorCurrentsFromAcceleration(angularSlip, Ramp.FUNCTION.apply(wantedAcceleration)));
  }
}
