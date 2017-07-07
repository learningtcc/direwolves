//package com.edgar.servicediscovery;
//
//import com.edgar.servicediscovery.verticle.ServiceDiscoveryVerticle;
//import io.vertx.core.Vertx;
//import io.vertx.core.json.JsonObject;
//import io.vertx.ext.unit.TestContext;
//import io.vertx.ext.unit.junit.VertxUnitRunner;
//import io.vertx.servicediscovery.Record;
//import io.vertx.servicediscovery.ServiceDiscovery;
//import io.vertx.servicediscovery.types.HttpEndpoint;
//import org.awaitility.Awaitility;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Created by Edgar on 2017/5/10.
// *
// * @author Edgar  Date 2017/5/10
// */
//@RunWith(VertxUnitRunner.class)
//public class ServiceDowngradeTest {
//
//  private Vertx vertx;
//
//  private MoreServiceDiscovery moreServiceDiscovery;
//
//  private ServiceDiscovery discovery;
//
//  @Before
//  public void setUp(TestContext testContext) {
//    vertx = Vertx.vertx();
//    discovery = ServiceDiscovery.create(vertx);
//    AtomicBoolean complete = new AtomicBoolean();
//    vertx.deployVerticle(ServiceDiscoveryVerticle.class.getName(), ar -> {
//      complete.set(true);
//    });
//    Awaitility.await().until(() -> complete.get());
//  }
//
//  @Test
//  public void testDefault() {
//    moreServiceDiscovery = MoreServiceDiscovery.create(vertx, new MoreServiceDiscoveryOptions());
//
//    AtomicInteger seq = new AtomicInteger();
//    addService(seq);
//
//    Awaitility.await().until(() -> seq.get() == 3);
//
//    AtomicInteger selectSeq = new AtomicInteger();
//    List<Integer> selected = new CopyOnWriteArrayList<>();
//    List<String> selectedIds = new CopyOnWriteArrayList<>();
//    select(3000, selectSeq, selected, selectedIds);
//
//    Awaitility.await().until(() -> selected.size() == 3000);
//    Assert.assertEquals(3, new HashSet<>(selected).size());
//    Assert.assertEquals(3, new HashSet<>(selectedIds).size());
//    long aSize = selected.stream()
//            .filter(i -> 8081 == i)
//            .count();
//    long bSize = selected.stream()
//            .filter(i -> 8082 == i)
//            .count();
//    long cSize = selected.stream()
//            .filter(i -> 8083 == i)
//            .count();
//    Assert.assertEquals(aSize, 1000);
//    Assert.assertEquals(bSize, 1000);
//    Assert.assertEquals(cSize, 1000);
//
//    AtomicBoolean closeComplete = new AtomicBoolean();
//    close(selectedIds.get(0), closeComplete);
//    Awaitility.await().until(() -> closeComplete.get());
//
//    AtomicInteger selectSeq2 = new AtomicInteger();
//    List<Integer> selected2 = new CopyOnWriteArrayList<>();
//    List<String> selectedIds2 = new CopyOnWriteArrayList<>();
//    select(3000, selectSeq2, selected2, selectedIds2);
//
//    Awaitility.await().until(() -> selected2.size() == 3000);
//    int first = selected.get(0);
//    int second = selected.get(1);
//    int thrid = selected.get(2);
//    aSize = selected2.stream()
//            .filter(i -> first == i)
//            .count();
//    bSize = selected2.stream()
//            .filter(i -> second == i)
//            .count();
//    cSize = selected2.stream()
//            .filter(i -> thrid == i)
//            .count();
//    Assert.assertEquals(aSize, 0);
//    Assert.assertEquals(bSize, 1500);
//    Assert.assertEquals(cSize, 1500);
//  }
//
//  @Test
//  public void testRandom() {
//    moreServiceDiscovery = MoreServiceDiscovery.create(vertx, new MoreServiceDiscoveryOptions()
//            .addStrategy("test", "random"));
//
//    AtomicInteger seq = new AtomicInteger();
//    addService(seq);
//
//    Awaitility.await().until(() -> seq.get() == 3);
//
//    AtomicInteger selectSeq = new AtomicInteger();
//    List<Integer> selected = new CopyOnWriteArrayList<>();
//    List<String> selectedIds = new CopyOnWriteArrayList<>();
//    select(3000, selectSeq, selected, selectedIds);
//
//    Awaitility.await().until(() -> selectSeq.get() == 3000);
//    Assert.assertEquals(3, new HashSet<>(selected).size());
//    Assert.assertEquals(3, new HashSet<>(selectedIds).size());
//    long aSize = selected.stream()
//            .filter(i -> 8081 == i)
//            .count();
//    long bSize = selected.stream()
//            .filter(i -> 8082 == i)
//            .count();
//    long cSize = selected.stream()
//            .filter(i -> 8083 == i)
//            .count();
//    Assert.assertFalse(aSize == 1000 && bSize == 1000 && cSize == 1000);
//  }
//
//
//  @Test
//  public void testRoundRobin() {
//    moreServiceDiscovery = MoreServiceDiscovery.create(vertx, new MoreServiceDiscoveryOptions()
//            .addStrategy("test", "round_robin"));
//
//    AtomicInteger seq = new AtomicInteger();
//    addService(seq);
//
//    Awaitility.await().until(() -> seq.get() == 3);
//
//    AtomicInteger selectSeq = new AtomicInteger();
//    List<Integer> selected = new CopyOnWriteArrayList<>();
//    List<String> selectedIds = new CopyOnWriteArrayList<>();
//    select(3000, selectSeq, selected, selectedIds);
//
//    Awaitility.await().until(() -> selectSeq.get() == 3000);
//    Assert.assertEquals(3, new HashSet<>(selected).size());
//    Assert.assertEquals(3, new HashSet<>(selectedIds).size());
//    long aSize = selected.stream()
//            .filter(i -> 8081 == i)
//            .count();
//    long bSize = selected.stream()
//            .filter(i -> 8082 == i)
//            .count();
//    long cSize = selected.stream()
//            .filter(i -> 8083 == i)
//            .count();
//    Assert.assertEquals(aSize, 1000);
//    Assert.assertEquals(bSize, 1000);
//    Assert.assertEquals(cSize, 1000);
//  }
//
//  @Test
//  public void testSticky() {
//    moreServiceDiscovery = MoreServiceDiscovery.create(vertx, new MoreServiceDiscoveryOptions()
//            .addStrategy("test", "sticky"));
//
//    AtomicInteger seq = new AtomicInteger();
//    addService(seq);
//
//    Awaitility.await().until(() -> seq.get() == 3);
//
//    AtomicInteger selectSeq = new AtomicInteger();
//    List<Integer> selected = new CopyOnWriteArrayList<>();
//    List<String> selectedIds = new CopyOnWriteArrayList<>();
//
//    selectSticky3000(selectSeq, selected, selectedIds);
//    Awaitility.await().until(() -> selectSeq.get() == 3000);
//    Assert.assertEquals(2, new HashSet<>(selected).size());
//    Assert.assertEquals(2, new HashSet<>(selectedIds).size());
//    long aSize = selected.stream()
//            .filter(i -> selected.get(0) == i)
//            .count();
//    long bSize = selected.stream()
//            .filter(i -> selected.get(2000) == i)
//            .count();
//    Assert.assertEquals(aSize, 500);
//    Assert.assertEquals(bSize, 2500);
//  }
//
//  @Test
//  public void testWeightEquilibrium() {
//    moreServiceDiscovery = MoreServiceDiscovery.create(vertx, new MoreServiceDiscoveryOptions()
//            .addStrategy("test", "weight_round_robin"));
//
//    AtomicInteger seq = new AtomicInteger();
//    addService(seq);
//
//    Awaitility.await().until(() -> seq.get() == 3);
//
//    AtomicInteger selectSeq = new AtomicInteger();
//    List<Integer> selected = new CopyOnWriteArrayList<>();
//    List<String> selectedIds = new CopyOnWriteArrayList<>();
//    select(3000, selectSeq, selected, selectedIds);
//
//    Awaitility.await().until(() -> selectSeq.get() == 3000);
//    System.out.println(selected);
//    Assert.assertEquals(3, new HashSet<>(selected).size());
//    Assert.assertEquals(3, new HashSet<>(selectedIds).size());
//    long aSize = selected.stream()
//            .filter(i -> 8081 == i)
//            .count();
//    long bSize = selected.stream()
//            .filter(i -> 8082 == i)
//            .count();
//    long cSize = selected.stream()
//            .filter(i -> 8083 == i)
//            .count();
//    Assert.assertEquals(aSize, 1000);
//    Assert.assertEquals(bSize, 1000);
//    Assert.assertEquals(cSize, 1000);
//  }
//
//  @Test
//  public void testWeightDisequilibrium() {
//    moreServiceDiscovery = MoreServiceDiscovery.create(vertx, new MoreServiceDiscoveryOptions()
//            .addStrategy("test", "weight_round_robin"));
//
//    AtomicInteger seq = new AtomicInteger();
//    addWeightService(seq);
//
//    Awaitility.await().until(() -> seq.get() == 3);
//
//    AtomicInteger selectSeq = new AtomicInteger();
//    List<Integer> selected = new CopyOnWriteArrayList<>();
//    List<String> selectedIds = new CopyOnWriteArrayList<>();
//    select(7000, selectSeq, selected, selectedIds);
//
//    Awaitility.await().until(() -> selectSeq.get() == 7000);
//    Assert.assertEquals(3, new HashSet<>(selected).size());
//    Assert.assertEquals(3, new HashSet<>(selectedIds).size());
//    long aSize = selected.stream()
//            .filter(i -> 8081 == i)
//            .count();
//    long bSize = selected.stream()
//            .filter(i -> 8082 == i)
//            .count();
//    long cSize = selected.stream()
//            .filter(i -> 8083 == i)
//            .count();
//    Assert.assertEquals(aSize, 5000);
//    Assert.assertEquals(bSize, 1000);
//    Assert.assertEquals(cSize, 1000);
//    //刚开始的请求存在并发，并不是严格按照这个顺序
////    Assert.assertEquals(8081l, selected.get(0), 0);
////    Assert.assertEquals(8081l, selected.get(1), 0);
////    Assert.assertEquals(8082l, selected.get(2),0);
////    Assert.assertEquals(8081l, selected.get(3), 0);
////    Assert.assertEquals(8083l, selected.get(4), 0);
////    Assert.assertEquals(8081l, selected.get(5), 0);
////    Assert.assertEquals(8081l, selected.get(6), 0);
//  }
//
//  private void select(int count, AtomicInteger seq, List<Integer> selected, List<String>
//          selectedIds) {
//    for (int i = 0; i < count; i++) {
//      moreServiceDiscovery.queryForInstance("test", ar -> {
//        if (ar.succeeded()) {
//          int port = ar.result().getLocation().getInteger("port");
//          selected.add(port);
//          String id = ar.result().getRegistration();
//          selectedIds.add(id);
//        } else {
//          ar.cause().printStackTrace();
//        }
//        seq.incrementAndGet();
//      });
//    }
//  }
//
//  private void close(String id, AtomicBoolean complete) {
//    vertx.eventBus().send("service.discovery.close",
//                          new JsonObject().put("id", id), ar -> {
//              complete.set(true);
//            });
//  }
//
//  private void selectSticky3000(AtomicInteger seq, List<Integer> selected,
//                                List<String> selectedIds) {
//    for (int i = 0; i < 500; i++) {
//      moreServiceDiscovery.queryForInstance("test", ar -> {
//        if (ar.succeeded()) {
//          selected.add(ar.result().getLocation().getInteger("port"));
//          selectedIds.add(ar.result().getRegistration());
//        } else {
//          ar.cause().printStackTrace();
//        }
//        seq.incrementAndGet();
//      });
//    }
//    Awaitility.await().until(() -> seq.get() == 500);
//    int first = selected.get(0);
//
//    AtomicBoolean unpublished = new AtomicBoolean();
//    moreServiceDiscovery.queryAllInstances("test", ar -> {
//      List<Record> instances = ar.result();
//      String firstId = instances.stream()
//              .filter(i -> i.getLocation().getInteger("port") == first)
//              .findFirst()
//              .get().getRegistration();
//      discovery.unpublish(firstId, ar2 -> {
//        unpublished.set(true);
//      });
//    });
//
//    Awaitility.await().until(() -> unpublished.get());
//
//    for (int i = 0; i < 1000; i++) {
//      moreServiceDiscovery.queryForInstance("test", ar -> {
//        if (ar.succeeded()) {
//          selected.add(ar.result().getLocation().getInteger("port"));
//          selectedIds.add(ar.result().getRegistration());
//        } else {
//          ar.cause().printStackTrace();
//        }
//        seq.incrementAndGet();
//      });
//    }
//    Awaitility.await().until(() -> seq.get() == 1500);
//
//    AtomicBoolean published = new AtomicBoolean();
//    discovery.publish(HttpEndpoint.createRecord("test", "localhost", first, "/"), ar -> published
//            .set(true));
//
//    Awaitility.await().until(() -> published.get());
//
//    for (int i = 0; i < 1500; i++) {
//      moreServiceDiscovery.queryForInstance("test", ar -> {
//        if (ar.succeeded()) {
//          selected.add(ar.result().getLocation().getInteger("port"));
//          selectedIds.add(ar.result().getRegistration());
//        } else {
//          ar.cause().printStackTrace();
//        }
//        seq.incrementAndGet();
//      });
//    }
//    Awaitility.await().until(() -> seq.get() == 3000);
//  }
//
//  private void addService(AtomicInteger seq) {
//    discovery.publish(HttpEndpoint.createRecord("test", "localhost", 8081, "/"),
//                      ar -> seq.incrementAndGet());
//    discovery.publish(HttpEndpoint.createRecord("test", "localhost", 8082, "/"),
//                      ar -> seq.incrementAndGet());
//    discovery.publish(HttpEndpoint.createRecord("test", "localhost", 8083, "/"),
//                      ar -> seq.incrementAndGet());
//  }
//
//  private void addWeightService(AtomicInteger seq) {
//    discovery.publish(HttpEndpoint.createRecord("test", "localhost", 8081, "/").setMetadata(new
//                                                                                                    JsonObject()
//                                                                                                    .put("weight",
//                                                                                                         5)),
//                      ar -> seq.incrementAndGet());
//    discovery.publish(HttpEndpoint.createRecord("test", "localhost", 8082, "/").setMetadata(new
//                                                                                                    JsonObject()
//                                                                                                    .put("weight",
//                                                                                                         1)),
//                      ar -> seq.incrementAndGet());
//    discovery.publish(HttpEndpoint.createRecord("test", "localhost", 8083, "/").setMetadata(new
//                                                                                                    JsonObject()
//                                                                                                    .put("weight",
//                                                                                                         1)),
//                      ar -> seq.incrementAndGet());
//  }
//}
