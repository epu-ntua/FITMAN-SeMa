/**
 * Match Datatypes using a predefined similarity matrix
 * (based on DataTypeSimilarity from COMA++ 2008)
 * 
 * @author do, massmann
 */
package de.wdilab.ml.impl.matcher.simple.meta;

import org.apache.log4j.Logger;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

public class DatatypeMatcher extends AbstractSimpleAttributeObjectMatcher implements
		IAttributeObjectMatcher {

	protected static final Logger log = Logger.getLogger(DatatypeMatcher.class);

	  //generic data types
	  protected static final int GEN_ID          = 0;
	  protected static final int GEN_BOOLEAN     = 1;
	  protected static final int GEN_CHARACTER   = 2;
	  protected static final int GEN_CHAR_FIXED  = 3;
	  protected static final int GEN_CHAR_VAR    = 4;
	  protected static final int GEN_NUMBER      = 5;
	  protected static final int GEN_NUM_INT     = 6;
	  protected static final int GEN_NUM_FLOAT   = 7;
	  protected static final int GEN_NUM_DEC     = 8;
	  protected static final int GEN_TIME        = 9;
	  protected static final int GEN_TIM_DATE    = 10;
	  protected static final int GEN_TIM_TIME    = 11;
	  protected static final int GEN_ENUMERATION = 12;
	  protected static final int GEN_BINARY      = 13;
	  protected static final int GEN_OTHER       = 14;

	  //compatiblity matrix of generic data types
	  public static final double[][] compatibility = {
	      /* ID   BOOL CHAR _FIX _VAR NUM  _INT _FL  _DEC TIME _DAT _TIM  ENUM BIN OTHER*/
	      /* ID   */ {
	      1.0}
	      ,
	      /* BOOL */{
	      0.0, 1.0}
	      ,
	      /* CHAR */{
	      0.6, 0.4, 1.0}
	      ,
	      /* _FIX */{
	      0.6, 0.4, 0.8, 1.0}
	      ,
	      /* _VAR */{
	      0.6, 0.4, 0.8, 0.8, 1.0}
	      ,
	      /* NUM  */{
	      0.8, 0.0, 0.6, 0.6, 0.6, 1.0}
	      ,
	      /* _INT */{
	      0.8, 0.6, 0.6, 0.6, 0.6, 0.8, 1.0}
	      ,
	      /* _FL  */{
	      0.0, 0.0, 0.6, 0.6, 0.6, 0.8, 0.6, 1.0}
	      ,
	      /* _DEC */{
	      0.0, 0.0, 0.4, 0.4, 0.4, 0.8, 0.6, 0.8, 1.0}
	      ,
	      /* TIME */{
	      0.0, 0.0, 0.4, 0.4, 0.4, 0.0, 0.0, 0.0, 0.0, 1.0}
	      ,
	      /* _DAT */{
	      0.0, 0.0, 0.4, 0.4, 0.4, 0.0, 0.0, 0.0, 0.0, 0.8, 1.0}
	      ,
	      /* _TIM */{
	      0.0, 0.0, 0.4, 0.4, 0.4, 0.0, 0.0, 0.0, 0.0, 0.8, 0.8, 1.0}
	      ,
	      /* ENUM */{
	      0.0, 0.0, 0.6, 0.4, 0.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0}
	      ,
	      /* BIN  */{
	      0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0}
	      ,
	      /* OTHER*/{
	      0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
	  };

	  // function to map local data types to generic data types
	  public static int mapLocalType(String dataType) {
	    if (dataType != null) {
	      dataType = dataType.toLowerCase();
	      //generic names
	      if (dataType.equals("boolean")) return GEN_BOOLEAN;
	      if (dataType.equals("char") || dataType.equals("character")) return GEN_CHAR_FIXED;
	      if (dataType.equals("string")) return GEN_CHAR_VAR;
	      if (dataType.equals("number")) return GEN_NUMBER;
	      if (dataType.equals("int") || dataType.equals("integer"))     return GEN_NUM_INT;
	      if (dataType.equals("float")) return GEN_NUM_FLOAT;
	      if (dataType.equals("double")) return GEN_NUM_FLOAT;
	      if (dataType.equals("date")) return GEN_TIM_DATE;
	      if (dataType.equals("time")) return GEN_TIM_TIME;
	      if (dataType.equals("datetime")) return GEN_TIME;
	      if (dataType.equals("real")) return GEN_NUM_FLOAT;

	      //some SQL/JDBC-specific type names
	      if (dataType.equals("bit")) return GEN_BOOLEAN;
	      if (dataType.equals("distinct")) return GEN_ID;
	      if (dataType.equals("bigint")) return GEN_NUM_INT;
	      if (dataType.equals("smallint")) return GEN_NUM_INT;
	      if (dataType.equals("tinyint")) return GEN_NUM_INT;
	      if (dataType.equals("decimal")) return GEN_NUM_DEC;
	      if (dataType.equals("numeric")) return GEN_NUM_DEC;
	      if (dataType.equals("timestamp")) return GEN_TIME;
	      if (dataType.equals("varchar")) return GEN_CHAR_VAR;
	      if (dataType.equals("longvarchar")) return GEN_CHAR_VAR;
	      if (dataType.equals("binary")) return GEN_BINARY;
	      if (dataType.equals("varbinary")) return GEN_BINARY;
	      if (dataType.equals("longvarbinary")) return GEN_BINARY;
	      if (dataType.equals("clob")) return GEN_CHAR_VAR;
	      if (dataType.equals("blob")) return GEN_BINARY;

	      //some XML-specific type names
	      if (dataType.equals("id")) return GEN_ID;
	      if (dataType.equals("idref")) return GEN_ID;
	      if (dataType.equals("idrefs")) return GEN_ENUMERATION;
	      if (dataType.equals("nmtoken")) return GEN_CHARACTER;
	      if (dataType.equals("nmtokens")) return GEN_ENUMERATION;
	      if (dataType.equals("enumeration")) return GEN_ENUMERATION;
	      if (dataType.equals("cdata")) return GEN_CHAR_VAR;
	      if (dataType.equals("i1") || dataType.equals("ui1")) return GEN_NUM_INT;
	      if (dataType.equals("i2") || dataType.equals("ui2")) return GEN_NUM_INT;
	      if (dataType.equals("i4") || dataType.equals("ui4")) return GEN_NUM_INT;
	      if (dataType.equals("i8") || dataType.equals("ui8")) return GEN_NUM_INT;
	      if (dataType.equals("r4")) return GEN_NUM_FLOAT;
	      if (dataType.equals("r8")) return GEN_NUM_FLOAT;
	      if (dataType.equals("uri")) return GEN_ID;
	      if (dataType.equals("anyuri")) return GEN_ID;
	      if (dataType.equals("normalizedstring")) return GEN_CHAR_VAR;
	      if (dataType.startsWith("bin")) return GEN_BINARY;
	      if (dataType.startsWith("fixed")) return GEN_NUM_DEC;
	      if (dataType.startsWith("datetime")) return GEN_TIME;
	      if (dataType.startsWith("time")) return GEN_TIM_TIME;

	      //Microsoft SQL Server specific type names
	      if (dataType.equals("nvarchar")) return GEN_CHAR_VAR;
	      if (dataType.equals("nchar")) return GEN_CHAR_FIXED;
	      if (dataType.equals("ntext")) return GEN_CHAR_VAR;
	      if (dataType.equals("money")) return GEN_NUM_DEC;
	      if (dataType.equals("image")) return GEN_BINARY;
	      if (dataType.endsWith("identity")) return GEN_NUM_INT;

	      //Oracle specific type names
	      if (dataType.equals("varchar2")) return GEN_CHAR_VAR;
	    }
	    return GEN_OTHER;
	  }

	  public static float computeDataTypeSimilarity(String dataType1,
	                                                String dataType2) {
	    if (dataType1 == null || dataType2 == null) return 0;
	    float typesim = 0;
	    int genTypeId1 = mapLocalType(dataType1);
	    int genTypeId2 = mapLocalType(dataType2);
	    //if (genTypeId1 == GEN_OTHER)
	    //  System.out.println("DataTypeSimilarity.computeDataTypeSimilarity(): Type [" + dataType1 + "] not identified");
	    //if (genTypeId2 == GEN_OTHER)
	    //  System.out.println("DataTypeSimilarity.computeDataTypeSimilarity(): Type [" + dataType2 + "] not identified");

	    if (genTypeId1 > genTypeId2)
	      typesim = (float) compatibility[genTypeId1][genTypeId2];
	    else
	      typesim = (float) compatibility[genTypeId2][genTypeId1];
	    return typesim;
	  }
	
	/**
	 * @param attr1
	 * @param attr2
	 */
	public DatatypeMatcher(final String attr1, final String attr2) {
		super(attr1, attr2, 0f);
	}

	/**
	 * @param attr
	 */
	public DatatypeMatcher(final String attr) {
		super(attr, 0f);
	}

	/**
   * 
   */
	public DatatypeMatcher() {
		super();
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public DatatypeMatcher(final String attr1, final String attr2,
			final float threshold) {
		super(attr1, attr2, threshold);
	}

	/**
	 * @param attr
	 * @param threshold
	 */
	public DatatypeMatcher(final String attr, final float threshold) {
		super(attr, threshold);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .oi. IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(final IObjectInstanceProvider oip1,
			final IObjectInstanceProvider oip2, final IMappingStore mrs)
			throws MappingStoreException {

		log.info("Match Start.");

		for (final IObjectInstance oiLeft : oip1) {
			String valueL = oiLeft.getStringValue(attrLinks);
			for (final IObjectInstance oiRight : oip2) {
				String valueR = oiRight.getStringValue(attrRechts);
//				double sim = 0; 
//				if (valueL==null || valueR==null){ sim=0;
//				} else if (valueL.equals(valueR)){	sim=1;
//				}
				float sim = computeDataTypeSimilarity(valueL, valueR);
				mrs.add(oiLeft, oiRight, new Similarity(sim));
			}
		}
	}

	@Override
	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void match(final IMappingProvider mp, final IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
