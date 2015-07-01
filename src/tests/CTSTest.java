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

class ColaMarkets {
    public static final String tablename = "ColaMarkets";
    public int marketId;
    public String name;
    public Sdo_geometry shape;

    public ColaMarkets(int _marketId, String _name, Sdo_geometry _shape) {
        this.marketId = _marketId;
        this.name = _name;
        this.shape = _shape;
    }
}

public class CTSTest
{
    public static void main(String argv[])
    {
        boolean ctsstatus;
        //SystemDefs global = new SystemDefs("bingjiedb", 100, 70, null);
        //JavabaseDB.openDB("/tmp/nwangdb", 5000);

        CTSDriver cts = new CTSDriver();

        ctsstatus = cts.createTest();
        if (ctsstatus != true) {
            System.out.println("Error ocurred during CTS test");
        }
        else {
            System.out.println("CTS test completed successfully");
        }
    }
}


class CTSDriver extends TestDriver
        implements GlobalConst {

    private boolean OK = true;
    private boolean FAIL = false;

    /**
     * Constructor
     */
    public CTSDriver() {

    }

    public boolean createTest() {

        System.out.print("Started cts tests" + "\n");

        boolean status = OK;
        int numMarkets_attrs = 3;

        String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.ctstestdb";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".ctslog";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_ctscmd = remove_cmd + dbpath;

        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_ctscmd);
        } catch (IOException e) {
            System.err.println("" + e);
        }


    /*
    ExtendedSystemDefs extSysDef =
      new ExtendedSystemDefs( "/tmp/minibase.jointestdb", "/tmp/joinlog",
			      1000,500,200,"Clock");
    */

        SystemDefs sysdef = new SystemDefs(dbpath, 1000, NUMBUF, "Clock");

        // creating the colamarkets relation
        AttrType[] Mtypes = new AttrType[3];
        Mtypes[0] = new AttrType(AttrType.attrInteger);
        Mtypes[1] = new AttrType(AttrType.attrString);
        Mtypes[2] = new AttrType(AttrType.attrSdogeometry);


        //SOS
        short[] Msizes = new short[1];
        Msizes[0] = 30; //first elt. is 30

        Tuple t = new Tuple();
        try{
            t.setHdr((short) 3, Mtypes, Msizes);
        } catch (Exception e) {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }

        int size = t.size();

        if (status != OK) {
            //bail out
            System.err.println("*** Error creating relation for colamarkets");
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

