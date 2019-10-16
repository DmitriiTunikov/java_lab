package adapter;

public interface ByteAdapter {
  Byte getNextByte();

  void setMetrics(int start, int offset);

  Integer getStart();
}
