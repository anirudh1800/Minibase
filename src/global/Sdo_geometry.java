package global;

import java.util.Arrays;
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

    public String toString(){
        return "RECTANGLE ORDINATES: " + Arrays.toString(coordinatesOfShape.toArray());
    }
    public double area() {
        double area = abs(coordinatesOfShape.get(2) - coordinatesOfShape.get(0)) * 
                      abs(coordinatesOfShape.get(1) - coordinatesOfShape.get(3))
        return area;
    }
    public double intersection(Sdo_geometry x) {
        
    }
}
