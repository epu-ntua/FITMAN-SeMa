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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Vector;

import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;

/**
 * This class extracts the metadata from a sql file as a model.
 * 
 * Can be used as a workaround if the ODBC parse doesn't support the RDBMS.
 *  
 * @author Viet Hung Do, Sabine Massmann
 * 
 */
public class SQLParser extends InsertParser {

	private static BufferedReader inBuffer = null;
	private static HashMap<String, Vector<String[]>> tab_Attributes = null;
	
	public SQLParser(boolean dbInsert){
		super(dbInsert, Source.TYPE_SQL);
	}

	@Override
	public int parseSingleSource(String filename, String schemaName, String author, String domain, String version, String comment) {
//	    System.out.println("Process schema " + sourceName + " from file " + filename + " ... " );
	    // start logging, initiate insert statements

		beforeParse(schemaName, filename);
		// parse information into temp tables (parse_<tablename>)
		parse();
	//	 additional: author, domain, version
		setInformation(author, domain, version, comment);
		   // copy information into real tables (<tablename>), delete temp tables, close statements
		afterParse();
	    return source_id;
	}
	
	
	/**
	 * parse SQL-Datei und speichern die extrahierten Daten in Tabellen
	 * "parse_object" & "parse_link"
	 * 
	 * @param fileName
	 */
	private void parse() {
		sqlParse(provider); // Datenbankname

		// int colNumber = 1;
		int id = 0;

		if (tab_Attributes.keySet().size() > 0) {
			// erzeugt Collection von Attributen
			for (String tableName : tab_Attributes.keySet()) {
				int count = tab_Attributes.get(tableName).size();

				if (count != 0) {
					int tableId = ++id;
					String tableAcc = String.valueOf(tableId);

					// fuegt Infos ueber die Tabelle in die Datenbank ein
//					loadObject(sourceName, tableAcc, tableName, tableType,
//							namespace, tableTypeSpace, tableKind, tableComment);

					int root_id = insertObject(source_id,  tableAcc,  tableName,  Element.KIND_GLOBELEM);
					
					if (!tab_Attributes.get(tableName).isEmpty()) {
						// fuegt Infos ueber die Attribute der Tabelle in die
						// Datenbank ein
						for (String[] attr : tab_Attributes.get(tableName)) {
							int spalteId = ++id;
							String colAcc = String.valueOf(spalteId);
							String colName = attr[0];
							String colTyp = attr[1];

							// fuegt Attribut in die Datenbank ein
//							loadObject(sourceName, colAcc, colName, colTyp,
//									namespace, colTypspace, colKind, colComment);

							int current_id = 
								insertObject(source_id, colAcc, colName, colTyp, null, Element.KIND_ELEMENT, null, null);
							
							// Link zwischen Attribut und seine Tabelle in die
							// Datenbank ein
//							loadLink(sourceName, colAcc, sourceName, tableAcc,
//									SourceRelationship.REL_IS_A, null);
							
							insertLink(sourcerel_id, root_id, current_id);
						}
					}
				}
			}
		}
	}

	/**
	 * parse eine SQL-Datei
	 * 
	 * @param filename
	 * @param tab_Attributes
	 *            (HashMap fuer Speicherung von Tabllen und Ihren Attributen)
	 * @return
	 */
	private void sqlParse(String fileName) {
		tab_Attributes = new HashMap<String, Vector<String[]>>();
		// Vector zum Speichern von Attributnamen, Attributtypen
		Vector<String[]> attributes = new Vector<String[]>();
		String tableName = "";
		String attributName = "";

		if (readFile(fileName) == 0) {
			String line = "";
			// checkt, ob ein Textbereich aus Code-Zeilen fuer Erzeugung einer
			// Tabelle besteht
			boolean intable = false;
			try {
				while (true) {

					try {
						line = inBuffer.readLine();
					} catch (IOException e) {
						System.out.println("SQLParser.sqlParse() Error reading line " + e.getMessage());
					}
					// line = inBuffer.readLine();

					// checkt, ob Namen und Typen aller Attribute einer Tabelle
					// schon extrahiert wurden
					if (intable) {
						if (line.indexOf("(") != line.length() - 1) {
							int begin = 0;
							int end = 0;
							// leere Zeichen entfernen
							while (line.charAt(begin) == ' '
									|| line.charAt(begin) == '\t') {
								begin++;
							}
							if (line.charAt(begin) == '(') {
								begin++;
							}
							line = line.substring(begin);

							if (!line.startsWith("--")) {
								if (((line.startsWith("KEY ") || line
										.startsWith("key ")) || (line
										.contains(" KEY ") || line
										.contains(" key ")))
										|| (line.charAt(line.length() - 1) == ';' || line
												.contains("CONSTRAINT"))) {
									// ja, fuegt alle Attributwerten dieser
									// Tabelle in HashMap ein
									intable = false;
									tab_Attributes.get(tableName).addAll(
											attributes);
									attributes = new Vector<String[]>();
								} else {
									if ((!line.startsWith("CHECK ") && !line
											.startsWith("CHECK("))
											&& (!line.startsWith("check ") && !line
													.startsWith("check("))) {
										if (line.contains("`")) // intable =
																// true)
										{

											begin = line.indexOf("`") + 1;
											end = line.lastIndexOf("`");
											attributName = line.substring(
													begin, end);
											end += 2;
										} else {
											end = 0;
											String line_Temp = line;
											while (line_Temp.charAt(0) != ' '
													&& line_Temp.charAt(0) != '\t') {
												end++;
												line_Temp = line_Temp
														.substring(1);
											}
											// Name des Attributes
											attributName = line.substring(0,
													end);
										}
										begin = 0;
										// Typ des Attributes extrahiert
										String typ = null;
										line = line.substring(end);

										while (line.charAt(begin) == ' '
												|| line.charAt(begin) == '\t') {
											begin++;

										}

										line = line.substring(begin);
										end = 0;
										String line_Temp = line;
										while ((line_Temp.charAt(0) != ' ' && line_Temp
												.charAt(0) != '\t')
												&& (line_Temp.charAt(0) != ',' && line_Temp
														.charAt(0) != '(')
												&& (line_Temp.charAt(0) != ')' && line_Temp
														.length() > 1)) {
											end++;
											line_Temp = line_Temp.substring(1);
										}

										if ((line_Temp.charAt(0) != ' ' && line_Temp
												.charAt(0) != '\t')
												&& (line_Temp.charAt(0) != ',' && line_Temp
														.charAt(0) != '(')
												&& (line_Temp.charAt(0) != ')'))
											end++;
										typ = line.substring(0, end)
												.toUpperCase();

										// zur Speicherung vom Namen und Typ
										// eines Attributes
										String[] name_typ = new String[2];
										name_typ[0] = attributName;
										name_typ[1] = typ;
										// System.out.println("######################"
										// + attributName + "###" + typ+"####");
										attributes.add(name_typ);
									}
								}
							}
						}
					}

					String create_Table = "CREATE TABLE";
					// Tabellename extrahiert
					if (line.contains("CREATE TABLE")
							|| line.contains("create table")) {

						if (line.contains("create table"))
							create_Table = "create table";
						int begin = 0;
						int end = 0;
						if (line.contains("`")) {
							begin = line.indexOf("`") + 1;
							end = line.lastIndexOf("`");
							tableName = line.substring(begin, end);
						} else {
							line = line
									.substring(line.indexOf(create_Table) + 13);
							if (line.indexOf(' ') != -1)
								tableName = line
										.substring(0, line.indexOf(' '));
							else
								tableName = line.substring(0, line.length());
						}
						tab_Attributes.put(tableName, new Vector<String[]>());
						intable = true;
					}
				}
			} catch (NullPointerException e) {
			}
			try {
				inBuffer.close();
			} catch (IOException e) {
				System.out.println("SQLParser.sqlParse() Error closing buffer " + e.getMessage());
			}
		}
	}
	
	/**
	 * Test, ob die Datei existiert (wenn nein -> error = 1) Test, ob die Datei
	 * ist zu lesen (wenn nein -> error = 2) Test, ob die Dateityp SQL-Typ ist
	 * (wenn nein -> error = 3)
	 * 
	 * @param fileName
	 * @return Zustand des Fehlers als eine Zahl (kein Fehler -> error = 0)
	 */
	public static int readFile(String fileName) {
		int error = 0;
		if (fileName.substring(fileName.lastIndexOf(".")).equals(".sql")) {
			try {
				if (fileName.startsWith("http")){
					URL url = new URL(fileName);
			        URLConnection connection = url.openConnection();
			        inBuffer= new BufferedReader(
			        		new InputStreamReader(connection.getInputStream()));
				} else {
					Reader in = new FileReader(fileName);
					inBuffer = new BufferedReader(in);
				}
			} catch (FileNotFoundException e) {
				System.out.println("SQLParser.readFile) Error file not found " + e.getMessage());
				error = 1;
			} catch (MalformedURLException e) {
				System.err.println("SQLParser.readFile Error MalformedURLException " + e.getLocalizedMessage());
			} catch (IOException e) {
				System.err.println("SQLParser.readFile Error IOException " + e.getLocalizedMessage());
			}
		} else {
			error = 3;
		}
		return error;
	}
	

	/**
	 * 
	 * @return Ergebnis von Parsen einer Datei
	 */
	public static HashMap<String, Vector<String[]>> getResult() {
		return tab_Attributes;
	}

}
