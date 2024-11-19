package simpledb;

// (?) Should I define the fields as private or as public?

public class Table {
    public String name;
    public DBFile file;
    public String pkeyField;

    // Constructor
    public Table(DBFile file, String name, String pkeyField) {
        this.file = DBFile;
        this.name = name;
        this.pkeyField = pkeyField;
    }
}