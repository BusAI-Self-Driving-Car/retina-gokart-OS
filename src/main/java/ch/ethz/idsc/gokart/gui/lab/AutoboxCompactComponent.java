// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Color;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.gokart.lcm.LoggerModule;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.gokart.lcm.imu.Vmu932LcmServerModule;
import ch.ethz.idsc.gokart.lcm.imu.Vmu93xImuLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu93xLcmServerBase;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

/* package */ class AutoboxCompactComponent extends ToolbarsComponent implements StartAndStoppable {
  private static final Clip CLIP_AHEAD = Clips.absoluteOne();
  // ---
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.getProvider();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final Vmu93xImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  // private final Vmu93xImuLcmClient vmu932ImuLcmClient = new Vmu932ImuLcmClient();
  private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private final LinmotGetListener linmotGetListener = getEvent -> linmotGetEvent = getEvent;
  private final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  private final Vmu931ImuFrameListener vmu931ImuFrameListener = getEvent -> vmu931ImuFrame = getEvent;
  // private final Vmu931ImuFrameListener vmu932ImuFrameListener = getEvent -> vmu932ImuFrame = getEvent;
  private final LinmotInitButton linmotInitButton = new LinmotInitButton();
  private final MiscResetButton miscResetButton = new MiscResetButton();
  private final SteerInitButton steerInitButton = new SteerInitButton();
  private int davisImuFrameCount = 0;
  private final DavisImuFrameListener davisImuFrameListener = davisImuFrame -> ++davisImuFrameCount;
  private final JTextField jTF_rimoRatePair;
  private final JTextField jTF_linmotTemp;
  private final JTextField jTF_manualControl;
  private final JTextField jTF_ahead;
  private final JTextField jTF_davis240c;
  private final JTextField jTF_vmu931_acc;
  private final JTextField jTF_vmu931_gyr;
  // private final JTextField jTF_vmu932_acc;
  // private final JTextField jTF_vmu932_gyr;
  // private final JTextField jTF_localPose;
  // private final JButton jButtonAppend = new JButton("pose append");
  private final JTextField jTF_localQual;
  private JTextField jTF_uptime = new JTextField();
  // private final Tensor poseList = Tensors.empty();
  private final LoggerModule loggerModule = ModuleAuto.INSTANCE.getInstance(LoggerModule.class);
  // ---
  private GokartPoseEvent gokartPoseEvent;
  private RimoGetEvent rimoGetEvent;
  private LinmotGetEvent linmotGetEvent;
  private Vmu931ImuFrame vmu931ImuFrame;
  // private Vmu931ImuFrame vmu932ImuFrame;

  public void update() {
    {
      linmotInitButton.updateEnabled();
      miscResetButton.updateEnabled();
      steerInitButton.updateEnabled();
      if (Objects.nonNull(rimoGetEvent)) {
        String pair = rimoGetEvent.getAngularRate_Y_pair().map(Round._3).toString();
        jTF_rimoRatePair.setText(pair);
      }
    }
    {
      if (Objects.nonNull(linmotGetEvent)) {
        Scalar temperatureMax = linmotGetEvent.getWindingTemperatureMax();
        Scalar rescaled = LinmotConfig.CLIP_TEMPERATURE.rescale(temperatureMax);
        Color color = ColorFormat.toColor(ColorDataGradients.TEMPERATURE.apply(rescaled));
        jTF_linmotTemp.setText(temperatureMax.map(Round._1).toString());
        jTF_linmotTemp.setBackground(color);
      }
    }
    {
      Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
      {
        String string = optional.isPresent() //
            ? optional.get().toString()
            : ToolbarsComponent.UNKNOWN;
        jTF_manualControl.setText(string);
      }
      {
        String string = ToolbarsComponent.UNKNOWN;
        if (optional.isPresent()) {
          ManualControlInterface manualControlInterface = optional.get();
          Scalar aheadAverage = manualControlInterface.getAheadAverage();
          Scalar rescaled = CLIP_AHEAD.rescale(aheadAverage);
          Color color = ColorFormat.toColor(ColorDataGradients.TEMPERATURE.apply(rescaled));
          jTF_ahead.setBackground(color);
          string = aheadAverage.map(Round._4).toString();
        }
        jTF_ahead.setText(string);
      }
    }
    {
      String text = "#=" + davisImuFrameCount;
      jTF_davis240c.setText(text);
    }
    { // pose coordinates
      // String string = Objects.nonNull(gokartPoseEvent) //
      // ? gokartPoseEvent.getPose().map(Round._3).toString()
      // : ToolbarsComponent.UNKNOWN;
      // jTF_localPose.setText(string);
    }
    {
      if (Objects.nonNull(vmu931ImuFrame)) {
        jTF_vmu931_acc.setText(vmu931ImuFrame.acceleration().map(Round._3).toString());
        jTF_vmu931_gyr.setText(vmu931ImuFrame.gyroscope().map(Round._3).toString());
      }
      // if (Objects.nonNull(vmu932ImuFrame)) {
      // jTF_vmu932_acc.setText(vmu932ImuFrame.acceleration().map(Round._3).toString());
      // jTF_vmu932_gyr.setText(vmu932ImuFrame.gyroscope().map(Round._3).toString());
      // }
    }
    if (Objects.isNull(gokartPoseEvent)) { // pose quality
      jTF_localQual.setText(ToolbarsComponent.UNKNOWN);
      jTF_localQual.setBackground(null);
    } else {
      String string = gokartPoseEvent.getQuality().map(Round._3).toString();
      jTF_localQual.setText(string);
      Color color = ColorFormat.toColor(ColorDataGradients.MINT.apply(RealScalar.ONE.subtract(gokartPoseEvent.getQuality())));
      jTF_localQual.setBackground(color);
    }
    if (Objects.nonNull(loggerModule)) {
      Date date = loggerModule.uptime();
      long diff = System.currentTimeMillis() - date.getTime();
      diff /= 1000;
      jTF_uptime.setText(diff + "[s]");
    }
  }

  public AutoboxCompactComponent() {
    {
      JToolBar jToolBar = createRow("Actuation");
      jToolBar.add(linmotInitButton.getComponent());
      jToolBar.add(miscResetButton.getComponent());
      jToolBar.add(steerInitButton.getComponent());
    }
    jTF_rimoRatePair = createReading("Rimo");
    jTF_linmotTemp = createReading("Linmot");
    jTF_davis240c = createReading("Davis240C");
    jTF_manualControl = createReading("Manual");
    jTF_ahead = createReading("Ahead");
    jTF_vmu931_acc = createReading("Vmu931 acc");
    jTF_vmu931_gyr = createReading("Vmu931 gyr");
    // jTF_vmu932_acc = createReading("Vmu932 acc");
    // jTF_vmu932_gyr = createReading("Vmu932 gyr");
    // ---
    List<Class<? extends AbstractModule>> list = Arrays.asList(Vmu931LcmServerModule.class, Vmu932LcmServerModule.class);
    for (Class<? extends AbstractModule> cls : list) {
      Vmu93xLcmServerBase vmu93xLcmServerBase = ModuleAuto.INSTANCE.getInstance(cls);
      if (Objects.nonNull(vmu93xLcmServerBase)) {
        JToolBar jToolBar = createRow(cls.getSimpleName().substring(0, 6) + " ctrl");
        {
          JButton jButton = new JButton("status");
          StaticHelper.actionListener(jButton, vmu93xLcmServerBase::requestStatus, 3000);
          jToolBar.add(jButton);
        }
        {
          JButton jButton = new JButton("self-test");
          StaticHelper.actionListener(jButton, vmu93xLcmServerBase::requestSelftest, 3000);
          jToolBar.add(jButton);
        }
        {
          JButton jButton = new JButton("calibration");
          StaticHelper.actionListener(jButton, vmu93xLcmServerBase::requestCalibration, 3000);
          jToolBar.add(jButton);
        }
      }
    }
    // jTF_localPose = createReading("Pose");
    jTF_localQual = createReading("Pose quality");
    if (Objects.nonNull(loggerModule))
      jTF_uptime = createReading("Uptime");
    // if (false) {
    // JToolBar jToolBar = createRow("store");
    // jButtonAppend.addActionListener(actionEvent -> {
    // if (Objects.nonNull(gokartPoseEvent)) {
    // Tensor state = gokartPoseEvent.getPose();
    // state = PoseHelper.toUnitless(state);
    // state.set(Round._2, 0);
    // state.set(Round._2, 1);
    // state.set(Round._6, 2);
    // poseList.append(state);
    // try {
    // Put.of(HomeDirectory.file("track.mathematica"), poseList);
    // Export.of(HomeDirectory.file("track.csv"), poseList);
    // } catch (Exception exception) {
    // exception.printStackTrace();
    // }
    // }
    // });
    // jToolBar.add(jButtonAppend);
    // }
    {
      JTextField jTextField = createEditing("tag driver");
      jTextField.setText("");
      jTextField.addActionListener(e -> {
        BinaryBlob binaryBlob = new BinaryBlob();
        binaryBlob.data = jTextField.getText().getBytes();
        binaryBlob.data_length = binaryBlob.data.length;
        String string = new String(binaryBlob.data);
        if (jTextField.getText().equals(string)) {
          LCM.getSingleton().publish("gokart.driver", binaryBlob);
          System.out.println("gokart.driver=" + string);
          jTextField.setBackground(Color.GREEN);
          jTextField.setEnabled(false);
          new Thread(new Runnable() {
            @Override
            public void run() {
              try {
                Thread.sleep(500);
              } catch (Exception e) {
                e.printStackTrace();
              }
              jTextField.setBackground(null);
              jTextField.setEnabled(true);
            }
          }).start();
        } else
          new RuntimeException("string encoding error").printStackTrace();
      });
    }
  }

  @Override // from StartAndStoppable
  public void start() {
    rimoGetLcmClient.addListener(rimoGetListener);
    rimoGetLcmClient.startSubscriptions();
    linmotGetLcmClient.addListener(linmotGetListener);
    linmotGetLcmClient.startSubscriptions();
    // ---
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
    // ---
    davisImuLcmClient.addListener(davisImuFrameListener);
    davisImuLcmClient.startSubscriptions();
    // ---
    vmu931ImuLcmClient.addListener(vmu931ImuFrameListener);
    vmu931ImuLcmClient.startSubscriptions();
    // ---
    // vmu932ImuLcmClient.addListener(vmu932ImuFrameListener);
    // vmu932ImuLcmClient.startSubscriptions();
  }

  @Override // from StartAndStoppable
  public void stop() {
    linmotGetLcmClient.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
    vmu931ImuLcmClient.stopSubscriptions();
    // vmu932ImuLcmClient.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
  }
}
