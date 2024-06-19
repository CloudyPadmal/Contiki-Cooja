package org.contikios.inbody;

import org.contikios.cooja.Mote;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.plugins.Visualizer;
import org.contikios.cooja.plugins.VisualizerSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.geom.Area;
import java.beans.PropertyVetoException;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class PhantomVisualizerSkin implements VisualizerSkin {
    private static final Logger logger = LoggerFactory.getLogger(PhantomVisualizerSkin.class);

    private static final Color COLOR_SKIN = new Color(250, 219, 77, 128);
    private static final Color COLOR_FAT = new Color(245, 166, 35, 128);
    private static final Color COLOR_MUSCLE = new Color(208, 2, 27, 128);
    private static final Color COLOR_BOUNDARY = new Color(0, 0, 0, 128);

    private static final double PHANTOM_BOUND_WIDTH = 2;
    private static double PHANTOM_LENGTH = 300;
    private static final double SKIN_THICKNESS = 1;
    private static final double FAT_THICKNESS = 25;
    private static final double MUSCLE_THICKNESS = 30;

    private Area skinRegion;
    private Area fatRegion;
    private Area muscleRegion;

    private Simulation simulation;
    private Visualizer visualizer;


    private JInternalFrame miniDialogBox;

    @Override
    public void setActive(Simulation simulation, Visualizer visualizer) {
        if (!(simulation.getRadioMedium() instanceof InBody)) {
            logger.error("Cannot activate In-body skin for unknown radio medium: " + simulation.getRadioMedium());
            return;
        }
        this.simulation = simulation;
        this.visualizer = visualizer;

        // GUI dialog to set phantom parameters
        SpinnerNumberModel phantomLengthModel = new SpinnerNumberModel(300, 300, 1000, 1);
        JSpinner phantomLengthSpinner = new JSpinner(phantomLengthModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(phantomLengthSpinner, "0 mm");
        phantomLengthSpinner.setEditor(editor);

        ((JSpinner.DefaultEditor) phantomLengthSpinner.getEditor()).getTextField().setColumns(5);
        phantomLengthSpinner.setToolTipText("Length of the phantom (mm)");
        phantomLengthSpinner.addChangeListener(e -> {
            PHANTOM_LENGTH = (int) phantomLengthModel.getValue();
            visualizer.repaint();
        });

        visualizer.registerSimulationMenuAction(PhantomLengthMenuAction.class);

        JPanel miniDialogBoxContent = new JPanel();
        miniDialogBoxContent.setLayout(new BoxLayout(miniDialogBoxContent, BoxLayout.Y_AXIS));
        miniDialogBoxContent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box phantomLengthBox = Box.createHorizontalBox();
        phantomLengthBox.add(new JLabel("Phantom Length: "));
        phantomLengthBox.add(Box.createHorizontalStrut(5));
        phantomLengthBox.add(phantomLengthSpinner);

        miniDialogBoxContent.add(phantomLengthBox);

        miniDialogBox = new JInternalFrame("Phantom Length", false, true);
        miniDialogBox.setVisible(false);
        miniDialogBox.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        miniDialogBox.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent ife) {
                super.internalFrameClosed(ife);
                miniDialogBox.setVisible(false);
            }
        });

        miniDialogBox.getContentPane().add(BorderLayout.CENTER, miniDialogBoxContent);
        miniDialogBox.pack();
    }

    @Override
    public void setInactive() {
        if (simulation == null) {
            return;
        }
        // Clean up custom dialog boxes
        visualizer.getCurrentCanvas().remove(miniDialogBox);
        visualizer.unregisterSimulationMenuAction(PhantomLengthMenuAction.class);
    }

    @Override
    public Color[] getColorOf(Mote mote) {
        return null;
    }

    @Override
    public void paintBeforeMotes(Graphics g) {
        if (simulation == null) {
            return;
        }

        Area skinRegion = new Area();
        Area fatRegion = new Area();
        Area muscleRegion = new Area();
        Area rightBoundRegion = new Area();
        Area leftBoundRegion = new Area();

        // Draw around a fixed coordinate
        Point gridCenter = visualizer.transformPositionToPixel(0, 0, 0);

        Point phantomLengthEdge = visualizer.transformPositionToPixel(PHANTOM_LENGTH, 0, 0);
        int phantomWidth = phantomLengthEdge.x - gridCenter.x;

        Point phantomSkinEdge = visualizer.transformPositionToPixel(0, SKIN_THICKNESS, 0);
        int phantomSkinHeight = visualizer.transformPositionToPixel(0, SKIN_THICKNESS, 0).y - gridCenter.y;

        Point phantomFatEdge = visualizer.transformPositionToPixel(0, FAT_THICKNESS + SKIN_THICKNESS, 0);
        int phantomFatHeight = visualizer.transformPositionToPixel(0, FAT_THICKNESS, 0).y - gridCenter.y;

        Point phantomMuscleEdge = visualizer.transformPositionToPixel(0, MUSCLE_THICKNESS + FAT_THICKNESS + SKIN_THICKNESS, 0);
        int phantomMuscleHeight = visualizer.transformPositionToPixel(0, MUSCLE_THICKNESS, 0).y - gridCenter.y;

        Point phantomBoundEdge = visualizer.transformPositionToPixel(PHANTOM_BOUND_WIDTH, 0, 0);
        int phantomBoundWidth = phantomBoundEdge.x - gridCenter.x;
        int phantomBoundHeight = phantomMuscleEdge.y - gridCenter.y;

        // Draw skin
        skinRegion.add(new Area(new Rectangle(
                gridCenter.x, gridCenter.y,
                phantomWidth, phantomSkinHeight)));
        g.setColor(COLOR_SKIN);
        ((Graphics2D) g).fill(skinRegion);
        g.setColor(Color.GRAY);
        ((Graphics2D) g).draw(skinRegion);

        // Draw fat
        fatRegion.add(new Area(new Rectangle(
                gridCenter.x, phantomSkinEdge.y,
                phantomWidth, phantomFatHeight)));
        g.setColor(COLOR_FAT);
        ((Graphics2D) g).fill(fatRegion);
        g.setColor(Color.GRAY);
        ((Graphics2D) g).draw(fatRegion);

        // Draw muscle
        muscleRegion.add(new Area(new Rectangle(
                gridCenter.x, phantomFatEdge.y,
                phantomWidth, phantomMuscleHeight)));
        g.setColor(COLOR_MUSCLE);
        ((Graphics2D) g).fill(muscleRegion);
        g.setColor(Color.GRAY);
        ((Graphics2D) g).draw(muscleRegion);

        // Draw blocking bounds on either side of the phantom
        g.setColor(COLOR_BOUNDARY);

        leftBoundRegion.add(new Area(new Rectangle(
                gridCenter.x - phantomBoundWidth, gridCenter.y,
                phantomBoundWidth, phantomBoundHeight)));
        ((Graphics2D) g).fill(leftBoundRegion);
        ((Graphics2D) g).draw(leftBoundRegion);

        rightBoundRegion.add(new Area(new Rectangle(
                phantomLengthEdge.x, gridCenter.y,
                phantomBoundWidth, phantomBoundHeight)));
        ((Graphics2D) g).fill(rightBoundRegion);
        ((Graphics2D) g).draw(rightBoundRegion);

        // Save the regions to use with signal propagation
        this.skinRegion = skinRegion;
        this.fatRegion = fatRegion;
        this.muscleRegion = muscleRegion;
    }

    public Area getSkinRegion() {
        return skinRegion;
    }

    public Area getFatRegion() {
        return fatRegion;
    }

    public Area getMuscleRegion() {
        return muscleRegion;
    }

    @Override
    public void paintAfterMotes(Graphics g) {

    }

    @Override
    public Visualizer getVisualizer() {
        return visualizer;
    }

    private void updatePhantomLength() {
        if (miniDialogBox.getDesktopPane() == null) {
            visualizer.getDesktopPane().add(miniDialogBox);
        }
        miniDialogBox.pack();
        /* Place frame in the upper right corner of the visualizer canvas */
        Point visCanvasPos = SwingUtilities.convertPoint(
                visualizer.getCurrentCanvas(),
                visualizer.getCurrentCanvas().getLocation(),
                visualizer.getDesktopPane());
        miniDialogBox.setLocation(
                visCanvasPos.x + visualizer.getCurrentCanvas().getWidth() - miniDialogBox.getWidth(),
                visCanvasPos.y);
        /* Try to place on top with focus */
        miniDialogBox.setLayer(JLayeredPane.MODAL_LAYER);
        miniDialogBox.setVisible(true);
        miniDialogBox.moveToFront();
        try {
            miniDialogBox.setSelected(true);
        } catch (PropertyVetoException ex) {
            logger.warn("Failed getting focus");
        }
    }

    public static class PhantomLengthMenuAction implements Visualizer.SimulationMenuAction {

        @Override
        public boolean isEnabled(Visualizer visualizer, Simulation simulation) {
            return true;
        }

        @Override
        public String getDescription(Visualizer visualizer, Simulation simulation) {
            return "Set phantom length";
        }

        @Override
        public void doAction(Visualizer visualizer, Simulation simulation) {
            VisualizerSkin[] skins = visualizer.getCurrentSkins();
            for (VisualizerSkin skin : skins) {
                if (skin instanceof PhantomVisualizerSkin this_skin) {
                    this_skin.updatePhantomLength();
                }
            }
        }
    }
}
