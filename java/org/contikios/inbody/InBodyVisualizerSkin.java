package org.contikios.inbody;

import org.contikios.cooja.ClassDescription;
import org.contikios.cooja.Mote;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.interfaces.Position;
import org.contikios.cooja.plugins.Visualizer;
import org.contikios.cooja.plugins.VisualizerSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Set;

@ClassDescription("In-body signals")
public class InBodyVisualizerSkin implements VisualizerSkin {
    private static final Logger logger = LoggerFactory.getLogger(InBodyVisualizerSkin.class);
    private Simulation simulation;
    private Visualizer visualizer;

    private InBodyChannelModel channelModel;

    @Override
    public void setActive(Simulation simulation, Visualizer visualizer) {
        if (!(simulation.getRadioMedium() instanceof InBody radioMedium)) {
            logger.error("Cannot activate In-body skin for unknown radio medium: " + simulation.getRadioMedium());
            return;
        }
        this.simulation = simulation;
        this.visualizer = visualizer;

        channelModel = radioMedium.getChannelModel();
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
        Area[] phantomAreas = channelModel.getPhantomBoundaries();
        Area skinArea = phantomAreas[0];
        Area fatArea = phantomAreas[1];
        Area muscleArea = phantomAreas[2];
        Area restrictedArea = phantomAreas[3];
        Area airArea = phantomAreas[4];

        Area signalArea = new Area();

        for (Mote mote : selectedMotes) {
            Position mote_pos = mote.getInterfaces().getPosition();
            if (mote_pos == null) {
                continue;
            }
            Point mote_point = visualizer.transformPositionToPixel(mote_pos);
            int x = mote_point.x;
            int y = mote_point.y;
            Point radioRange = visualizer.transformPositionToPixel(25, 25, 0);

            // Draw the signal distribution pattern of the mote
            g.setColor(Color.GREEN);
            signalArea.add(new Area(new Ellipse2D.Double(
                    x - (int) (radioRange.x / 2), y - (int) (radioRange.x / 2), radioRange.x, radioRange.x)));
            System.out.println((int) (radioRange.x / 2) + " " + radioRange.x + " " + (int) (radioRange.y / 2));
            signalArea.subtract(skinArea);
            signalArea.subtract(muscleArea);
            signalArea.subtract(restrictedArea);
            Area signalAirArea = new Area(signalArea);
            signalArea.subtract(airArea);
            RadialGradientPaint paint = new RadialGradientPaint(
                    x, y,
                    (int) (radioRange.x/2), new float[]{0.0f, 1.0f}, new Color[]{Color.GREEN, Color.WHITE});
            ((Graphics2D) g).setPaint(paint);
            ((Graphics2D) g).fill(signalArea);

            signalAirArea.subtract(signalArea);
            RadialGradientPaint paint2 = new RadialGradientPaint(
                    x, y,
                    (int) (radioRange.x/2), new float[]{0.0f, 1.0f}, new Color[]{Color.YELLOW, Color.WHITE});
            ((Graphics2D) g).setPaint(paint2);
            ((Graphics2D) g).fill(signalAirArea);
        }
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
