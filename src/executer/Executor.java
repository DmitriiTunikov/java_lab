package executer;

import adapter.AdapterType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user_phmf3 on 10.11.18.
 */
public interface Executor extends Runnable {
  void setConsumer(Executor consumer) throws IOException;

  void setAdapter(Executor provider, Object adapter, AdapterType typeOfAdapter);

  ArrayList<AdapterType> getReadableTypes();

  void setConfigFile(String configFile) throws IOException;

  void setOutput(DataOutputStream output);

  void setInput(DataInputStream input);

  boolean isReadyToWrite();

  boolean isReadyToRead();

  boolean isAvailable();

  boolean isOver();

  void run();
}
