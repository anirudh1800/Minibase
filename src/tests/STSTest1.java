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

public class STSTest1
{
    public static void main(String argv[])
    {
        boolean stsstatus;

        STSDriver sts = new STSDriver();
        stsstatus = sts.selectTest();

        if (stsstatus != true) {
            System.out.println("Error ocurred during STS test");
        }
        else {
            System.out.println("STS test completed successfully");
        }
        Query1();
    }
}

  private void Query1_CondExpr(CondExpr[] expr) {
    expr[0].next  = null;
    expr[0].op    = new AttrOperator(AttrOperator.aopEQ);
    expr[0].type1 = new AttrType(AttrType.attrSymbol);
    expr[0].type2 = new AttrType(AttrType.attrSymbol);
    expr[0].operand1.symbol = new FldSpec (new RelSpec(RelSpec.outer),1);
    expr[0].operand2.symbol = new FldSpec (new RelSpec(RelSpec.innerRel),1);

    expr[1].op    = new AttrOperator(AttrOperator.aopEQ);
    expr[1].next  = null;
    expr[1].type1 = new AttrType(AttrType.attrSymbol);
    expr[1].type2 = new AttrType(AttrType.attrInteger);
    expr[1].operand1.symbol = new FldSpec (new RelSpec(RelSpec.innerRel),2);
    expr[1].operand2.integer = 1;

    expr[2] = null;
  }

  public void Query1() {

    System.out.print("**********************Query1 strating *********************\n");
    boolean status = OK;

    // Sailors, Boats, Reserves Queries.
    System.out.print ("Query: Find the area of a cola market cola_a "
            "SELECT c.name, SDO_GEOM.SDO_AREA(c.shape, 0.005)" 
            + " FROM cola_markets c" 
            + " WHERE c.name = 'cola_a'\n\n");

    System.out.print ("\n(Tests FileScan, Projection, and Sort-Merge Join)\n");

    CondExpr[] outFilter = new CondExpr[2];
    outFilter[0] = new CondExpr();
    outFilter[1] = new CondExpr();
    
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

    FldSpec [] Mprojection = new FldSpec[1];
    Mprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);

    AttrType [] jtype = new AttrType[2];
    jtype[0] = new AttrType (AttrType.attrString);
    jtype[1] = new AttrType (AttrType.attrSdogeometry);

    CondExpr [] selects = new CondExpr [1];
    selects = null;

    FileScan am = null;
    try {
      am  = new FileScan("colamarkets.in", Mtypes, Msizes,
              (short)3, (short)1,
              Mprojection, null);
    }
    catch (Exception e) {
      status = FAIL;
      System.err.println (""+e);
    }

    if (status != OK) {
      //bail out
      System.err.println ("*** Error setting up scan for sailors");
      Runtime.getRuntime().exit(1);
    }
    try {
        while ((t = am.get_next()) != null) {
            t.print(jtype);
            qcheck5.Check(t);
        }
    }
    catch (Exception e) {
        System.err.println (""+e);
        Runtime.getRuntime().exit(1);
    }
  }
class STSDriver extends TestDriver
        implements GlobalConst {

    private boolean OK = true;
    private boolean FAIL = false;
    private Vector colamarkets;
    private SdoGeoMetaData sdogeommetadata;
    /**
     * Constructor
     */
    public STSDriver() {

    }

    public boolean selectTest() {

        System.out.print("Started STS tests" + "\n");

        //build ColaMarkets table
        colamarkets = new Vector();

        Vector<Double> v = new Vector<Double>();

        v.add(1.0);
        v.add(1.0);
        v.add(2.0);
        v.add(3.0);

        colamarkets.addElement(new ColaMarkets(1, "cola_a", new Sdo_geometry(Sdo_gtype.RECTANGLE, v)));

        v.clear();

        v.add(2.5);
        v.add(3.5);
        v.add(3.5);
        v.add(4.5);

        colamarkets.addElement(new ColaMarkets(2, "cola_b", new Sdo_geometry(Sdo_gtype.RECTANGLE, v)));

        boolean status = OK;
        int numMarkets = 2;
        int numMarkets_attrs = 3;

        String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.ststestdb";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".stslog";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_stscmd = remove_cmd + dbpath;

        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_stscmd);
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
                rid = f.selectRecord(t.returnTupleByteArray());
            } catch (Exception e) {
                System.err.println("*** error in Heapfile.selectRecord() ***");
                status = FAIL;
                e.printStackTrace();
            }
        }

        dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.sdogeommetadb";
        logpath = "/tmp/" + System.getProperty("user.name") + ".sdogeommetalog";

        remove_cmd = "/bin/rm -rf ";
        remove_logcmd = remove_cmd + logpath;
        remove_dbcmd = remove_cmd + dbpath;
        remove_stscmd = remove_cmd + dbpath;

        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_stscmd);
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

        // selecting the tuple into file "sdogeommetadata"
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
            rid = f.selectRecord(t.returnTupleByteArray());
        } catch (Exception e) {
            System.err.println("*** error in Heapfile.selectRecord() ***");
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

