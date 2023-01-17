/*
 * Copyright (c) 2008, Swedish Institute of Computer Science.
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
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of MSPSim.
 *
 * -----------------------------------------------------------------
 *
 * Main
 *
 * Authors : Joakim Eriksson, Niclas Finne
 * Created : 6 nov 2008
 */

package se.sics.mspsim;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import se.sics.mspsim.platform.GenericNode;
import se.sics.mspsim.util.ArgumentManager;

/**
 *
 */
public class Main {

  public static GenericNode createNode(String className) {
    try {
      Class<? extends GenericNode> nodeClass = Class.forName(className).asSubclass(GenericNode.class);
      return nodeClass.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException | ClassCastException | InstantiationException | IllegalAccessException e) {
      // Can not find specified class, or wrong class type, or failed to instantiate
    } catch (InvocationTargetException | NoSuchMethodException e) {
        e.printStackTrace();
    }
      return null;
  }

  public static String getNodeTypeByPlatform(String platform) {
    return switch (platform) { // Sorted alphabetically, on return value.
      case "esb" -> "se.sics.mspsim.platform.esb.ESBNode";
      case "jcreate" -> "se.sics.mspsim.platform.jcreate.JCreateNode";
      case "sentilla-usb" -> "se.sics.mspsim.platform.sentillausb.SentillaUSBNode";
      case "cc430" -> "se.sics.mspsim.platform.ti.CC430Node";
      case "exp1101" -> "se.sics.mspsim.platform.ti.Exp1101Node";
      case "exp1120" -> "se.sics.mspsim.platform.ti.Exp1120Node";
      case "exp5438" -> "se.sics.mspsim.platform.ti.Exp5438Node";
      case "trxeb1120" -> "java.se.sics.mspsim.platform.ti.Trxeb1120Node.java";
      case "trxeb2520" -> "java.se.sics.mspsim.platform.ti.Trxeb2520Node.java";
      case "sky" -> "java.se.sics.mspsim.platform.sky.SkyNode.java";
      case "telos" -> "java.se.sics.mspsim.platform.sky.TelosNode.java";
      case "tyndall" -> "java.se.sics.mspsim.platform.tyndall.TyndallNode.java";
      case "wismote" -> "java.se.sics.mspsim.platform.wismote.WismoteNode.java";
      case "z1" -> "java.se.sics.mspsim.platform.z1.Z1Node.java";
      // Try to guess the node type.
      default -> "se.sics.mspsim.platform." + platform + '.'
              + Character.toUpperCase(platform.charAt(0))
              + platform.substring(1).toLowerCase() + "Node";
    };
  }

  public static void main(String[] args) throws IOException {
    ArgumentManager config = new ArgumentManager();
    config.handleArguments(args);

    String nodeType = config.getProperty("nodeType");
    String platform = nodeType;
    GenericNode node;
    if (nodeType == null) {
      platform = config.getProperty("platform");
      if (platform == null) {
          // Default platform
          platform = "sky";

          // Guess platform based on firmware filename suffix.
          // TinyOS's firmware files are often named 'main.exe'.
          String[] a = config.getArguments();
          if (a.length > 0 && !"main.exe".equals(a[0])) {
              int index = a[0].lastIndexOf('.');
              if (index > 0) {
                  platform = a[0].substring(index + 1);
              }
          }
      }
      nodeType = getNodeTypeByPlatform(platform);
    }
    node = createNode(nodeType);
    if (node == null) {
      System.err.println("MSPSim does not currently support the platform '" + platform + "'.");
      System.exit(1);
    }
    node.setupArgs(config);
  }

}
