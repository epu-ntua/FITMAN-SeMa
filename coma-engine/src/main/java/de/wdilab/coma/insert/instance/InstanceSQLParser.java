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

package de.wdilab.coma.insert.instance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;

/**
 * Import instances of a sql file. Assumption is that the sql file
 * used to import the metadata also contains the instance data. 
 * 
 * @author Viet Hung Do, Sabine Massmann
 * 
 */
public class InstanceSQLParser {
	public DataImport importer = null;
	private static BufferedReader inBuffer = null;
	private static HashMap<String, Vector<String>> table_Instances = null;

	/**
	 * Konstruktor, setzt DataImport
	 * 
	 * @param importer
	 */
	public InstanceSQLParser(DataImport importer) {
		this.importer = importer;

	}

	/**
	 * wiederholen die Methode "parseInstancesForTable" zum Einf�gen von
	 * extrahierten Daten (Instanzdaten aller Tabllen) in Tabelle "instances_X"
	 * 
	 * @param schemaGraph
	 * @return SchemaGraph
	 */
	public Graph parseInstances(Graph schemaGraph) {

		int id = schemaGraph.getSource().getId();
		// importer.createInstancesTable(id);
		importer.prepareInstancesStatement(id);

		String fileName = schemaGraph.getSource().getProvider();

		/*
		 * MM if (filename.startsWith(SourceParser.SOURCE_DIR)){ // file is in a
		 * path beginning with current location thus "." }
		 */
		// alle Tabellenamen
		ArrayList<Element> inners = schemaGraph.getInners();
		// Attribute einer Tabelle
		ArrayList<Element> children = null;

		// parse die SQL-Datei und speichern die Ergenisse der Methode
		// sqlInstanceParse(fileName) in einem HashMap
		sqlInstanceParse(fileName);
		if (inners != null) {
			for (int i = 0; i < inners.size(); i++) {
				Element inner = inners.get(i); // Tabellenname
				children = schemaGraph.getChildren(inner); // Attribute
				if (children.isEmpty())
					continue;

				// parse Instanzen für eine Tabelle und finden related elements
				parseInstancesForTable(fileName, inner, children);
			}
		}
		// schemaGraph.print();
		return schemaGraph;
	}

	/**
	 * speichern die extrahierten Daten (Instanzdaten von einer bestimmten
	 * Tablle) in der Tabelle "instances_X"
	 * 
	 * @param table
	 * @param attributes
	 * @param table_Instances
	 */
	private void parseInstancesForTable(String fileName, Element table,
			ArrayList<Element> attributes) {

		String tableName = table.getName();

		Vector<String> instances = new Vector<String>();
		// checkt, ob table_Instances nicht gleich null ist
		if (table_Instances != null) {

			// Vector enthält alle Instanzdaten der Tablle
			instances = table_Instances.get(tableName);

			int connect = 1;
			String connectString = "";

			// checkt, ob der Vector nicht leer ist
			if (instances != null) {
				Iterator iter = instances.iterator();

				while (iter.hasNext()
						&& connect < Repository.INSTANCES_MAX_PER_ELEMENT) {
					connectString = String.valueOf(connect);

					for (int j = 0; j < attributes.size(); j++) {
						if (iter.hasNext()) {
							Element e = attributes.get(j);
							String value = (String) iter.next();
							if (value != null) {

								e.addInstance(value);
								int id = -1;
								String attributeName = e.getName();

								// f�gen daten in die Datenbank ein
								id = importer.insertInstance(connectString,
										e.getId(), id, attributeName, value);
								importer.updateInstance(id, id);
							}
						}
					}
					connect++;
				}
			}
		}

	}

	/**
	 * parse eine SQL-Datei und speichern die extrahierten Daten (Tabellennamen
	 * und ihre Instanzdaten) in einem HashMap
	 * 
	 * @param filename
	 * @return ein HashMap zwischen Tabelle und ihrer Attributwerte
	 */
	public static void sqlInstanceParse(String fileName) {

		table_Instances = new HashMap<String, Vector<String>>();

		if (readFile(fileName) == 0) {
			// Variable zum Speichern der aktuellen Zeile der Datei
			String line = "";
			StringBuilder sb = new StringBuilder();
			boolean is_insert_Into = false;
			boolean is_Values = true;
			String insert_Into = "INSERT INTO";
			String values = "VALUES";
			String tableName = "";
			try {
				while (true && line != null) {

					try {
						// aktuelle Zeile
						line = inBuffer.readLine();

						// checkt, ob Zeile "insert into" oder "INSERT INTO"
						// enth�lt
						if (line.contains("INSERT INTO")
								|| line.contains("insert into")) {
							if (line.contains("insert into")) {
								insert_Into = "insert into";
							}
							is_insert_Into = true;
							is_Values = true;
							if (line.contains("VALUES")
									|| line.contains("values")) {
								is_Values = false;
								if (line.contains("VALUES"))
									values = "VALUES";
								else
									values = "values";
							}
							sb.append(line);

							// parse Tabellenname
							tableName = parseTableName(sb.toString(), values,
									insert_Into);

							if (line.lastIndexOf(");") == line.length() - 2
									|| line.lastIndexOf(") ;") == line.length() - 3) {

								String daten = sb.toString();
								daten = daten
										.substring(daten.indexOf(values) + 6);

								// parse Daten
								parseDaten(table_Instances, daten, tableName);
								sb.setLength(0);
								is_insert_Into = false;
							}
						}
						// Zeile enth�lt weder "INSERT INTO" noch "insert into"
						else {
							// Zeile enth�lt entweder "VALUES" oder "values"
							if (is_Values & line.contains("VALUES")
									|| line.contains("values")) {
								is_Values = false;
								if (line.contains("VALUES"))
									values = "VALUES";
								else
									values = "values";

							}
							if (is_insert_Into) {
								sb.append(line);
								if ((line.lastIndexOf(");") == line.length() - 2 || line
										.lastIndexOf(") ;") == line.length() - 3)
										|| line.lastIndexOf(")\t;") == line
												.length() - 3) {

									String daten = sb.toString();

									daten = daten.substring(daten
											.indexOf(values) + 6);

									parseDaten(table_Instances, daten,
											tableName);
									sb.setLength(0);
									is_insert_Into = false;
								}
							}
						}
					} catch (IOException e) {
						System.out.println("InstanceSQLParser.sqlInstanceParse(): Error " + e.getMessage());
					}
				}
			} catch (NullPointerException e) {
				// e.printStackTrace();
			}
		}
	}

	/**
	 * Extrahieren eines Tabellennamen aus einer Zeile der SQL-Datei
	 * 
	 * @param line
	 * @param values
	 * @param insert_Into
	 * @return Tabellenname
	 */
	private static String parseTableName(String line, String values,
			String insert_Into) {

		int begin, end;
		String tableName = "";
		// checkt, ob die Zeile weder "VALUES" noch "values" enth�lt
		if (!line.contains("VALUES") && !line.contains("values")) {
			if (line.indexOf("(") == -1) {
				if (line.substring(0, line.length()).contains("`")) {
					begin = line.indexOf("`") + 1;
					line = line.substring(begin);
					end = line.indexOf("`");
					tableName = line.substring(0, end);
				} else {
					begin = line.indexOf(insert_Into) + 12;
					end = line.length();
					tableName = line.substring(begin, end);
					tableName = tableName.replace(" ", "");
				}
			} else {
				if (line.substring(0, line.indexOf("(")).contains("`")) {
					begin = line.indexOf("`") + 1;
					line = line.substring(begin);
					end = line.indexOf("`");
					tableName = line.substring(0, end);
				} else {
					begin = line.indexOf(insert_Into) + 12;
					end = line.indexOf("(");
					tableName = line.substring(begin, end);
					tableName = tableName.replace(" ", "");
				}
			}
		}

		// Zeile enth�lt entweder "VALUES" oder "values"
		else {
			if (line.indexOf(values) < line.indexOf("(")
					|| line.indexOf("(") == -1) {

				if (line.substring(0, line.indexOf(values)).contains("`")) {
					begin = line.indexOf("`") + 1;
					line = line.substring(begin);
					end = line.indexOf("`");
					tableName = line.substring(0, end);

				} else {
					begin = line.indexOf(insert_Into) + 12;
					end = line.indexOf(values);
					tableName = line.substring(begin, end);
					tableName = tableName.replace(" ", "");
				}
			} else {
				if (line.substring(0, line.indexOf("(")).contains("`")) {
					begin = line.indexOf("`") + 1;
					line = line.substring(begin);
					end = line.indexOf("`");
					tableName = line.substring(0, end);
				} else {
					begin = line.indexOf(insert_Into) + 12;
					end = line.indexOf("(");
					tableName = line.substring(begin, end);
					tableName = tableName.replace(" ", "");
				}
			}
		}
		return tableName;

	}

	/**
	 * Extrahieren von Instanzdaten einer Tabelle aus einer Zeile der SQL-Datei
	 * 
	 * @param table_Instances
	 * @param line
	 * @param tableName
	 */
	private static void parseDaten(
			HashMap<String, Vector<String>> table_Instances, String line,
			String tableName) {
		// variable zum Checken, ob die Zeile Instanzdaten enth�lt
		boolean insert_Data = true;
		if (table_Instances.get(tableName) == null) {
			Vector<String> instances = new Vector<String>();
			table_Instances.put(tableName, instances);
		}
		while (insert_Data) {

			String attribute_Value = "";
			int begin = 0;
			int end1 = 0;
			int end2 = 0;
			int end = 0;
			// leere Zeichen entfernen

			while (line.indexOf("\t") == 0) {
				line = line.substring(line.indexOf("\t") + 1);
			}

			while (line.indexOf(" ") == 0 || line.indexOf("(") == 0) {
				line = line.substring(1);
			}

			// ---------------------------Attributwert als Zeichenkette (in
			// Klammern ' ') ---------------//
			if (line.indexOf("'") == 0)// || line.indexOf("'") == 1)
			{
				boolean exit = false;
				if (line.indexOf("''") != 0) {
					begin = line.indexOf("'");
					line = line.substring(begin + 1);
					String temp = line;
					temp.replace("\'", "\\'");

					int anzahl = 0;
					boolean is_Slash = false;
					while (!exit) {

						exit = false;

						while (temp.indexOf("\\'") == temp.indexOf("'") - 1) {
							is_Slash = true;
							temp = temp.substring(temp.indexOf("'") + 1);
							anzahl++;
						}
						temp = temp.substring(temp.indexOf("'") + 1);
						anzahl++;

						// leere Zeichen entfernen
						while (line.indexOf("\t") == 0) {
							line = line.substring(line.indexOf("\t") + 1);
						}
						while (temp.indexOf(' ') == 0) {
							temp = temp.substring(1);

						}
						if (temp.indexOf(",") == 0 || temp.indexOf(")") == 0) {
							if (temp.indexOf(",") == 0)
								exit = true;
							else {
								// checkt, ob es noch einen Attributwert in der
								// aktuellen Zeile gibt
								temp = temp.substring(1);

								// leere Zeichen entfernen
								while (line.indexOf("\t") == 0) {
									line = line
											.substring(line.indexOf("\t") + 1);
								}
								while (temp.indexOf(' ') == 0) {
									temp = temp.substring(1);
								}
								if (temp.indexOf(";") == 0
										|| temp.indexOf(",") == 0) {
									exit = true;

								}
								if (temp.indexOf(";") == 0) {
									insert_Data = false;
								}
							}
						}
					}

					String temp2 = line;

					int index = 0;
					for (int i = 0; i < anzahl; i++) {

						index += temp2.indexOf("'");
						if (i >= 1) {
							index++;
						}
						temp2 = temp2.substring(temp2.indexOf("'") + 1);

					}
					if (is_Slash)
						index++;
					end = index;

					attribute_Value = line.substring(0, end);

					// checkt, ob Attributwert kein leeres Zeichen ist
					if (!attribute_Value.equals("")) {
						table_Instances.get(tableName).add(attribute_Value);
					}
					// Attributwert ist leeres Zeichen
					else
						table_Instances.get(tableName).add(null);

					line = line.substring(end + 1);
					end = line.indexOf(",");

				} else {
					table_Instances.get(tableName).add(null);

					// leere Zeichen entfernen
					line = line.substring(2);
					while (line.indexOf("\t") == 0) {
						line = line.substring(line.indexOf("\t") + 1);

					}
					while (line.indexOf(" ") == 0 || line.indexOf("(") == 0) {
						line = line.substring(1);

					}
					if (line.startsWith(");"))
						insert_Data = false;
					else
						end = line.indexOf(",");
				}

			}

			// ------------------Attributwert ist keine Zeichenkette (nich in
			// Klammern ' ') -----------------------//
			else {

				end1 = line.indexOf(",");
				end2 = line.indexOf(";");

				if (end2 < end1 && end2 != -1) {
					end = end2;
					insert_Data = false;
				} else {
					if (end1 == -1) {
						end = end2;
						insert_Data = false;
					} else {
						end = end1;
					}
				}

				if (line.substring(0, end).contains(")")) {
					if (line.indexOf("(") == 0) {

						attribute_Value = line.substring(1, line.indexOf(")"));

					} else
						attribute_Value = line.substring(0, line.indexOf(")"));
					if (end == end2)
						insert_Data = false;

					// leere Zeichen entfernen
					attribute_Value = attribute_Value.replace(" ", "");
					attribute_Value = attribute_Value.replace("\t", "");

				}

				else {
					if (line.indexOf("(") == 0) {
						attribute_Value = line.substring(1, end);
					} else
						attribute_Value = line.substring(0, end);
					// leere Zeichen entfernen
					attribute_Value = attribute_Value.replace(" ", "");
					attribute_Value = attribute_Value.replace("\t", "");
				}
				if (!attribute_Value.equals("")
						&& !attribute_Value.toUpperCase().equals("NULL")) {
					table_Instances.get(tableName).add(attribute_Value);

				} else {
					table_Instances.get(tableName).add(null);
				}
			}

			if (insert_Data)
				line = line.substring(end + 1);
		}

	}

	/**
	 * Main-Methode
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

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
				Reader in = new FileReader(fileName);
				inBuffer = new BufferedReader(in);
			} catch (FileNotFoundException e) {
				System.out.println("InstanceSQLParser.readFile(): Error " + e.getMessage());
				error = 1;
//			} catch (IOException e1) {
//				System.out.println("InstanceSQLParser.readFile(): Error " + e.getMessage());
//				e1.printStackTrace();
//				error = 2;
			}
		} else
			error = 3;
		return error;
	}

	/**
	 * 
	 * @return Ergebnis von Parsen einer Datei
	 */
	public HashMap<String, Vector<String>> getResults() {
		return table_Instances;
	}

	/**
	 * Setzt table_Instances
	 * 
	 * @param result
	 */
	public void setResults(HashMap<String, Vector<String>> result) {
		table_Instances = result;
	}

}
