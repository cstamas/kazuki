package io.kazuki.v0.store.index;

import io.kazuki.v0.internal.helper.TestSupport;
import io.kazuki.v0.store.Foo;
import io.kazuki.v0.store.Key;
import io.kazuki.v0.store.index.query.QueryBuilder;
import io.kazuki.v0.store.index.query.QueryOperator;
import io.kazuki.v0.store.index.query.ValueType;
import io.kazuki.v0.store.keyvalue.KeyValueIterator;
import io.kazuki.v0.store.keyvalue.KeyValueStore;
import io.kazuki.v0.store.keyvalue.KeyValueStoreIteration.SortDirection;
import io.kazuki.v0.store.lifecycle.Lifecycle;
import io.kazuki.v0.store.schema.SchemaStore;
import io.kazuki.v0.store.schema.TypeValidation;
import io.kazuki.v0.store.schema.model.Schema;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public abstract class SecondaryIndexStoreTestBase extends TestSupport {
  protected abstract Injector getInjector();

  @Test
  public void testDemo() throws Exception {
    Injector inject = getInjector();

    final Lifecycle lifecycle =
        inject.getInstance(com.google.inject.Key.get(Lifecycle.class, Names.named("foo")));

    KeyValueStore store =
        inject.getInstance(com.google.inject.Key.get(KeyValueStore.class, Names.named("foo")));

    SecondaryIndexStore index =
        inject
            .getInstance(com.google.inject.Key.get(SecondaryIndexStore.class, Names.named("foo")));

    SchemaStore manager =
        inject.getInstance(com.google.inject.Key.get(SchemaStore.class, Names.named("foo")));

    lifecycle.init();
    lifecycle.start();

    Assert.assertFalse(store.iterators().iterator("$schema", Schema.class, SortDirection.ASCENDING)
        .hasNext());

    manager.createSchema("foo", Foo.FOO_SCHEMA);

    Key k0 = store.create("foo", Foo.class, new Foo("k00", "v99"), TypeValidation.STRICT);
    Key k1 = store.create("foo", Foo.class, new Foo("k11", "v88"), TypeValidation.STRICT);
    Key k2 = store.create("foo", Foo.class, new Foo("k22", "v77"), TypeValidation.STRICT);
    Key k3 = store.create("foo", Foo.class, new Foo("k33", "v66"), TypeValidation.STRICT);
    Key k4 = store.create("foo", Foo.class, new Foo("k44", "v55"), TypeValidation.STRICT);
    Key k5 = store.create("foo", Foo.class, new Foo("k00", "v55"), TypeValidation.STRICT);

    try (KeyValueIterator<Key> iter =
        index.queryWithoutPagination(
            "foo",
            Foo.class,
            "fooKey",
            new QueryBuilder()
                .andMatchesSingle("fooKey", QueryOperator.EQ, ValueType.STRING, "k00").build(),
            SortDirection.ASCENDING, null, null).iterator()) {

      Assert.assertTrue(iter.hasNext());
      Assert.assertEquals(k0, iter.next());

      Assert.assertTrue(iter.hasNext());
      Assert.assertEquals(k5, iter.next());

      Assert.assertFalse(iter.hasNext());
    }

    try (KeyValueIterator<Key> iter =
        index.queryWithoutPagination(
            "foo",
            Foo.class,
            "fooKey",
            new QueryBuilder()
                .andMatchesSingle("fooKey", QueryOperator.EQ, ValueType.STRING, "k00").build(),
            SortDirection.ASCENDING, 1L, null).iterator()) {

      Assert.assertTrue(iter.hasNext());
      Assert.assertEquals(k5, iter.next());

      Assert.assertFalse(iter.hasNext());
    }

    try (KeyValueIterator<Key> iter =
        index.queryWithoutPagination(
            "foo",
            Foo.class,
            "fooKey",
            new QueryBuilder()
                .andMatchesSingle("fooKey", QueryOperator.EQ, ValueType.STRING, "k00").build(),
            SortDirection.DESCENDING, null, null).iterator()) {

      Assert.assertTrue(iter.hasNext());
      Assert.assertEquals(k5, iter.next());

      Assert.assertTrue(iter.hasNext());
      Assert.assertEquals(k0, iter.next());

      Assert.assertFalse(iter.hasNext());
    }

    try (KeyValueIterator<Key> iter =
        index.queryWithoutPagination(
            "foo",
            Foo.class,
            "fooKey",
            new QueryBuilder()
                .andMatchesSingle("fooKey", QueryOperator.EQ, ValueType.STRING, "k00").build(),
            SortDirection.DESCENDING, 1L, null).iterator()) {

      Assert.assertTrue(iter.hasNext());
      Assert.assertEquals(k0, iter.next());

      Assert.assertFalse(iter.hasNext());
    }

    for (QueryOperator op : ImmutableList.of(QueryOperator.NE, QueryOperator.GT)) {
      try (KeyValueIterator<Key> iter =
          index.queryWithoutPagination("foo", Foo.class, "fooKey",
              new QueryBuilder().andMatchesSingle("fooKey", op, ValueType.STRING, "k00").build(),
              SortDirection.ASCENDING, null, null).iterator()) {

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k1, iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k2, iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k3, iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k4, iter.next());

        Assert.assertFalse(iter.hasNext());
      }
    }

    for (QueryOperator op : ImmutableList.of(QueryOperator.NE, QueryOperator.GT)) {
      try (KeyValueIterator<Key> iter =
          index.queryWithoutPagination("foo", Foo.class, "fooKey",
              new QueryBuilder().andMatchesSingle("fooKey", op, ValueType.STRING, "k00").build(),
              SortDirection.DESCENDING, null, null).iterator()) {

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k4, iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k3, iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k2, iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(k1, iter.next());

        Assert.assertFalse(iter.hasNext());
      }
    }

    store.clear(false, false);
  }
}