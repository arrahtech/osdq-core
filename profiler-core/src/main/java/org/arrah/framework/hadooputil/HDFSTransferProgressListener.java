package org.arrah.framework.hadooputil;

@FunctionalInterface
public interface HDFSTransferProgressListener {
  public void progressUpdate(final int progressCounter);
}
