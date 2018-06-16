// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;

public enum OfflineVectorTables {
  ;
  public static OfflineVectorTable linmotGet() {
    return new OfflineVectorTable(LinmotLcmServer.CHANNEL_GET, LinmotGetEvent::new);
  }

  public static OfflineVectorTable linmotPut() {
    return new OfflineVectorTable(LinmotLcmServer.CHANNEL_PUT, LinmotPutEvent::new);
  }

  public static OfflineVectorTable steerGet() {
    return new OfflineVectorTable(SteerLcmServer.CHANNEL_GET, SteerGetEvent::new);
  }

  public static OfflineVectorTable rimoGet() {
    return new OfflineVectorTable(RimoLcmServer.CHANNEL_GET, RimoGetEvent::new);
  }

  public static OfflineVectorTable rimoPut() {
    return new OfflineVectorTable(RimoLcmServer.CHANNEL_PUT, RimoPutHelper::from);
  }
}
