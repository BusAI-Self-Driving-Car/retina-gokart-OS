// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.retina.util.img.ImageReflect;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** gives an image of the maps generated by the SLAM algorithm */
/* package */ class SlamMapFrame {
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final byte[] bytes;
  private final double cornerX;
  private final double cornerY;
  private final double cellDim;
  private final int kartLength;
  private final int waypointRadius;
  private final int mapWidth;
  private final int mapHeight;

  SlamMapFrame(SlamConfig slamConfig) {
    mapWidth = slamConfig.mapWidth();
    mapHeight = slamConfig.mapHeight();
    cornerX = Magnitude.METER.toDouble(slamConfig.corner.Get(0));
    cornerY = Magnitude.METER.toDouble(slamConfig.corner.Get(1));
    cellDim = Magnitude.METER.toDouble(slamConfig.cellDim);
    kartLength = slamConfig.kartLength();
    waypointRadius = slamConfig.waypointRadius();
    bufferedImage = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_BYTE_INDEXED);
    graphics = bufferedImage.createGraphics();
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
  }

  /** draws an ellipse representing the vehicle pose onto the map frame
   * 
   * @param pose with or without units
   * @param color in which ellipse representing vehicle is filled */
  public void addGokartPose(Tensor pose, Color color) {
    SlamMapFrameUtil.addGokartPose(pose, color, graphics, cornerX, cornerY, cellDim, kartLength);
  }

  /** draws all detected way points. Visible way points are drawn green and non visible ones orange
   * 
   * @param slamWaypoints */
  public void drawSlamWaypoints(List<SlamWaypoint> slamWaypoints) {
    VisGeneralUtil.clearFrame(bytes);
    for (int i = 0; i < slamWaypoints.size(); i++) {
      Color color = slamWaypoints.get(i).isVisible() ? Color.GREEN : Color.ORANGE;
      SlamMapFrameUtil.drawWaypoint(graphics, slamWaypoints.get(i), color, waypointRadius, cornerX, cornerY, cellDim);
    }
  }

  /** draws the way point selected to be followed in blue color
   * 
   * @param slamWaypoint */
  public void drawSelectedSlamWaypoint(SlamWaypoint slamWaypoint) {
    SlamMapFrameUtil.drawWaypoint(graphics, slamWaypoint, Color.BLUE, waypointRadius, cornerX, cornerY, cellDim);
  }

  /** @return frame such that x axis points right and y axis points upwards of underlying map object */
  public BufferedImage getFrame() {
    return ImageReflect.flipHorizontal(bufferedImage);
  }

  public byte[] getBytes() {
    return bytes;
  }
}