/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.server;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.Binding;
import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.JNRPEConfig;
import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.engine.services.plugins.CommandLine.Command;
import it.jnrpe.engine.services.plugins.CommandLine.Option;
import java.io.File;
import java.util.Optional;
import java.util.ServiceLoader;

@Command(name = "jnrpe")
public class JNRPEServer {
  @Option(
      names = {"-c", "--conf"},
      defaultValue = "/etc/jnrpe/jnrpe.yml",
      paramLabel = "PATH",
      required = true,
      description = "Path to the JNRPE server config file (defaults to '/etc/jnrpe/jnrpe.yml')")
  private String confFile;

  private void bind(Binding binding) {
    // retrieve the network service
    ServiceLoader<INetworkListener> serviceLoader = ServiceLoader.load(INetworkListener.class);
    Optional<INetworkListener> listener = serviceLoader.findFirst();
    if (listener.isPresent()) {
      INetworkListener netListener = listener.get();
      if (netListener.supportBinding(binding)) {
        EventManager.info(
            "Binding on port %d using network provider named '%s'",
            binding.getPort(), netListener.getName());
        netListener.bind(binding);
      }
    } else {
      EventManager.fatal("No network services has been found");
      System.exit(-1);
    }
  }

  public void start() throws Exception {
    // Parsing the configuration
    File confFile = new File(this.confFile);
    if (!confFile.canRead()) {
      EventManager.fatal("Unable to read the configuration file at %s", confFile.getAbsolutePath());
      // System.exit(-1);
      throw new Exception(
          String.format("Unable to read the configuration file at %s", confFile.getAbsolutePath()));
    }

    ConfigSource.setConfigFile(confFile);

    ServiceLoader<IConfigProvider> configProviderServiceLoader =
        ServiceLoader.load(IConfigProvider.class);

    Optional<JNRPEConfig> config = Optional.empty();

    for (IConfigProvider configProvider : configProviderServiceLoader) {
      Optional<JNRPEConfig> conf = configProvider.getConfig();
      if (conf.isPresent()) {
        config = conf;
        break;
      }
    }

    config.ifPresentOrElse(
        cfg -> cfg.getServer().getBindings().forEach(this::bind),
        () ->
            EventManager.fatal(
                "No config provider has been able to parse the provided config file (%s)",
                confFile.getAbsolutePath()));
  }
}
