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

package de.wdilab.coma.gui.dlg.editor;

import java.awt.Color;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

/**
 * This class creates a document with SQL syntax highlighting.
 * 
 * @author Sabine Massmann
 */

public class GrammarSyntaxDocument extends DefaultStyledDocument
{

  public GrammarSyntaxDocument()
  {
    root = this.getDefaultRootElement();

    scanner.setKeywords(KEYWORDS);

//    * keywords: SELECT, UPDATE, etc.
//    * single line comments: -- this is single line comment
//    * multi line comments: /* this is multi line comment */
//    * quoted identifiers: "this is quoted identifier"
//    * strings: 'this is a string' 
        
    // set colors to be used to display different types of information
    StyleConstants.setForeground(sas[Scanner.TOKEN_STRING] = new SimpleAttributeSet(),Color.lightGray);
    StyleConstants.setForeground(sas[Scanner.TOKEN_COMMENT_ML] = new SimpleAttributeSet(),Color.gray);
    StyleConstants.setForeground(sas[Scanner.TOKEN_COMMENT_SL] = new SimpleAttributeSet(),Color.gray);
    StyleConstants.setForeground(sas[Scanner.TOKEN_KEYWORD] = new SimpleAttributeSet(),Color.blue);
//    StyleConstants.setForeground(sas[Scanner.TOKEN_QUOTED_ID] = new SimpleAttributeSet(),Color.gray);
    StyleConstants.setForeground(sas[Scanner.TOKEN_NUMBER] = new SimpleAttributeSet(),Color.red);
    sas[Scanner.TOKEN_WHITESPACE] = new SimpleAttributeSet();
    
    int fontSize = 16;
    StyleConstants.setFontSize(sas[Scanner.TOKEN_STRING] ,fontSize);
    StyleConstants.setFontSize(sas[Scanner.TOKEN_COMMENT_ML] ,fontSize);
    StyleConstants.setFontSize(sas[Scanner.TOKEN_COMMENT_SL],fontSize);
    StyleConstants.setFontSize(sas[Scanner.TOKEN_KEYWORD],fontSize);
    StyleConstants.setFontSize(sas[Scanner.TOKEN_NUMBER],fontSize);
    StyleConstants.setFontSize(sas[Scanner.TOKEN_WHITESPACE],fontSize);
  }

  // TODO update keywords (at best automatically)
  public final String[] KEYWORDS =
  {
		  // RESOLUTION 1
		  "PATHS", "INNERPATHS", "LEAFPATHS", "NODES", "INNERNODES", "LEAFNODES", "UPPATHS", "DOWNPATHS",
		  // RESOLUTION 2
		  "SELFPATH", "SELFNODE", "PARENTS", "CHILDREN", "LEAVES", "SIBLINGS",
		  // RESOLUTION 3
		  "NAME", "NAMETOKEN", "DATATYPE", "COMMENT", "COMMENTTOKEN", "PATH", "PATHTOKEN", "SYNONYMS", "CONSTRAINTS", "STATISTICS", 
		  "INSTANCE_CONSTRAINTS", "INSTANCE_CONTENT",
		  // SIMMEASURES
		  "FEATVECT","DATATYPESIM","USERSYN","USERTAX","PREFIX","SUFFIX","AFFIX","CHARFREQ",
		  "DIGRAM","TRIGRAM","SOUNDEX","EDITDIST","SINGLEERR","NAME","SIM_EQUAL","SIM_TFIDF",
		  // COMBINATION
		  "MAX", "AVERAGE", "WEIGHTED", "MIN",
		  // SET COMBINATION
		  "SET_AVERAGE", "SET_DICE", "SET_MIN", "SET_MAX",   
		  // SELECTION
		  "MAXN", "MAXDELTA", "THRESHOLD", "MULTIPLE", "FORWARD", "BACKWARD","BOTH","SIMPLE",
  };

  private void Highlight(Scanner.Token token)
  {
    setCharacterAttributes(token.start,token.end-token.start+1,sas[token.id],true);
  }

  /**
   * Document root.
   */
  private Element root;

  /**
   * Tokens highlighting attributes.
   */
  private SimpleAttributeSet[] sas = new SimpleAttributeSet[Scanner.TOKEN_COUNT];

  /**
   * Scanner used to find out tokens.
   */
  private Scanner scanner = new Scanner();

  /**
   * For each and every eoln in the text this array contains the token being scanned
   * when the scanner lookaheads that eoln.
   */
  private ExpandingArray lineToks = new ExpandingArray(1);

  /**
   * Scans the text from scanBegin to scanEnd augmenting scanned portion of text if necessary.
   * @param scanBegin       must be index of the first character of the firstLine to scan
   * @param scanEnd         must be index of the last character of the last line to scan
   *                        (eoln or one character after end of the text)
   * @param highlightBegin  highlighting starts when index highlightBegin is reached (an optimalization).
   * @param firstLine       auxiliary information which can be dervied from scanBegin
   */
  private void HighlightAffectedText(int scanBegin,int scanEnd,int highlightBegin,int firstLine)
  {

    Scanner.Token token = null;                                     // scanned token
    Scanner.Token last_line_tok = null;                             // last line tok affected by scan cycle
    int last_line_idx = -1;                                         // the index of the line of last_line_tok
    boolean eot = false;
    int current_pos = 0;

    // sets document content and interval to be scanned:
    scanner.setDocument(this);
    scanner.setInterval(scanBegin,scanEnd);
    // loads state from line token associated to the end of line before first_line:
    if (firstLine>0 && lineToks.items[firstLine-1]!=null)
    {
      Scanner.Token t = (Scanner.Token) lineToks.items[firstLine-1];
      scanner.setState(t.id,t.start);
    }

    for(;;)
    {
      while (scanner.nextToken())
      {
        token = scanner.getToken();
        eot = scanner.eot();
        current_pos = scanner.getCurrentPos();

        // highlight token:
        if (current_pos>=highlightBegin) Highlight(token);

        // update line toks for every eoln contained by scanned token:
        if (token.isMultiline())
        {
          int fline = root.getElementIndex(token.start);
          int lline = root.getElementIndex(token.end)-1;

          // if the token is not terminated (we are at the end of the text):
          if (eot) lline++;

          lineToks.fill(fline,lline,new Scanner.Token(token.id,token.start,token.end));
        }

        // mark eoln by empty token:
        if (!eot && scanner.eoln())
        {
          last_line_idx = root.getElementIndex(token.end);
          last_line_tok = (Scanner.Token) lineToks.items[last_line_idx];
          lineToks.items[last_line_idx] = null;
        }
      }
      // assertion: scanner.lookahead=='\n' || scanner.lookahead=='\0'
      // scan cycle ends when it visits all the lines between its starting pos and ending pos,
      // eoln and end of token is reached.

      // The token associated with eoln of last scanned line was changed by previous
      // scan cycle. Former token was saved in last_line_tok.
      // We should continue scanning until this token ends.
      if (last_line_tok!=null)
      {
        // end of the text:
        if (eot) break;

        // Start pos: we can increment position because we are on \n (optimalization)
        // End pos: sets the end of scanning to the end of last line containing last_line_tok token:
        scanner.setInterval(
          current_pos+1,
          root.getElement(root.getElementIndex(last_line_tok.end)).getEndOffset()-1);
      } else
        break;
    }
  }

  /**
   * Overrides any text insertion in the document. Inserted text is highlighted.
   */
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException
  {
    Element line = root.getElement(root.getElementIndex(offset));   // line where insertion started
    int length = str.length();                                      // length of inserted text
    int former_line_count = root.getElementCount();                 // the number of lines before the insertion
    int scan_begin = line.getStartOffset();                         // start scanning at the boln of first affected line
    int scan_end = line.getEndOffset()+length-1;                    // end scanning at the eoln of last affected line (after insertion)

    // insert attribute-free text:
    super.insertString(offset,str,sas[Scanner.TOKEN_WHITESPACE]);

    int line_count = root.getElementCount();                        // the number of lines after the insertion
    int first_line = root.getElementIndex(scan_begin);              // the first affected line index
    int lines_inserted = line_count - former_line_count;            // the number of inserted eolns

    // one or more eolns were added:
    if (lines_inserted>0)
      lineToks.shift(first_line,lines_inserted);

    // highlight:
    HighlightAffectedText(scan_begin,scan_end,offset,first_line);
  }

  public void remove(int offset, int length) throws BadLocationException
  {
    int former_line_count = root.getElementCount();                 // the number of lines before the insertion

    // delete:
    super.remove(offset, length);

    Element line = root.getElement(root.getElementIndex(offset));   // line where deletion started
    int scan_begin = line.getStartOffset();                         // start scanning at the boln of first affected line
    int scan_end = line.getEndOffset()-1;                           // end scanning at the eoln of last affected line (after deleteion)
    int line_count = root.getElementCount();                        // the number of lines after the insertion
    int first_line = root.getElementIndex(scan_begin);              // the first affected line index
    int lines_deleted = former_line_count - line_count;             // the number of inserted eolns

    // one or more eolns were deleted:
    if (lines_deleted>0)
      lineToks.unshift(first_line,lines_deleted);

    // highlight:
    HighlightAffectedText(scan_begin,scan_end,offset,first_line);
  }

  public static void main(String[] args) {
	  JEditorPane pane = new JEditorPane();
//	    pane.setFont(new java.awt.Font("Monospaced", 0, 14));
	    pane.setDoubleBuffered(true);
	    pane.setSelectionColor(Color.blue);
	    pane.setSelectedTextColor(Color.white);
	    
	    EditorKit editorKit = new StyledEditorKit()
	    {
	      public Document createDefaultDocument()
	      {
	        return new GrammarSyntaxDocument();
	      }
	    };

	    pane.setEditorKitForContentType("text/sql", editorKit);
	    pane.setContentType("text/sql");

	    JFrame frame = new JFrame();
	    
	    JScrollPane jScrollPane1 = new JScrollPane();
	    jScrollPane1.getViewport().add(pane, null);
	    frame.getContentPane().add(jScrollPane1);
	    frame.pack();
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}
  
}

/**
 * Simple expanding array. The array is expandable by amortized doubling
 * with number of valid items stored in count field with minimum MIN_SIZE.
 */
class ExpandingArray
{
  /**
   * Minimal size of the array.
   */
  public final int MIN_SIZE = 20;

  /**
   * Items of the array.
   */
  public Object[] items;

  /**
   * The number of valid items in the array.
   */
  public int count = 0;

  public ExpandingArray(int initCount)
  {
    items = new Object[Math.max(initCount,MIN_SIZE)];
    count = initCount;
  }

  /**
   * Fills the portion [start;end] of the array by item.
   */
  public void fill(int start,int end,Object item)
  {
     for (int i = start;i<=end;i++)
       items[i] = item;
  }

  /**
   * Shifts a part of the array starting from shiftStart by shiftLength
   * elements to the right. Expands the array if necessary.
   * Inserts null elements to empty positions.
   * 
   * @param shiftStart    the first element index to be shifted
   * @param shiftLength   the number of elements to insert before the first shifted one
   */
  public void shift(int shiftStart,int shiftLength)
  {
    int new_count = count + shiftLength;
    Object[] new_items;

    // expands the array if necessary:
    if (new_count>items.length)
      new_items = new Object[new_count << 1]; else
      new_items = items;

    // elements preceding inserted ones:
    if (new_items!=items)
      System.arraycopy(items,0,new_items,0,shiftStart);

    // elements following inserted ones:
    System.arraycopy(items,shiftStart,new_items,shiftStart+shiftLength,count-shiftStart);

    if (new_items==items && count>shiftStart)
      fill(shiftStart,shiftStart+shiftLength-1,null);

    items = new_items;
    count = new_count;
  }

  /**
   * Removes shiftLength elements starting from shiftStart one shifting following
   * elements to the left. If the array is to long (its valid elements occupies
   * its fourth or less) than it is shortened to one half.
   * 
   * @param shiftStart    the first element index to be shifted
   * @param shiftLength   the number of elements to insert before the first shifted one
   */
  public void unshift(int shiftStart,int shiftLength)
  {
    int new_count = count - shiftLength;
    Object[] new_items = items;

    // shrinks the array if possible:
    if (new_count<items.length >> 2 && items.length >> 1 > MIN_SIZE)
      new_items = new Object[items.length >> 1]; else
      new_items = items;

    // elements preceding deleted ones:
    if (new_items!=items)
      System.arraycopy(items,0,new_items,0,shiftStart);

    // elements following deleted ones:
    System.arraycopy(items,shiftStart+shiftLength,new_items,shiftStart,new_count-shiftStart);

    items = new_items;
    count = new_count;
  }

}