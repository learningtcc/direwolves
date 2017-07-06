package com.edgar.direwolves.servicediscovery;

import com.edgar.servicediscovery.ProviderStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

/**
 * Created by edgar on 17-5-6.
 */
public class RoundRobinStrategyTest extends StrategyTest {

  @Test
  public void testRandom() {
    com.edgar.servicediscovery.ProviderStrategy providerStrategy = ProviderStrategy.roundRobin();
    List<String> selected = select3000(providerStrategy);
    Assert.assertEquals(3, new HashSet<>(selected).size());
    long aSize = selected.stream()
            .filter(i -> "a".equals(i))
            .count();
    long bSize = selected.stream()
            .filter(i -> "b".equals(i))
            .count();
    long cSize = selected.stream()
            .filter(i -> "c".equals(i))
            .count();
    Assert.assertEquals(aSize, 1000);
    Assert.assertEquals(bSize, 1000);
    Assert.assertEquals(cSize, 1000);
  }

}
