/*
 * Copyright (c) 2021 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.workers.process;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience wrapper around a thread-safe BlockingQueue. Keeps track of available ports for Kube
 * Pod Processes.
 *
 * Although this data structure can do without the wrapper class, this class allows easier testing
 * via the {@link #getNumAvailablePorts()} function.
 *
 * The singleton pattern clarifies that only one copy of this class is intended to exist per
 * scheduler deployment.
 */
public class KubePortManagerSingleton {

  private static final Logger LOGGER = LoggerFactory.getLogger(KubePortManagerSingleton.class);

  private static KubePortManagerSingleton instance;

  private static final int MAX_PORTS_PER_WORKER = 4; // A sync has two workers. Each worker requires 2 ports.
  private final BlockingQueue<Integer> workerPorts;

  private KubePortManagerSingleton(final Set<Integer> ports) {
    workerPorts = new LinkedBlockingDeque<>(ports);
  }

  /**
   * Make sure init(ports) is called once prior to repeatedly using getInstance().
   *
   * @return
   */
  public static synchronized KubePortManagerSingleton getInstance() {
    if (instance == null) {
      throw new RuntimeException("Must initialize with init(ports) before using.");
    }
    return instance;
  }

  /**
   * Sets up the port range; make sure init(ports) is called once prior to repeatedly using
   * getInstance().
   *
   * @return
   */
  public static synchronized void init(final Set<Integer> ports) {
    if (instance != null) {
      throw new RuntimeException("Cannot initialize twice!");
    }
    instance = new KubePortManagerSingleton(ports);
  }

  public Integer take() throws InterruptedException {
    return workerPorts.poll(10, TimeUnit.MINUTES);
  }

  public void offer(final Integer port) {
    if (!workerPorts.contains(port)) {
      workerPorts.add(port);
    }
  }

  public int getNumAvailablePorts() {
    return workerPorts.size();
  }

  public int getSupportedWorkers() {
    return workerPorts.size() / MAX_PORTS_PER_WORKER;
  }

}
