package global;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.awt.Rectangle;

/**
 * Created by anirudhgali on 6/28/15.
 */

public class Sdo_geometry implements GlobalConst {
    public Sdo_gtype shapeType;
    public double[] coordinatesOfShape;

    public Sdo_geometry(Sdo_gtype _shapeType, double[] _coordinatesOfShape) {
        this.shapeType = _shapeType;
        this.coordinatesOfShape = _coordinatesOfShape;
    }

    public double area() {
        double area = Math.abs(coordinatesOfShape[2] - coordinatesOfShape[0]) *
                Math.abs(coordinatesOfShape[1] - coordinatesOfShape[3]);
        return area;
    }

    public Sdo_geometry intersection(Sdo_geometry X) {
        double width = Math.abs(coordinatesOfShape[2] - coordinatesOfShape[0]);
        double height = Math.abs(coordinatesOfShape[3] - coordinatesOfShape[1]);
        double x = Math.abs(coordinatesOfShape[0]);
        double y = Math.abs(coordinatesOfShape[1]);
        Rectangle2D.Double r = new Rectangle2D.Double (x,y,width,height);

        width = Math.abs(X.coordinatesOfShape[2] - X.coordinatesOfShape[0]);
        height = Math.abs(X.coordinatesOfShape[3] - X.coordinatesOfShape[1]);
        x = Math.abs(X.coordinatesOfShape[0]);
        y = Math.abs(X.coordinatesOfShape[1]);

        Rectangle2D.Double r2 = new Rectangle2D.Double (x,y,width,height);

        boolean Intersects = r.intersects(r2);

        if (Intersects) {
            Rectangle2D r3 = r.createIntersection(r2);
            double ix2 = r3.getMaxX();
            double iy2 = r3.getMaxY();
            double ix1 = r3.getMinX();
            double iy1 = r3.getMinY();
            double array[] = new double[]{ix1,iy1,ix2,iy2};
            Sdo_geometry intersectionShape = new Sdo_geometry(GlobalConst.Sdo_gtype.RECTANGLE, array);
            System.out.println("intersection ");
            System.out.println("(" + ix1 + "," + iy1 + ")" +"   " +"(" + ix2 + "," + iy2 + ")");
            return intersectionShape;
        } else {
            System.out.println("No intersection between rectangles");
        }
        return null;
    }


}
