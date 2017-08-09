// code by jph
package ch.ethz.idsc.retina.util;

import java.util.Objects;

/** slows down playback to realtime
 * 
 * 1) nano seconds
 * 2) long encoding */
public class RealtimeSleeper {
  private static final long MICRO = 1000000;
  // ---
  private final double speed;
  private Long ref = null;
  private long tic;
  private long sleepTotal = 0;

  /** Example:
   * speed of 0.5 will slow down playback to half realtime speed
   * 
   * @param speed */
  public RealtimeSleeper(double speed) {
    this.speed = speed;
  }

  /** @param time in nano seconds */
  public void now(long time) {
    if (notInitialized()) {
      ref = time;
      tic = System.nanoTime();
    } else {
      long act = time - ref;
      long toc = System.nanoTime() - tic;
      final long sleep = Math.round(act - toc * speed);
      if (0 < sleep)
        try {
          long millis = sleep / MICRO;
          int nanos = (int) (sleep % MICRO);
          Thread.sleep(millis, nanos);
          sleepTotal += sleep;
        } catch (InterruptedException exception) {
          exception.printStackTrace();
        }
    }
  }

  private boolean notInitialized() {
    return Objects.isNull(ref);
  }

  /** @return total sleep in nano seconds required to slow down to real time */
  public long getSleepTotal() {
    return sleepTotal;
  }

  public double getSleepTotalSec() {
    return sleepTotal * 1e-9;
  }
}
