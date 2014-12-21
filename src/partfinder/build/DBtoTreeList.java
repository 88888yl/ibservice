package partfinder.build;

import partfinder.database.DBtoObject;
import partfinder.utils.GetTableInfo;
import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 10/1/14.
 * Study code
 */
public class DBtoTreeList {
    private String URL;
    private String USER;
    private String PWD;
    private String partNumber = null;
    private String description = null;
    private String searchTableName = null;
    private String rdo = null;
    private String mep = null;
    private boolean isAllInfo = false;
    private List<String> dbTableNames = null;
    private int radioCheck = 0;
    private boolean ifchildren = false;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<DBtoObject> objectList = null;
    public List<List<Entry>> treeLists = null;

    public DBtoTreeList(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public DBtoTreeList(String URL, String USER, String PWD, String partNumber, String description, String mep, String rdo) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
        this.partNumber = partNumber;
        this.description = description;
        this.mep = mep;
        this.rdo = rdo;
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

    public List<List<Entry>> TableToTreeList() {
        treeLists = new ArrayList<List<Entry>>();
        List<Entry> treeList;
        if (searchTableName == null) {
            GetTableInfo getTableInfo = new GetTableInfo();
            getTableInfo.getConnect();
            List<String> tableNames = getTableInfo.getTableNames();
            getTableInfo.closeAll();
            for (String tableName : tableNames) {
                if (partNumber == null && description == null && mep == null && rdo == null)
                    treeList = ALlToTreeList(tableName);
                else {
                    treeList = KeyWordToTreeList(tableName);
                }
                treeLists.add(treeList);
            }
            return treeLists;
        } else {
            if (partNumber == null && description == null && mep == null && rdo == null)
                treeList = ALlToTreeList(searchTableName);
            else {
                treeList = KeyWordToTreeList(searchTableName);
            }
            treeLists.add(treeList);
            return treeLists;
        }
    }

    private List<Entry> ALlToTreeList(String tableName) {
        List<Entry> treeList = new ArrayList<Entry>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(String.format(
                    "select * from %s order by \"Row_Number\"", tableName));
            rs.next();
            XMLElement data = new XMLElement();
            data.xmlbegin = String
                    .format("<bom level='%s' type='%s' name='%s' Revision='%s' desc='%s' mep='%s' mfg='%s' rdo='%s'>\n",
                            rs.getString("Level_Number"), rs.getString("Type"),
                            " ", rs.getString("Revision"), rs.getString("Description"),
                            rs.getString("Manufacturer_Equivalent_part"),
                            rs.getString("Manufacturer"), rs.getString("rdo")).replaceAll("&", "&amp;");
            data.xmlbody = String
                    .format("<bom level='%s' type='%s' name='%s' Revision='%s' desc='%s' mep='%s' mfg='%s' rdo='%s' />\n",
                            rs.getString("Level_Number"), rs.getString("Type"),
                            " ", rs.getString("Revision"), rs.getString("Description"),
                            rs.getString("Manufacturer_Equivalent_part"),
                            rs.getString("Manufacturer"), rs.getString("rdo")).replaceAll("&", "&amp;");
            data.xmlend = " </bom> \n";
            treeList.add(new Entry(0, data));

            while (rs.next()) {
                treeList.add(new Entry(Integer.valueOf(rs.getString("Level_Number")),
                        ResultToXMLElement(rs)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treeList;
    }

    private List<Entry> KeyWordToTreeList(String searchTableName) {
        List<Entry> treeList = new ArrayList<Entry>();

        try {
            //objectList.clear();
            objectList = new ArrayList<DBtoObject>();
            stmt = con.createStatement();
            rs = stmt.executeQuery(String.format(
                    "select * from %s order by \"Row_Number\"", searchTableName));
            rs.next();
            XMLElement data = new XMLElement();
            data.xmlbegin = String
                    .format("<bom level='%s' type='%s' name='%s' Revision='%s' desc='%s' mep='%s' mfg='%s' rdo='%s'>  \n",
                            rs.getString("Level_Number"), rs.getString("Type"),
                            " ", rs.getString("Revision"), rs.getString("Description"),
                            rs.getString("Manufacturer_Equivalent_part"),
                            rs.getString("Manufacturer"), rs.getString("rdo")).replaceAll("&", "&amp;");
            data.xmlbody = String
                    .format("<bom level='%s' type='%s' name='%s' Revision='%s' desc='%s' mep='%s' mfg='%s' rdo='%s'/> \n",
                            rs.getString("Level_Number"), rs.getString("Type"),
                            " ", rs.getString("Revision"), rs.getString("Description"),
                            rs.getString("Manufacturer_Equivalent_part"),
                            rs.getString("Manufacturer"), rs.getString("rdo")).replaceAll("&", "&amp;");
            data.xmlend = "</bom>\n";
            treeList.add(new Entry(0, data));

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
            System.out.println(searchTableName + "-------: Searching...");

            for (int i = 0; i < objectList.size(); ++i) {
                treeList = SearchByKeyWords(objectList, treeList, i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treeList;
    }

    private List<Entry> SearchByKeyWords(
            List<DBtoObject> objectList, List<Entry> treeList, int i)throws SQLException {
        String mPn = " ";
        String mDesc = " ";
        String mMep = " ";
        String mRdo = " ";

        String oPn = " ";
        String oDesc = " ";
        String oMep = " ";
        String oRdo = " ";

        if (partNumber != null) {
            mPn = objectList.get(i).getName().toLowerCase();
            oPn = partNumber.toLowerCase();
        }
        if (description != null) {
            mDesc = objectList.get(i).getDescription().toLowerCase();
            oDesc = description.toLowerCase();
        }
        if (mep != null) {
            mMep = objectList.get(i).getManufacturer_Equivalent_part().toLowerCase();
            oMep = mep.toLowerCase();
        }
        if (rdo != null) {
            mRdo = objectList.get(i).getRDO().toLowerCase();
            oRdo = rdo.toLowerCase();
        }

        String keyWord = mPn + " " + mDesc + " " + mMep + " " + mRdo;
        String noSpaceKeyWord = keyWord.replaceAll(" ", "");

        if (radioCheck == 0) {
            if (keyWord.contains(oPn) && keyWord.contains(oDesc) && keyWord.contains(oMep)
                    && keyWord.contains(oRdo)) {
                treeList = RecursiveSearchFathers(i, treeList);
                if (ifchildren)
                    treeList = RecursiveSearchChildren(i, objectList.get(i).getLevel_Number(), treeList);
            }
        } else {
            if (noSpaceKeyWord.contains(oPn) || noSpaceKeyWord.contains(oDesc)
                    || noSpaceKeyWord.contains(oMep) || noSpaceKeyWord.contains(oRdo)) {
                treeList = RecursiveSearchFathers(i, treeList);
                if (ifchildren)
                    treeList = RecursiveSearchChildren(i, objectList.get(i).getLevel_Number(), treeList);
            }
        }
        return treeList;
    }

    private List<Entry> RecursiveSearchFathers(int i, List<Entry> treeList) throws SQLException {
        if (objectList.get(i).getFather_number() != 0) {
            int fa_row = objectList.get(i).getFather_number() - 1;
            treeList = RecursiveSearchFathers(fa_row, treeList);
        }
        treeList.add(new Entry(objectList.get(i).getLevel_Number(),
                ObjToXMLElement(objectList.get(i))));
        return treeList;
    }

    private List<Entry> RecursiveSearchChildren(int i, int level, List<Entry> treeList) throws SQLException {
        if (i < objectList.size() - 1 && objectList.get(i + 1).getLevel_Number() > level) {
            treeList.add(new Entry(objectList.get(i + 1).getLevel_Number(),
                    ObjToXMLElement(objectList.get(i + 1))));
            treeList = RecursiveSearchChildren(i + 1, level, treeList);
        }
        return treeList;
    }

    private XMLElement ResultToXMLElement(ResultSet rs) throws SQLException {
        XMLElement ret = new XMLElement();
        ret.xmlbegin = String
                .format(
                        "<bom level='%s' marknum='%s' type='%s' name='%s' revision='%s' " +
                                "state='%s' uom='%s' quantity='%s' desc='%s' mep='%s' mfg='%s' rdo='%s'>\n",
                        rs.getString("Level_Number"), rs.getString("Mark_Number"),
                        rs.getString("Type"), rs.getString("Name"),
                        rs.getString("Revision"), rs.getString("State"),
                        rs.getString("UOM"), rs.getString("Quantity"),
                        rs.getString("Description"),
                        rs.getString("Manufacturer_Equivalent_part"),
                        rs.getString("Manufacturer"),
                        rs.getString("RDO")).replaceAll("&", "&amp;");
        ret.xmlbody = String
                .format(
                        "<bom level='%s' marknum='%s' type='%s' name='%s' revision='%s' " +
                                "state='%s' uom='%s' quantity='%s' desc='%s' mep='%s' mfg='%s' rdo='%s' />\n",
                        rs.getString("Level_Number"), rs.getString("Mark_Number"),
                        rs.getString("Type"), rs.getString("Name"),
                        rs.getString("Revision"), rs.getString("State"),
                        rs.getString("UOM"), rs.getString("Quantity"),
                        rs.getString("Description"),
                        rs.getString("Manufacturer_Equivalent_part"),
                        rs.getString("Manufacturer"),
                        rs.getString("RDO")).replaceAll("&", "&amp;");

        ret.xmlend = String.format(" </bom> \n");

        return ret;
    }

    private XMLElement ObjToXMLElement(DBtoObject obj) throws SQLException {
        XMLElement ret = new XMLElement();
        ret.xmlbegin = String
                .format(
                        "<bom level='%s' marknum='%s' type='%s' name='%s' revision='%s' " +
                                "state='%s' uom='%s' quantity='%s' desc='%s' mep='%s' mfg='%s' rdo='%s'>\n",
                        String.valueOf(obj.getLevel_Number()),
                        String.valueOf(obj.getMark_Number()),
                        obj.getType(),
                        obj.getName(),
                        String.valueOf(obj.getRevision()),
                        obj.getState(),
                        obj.getUOM(),
                        String.valueOf(obj.getQuantity()),
                        obj.getDescription(),
                        obj.getManufacturer_Equivalent_part(),
                        obj.getManufacturer(),
                        obj.getRDO()
                ).replaceAll("&", "&amp;");
        ret.xmlbody = String
                .format(
                        "<bom level='%s' marknum='%s' type='%s' name='%s' revision='%s' " +
                                "state='%s' uom='%s' quantity='%s' desc='%s' mep='%s' mfg='%s' rdo='%s'/>\n",
                        String.valueOf(obj.getLevel_Number()),
                        String.valueOf(obj.getMark_Number()),
                        obj.getType(),
                        obj.getName(),
                        String.valueOf(obj.getRevision()),
                        obj.getState(),
                        obj.getUOM(),
                        String.valueOf(obj.getQuantity()),
                        obj.getDescription(),
                        obj.getManufacturer_Equivalent_part(),
                        obj.getManufacturer(),
                        obj.getRDO()
                ).replaceAll("&", "&amp;");
        ret.xmlend = String.format(" </bom> \n");

        return ret;
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

    public boolean isAllInfo() {
        return isAllInfo;
    }

    public void setAllInfo(boolean isAllInfo) {
        this.isAllInfo = isAllInfo;
    }

    public boolean isIfchildren() {
        return ifchildren;
    }

    public int getRadioCheck() {
        return radioCheck;
    }

    public void setRadioCheck(int radioCheck) {
        this.radioCheck = radioCheck;
    }

    public void setIfchildren(boolean ifchildren) {
        this.ifchildren = ifchildren;
    }

    public String getSearchTableName() {
        return searchTableName;
    }

    public void setSearchTableName(String searchTableName) {
        this.searchTableName = searchTableName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDbTableNames(List<String> dbTableNames) {
        this.dbTableNames = dbTableNames;
    }

    public List<String> getDbTableNames() {
        return dbTableNames;
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
}
