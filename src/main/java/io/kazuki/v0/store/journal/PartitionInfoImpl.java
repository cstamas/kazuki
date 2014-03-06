package io.kazuki.v0.store.journal;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartitionInfoImpl implements PartitionInfo {
  private final String partitionId;
  private final long minId;
  private AtomicLong maxId;
  private AtomicLong size;
  private AtomicBoolean closed;

  public PartitionInfoImpl(@JsonProperty("partitionId") String partitionId,
      @JsonProperty("minId") long minId, @JsonProperty("maxId") long maxId,
      @JsonProperty("size") long size, @JsonProperty("closed") boolean closed) {
    this.partitionId = partitionId;
    this.minId = minId;
    this.maxId = new AtomicLong(maxId);
    this.size = new AtomicLong(size);
    this.closed = new AtomicBoolean(closed);
  }

  @Override
  public long getMaxId() {
    return this.maxId.get();
  }

  @Override
  public long getMinId() {
    return this.minId;
  }

  @Override
  public String getPartitionId() {
    return this.partitionId;
  }

  @Override
  public long getSize() {
    return this.size.get();
  }

  @Override
  public boolean isClosed() {
    return this.closed.get();
  }

  public void setSize(long size) {
    this.size.set(size);
  }

  public void setClosed(boolean closed) {
    this.closed.set(closed);
  }

  public synchronized void setMaxId(long maxId) {
    this.maxId.set(maxId);
  }

  public PartitionInfo snapshot() {
    return new PartitionInfoSnapshot(this.partitionId, this.minId, this.maxId.get(),
        this.size.get(), this.closed.get());
  }
}
