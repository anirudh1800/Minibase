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

    public double Distance(Sdo_geometry X)
    {
        double recA[] = new double[8];
        double recB[] = new double[8];
        double distance[] = new double[16];
        int count=0;

        recA[0]=coordinatesOfShape[0];
        recA[1]=coordinatesOfShape[1];
        recA[2]=coordinatesOfShape[2];
        recA[3]=coordinatesOfShape[3];
        recA[4]=coordinatesOfShape[0];
        recA[5]=coordinatesOfShape[3];
        recA[6]=coordinatesOfShape[2];
        recA[7]=coordinatesOfShape[1];

        recB[0]=X.coordinatesOfShape[0];
        recB[1]=X.coordinatesOfShape[1];
        recB[2]=X.coordinatesOfShape[2];
        recB[3]=X.coordinatesOfShape[3];
        recB[4]=X.coordinatesOfShape[0];
        recB[5]=X.coordinatesOfShape[3];
        recB[6]=X.coordinatesOfShape[2];
        recB[7]=X.coordinatesOfShape[1];

        for (int i=0;i<8;i=i+2) {
            for(int j=0;j<8;j=j+2) {
                distance[count]=dist(recA[i],recA[i+1],recB[j],recB[j+1]);
                count++;
            }
        }
        int p1=0;
        double minDist=distance[p1];
        for (p1=0;p1<16;p1++) {
            if (distance[p1]<minDist)
                minDist=distance[p1];
        }
        System.out.println("Minimum distance= "+minDist);       
        return minDist;
    }

    private double dist(double x1, double y1, double x2, double y2) 
    {
        double dist=Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        return dist;
    }
}
