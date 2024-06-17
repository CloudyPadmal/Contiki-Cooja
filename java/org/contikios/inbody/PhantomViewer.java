package org.contikios.inbody;

import org.contikios.cooja.*;

@ClassDescription("Fat-IBC Phantom Viewer")
@PluginType(PluginType.PType.SIM_PLUGIN)
@SupportedArguments(radioMediums = {InBody.class})
public class PhantomViewer extends VisPlugin {

    public PhantomViewer(Simulation simulationToVisualize, Cooja gui) {
        super("Fat-IBC Environment", gui);
    }
}
