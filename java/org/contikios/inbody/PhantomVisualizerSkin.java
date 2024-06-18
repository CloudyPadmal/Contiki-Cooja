package org.contikios.inbody;

import org.contikios.cooja.Mote;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.plugins.Visualizer;
import org.contikios.cooja.plugins.VisualizerSkin;

import java.awt.*;

public class PhantomVisualizerSkin implements VisualizerSkin {
    private Simulation simulation;
    private Visualizer visualizer;

    @Override
    public void setActive(Simulation simulation, Visualizer visualizer) {
        this.simulation = simulation;
        this.visualizer = visualizer;
    }

    @Override
    public void setInactive() {

    }

    @Override
    public Color[] getColorOf(Mote mote) {
        return null;
    }

    @Override
    public void paintBeforeMotes(Graphics g) {
        // This is where I should be drawing the phantom with the three layers
    }

    @Override
    public void paintAfterMotes(Graphics g) {

    }

    @Override
    public Visualizer getVisualizer() {
        return visualizer;
    }
}
