package org.contikios.inbody;

import org.contikios.cooja.ClassDescription;
import org.contikios.cooja.Cooja;
import org.contikios.cooja.RadioConnection;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.interfaces.Radio;
import org.contikios.cooja.radiomediums.AbstractRadioMedium;

import java.util.List;

@ClassDescription("Fat Tissue-based In-Body Radio Medium (Fat-IBC)")
public class InBody extends AbstractRadioMedium {

    private final InBodyChannelModel channelModel;

    public InBody(Simulation simulation) {
        super(simulation);
        channelModel = new InBodyChannelModel(simulation);
    }

    public InBodyChannelModel getChannelModel() {
        return channelModel;
    }

    @Override
    public List<Radio> getNeighbors(Radio radio) {
        return super.getNeighbors(radio);
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    protected RadioConnection createConnections(Radio radio) {
        return null;
    }
}
