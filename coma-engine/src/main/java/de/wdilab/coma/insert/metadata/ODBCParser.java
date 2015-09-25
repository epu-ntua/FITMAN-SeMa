/*
 *  COMA 3.0 Community Edition
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package de.wdilab.coma.insert.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;

//sucessfully tested:
//MYSQL 5.1 (=Type 4) Version 5.1.50-community
//MICROSOFT SQL SERVER 2005 (=Type 2) Version 09.00.4053
//ORACLE 11g (=Type 3) Version 11.01.0070
//DB2/NT (=Type 1) Version 09.07.0001
/**
 * This class imports relation metadata from a RDBMS through ODBC.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class ODBCParser extends InsertParser {
	public static final String DRIVER_NAME = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static final String DB_URL = "jdbc:odbc:";
	  
	public ODBCParser(boolean dbInsert){
		super(dbInsert, Source.TYPE_ODBC);
	}

	
	public void parseMultipleSources(String[] filename, String userName, String userPass) {
		for (int i = 0; i < filename.length; i++) {
			parseSingleSource(filename[i], userName, userPass, null);
		}
	}
	
	@Override
	public int parseSingleSource(String filename, String schemaName, String author, String domain, String version, String comment) {
		System.out.println("ODBCParser.parseSingleSource is not supported with filename");
		return source_id;
	}
	
	public void parseSingleSource(String odbcEntry, String userName, String userPass, String schemaName) {
		parseSingleSource(odbcEntry, userName, userPass, schemaName, null, null, null, null);
	}
	
	int WAIT = 1000;  // works with 1000 (=1sec), 500, 100
	
	public void parseSingleSource(String odbcEntry, String userName, String userPass, String schemaName, String author, String domain, String version, String comment) {
//	      ODBCParser odbcParser = new ODBCParser(odbcEntry, userName, userPass, sourceName, date, dbInsert);
		ODBCHandler odbcHandler = new ODBCHandler(odbcEntry, userName, userPass, schemaName, this);
//	      System.out.println("Process database " + schemaName + " from " +  odbcEntry);
	      beforeParse(odbcEntry, userName, userPass, schemaName);
//	      boolean status = 
	    	  parse(odbcHandler);
	    	  try {
				odbcHandler.dbConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      afterParse();
	}
	
	  boolean parse(ODBCHandler odbcHandler) {
	      if (odbcHandler.checkConnection()) {
	          odbcHandler.getEverything();
	          odbcHandler.processSchema();
	          return true;
	      }
	      return false;
	  }
	  
	  public static String[] getSchemas(String odbcEntry, String userName, String userPass) {
	      ODBCHandler odbcHandler = new ODBCHandler(odbcEntry, userName, userPass, null, null);
	      String[] schemas = null;
	      if (odbcHandler.checkConnection()) {
	          System.out.print("Getting schemas from ODBC " + odbcEntry + " ... " );  
	          schemas = odbcHandler.getSchemas();
	      }
	      return schemas; 
	  }
	  
}

class ODBCHandler {

	boolean verbose = false;
	
		  //Inner classes    
		  class Table {
		    String tableName;
		    String tableType;
		    String remarks;

		    public String toString() {
		      return "Table " + tableName + "(" + tableType + ")" +
		          "(Remarks:" + remarks + ")";
		    }
		    public void print() {
		      System.out.println(toString());
		    }
		  }

		  class PrimaryKey {
		    String tableName;
		    String keyColumn;
		    String keyName;

		    public String toString() {
		      return "Primary key " + keyName + ":" + tableName + "." + keyColumn;
		    }
		    public void print() {
		      System.out.println(toString());
		    }
		  }

		//TODO what are cross references used for, support again?
		  class CrossReference { 
		    String primaryTable;
		    String primaryColumn;
		    String primaryKeyName;
		    String foreignTable;
		    String foreignColumn;
		    String foreignKeyName;
		    short updateRule;
		    short deleteRule;
		    short deferrability;

		    public String toString() {
		      return "Cross reference " +
		          primaryKeyName + ":" + primaryTable + "." + primaryColumn + "<=" +
		          foreignKeyName + ":" + foreignTable + "." + foreignColumn;
		    }
		    public void print() {
		      System.out.println(toString());
		    }
		  }

		  class UniqueConstraint {
		    String tableName;
		    String uniqueColumn;
		    String uniqueConstName;

		    public String toString() {
		      return "Unique constraint " + uniqueConstName + ":" +
		          tableName + "." + uniqueColumn;
		    }
		    public void print() {
		      System.out.println(toString());
		    }
		  }

		  class CheckConstraint {
		    String tableName;
		    String checkColumn;
		    String checkConstName;
		    String constText;

		    public String toString() {
		      return "Check constraint " + checkConstName + "<" + constText + "> on " +
		          tableName + "." + checkColumn;
		    }
		    public void print() {
		      System.out.println(toString());
		    }
		  }

		  class Column {
		    String tableName;
		    String columnName;

		    /* Common column properties */
		    short dataType;   // JDBC data types from java.sql.Types
		    String typeName;
		    int columnSize;
		    int decimalDigits;
		    int numPrecRadix;
		    String nullable;
		    String remarks;
		    String columnDef;  // default value
		    int charOctetLength;
		    int ordinalPosition;
		    String isNullable;

		    int rowCount;
		    int nullCount;

		    public void print() {
		      System.out.println("Column " + tableName + "." + columnName
		                         + "(" + typeName + ":" + dataType + ")(colLength:" +
		                         columnSize + ")"
		                         + "(dezDigits:" + decimalDigits + ")(nullcount:" +
		                         nullCount + ")");
		    }
		  }

		  public final static short DBMSDB2    = 1;
		  public final static short DBMSMSSQL  = 2;
		  public final static short DBMSORACLE = 3;
		  public final static short DBMSMYSQL  = 4;
		  public final static short DBMSOTHER  = 0;

		//  private String userName;
		//  private String userPass;
		  Connection dbConnection;
		  private DatabaseMetaData dbMetadata;
//		  private Statement dbStatement;

		  private String DBMSName; // DB2, MS SQL Server, Oracle ...
		  private short DBMSType;
		  private String DBMSVersion;

		  private String schemaName;
		  private ArrayList<String> schemas = null;
		  private ArrayList<Table> tables = null;
		  private ArrayList<Column> columns = null;
		  private ArrayList<PrimaryKey> primaryKeys = null;
//		  private ArrayList<CrossReference> crossRefs = null;
		  private ArrayList<UniqueConstraint> uniqueConsts = null;
//		  private ArrayList<CheckConstraint> checkConsts = null;

		  ODBCParser parser = null;

		  DatabaseMetaData getMetaData(){
			  return dbMetadata;
		  }
		  
		  public ODBCHandler(String odbcEntry, String userName, String userPass, String schemaName, ODBCParser parser) {
		    this.schemaName = schemaName;
//		    this.userName = userName;
//		    this.userPass = userPass;
		    this.parser = parser;
		    try {
//		      Driver driver = (Driver) Class.forName(DRIVER_NAME).newInstance();
		      dbConnection = DriverManager.getConnection(ODBCParser.DB_URL + odbcEntry, userName, userPass);

		      dbMetadata = dbConnection.getMetaData();
		      DBMSName = dbMetadata.getDatabaseProductName().toUpperCase();
		      DBMSVersion = dbMetadata.getDatabaseProductVersion();
		    }
		    catch (SQLException e) {
		      System.out.println("ODBCHandler():SQLException: " + e.getMessage());
		    }
		    catch (Exception e) {
		      System.out.println("ODBCHandler():Exception: " + e.getMessage());
		    }
		    if (dbConnection!=null && dbMetadata!=null) {
//		    	parser.setProvider(DB_URL + odbcEntry + ":" + schemaName);
		        
		        DBMSName = DBMSName.toUpperCase();
		        if (DBMSName.indexOf("DB2") != -1) DBMSType = DBMSDB2;
		        else if (DBMSName.indexOf("SQL SERVER") != -1) DBMSType = DBMSMSSQL;
		        else if (DBMSName.indexOf("ORACLE") != -1) DBMSType = DBMSORACLE;
		        else if (DBMSName.indexOf("MYSQL") !=-1) DBMSType = DBMSMYSQL; 
		        else DBMSType = DBMSOTHER;

		        //System.out.println("DBURL: " + DB_URL + odbcEntry + "; Driver: "+ DRIVER_NAME);
		       if (verbose) System.out.println("DBMS: " + DBMSName + "(" + DBMSVersion + "): Connected!");
		    }    
		  }

//		  void print() {
//		    System.out.println("Schema: " + schemaName);
//		    if (tables != null && !tables.isEmpty())
//		      for (int i = 0; i < tables.size(); i++)
//		       tables.get(i).print();
//		    if (columns != null || !columns.isEmpty())
//		      for (int i = 0; i < columns.size(); i++)
//		        columns.get(i).print();
////		    if (crossRefs != null && !crossRefs.isEmpty())
////		        for (int i = 0; i < crossRefs.size(); i++)
////		          crossRefs.get(i).print();
//		    
//		    if (primaryKeys != null && !primaryKeys.isEmpty())
//		      for (int i = 0; i < primaryKeys.size(); i++)
//		        primaryKeys.get(i).print();    
//		    if (uniqueConsts != null && !uniqueConsts.isEmpty())
//		      for (int i = 0; i < uniqueConsts.size(); i++)
//		        uniqueConsts.get(i).print();
//		    if (checkConsts != null && !checkConsts.isEmpty())
//		      for (int i = 0; i < checkConsts.size(); i++)
//		        checkConsts.get(i).print();
//		  }
		  
		  boolean checkConnection() {
		      if (dbConnection!=null && dbMetadata!=null) return true;
		      return false;
		  }
		  
		  void getEverything() {    
			  if (verbose) System.out.print("Retrieving table metadata ... ");
		      getTables();
		      if (verbose) System.out.println(tables.size() + " tables");

		      if (verbose) System.out.print("Retrieving column metadata ... ");
		      getColumns();
		      if (verbose) System.out.println(columns.size() + " columns");

//		      if (verbose) System.out.print("Retrieving cross references ... ");
//		      getCrossReferences(); 
//		      if (verbose) System.out.println(crossRefs.size() + " cross refrences");
		      
//		      if (crossRefs != null && !crossRefs.isEmpty())
//		          for (int i = 0; i < crossRefs.size(); i++)
//		            ( crossRefs.get(i)).print();

		      /*         
		      System.out.print("Retrieving primary keys ... ");
		      getPrimaryKeys();
		      System.out.println(primaryKeys.size() + " primary keys");
		      
		      System.out.print("Retrieving unique constraints ... ");
		      getUniqueConstraints();
		      System.out.println(uniqueConsts.size() + " unique constraints");
		    
		      System.out.print("Retrieving check constraints ... ");
		      getCheckConstraints();
		      System.out.println(checkConsts.size() + " check constraints");
		      */
		      
		      if (verbose) System.out.println("Done!");
		  }
		  
		  String[] getSchemas() {
		      if (schemas==null) {
		          schemas = new ArrayList<String>();
		          try {         
		              ResultSet rs = dbMetadata.getSchemas();
		              while (rs.next()) {
		                  String name = rs.getString(1);              
		                  schemas.add(name);
		              }
		              rs = dbMetadata.getCatalogs();
		              while (rs.next()) {
		                  String name = rs.getString(1);                  
		                  schemas.add(name);
		              }
		          }
		          catch (SQLException e) {
		              System.out.println("getSchemas():SQLException: " + e.getMessage());
		          }
		          catch (Exception e) {
		              System.out.println("getSchemas():Exception: " + e.getMessage());
		          }
		      }
		      if (schemas.size()>0) 
		          return schemas.toArray(new String[schemas.size()]);
		      return null;         
		  }

		  Table[] getTables() {
		    if (tables == null) {
		      tables = new ArrayList<Table>();
		      try {
		        String[] tableTypes = { "TABLE"};
		        ResultSet trs = dbMetadata.getTables(null, schemaName, "%", tableTypes); // works for 3.51 driver
				if (DBMSType == 1 || DBMSType == 2 || DBMSType == 4) { // not for Oracle because of problems
					if (trs.getType()!=ResultSet.TYPE_FORWARD_ONLY && trs.next()) {
						trs.previous();
					} else if (trs.getType()==ResultSet.TYPE_FORWARD_ONLY && trs.next()){
						 trs = dbMetadata.getTables(null, schemaName, "%", tableTypes);
					} else {
						trs = dbMetadata.getTables(schemaName, "%", "%", tableTypes);  // works for 5.1 driver
					}
				}
		        while (trs.next()) {
		          Table t = new Table();
		          t.tableName = trs.getString("TABLE_NAME");
		          t.tableType = trs.getString("TABLE_TYPE");
		          t.remarks = trs.getString("REMARKS");
		          tables.add(t);
		        }
		      }
		      catch (SQLException e) {
		        System.out.println("getTables():SQLException: " + e.getMessage());
		      }
		      catch (Exception e) {
		        System.out.println("getTables():Exception: " + e.getMessage());
		      }
		    }
		    if (tables.size() > 0)
		      return tables.toArray(new Table[tables.size()]);
		    return null;
		  }

		  PrimaryKey[] getPrimaryKeys() {
		    if (primaryKeys == null) {
		      primaryKeys = new ArrayList<PrimaryKey>();
		      Table t;
		      ResultSet pkrs;
		      for (int i = 0; i < tables.size(); i++) {
		        t = tables.get(i);
		        try {
		          pkrs = dbMetadata.getPrimaryKeys(null, schemaName, t.tableName);
		          while (pkrs.next()) {
		            PrimaryKey pk = new PrimaryKey();
		            pk.tableName = pkrs.getString("TABLE_NAME");
		            pk.keyColumn = pkrs.getString("COLUMN_NAME");
		            pk.keyName   = pkrs.getString("PK_NAME");
		            primaryKeys.add(pk);
		          }
		        }
		        catch (SQLException e) {
		          System.out.println("getPrimaryKeys():SQLException: " + e.getMessage());
		        }
		        catch (Exception e) {
		          System.out.println("getPrimaryKeys():Exception: " + e.getMessage());
		        }
		      }
		    }
		    if (primaryKeys.size() > 0)
		      return primaryKeys.toArray(new PrimaryKey[primaryKeys.size()]);
		    return null;
		  }

//		  CrossReference[] getCrossReferences() {
//		    if (crossRefs == null) {
//		      crossRefs = new ArrayList<CrossReference>();
//		      for (int i = 0; i < tables.size(); i++) {
//		        Table t = tables.get(i);
//		        ResultSet crrs = null;
//		        try {
//		          crrs = dbMetadata.getExportedKeys(null, schemaName, t.tableName);
//		          while (crrs.next()) {
//		            CrossReference cr = new CrossReference();
//		            cr.primaryTable   = crrs.getString("PKTABLE_NAME");
//		            cr.primaryColumn  = crrs.getString("PKCOLUMN_NAME");
//		            cr.foreignTable   = crrs.getString("FKTABLE_NAME");
//		            cr.foreignColumn  = crrs.getString("FKCOLUMN_NAME");
//		            cr.updateRule     = crrs.getShort("UPDATE_RULE");
//		            cr.deleteRule     = crrs.getShort("DELETE_RULE");
//		            cr.foreignKeyName = crrs.getString("FK_NAME");
//		            cr.primaryKeyName = crrs.getString("PK_NAME");
//		            cr.deferrability = crrs.getShort("DEFERRABILITY");
//		            
//		            //clean, because of some MySQL strange things, adding "`" to column and table names
//		            if (cr.primaryTable.startsWith("`")) 
//		                cr.primaryTable = cr.primaryTable.substring(1);
//		            if (cr.primaryTable.endsWith("`"))
//		                cr.primaryTable = cr.primaryTable.substring(0, cr.primaryTable.length()-1);
//		            
//		            if (cr.primaryColumn.startsWith("`")) 
//		                cr.primaryColumn = cr.primaryColumn.substring(1);
//		            if (cr.primaryColumn.endsWith("`"))
//		                cr.primaryColumn = cr.primaryColumn.substring(0, cr.primaryColumn.length()-1);
//		            
//		            if (cr.foreignTable.startsWith("`")) 
//		                cr.foreignTable = cr.foreignTable.substring(1);
//		            if (cr.foreignTable.endsWith("`"))
//		                cr.foreignTable = cr.foreignTable.substring(0, cr.foreignTable.length()-1);
//		            
//		            if (cr.foreignColumn.startsWith("`")) 
//		                cr.foreignColumn = cr.foreignColumn.substring(1);
//		            if (cr.foreignColumn.endsWith("`"))
//		                cr.foreignColumn = cr.foreignColumn.substring(0, cr.foreignColumn.length()-1);
//		            
//		            crossRefs.add(cr);
//		          }
//		        }
//		        catch (SQLException e) {
//		          System.out.println("getCrossReferences():SQLException: " +
//		                             e.getMessage());
//		        }
//		        catch (Exception e) {
//		          System.out.println("getCrossReferences():Exception: " + e.getMessage());
//		        }
//		      }
//		    }
//		    if (crossRefs.size() > 0)
//		      return crossRefs.toArray(new CrossReference[crossRefs.
//		                                                  size()]);
//		    return null;
//		  }

		  UniqueConstraint[] getUniqueConstraints() {
		    if (uniqueConsts == null) {
		      uniqueConsts = new ArrayList<UniqueConstraint>();
		      for (int i = 0; i < tables.size(); i++) {
		        Table t = tables.get(i);
		        ResultSet idrs = null;
		        try {
		          idrs = dbMetadata.getIndexInfo(null, schemaName, t.tableName, true, false);
		          while (idrs.next()) {
		            String tabName = idrs.getString("TABLE_NAME");
		            String indName = idrs.getString("INDEX_NAME");
		            String colName = idrs.getString("COLUMN_NAME");
		            if (indName != null) {
		              UniqueConstraint uc = new UniqueConstraint();
		              uc.tableName = tabName;
		              uc.uniqueColumn = colName;
		              uc.uniqueConstName = indName;
		              uniqueConsts.add(uc);
		            }
		          }
		        }
		        catch (SQLException e) {
		          System.out.println("getUniqueConstraints():SQLException: " +
		                             e.getMessage());
		        }
		        catch (Exception e) {
		          System.out.println("getUniqueConstraints():Exception: " +
		                             e.getMessage());
		        }
		      }
		    }
		    if (uniqueConsts.size() > 0)
		      return uniqueConsts.toArray(new UniqueConstraint[
		          uniqueConsts.size()]);
		    return null;
		  }

//		  CheckConstraint[] getCheckConstraints() {
//		    String DB2query = "SELECT A.tabname, B.colname, A.constname, A.text " +
//		        "FROM syscat.checks A, syscat.colchecks B " +
//		        "WHERE A.constname = B.constname";
//		    String SQLquery =
//		        "SELECT A.constraint_name, B.column_name, A.constraint_name, A.check_clause " +
//		        "FROM INFORMATION_SCHEMA.check_constraints A, INFORMATION_SCHEMA.constraint_column_usage B " +
//		        "WHERE A.constraint_name = B.constraint_name";
//		    String ORAquery =
//		        "SELECT A.table_name, B.column_name, A.constraint_name, A.search_condition " +
//		        "FROM all_constraints A, all_cons_columns B " +
//		        "WHERE A.constraint_name = B.constraint_name AND A.constraint_type ='C'";
//
//		    if (checkConsts == null) {
//		      checkConsts = new ArrayList<CheckConstraint>();
//		      String query = null;
//
//		      switch (DBMSType) {
//		        case DBMSDB2:
//		          query = DB2query;
//		          break; // IBM DB2
//		        case DBMSMSSQL:
//		          query = SQLquery;
//		          break; // MS SQL Server
//		        case DBMSORACLE:
//		          query = ORAquery;
//		          break; // Oracle
//		        default:
//		          query = null;
//		      }
//
//		      if (query!=null) {
//		        ResultSet ccrs = null;
//		        try {
//		          if (dbStatement==null) dbStatement = dbConnection.createStatement();
//		          ccrs = dbStatement.executeQuery(query);
//		          while (ccrs.next()) {
//		            CheckConstraint cc = new CheckConstraint();
//		            cc.tableName = ccrs.getString(1);
//		            cc.checkColumn = ccrs.getString(2);
//		            cc.checkConstName = ccrs.getString(3);
//		            cc.constText = ccrs.getString(4);
//		            checkConsts.add(cc);
//		          }
//		        }
//		        catch (SQLException e) {
//		          System.out.println("getCheckConstraints():SQLException: " + e.getMessage());
//		        }
//		        catch (Exception e) {
//		          System.out.println("getCheckConstraints():Exception: " + e.getMessage());
//		        }
//		      }
//		    }
//		    if (checkConsts.size() > 0)
//		      return checkConsts.toArray(new CheckConstraint[
//		          checkConsts.size()]);
//		    return null;
//		  }

		  Column[] getColumns() {
		    if (columns == null) {
		      columns = new ArrayList<Column>();
		      Table t;
		      ResultSet crs;
		      for (int i = 0; i < tables.size(); i++) {
		        t = (tables.get(i));
		        try {
		          crs = dbMetadata.getColumns(null, schemaName, t.tableName, null);
//		        	crs = dbMetadata.getColumns(null, null, t.tableName, null); // works for all
		          while (crs.next()) {
		            Column c = new Column();
		            c.tableName       = crs.getString("TABLE_NAME");
		            c.columnName      = crs.getString("COLUMN_NAME");
		            c.dataType        = crs.getShort("DATA_TYPE");
		            c.typeName        = crs.getString("TYPE_NAME").toUpperCase();
		            c.columnSize      = crs.getInt("COLUMN_SIZE");
		            c.decimalDigits   = crs.getInt("DECIMAL_DIGITS");
		            c.numPrecRadix    = crs.getInt("NUM_PREC_RADIX");
		            c.nullable        = crs.getString("NULLABLE");
		            c.remarks         = crs.getString("REMARKS");
		            c.columnDef       = crs.getString("COLUMN_DEF");
		            c.charOctetLength = crs.getInt("CHAR_OCTET_LENGTH");
		            c.ordinalPosition = crs.getInt("ORDINAL_POSITION");
		            c.isNullable      = crs.getString("IS_NULLABLE");
		            columns.add(c);
		          }
		        }
		        catch (SQLException e) {
		          System.out.println("getColumns(" + schemaName + "." +
		                             t.tableName + "):SQLException: " + e.getMessage());
		        }
		        catch (Exception e) {
		          System.out.println("getColumns():Exception: " + e.getMessage());
		        }
		      }
		    }
		    if (columns.size() > 0)
		      return columns.toArray(new Column[columns.size()]);
		    return null;
		  }

		  String typeToString(short dataType) {
		    switch (dataType) {
		      case Types.ARRAY: return "ARRAY";
		      case Types.BIGINT: return "BIGINT";
		      case Types.BINARY: return "BINARY";
		      case Types.BIT: return "BIT";
		      case Types.BLOB: return "BLOB";
		      case Types.BOOLEAN: return "BOOLEAN";
		      case Types.CHAR: return "CHAR";
		      case Types.CLOB: return "CLOB";
		      case Types.DATALINK: return "DATALINK";
		      case Types.DATE: return "DATE";
		      case Types.DECIMAL: return "DECIMAL";
		      case Types.DISTINCT: return "DISTINCT";
		      case Types.DOUBLE: return "DOUBLE";
		      case Types.FLOAT: return "FLOAT";
		      case Types.INTEGER: return "INTEGER";
		      case Types.JAVA_OBJECT: return "JAVA_OBJECT";
		      case Types.LONGVARBINARY: return "LONGVARBINARY";
		      case Types.LONGVARCHAR: return "LONGVARCHAR";
		      case Types.NULL: return "NULL";
		      case Types.NUMERIC: return "NUMERIC";
		      case Types.REAL: return "REAL";
		      case Types.REF: return "REF";
		      case Types.SMALLINT: return "SMALLINT";
		      case Types.STRUCT: return "STRUCT";
		      case Types.TIME: return "TIME";
		      case Types.TIMESTAMP: return "TIMESTAMP";
		      case Types.TINYINT: return "TINYINT";
		      case Types.VARBINARY: return "VARBINARY";
		      case Types.VARCHAR: return "VARCHAR";
		      case Types.OTHER: return "OTHER";
		      default: return "OTHER";
		    }
		  }

		  void processSchema() {
		    int id = 0;
		    
//		    //insert root node    
//		    String namespace = schemaName;      

		    Table[] tables = getTables();    
		    Column[] columns = getColumns();        
//		    CrossReference[] crossRefs = getCrossReferences();
		    
		    if (tables == null || columns == null){
		    	return;
		    }
		    
		    String[] tableAccs = new String[tables.length];
		    String[] colAccs   = new String[columns.length];
		           
		   for (int i=0; i<tables.length; i++) {
		        Table table = tables[i];
		        int tableId = ++id;
		        String tableAcc = String.valueOf(tableId);
		        tableAccs[i] = tableAcc;
		        
		        String tableName = table.tableName;
//		        String tableType = null;
//		        String tableTypespace = null;
//		        String tableKind = Element.KIND_GLOBELEM;
//		        String tableComment = null;
		        
		        //Insert table def
//		        parser.loadObject(schemaName, tableAcc, tableName, tableType, namespace, 
//		                                tableTypespace, tableKind, tableComment,
//		                                null, null, null, null, null, null);
		        
//		        int root_id = parser.insertObject(parser.source_id,  "root", 
		        int root_id = parser.insertObject(parser.source_id,  tableName, 
//		        		parser.sourceName,  Element.KIND_GLOBELEM);
		        		tableName,  Element.KIND_GLOBELEM);
		    
		       for (int j=0; j<columns.length; j++) {
		            Column col = columns[j];

		            if (col.tableName.equals(table.tableName)) {
		              int colId = ++id;
		              String colAcc = String.valueOf(colId);
		              colAccs[j] = colAcc;
		              
		              String columnName = col.columnName;
		              String columnType = typeToString(col.dataType);
//		              String columnTypespace = null;               
//		              String columnKind = Element.KIND_ELEMENT;
		              String columnComment = col.remarks;
		             
//		              //Check for cross reference, set name and type 
//		              if (crossRefs!=null) for (int k=0; k<crossRefs.length; k++) {
//		                  CrossReference crossRef = crossRefs[k];
//		                  //System.out.println(" foreign: [" + crossRef.foreignTable + "] and [" +crossRef.foreignColumn +"]");
//		                  if (tableName.equals(crossRef.foreignTable) && columnName.equals(crossRef.foreignColumn)) {
//		                      //this column of this table references the primary key of another table 
//		                      //set both name and type to the name of the primary key table 
//		                      columnName = crossRef.primaryTable; 
//		                      columnType = crossRef.primaryTable;           
////		                      columnTypespace = namespace;
//		                  }
//		              }
		                  
		              //Insert column def and link it to the table 
//		              parser.loadObject(schemaName, colAcc, columnName, columnType,
//		                                      namespace, columnTypespace, columnKind, columnComment,
//		                                      null, null, null, null,null,null);
//		              parser.loadLink(schemaName, colAcc, schemaName, tableAcc, SourceRelationship.REL_IS_A, null);
		              
		              int current_id = 
		            	  parser.insertObject(parser.source_id, colAcc, columnName, columnType, 
		            			  null, Element.KIND_ELEMENT, columnComment, null);
		              parser.insertLink(parser.sourcerel_id, root_id, current_id);
						
		            }
		          }
		      }       
		  }
		
}