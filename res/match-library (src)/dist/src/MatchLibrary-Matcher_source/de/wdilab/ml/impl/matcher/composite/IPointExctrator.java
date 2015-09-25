/**
 * 
 */
package de.wdilab.ml.impl.matcher.composite;

import java.awt.geom.Point2D;

import de.wdilab.ml.interfaces.oi.IObjectInstance;

/**
 * @author Nico Heller
 */
public interface IPointExctrator
{
  /**
   * @return 2Dpoint
   */
  Point2D getPoint( IObjectInstance oi);
}
