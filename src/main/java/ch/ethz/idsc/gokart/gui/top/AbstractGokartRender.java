// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class AbstractGokartRender implements RenderInterface {
  private final GokartPoseInterface gokartPoseInterface;

  public AbstractGokartRender(GokartPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
  }

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor state = gokartPoseInterface.getPose(); // units {x[m], y[m], angle[]}
    Scalar x = Magnitude.METER.apply(state.Get(0));
    Scalar y = Magnitude.METER.apply(state.Get(1));
    Scalar angle = state.Get(2);
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(Tensors.of(x, y, angle)));
    // ---
    protected_render(geometricLayer, graphics);
    // ---
    geometricLayer.popMatrix();
  }

  /** function is invoked with geometricLayer set to location of gokart
   * 
   * @param geometricLayer
   * @param graphics */
  public abstract void protected_render(GeometricLayer geometricLayer, Graphics2D graphics);
}
