package transporter;

import encoder.Encoder;
import executer.Executor;
import logger.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by user_phmf3 on 10.11.18.
 */
public class Transporter {
  private static final String splitDelim = " |:|=";
  private static final String comment = "#";
  private static final int sleepTime = 1000;
  private ArrayList<Executor> exs;
  private DataInputStream inputFile;
  private String inputFileName;
  private DataOutputStream outputFile;
  private Map<Executor, ArrayList<Integer>> executorConsumer;

  private enum valTypes {
    EXECUTOR
  }

  private static final Map<String, valTypes> mapTypes;

  static {
    mapTypes = new HashMap<>();
    mapTypes.put("executor", valTypes.EXECUTOR);
  }

  public Transporter(String inFile, String outFile, String confFile) throws IOException {
    inputFile = new DataInputStream(new FileInputStream(inFile));
    inputFileName = inFile;
    outputFile = new DataOutputStream(new FileOutputStream(outFile));
    exs = new ArrayList<>();
    executorConsumer = new HashMap<>();
    setConfigs(confFile);
    if (exs.isEmpty())
      throw new IOException("Empty list of executors");
    introduce();
  }

  private void setConfigs(String confFile) throws IOException {
    BufferedReader configReader = new BufferedReader(new FileReader(confFile));
    String line;
    while ((line = configReader.readLine()) != null) {
      String[] words = line.split(splitDelim);
      if (words.length < 2)
        throw new IOException("Wrong number of arguments in file: " + confFile + " at: " + line);
      if (words[0].startsWith(comment))
        continue;
      valTypes type = mapTypes.get(words[0]);
      if (type == null)
        throw new IOException("Unknown config: " + words[0] + " in file: " + confFile + " at: " + line);
      switch (type) {
        case EXECUTOR: {
          Executor newExecutor = new Encoder(inputFileName);
          newExecutor.setConfigFile(words[1]);
          exs.add(newExecutor);
          ArrayList<Integer> consumersIds = new ArrayList();
          for (int i = 2; i < words.length; i++) {
            consumersIds.add(Integer.parseInt(words[i]));
          }
          executorConsumer.put(newExecutor, consumersIds);
          break;
        }
      }
    }
  }

  private void introduce() throws IOException {
    exs.get(0).setInput(inputFile);

    for (Executor cur : exs) {
      ArrayList<Integer> ids = executorConsumer.get(cur);
      for (int id : ids) {
        cur.setConsumer(exs.get(id - 1));
      }
    }

    exs.get(exs.size() - 1).setOutput(outputFile);
  }

  public void run() {
    try {
      ArrayList<Thread> threads = new ArrayList<>();
      for (Executor ex : exs) {
        Thread th = new Thread(ex);
        threads.add(th);
        th.start();
        Logger.writeLn(th.getName() + " is started");
      }

      while (!exs.get(0).isOver())
        Thread.sleep(sleepTime);

      for (Thread th : threads) {
        while (th.isAlive())
          th.interrupt();
        Logger.writeLn(th.getName() + " is closed");
      }

      inputFile.close();
      outputFile.close();
      Logger.writeLn("i/o streams closed");
    } catch (IOException ex) {
      Logger.writeLn("Conveyer error! ");
      Logger.writeErrorLn(ex);
    } catch (InterruptedException ex) {
      Logger.writeErrorLn(ex);
    }
  }
}
