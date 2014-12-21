package partfinder.utils;

import partfinder.database.DBtoObject;
import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 14-10-18.
 * PN#:xxx
 */
public class SearchByPN {
    private String URL;
    private String USER;
    private String PWD;
    private String partNumber = null;
    private String description = null;
    private String rdo = null;
    private String mep = null;
    private String tableName = null;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    GetTableInfo getTableInfo = new GetTableInfo();

    public SearchByPN(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public Connection getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public List<List<DBtoObject>> FuzzySearch() {
        String tableDesc = null;
        List<List<DBtoObject>> infoLists = new ArrayList<List<DBtoObject>>();
        DBtoObject information = null;
        getTableInfo.getConnect();
        List<String> tableNames = getTableInfo.getTableNames();
        getTableInfo.closeAll();

        System.out.println(partNumber);
        System.out.println(description);
        System.out.println(mep);
        System.out.println(rdo);

        String mPn;
        String mDesc;
        String mMep;
        String mRdo;

        String oPn;
        String oDesc;
        String oMep;
        String oRdo;

        for (int i = 0; i < tableNames.size(); i++) {
            //for (int i = 0; i < 1; i++) {
            List<DBtoObject> objectList = new ArrayList<DBtoObject>();
            List<DBtoObject> infoList = new ArrayList<DBtoObject>();
            getResult(tableNames, objectList, i);

            mPn = " ";
            mDesc = " ";
            mMep = " ";
            mRdo = " ";

            oPn = " ";
            oDesc = " ";
            oMep = " ";
            oRdo = " ";

            for (DBtoObject anObjectList : objectList) {

                if (partNumber != null) {
                    mPn = anObjectList.getName().toLowerCase();
                    oPn = partNumber.toLowerCase();
                }

                if (description != null) {
                    mDesc = anObjectList.getDescription().toLowerCase();
                    oDesc = description.toLowerCase();
                }
                if (mep != null) {
                    mMep = anObjectList.getManufacturer_Equivalent_part().toLowerCase();
                    oMep = mep.toLowerCase();
                }
                if (rdo != null) {
                    mRdo = anObjectList.getRDO().toLowerCase();
                    oRdo = rdo.toLowerCase();
                }

                if (mPn.contains(oPn) && mDesc.contains(oDesc) && mMep.contains(oMep)
                        && mRdo.contains(oRdo)) {
                    information = anObjectList;
                    String tableName = tableNames.get(i).replace("BOM", "I");
                    try {
                        stmt = con.createStatement();
                        rs = stmt.executeQuery("Select \"Description\" From " + tableName);
                        while (rs.next()) {
                            tableDesc = rs.getString("Description");
                        }
                        rs.close();
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    information.setUsedByWho(tableDesc);
                    infoList.add(information);
                }
            }
            if (infoList.size() != 0) {
                infoLists.add(infoList);
            }
        }
        return infoLists;
    }

    private void getResult(List<String> tableNames, List<DBtoObject> objectList, int i) {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(String.format(
                    "select * from %s order by \"Row_Number\"", tableNames.get(i)));
            while (rs.next()) {
                DBtoObject dbToObject = new DBtoObject();
                dbToObject.setRow_number(rs.getInt("Row_Number"));
                dbToObject.setFather_number(rs.getInt("Father_Number"));
                dbToObject.setLevel_Number(rs.getInt("Level_Number"));
                if (rs.getString("Mark_Number") != null)
                    dbToObject.setMark_Number(rs.getString("Mark_Number"));
                if (rs.getString("Type") != null)
                    dbToObject.setType(rs.getString("Type"));
                if (rs.getString("Name") != null)
                    dbToObject.setName(rs.getString("Name"));
                if (rs.getString("Revision") != null)
                    dbToObject.setRevision(rs.getString("Revision"));
                if (rs.getString("State") != null)
                    dbToObject.setState(rs.getString("State"));
                if (rs.getString("UOM") != null)
                    dbToObject.setUOM(rs.getString("UOM"));
                if (rs.getString("Quantity") != null)
                    dbToObject.setQuantity(rs.getDouble("Quantity"));
                if (rs.getString("Description") != null)
                    dbToObject.setDescription(rs.getString("Description").replaceAll("&", "&amp;"));
                if (rs.getString("Manufacturer_Equivalent_part") != null)
                    dbToObject.setManufacturer_Equivalent_part(rs.getString("Manufacturer_Equivalent_part").replaceAll("&", "&amp;"));
                if (rs.getString("Manufacturer") != null)
                    dbToObject.setManufacturer(rs.getString("Manufacturer").replaceAll("&", "&amp;"));
                if (rs.getString("RDO") != null)
                    dbToObject.setRDO(rs.getString("RDO"));

                objectList.add(dbToObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeAll() {
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (stmt != null)
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (con != null)
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRdo() {
        return rdo;
    }

    public void setRdo(String rdo) {
        this.rdo = rdo;
    }

    public String getMep() {
        return mep;
    }

    public void setMep(String mep) {
        this.mep = mep;
    }
}
