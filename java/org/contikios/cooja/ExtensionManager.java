/*
 * Copyright (c) 2023, RISE Research Institutes of Sweden AB.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.contikios.cooja;

import java.util.LinkedHashMap;
import org.contikios.cooja.mspmote.plugins.MspCLI;
import org.contikios.cooja.mspmote.plugins.MspCodeWatcher;
import org.contikios.cooja.mspmote.plugins.MspCycleWatcher;
import org.contikios.cooja.mspmote.plugins.MspStackWatcher;
import org.contikios.cooja.plugins.BaseRSSIconf;
import org.contikios.cooja.plugins.BufferListener;
import org.contikios.cooja.plugins.DGRMConfigurator;
import org.contikios.cooja.plugins.EventListener;
import org.contikios.cooja.plugins.LogListener;
import org.contikios.cooja.plugins.Mobility;
import org.contikios.cooja.plugins.MoteInformation;
import org.contikios.cooja.plugins.MoteInterfaceViewer;
import org.contikios.cooja.plugins.Notes;
import org.contikios.cooja.plugins.PowerTracker;
import org.contikios.cooja.plugins.RadioLogger;
import org.contikios.cooja.plugins.ScriptRunner;
import org.contikios.cooja.plugins.TimeLine;
import org.contikios.cooja.plugins.VariableWatcher;
import org.contikios.cooja.plugins.Visualizer;
import org.contikios.cooja.serialsocket.SerialSocketClient;
import org.contikios.cooja.serialsocket.SerialSocketServer;

/**
 * Class for loading and querying dynamic extensions.
 */
public class ExtensionManager {
  static final LinkedHashMap<String, Class<? extends Plugin>> builtinPlugins = new LinkedHashMap<>();
  static {
    registerBuiltinPlugin(Visualizer.class);
    registerBuiltinPlugin(LogListener.class);
    registerBuiltinPlugin(TimeLine.class);
    registerBuiltinPlugin(Mobility.class);
    registerBuiltinPlugin(MoteInformation.class);
    registerBuiltinPlugin(MoteInterfaceViewer.class);
    registerBuiltinPlugin(VariableWatcher.class);
    registerBuiltinPlugin(EventListener.class);
    registerBuiltinPlugin(RadioLogger.class);
    registerBuiltinPlugin(ScriptRunner.class);
    registerBuiltinPlugin(Notes.class);
    registerBuiltinPlugin(BufferListener.class);
    registerBuiltinPlugin(DGRMConfigurator.class);
    registerBuiltinPlugin(BaseRSSIconf.class);
    registerBuiltinPlugin(PowerTracker.class);
    registerBuiltinPlugin(SerialSocketClient.class);
    registerBuiltinPlugin(SerialSocketServer.class);
    registerBuiltinPlugin(MspCLI.class);
    registerBuiltinPlugin(MspCodeWatcher.class);
    registerBuiltinPlugin(MspStackWatcher.class);
    registerBuiltinPlugin(MspCycleWatcher.class);
  }
  private static void registerBuiltinPlugin(final Class<? extends Plugin> pluginClass) {
    builtinPlugins.put(pluginClass.getName(), pluginClass);
  }

  /** Get the class for a named plugin, returns null if not found. */
  public static Class<? extends Plugin> getPluginClass(Cooja cooja, String name) {
    var clazz = builtinPlugins.get(name);
    if (clazz != null) {
      return clazz;
    }
    for (var candidate : cooja.getRegisteredPlugins()) {
      if (name.equals(candidate.getName())) {
        clazz = candidate;
        break;
      }
    }
    return clazz;
  }
}