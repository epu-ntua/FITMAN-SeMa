package de.wdilab.coma.matching;

import de.wdilab.ml.impl.MappingEntry;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleOneSourceAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

import java.security.PublicKey;

/**
 * Created with IntelliJ IDEA.
 * User: arnold
 * Date: 13.08.12
 * Time: 11:06
 */
public class SampleMatcher extends AbstractSimpleOneSourceAttributeObjectMatcher implements IAttributeObjectMatcher {


    public SampleMatcher( final String attr1, final String attr2, final float threshold) {
        super(attr1, attr2, threshold);
    }


    @Override
    public void match(IObjectInstanceProvider oip1, IObjectInstanceProvider oip2,
                      IMappingStore mrs) throws MappingStoreException {

        for( IObjectInstance oiLeft : oip1) {
            for( IObjectInstance oiRight : oip2) {

                float similarity = calculateSimilarity( oiLeft, oiRight);

                if( similarity < threshold)  {
                    continue;
                }

                else if( similarity  <= 1) {
                    mrs.add(new MappingEntry(oiLeft, oiRight, new
                            Similarity( similarity)));
                }

                else {
                    mrs.add( new MappingEntry( oiLeft, oiRight, new Similarity(0)));
                }

            }
        }

    }


    /**
     * Calculates the similarity (sample method)
     * @param left The left element
     * @param right The right element
     * @return The similarity.
     */
    private float calculateSimilarity( IObjectInstance left, IObjectInstance right) {

        String valueLeft = left.getValue( attrLinks).toString();
        String valueRight = right.getValue( attrRechts).toString();

        System.out.println( valueLeft + " <-> " + valueRight);

        if( valueLeft.equals( valueRight)) {
            return 1;
        } else {
            return 0;
        }

    }



    @Override
    public void match(IMappingProvider iMappingEntries, IMappingStore iMappingStore) throws MappingStoreException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}