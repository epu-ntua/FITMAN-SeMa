/**
 * 
 */
package de.wdilab.ml.impl.matcher.composite;

import java.awt.geom.Point2D;

import de.wdilab.ml.interfaces.oi.IObjectInstance;

/**
 * Simple Point2D Extractor. Assumes that the attribute value is already in Point2D ...
 * 
 * @author Nico Heller
 */
public class Point2DToPoint2DPointExtractor implements IPointExctrator
{
  final String attrName;

  public Point2DToPoint2DPointExtractor( final String attrName)
  {
    this.attrName = attrName;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.impl.matcher.composite.IPointExctrator#getPoint(de.wdilab.ml.interfaces.oi.
   * IObjectInstance)
   */
  @Override
  public Point2D getPoint( final IObjectInstance oi)
  {
    return (Point2D) oi.getValue( attrName);
  }

}
