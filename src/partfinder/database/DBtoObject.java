package partfinder.database;

/**
 * Created by myl on 14-10-7.
 */
public class DBtoObject {

    public int row_number = -1;
    public int father_number = -1;
    public int Level_Number = -1;
    public String Mark_Number = " ";
    public String Type = " ";
    public String Name = " ";
    public String Revision = " ";
    public String State = " ";
    public String UOM = " ";
    public double Quantity = -1;
    public String Description = " ";
    public String Manufacturer_Equivalent_part = " ";
    public String Manufacturer = " ";
    public String RDO = " ";
    public String UsedByWho = " ";

    public DBtoObject() {
    }

    public DBtoObject(int row_number, int father_number,
                      int level_Number, String mark_Number,
                      String type, String name, String revision,
                      String state, String UOM, double quantity,
                      String description, String Manufacturer_Equivalent_part,
                      String Manufacturer, String RDO) {
        this.row_number = row_number;
        this.father_number = father_number;
        this.Level_Number = level_Number;
        this.Mark_Number = mark_Number;
        this.Type = type;
        this.Name = name;
        this.Revision = revision;
        this.State = state;
        this.UOM = UOM;
        this.Quantity = quantity;
        this.Description = description;
        this.Manufacturer_Equivalent_part = Manufacturer_Equivalent_part;
        this.Manufacturer = Manufacturer;
        this.RDO = RDO;
    }

    public String getManufacturer_Equivalent_part() {
        return Manufacturer_Equivalent_part;
    }

    public void setManufacturer_Equivalent_part(String manufacturer_Equivalent_part) {
        Manufacturer_Equivalent_part = manufacturer_Equivalent_part;
    }

    public String getManufacturer() {
        return Manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        Manufacturer = manufacturer;
    }

    public int getRow_number() {
        return row_number;
    }

    public void setRow_number(int row_number) {
        this.row_number = row_number;
    }

    public int getFather_number() {
        return father_number;
    }

    public void setFather_number(int father_number) {
        this.father_number = father_number;
    }

    public int getLevel_Number() {
        return Level_Number;
    }

    public void setLevel_Number(int level_Number) {
        Level_Number = level_Number;
    }

    public String getMark_Number() {
        return Mark_Number;
    }

    public void setMark_Number(String mark_Number) {
        Mark_Number = mark_Number;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRevision() {
        return Revision;
    }

    public void setRevision(String revision) {
        Revision = revision;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getRDO() {
        return RDO;
    }

    public void setRDO(String RDO) {
        this.RDO = RDO;
    }

    public String getUsedByWho() {
        return UsedByWho;
    }

    public void setUsedByWho(String usedByWho) {
        UsedByWho = usedByWho;
    }
}