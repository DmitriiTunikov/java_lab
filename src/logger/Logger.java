package logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Logger {
  private static final String endl = "\n";
  private static final String errorTag = "Error: ";
  private static String fileName;

  public static void setLogFile(String logName) {
    fileName = logName;
  }

  public static void writeLn(String data) {
    PrintStream logWriter;
    try {
      if (fileName == null) {
        logWriter = System.out;
      } else {
        logWriter = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName, true)));
      }

      logWriter.println(data);
      if (fileName != null)
        logWriter.close();
    } catch (IOException ex) {
      System.out.println("logger.logger Error!");
      System.out.println(ex);
    }
  }

  public static void writeErrorLn(String error) {
    writeLn(errorTag + error);
  }
  public static void writeErrorLn(IOException error) {
    error.printStackTrace();
  }
  public static void writeErrorLn(InterruptedException error) {
    error.printStackTrace();
  }
}
