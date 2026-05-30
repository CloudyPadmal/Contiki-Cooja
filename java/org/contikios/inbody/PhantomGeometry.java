package org.contikios.inbody;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Manages the tissue layer rectangles and the restricted-zone classification for the Fat-IBC channel model
 */
class PhantomGeometry {
    
    private Rectangle2D skinRect;
    private Rectangle2D fatRect;
    private Rectangle2D muscleRect;
    private Rectangle2D restrictedBelowRect;
    
    private double phantomOx;
    private double phantomOy;
    private double phantomLen;
    private double phantomOyPlusTotalT;
    private boolean built = false;

    /**
     * Rebuilds all tissue rectangles and boundary parameters from the supplied phantom dimensions
     *
     * @param ox phantom origin X (mm)
     * @param oy phantom origin Y (mm)
     * @param len phantom horizontal length (mm)
     * @param st skin thickness (mm)
     * @param ft fat thickness (mm)
     * @param mt muscle thickness (mm)
     * @param rg restricted-gap width (mm) — reserved for future use
     */
    synchronized void rebuild(double ox, double oy, double len, double st, double ft, double mt, double rg) {
        double totalT = st + ft + mt;

        skinRect = new Rectangle2D.Double(ox, oy, len, st);
        fatRect = new Rectangle2D.Double(ox, oy + st, len, ft);
        muscleRect = new Rectangle2D.Double(ox, oy + st + ft, len, mt);
        
        final double INF = 1.0e9;
        restrictedBelowRect = new Rectangle2D.Double(-INF, oy + totalT, 2.0 * INF, INF);

        phantomOx = ox;
        phantomOy = oy;
        phantomLen = len;
        phantomOyPlusTotalT = oy + totalT;
        built = true;
    }

    synchronized TissueType getTissueAt(Point2D p) {
        if (!built) return TissueType.AIR;

        if (muscleRect.contains(p)) return TissueType.MUSCLE;
        if (fatRect.contains(p)) return TissueType.FAT;
        if (skinRect.contains(p)) return TissueType.SKIN;

        double px = p.getX();
        double py = p.getY();

        if (py >= phantomOyPlusTotalT) return TissueType.RESTRICTED;
        
        if (px < phantomOx) {
            double horizDist = phantomOx - px;           // mm to the left
            return (py < phantomOy - horizDist) ? TissueType.AIR : TissueType.RESTRICTED;
        }

        if (px > phantomOx + phantomLen) {
            double horizDist = px - (phantomOx + phantomLen); // mm to the right
            return (py < phantomOy - horizDist) ? TissueType.AIR : TissueType.RESTRICTED;
        }

        return TissueType.AIR;
    }

    Rectangle2D getSkinRect() {
        return skinRect;
    }

    Rectangle2D getFatRect() {
        return fatRect;
    }

    Rectangle2D getMuscleRect() {
        return muscleRect;
    }
}
