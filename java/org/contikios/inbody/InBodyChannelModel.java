package org.contikios.inbody;

import org.contikios.cooja.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Area;

public class InBodyChannelModel {

    private static final Logger logger = LoggerFactory.getLogger(InBodyChannelModel.class);

    private Simulation simulation;

    private Area[] phantomBoundaries;

    public InBodyChannelModel(Simulation simulation) {
        if (simulation == null) {
            logger.error("Simulation is null");
            return;
        }
        this.simulation = simulation;
    }

    public Area[] getPhantomBoundaries() {
        return phantomBoundaries;
    }

    public void setPhantomBoundaries(Area[] phantomBoundaries) {
        this.phantomBoundaries = phantomBoundaries;
    }

}
