package global;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

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

    public Sdo_geometry intersection(Sdo_geometry x) {
        return null;
    }
}
