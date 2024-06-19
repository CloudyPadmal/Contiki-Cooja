package org.contikios.inbody;

import org.contikios.cooja.ClassDescription;
import org.contikios.cooja.Mote;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.plugins.Visualizer;
import org.contikios.cooja.plugins.VisualizerSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Set;

@ClassDescription("In-body signals")
public class InBodyVisualizerSkin implements VisualizerSkin {
    private static final Logger logger = LoggerFactory.getLogger(InBodyVisualizerSkin.class);
    private Simulation simulation;
    private Visualizer visualizer;

    @Override
    public void setActive(Simulation simulation, Visualizer visualizer) {
        if (!(simulation.getRadioMedium() instanceof InBody)) {
            logger.error("Cannot activate In-body skin for unknown radio medium: " + simulation.getRadioMedium());
            return;
        }
        this.simulation = simulation;
        this.visualizer = visualizer;
    }

    @Override
    public void setInactive() {
        if (simulation == null) {
            // Skin was never activated
            return;
        }
    }

    @Override
    public Color[] getColorOf(Mote mote) {
        return null;
    }

    @Override
    public void paintBeforeMotes(Graphics g) {
        // This is where I should be drawing the signal distribution pattern of each mote
        Set<Mote> selectedMotes = visualizer.getSelectedMotes();
        Mote[] allMotes = simulation.getMotes();

        g.setColor(Color.BLACK);
    }

    @Override
    public void paintAfterMotes(Graphics g) {

    }

    @Override
    public Visualizer getVisualizer() {
        return visualizer;
    }
}
