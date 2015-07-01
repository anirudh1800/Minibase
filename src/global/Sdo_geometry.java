package global;

import java.util.Vector;

/**
 * Created by anirudhgali on 6/28/15.
 */

public class Sdo_geometry implements GlobalConst{
    public Sdo_gtype shapeType;
    public Vector<Double> coordinatesOfShape;

    public Sdo_geometry(Sdo_gtype _shapeType, Vector<Double> _coordinatesOfShape) {
        this.shapeType = _shapeType;
        this.coordinatesOfShape = _coordinatesOfShape;
    }
}
