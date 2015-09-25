/**
 * 
 */
package de.wdilab.ml.impl.matcher.neighbourhood;

import de.wdilab.ml.impl.mapping.mainmemory.MainMemoryMapping;
import de.wdilab.ml.impl.matcher.compose.Composer;
import de.wdilab.ml.impl.matcher.compose.pathfunction.AverageSimPathFunction;
import de.wdilab.ml.impl.matcher.compose.pathfunction.RelativeBothSimPathFunction;
import de.wdilab.ml.impl.matcher.compose.simfunction.MinimumSimFunction;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.ISimFunction;

/**
 * @author Nico Heller
 */
public class NHMatcher
{
  /**
   * @param mrs
   * @param asso1
   * @param same
   * @param asso2
   * @throws MappingStoreException
   */
  public void match( final IMappingStore mrs, final IMappingProvider asso1, final IMappingProvider same,
    final IMappingProvider asso2) throws MappingStoreException
  {
    Composer composer = new Composer( asso1, same);

    final MainMemoryMapping temp = new MainMemoryMapping();
    final ISimFunction min = new MinimumSimFunction();

    composer.compose( temp, min, new AverageSimPathFunction());

    composer = new Composer( temp, asso2);
    composer.compose( mrs, min, new RelativeBothSimPathFunction());
  }
}
