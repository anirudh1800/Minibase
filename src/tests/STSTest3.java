package tests;

import btree.BTreeFile;
import btree.IntegerKey;
import global.*;
import heap.Heapfile;
import heap.Scan;
import heap.Tuple;
import index.IndexScan;
import iterator.*;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by anirudhgali on 6/27/15.
 */
//SELECT c.name, SDO_GEOM.SDO_AREA(c.shape, 0.005) 
//    FROM cola_markets c 
//    WHERE c.name = 'cola_a'; 

public class STSTest3
{
    public static void main(String argv[])
    {
        boolean sts3status;

        STSDriver3 sts3 = new STSDriver3();
        sts3status = sts3.selectTest();

        if (sts3status != true) {
            System.out.println("Error ocurred during STS test3");
        }
        else {
            System.out.println("STS test3 completed successfully");
        }

    }
}

class STSDriver3 extends TestDriver
        implements GlobalConst {

    private boolean OK = true;
    private boolean FAIL = false;
    private Vector colamarkets;

    /**
     * Constructor
     */
    public STSDriver3() {
        System.out.print("Started STS tests3" + "\n");

        //build ColaMarkets table
        colamarkets = new Vector();

        double[] v = new double[] {1.0, 1.0, 2.0, 3.0};

        colamarkets.addElement(new ColaMarkets(1, "cola_a", new Sdo_geometry(Sdo_gtype.RECTANGLE, v)));

        v = new double[] {2.5, 3.5, 3.5, 4.5};

        colamarkets.addElement(new ColaMarkets(2, "cola_b", new Sdo_geometry(Sdo_gtype.RECTANGLE, v)));

        boolean status = OK;
        int numMarkets = 2;
        int numMarkets_attrs = 3;

        String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.STSTest3db";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".sts3log";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_sts3cmd = remove_cmd + dbpath;

        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_sts3cmd);
        } catch (IOException e) {
            System.err.println("" + e);
        }

    /*
    ExtendedSystemDefs extSysDef =
      new ExtendedSystemDefs( "/tmp/minibase.jointestdb", "/tmp/joinlog",
			      1000,500,200,"Clock");
    */

        SystemDefs sysdef = new SystemDefs(dbpath, 1000, NUMBUF, "Clock");

        // creating the sailors relation
        AttrType[] Mtypes = new AttrType[3];
        Mtypes[0] = new AttrType(AttrType.attrInteger);
        Mtypes[1] = new AttrType(AttrType.attrString);
        Mtypes[2] = new AttrType(AttrType.attrSdogeometry);

        //SOS
        short[] Msizes = new short[1];
        Msizes[0] = 30; //first elt. is 30

        Tuple t = new Tuple();
        try {
            t.setHdr((short) 3, Mtypes, Msizes);
        } catch (Exception e) {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }

        int size = t.size();
        System.out.println("Size:" + size);

        // selecting the tuple into file "colamarkets"
        RID rid;
        Heapfile f = null;
        try {
            f = new Heapfile("colamarkets.in");
        } catch (Exception e) {
            System.err.println("*** error in Heapfile constructor ***");
            status = FAIL;
            e.printStackTrace();
        }

        t = new Tuple(size);
        try {
            t.setHdr((short) 3, Mtypes, Msizes);
        } catch (Exception e) {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }

        for (int i = 0; i < numMarkets; i++) {
            try {
                t.setIntFld(1, ((ColaMarkets) colamarkets.elementAt(i)).marketId);
                t.setStrFld(2, ((ColaMarkets) colamarkets.elementAt(i)).name);
                t.setSdogeometryFld(3, ((ColaMarkets) colamarkets.elementAt(i)).shape);
            } catch (Exception e) {
                System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
                status = FAIL;
                e.printStackTrace();
            }

            try {
                rid = f.insertRecord(t.returnTupleByteArray());
            } catch (Exception e) {
                System.err.println("*** error in Heapfile.selectRecord() ***");
                status = FAIL;
                e.printStackTrace();
            }
        }



        if (status != OK) {
            //bail out
            System.err.println("*** Error creation relation for colamarkets");
            Runtime.getRuntime().exit(1);
        }
    }

    private void Query1_CondExpr(CondExpr[] expr) {
        expr[0].next  = null;
        expr[0].op    = new AttrOperator(AttrOperator.aopEQ);
        expr[0].type1 = new AttrType(AttrType.attrSymbol);
        expr[0].type2 = new AttrType(AttrType.attrString);
        expr[0].operand1.symbol = new FldSpec (new RelSpec(RelSpec.outer), 2);
        expr[0].operand2.string = "cola_a";

        expr[1].next  = null;
        expr[1].op    = new AttrOperator(AttrOperator.aopEQ);
        expr[1].type1 = new AttrType(AttrType.attrSymbol);
        expr[1].type2 = new AttrType(AttrType.attrString);
        expr[1].operand1.symbol = new FldSpec (new RelSpec(RelSpec.outer), 2);
        expr[1].operand2.string = "cola_b";

        expr[2] = null;

    }

    public void Query1() {

        System.out.print("**********************Query1 strating *********************\n");
        boolean status = OK;

        // Sailors, Boats, Reserves Queries.
        System.out.print ("Query: Find the distance of cola market cola_a and cola market cola_b"
                + "SELECT c.name, SDO_GEOM.SDO_DISTANCE"
                + " FROM cola_markets c_a, cola_markets c_b"
                + " WHERE c_a.name = 'cola_a' and c_b.name = 'cola_b'\n");

        System.out.print ("\n(Tests3 FileScan, Projection)\n");

        CondExpr[] outFilter = new CondExpr[3];
        outFilter[0] = new CondExpr();
        outFilter[1] = new CondExpr();
        outFilter[2] = new CondExpr();

        Query1_CondExpr(outFilter);

        Tuple t = new Tuple();
        t = null;

        AttrType [] Mtypes = new AttrType[3];
        Mtypes[0] = new AttrType (AttrType.attrInteger);
        Mtypes[1] = new AttrType (AttrType.attrString);
        Mtypes[2] = new AttrType (AttrType.attrSdogeometry);

        //SOS
        short [] Msizes = new short[1];
        Msizes[0] = 30; //first elt. is 30

        FldSpec [] Mprojection = new FldSpec[2];
        Mprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 2);
        Mprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 3);

        AttrType [] jtype = new AttrType[2];
        jtype[0] = new AttrType (AttrType.attrString);
        jtype[1] = new AttrType (AttrType.attrSdogeometry);

        CondExpr [] selects = new CondExpr [1];
        selects = null;

        FileScan am = null;
        try {
            am  = new FileScan("colamarkets.in", Mtypes, Msizes,
                    (short)3, (short)2,
                    Mprojection, null);
        }
        catch (Exception e) {
            status = FAIL;
            System.err.println (""+e);
            e.printStackTrace();
        }

        if (status != OK) {
            //bail out
            System.err.println ("*** Error setting up scan for sailors");
            Runtime.getRuntime().exit(1);
        }
        System.out.println("done");
        Sdo_geometry x[] = new Sdo_geometry[2];

        try {
            int i = 0;
            while ((t = am.get_next()) != null) {
                t.print(jtype);
                x[i++] = t.getSdogeometryFld(2);
            }

            Sdo_geometry sdoval = x[0].intersection(x[1]);
            if (sdoval == null) {
                double distance = x[0].Distance(x[1]);
                System.out.println("distance is " + distance);
            } else {
                System.out.println("distance is 0");
            }
        }

        catch (Exception e) {
            System.err.println (""+e);
            e.printStackTrace();
            Runtime.getRuntime().exit(1);
        }
    }

    public boolean selectTest() {
        Query1();

        System.out.print("Finished select testing 3" + "\n");
        return true;
    }

    private void Disclaimer() {
        System.out.print("\n\nAny resemblance of persons in this database to"
                + " people living or dead\nis purely coincidental. The contents of "
                + "this database do not reflect\nthe views of the University,"
                + " the Computer  Sciences Department or the\n"
                + "developers...\n\n");
    }
}
