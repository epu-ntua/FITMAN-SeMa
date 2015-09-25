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

package de.wdilab.coma.export;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * This class offers a function to export a string e.g. match result correspondences
 * to a file.
 * 
 * @author Sabine Massmann
 */
public class ExportUtil {

	
	public static boolean writeToFile(String fileName, String text, boolean append) {
		if (fileName == null){
			System.out.println("ExportUtil.writeToFile() Error no file name specified");
			return false;
		}		
	    PrintStream out=null;
	    FileOutputStream f;
		try {
			// create empty File
			f = new FileOutputStream(fileName, append);
			out = new PrintStream(f, true);
		    if (text!=null){
		    	out.println(text);
		    }
		    out.close();
		    f.close();
		} catch (FileNotFoundException e1) {
			  System.out.println("ExportUtil.writeToFile() Error opening output file :" + e1.getMessage());
			  return false;
		} catch (IOException e) {
			  System.out.println("ExportUtil.writeToFile() I/O Exception :" + e.getMessage());
			  return false;
		}
		return true;
	}
	
	
	
	
}
