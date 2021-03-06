// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LocalizationEmergencyModuleTest extends TestCase {
  public void testAuto() throws Exception {
    final int providerSize = RimoSocket.INSTANCE.getPutProviderSize();
    ModuleAuto.INSTANCE.runOne(LocalizationEmergencyModule.class);
    assertEquals(providerSize + 1, RimoSocket.INSTANCE.getPutProviderSize());
    ModuleAuto.INSTANCE.endOne(LocalizationEmergencyModule.class);
    assertEquals(providerSize, RimoSocket.INSTANCE.getPutProviderSize());
  }

  public void testSimple() {
    LocalizationEmergencyModule localizationEmergencyModule = new LocalizationEmergencyModule();
    final int providerSize = RimoSocket.INSTANCE.getPutProviderSize();
    localizationEmergencyModule.first();
    assertEquals(providerSize + 1, RimoSocket.INSTANCE.getPutProviderSize());
    {
      Optional<RimoPutEvent> putEvent = localizationEmergencyModule.putEvent();
      assertTrue(putEvent.isPresent());
    }
    localizationEmergencyModule.getEvent(GokartPoseEvents.create(Tensors.fromString("{2[m], 3[m], 4}"), RealScalar.ONE));
    {
      Optional<RimoPutEvent> putEvent = localizationEmergencyModule.putEvent();
      assertFalse(putEvent.isPresent());
    }
    localizationEmergencyModule.last();
    assertEquals(providerSize, RimoSocket.INSTANCE.getPutProviderSize());
  }
}