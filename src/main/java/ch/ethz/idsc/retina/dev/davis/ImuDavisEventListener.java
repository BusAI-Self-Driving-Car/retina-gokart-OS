// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.dev.davis._240c.ImuDavisEvent;

public interface ImuDavisEventListener extends DavisEventListener {
  void imu(ImuDavisEvent imuDavisEvent);
}
