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

//INSERT INTO user_sdo_geom_metadata (
//        TABLE_NAME,
//        COLUMN_NAME,
//        DIMINFO,
//        SRID) VALUES (
//        'cola_markets',
//        'shape',
//        SDO_DIM_ARRAY( -- 20X20 grid
//        SDO_DIM_ELEMENT('X', 0, 20, 0.005),
//        SDO_DIM_ELEMENT('Y', 0, 20, 0.005)
//        ),
//        NULL -- SRID
//        );

class SdoGeoMetaData{
    public String tablename;
    public String column_name;
    public double[] xdim;
    public double[] ydim;

    public SdoGeoMetaData(String tablename, String column_name, double[] xdim, double[] ydim) {
        this.tablename = tablename;
        this.column_name = column_name;
        this.xdim = xdim;
        this.ydim = ydim;
    }
}

public class ITSTest
{
    public static void main(String argv[])
    {
        boolean itsstatus;

        ITSDriver its = new ITSDriver();
        itsstatus = its.insertTest();

        if (itsstatus != true) {
            System.out.println("Error ocurred during ITS test");
        }
        else {
            System.out.println("ITS test completed successfully");
        }
    }
}


class ITSDriver extends TestDriver
        implements GlobalConst {

    private boolean OK = true;
    private boolean FAIL = false;
    private Vector colamarkets;
    private SdoGeoMetaData sdogeommetadata;
    /**
     * Constructor
     */
    public ITSDriver() {

    }

    public boolean insertTest() {

        System.out.print("Started ITS tests" + "\n");

        //build ColaMarkets table
        colamarkets = new Vector();

        double[] v = new double[] {1.0, 1.0, 2.0, 3.0};

        colamarkets.addElement(new ColaMarkets(1, "cola_a", new Sdo_geometry(Sdo_gtype.RECTANGLE, v)));

        v = new double[] {2.5, 3.5, 3.5, 4.5};

        colamarkets.addElement(new ColaMarkets(2, "cola_b", new Sdo_geometry(Sdo_gtype.RECTANGLE, v)));


        boolean status = OK;
        int numMarkets = 2;
        int numMarkets_attrs = 3;

        String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.itstestdb";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".itslog";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_itscmd = remove_cmd + dbpath;

        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_itscmd);
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

        // inserting the tuple into file "colamarkets"
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
                System.err.println("*** error in Heapfile.insertRecord() ***");
                status = FAIL;
                e.printStackTrace();
            }
        }

        dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.sdogeommetadb";
        logpath = "/tmp/" + System.getProperty("user.name") + ".sdogeommetalog";

        remove_cmd = "/bin/rm -rf ";
        remove_logcmd = remove_cmd + logpath;
        remove_dbcmd = remove_cmd + dbpath;
        remove_itscmd = remove_cmd + dbpath;

        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_itscmd);
        } catch (IOException e) {
            System.err.println("" + e);
        }
    /*
    ExtendedSystemDefs extSysDef =
      new ExtendedSystemDefs( "/tmp/minibase.jointestdb", "/tmp/joinlog",
			      1000,500,200,"Clock");
    */

        // creating the sdogeommetadata relation
        AttrType[] MDtypes = new AttrType[4];
        MDtypes[0] = new AttrType(AttrType.attrString);
        MDtypes[1] = new AttrType(AttrType.attrString);
        MDtypes[2] = new AttrType(AttrType.attrRealArray);
        MDtypes[3] = new AttrType(AttrType.attrRealArray);

        //SOS
        short[] MDsizes = new short[2];
        MDsizes[0] = 30; //first elt. is 30
        MDsizes[1] = 30;

        t = new Tuple();
        try {
            t.setHdr((short) 4, MDtypes, MDsizes);
        } catch (Exception e) {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }

        size = t.size();

        // inserting the tuple into file "sdogeommetadata"
        try {
            f = new Heapfile("sdogeommetadata.in");
        } catch (Exception e) {
            System.err.println("*** error in Heapfile constructor ***");
            status = FAIL;
            e.printStackTrace();
        }

        t = new Tuple(size);
        try {
            t.setHdr((short) 4, MDtypes, MDsizes);
        } catch (Exception e) {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }

        sdogeommetadata = new SdoGeoMetaData(ColaMarkets.tablename,"shape",new double[]{0, 20,0.005}, new double[]{0,20,0.005});

        try {
            t.setStrFld(1,sdogeommetadata.tablename);
            t.setStrFld(2,sdogeommetadata.column_name);
            t.setRealArrayFld(3, sdogeommetadata.xdim);
            t.setRealArrayFld(4,sdogeommetadata.ydim);
        } catch (Exception e) {
            System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
            status = FAIL;
            e.printStackTrace();
        }

        try {
            rid = f.insertRecord(t.returnTupleByteArray());
        } catch (Exception e) {
            System.err.println("*** error in Heapfile.insertRecord() ***");
            status = FAIL;
            e.printStackTrace();
        }


        if (status != OK) {
            //bail out
            System.err.println("*** Error creation relation for colamarkets");
            Runtime.getRuntime().exit(1);
        }

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

