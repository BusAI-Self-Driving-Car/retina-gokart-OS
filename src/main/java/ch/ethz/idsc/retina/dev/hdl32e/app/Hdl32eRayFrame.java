// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.app;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePosEvent;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePosEventListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRayBlockListener;

/** {@link Hdl32eRayFrame} requires that the binary "urg_provider" is located at
 * /home/{username}/Public/urg_provider
 * 
 * https://sourceforge.net/projects/urgnetwork/files/urg_library/
 * 
 * Quote from datasheet:
 * The light source of the sensor is infrared laser of
 * wavelength 785nm with laser class 1 safety
 * Max. Distance: 4000[mm]
 * 
 * The sensor is designed for indoor use only.
 * The sensor is not a safety device/tool.
 * The sensor is not for use in military applications.
 * 
 * typically the distances up to 5[m] can be measured correctly. */
public class Hdl32eRayFrame implements Hdl32eRayBlockListener, Hdl32ePosEventListener {
  public final JFrame jFrame = new JFrame();
  private Hdl32eRayComponent hdl32eRayComponent = new Hdl32eRayComponent();

  public Hdl32eRayFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 600, 600);
    jFrame.setContentPane(hdl32eRayComponent.jComponent);
    jFrame.setVisible(true);
  }

  @Override
  public void digest(FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    RayContainer rayContainer = new RayContainer();
    rayContainer.position = Arrays.copyOf(floatBuffer.array(), floatBuffer.limit());
    rayContainer.intensity = Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
    hdl32eRayComponent.rayContainer = rayContainer;
    hdl32eRayComponent.jComponent.repaint();
  }

  @Override
  public void positioning(Hdl32ePosEvent hdl32ePosEvent) {
    hdl32eRayComponent.hdl32ePosEvent = hdl32ePosEvent;
  }
}
