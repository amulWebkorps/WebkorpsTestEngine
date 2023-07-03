package com.codecompiler.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class CodeProcessingUtil {

  public String compilationCommand(String language, String studentId) {
    String command = null;
    if (language.equalsIgnoreCase("java")) {
      command = "javac Main.java";
    } else if (language.equalsIgnoreCase("python")) {
      command = "src/main/resources/temp/python" + studentId + ".py";
    } else if (language.equalsIgnoreCase("cpp")) {
      command = "g++ cpp" + studentId + ".cpp -o cpp" + studentId;
    } else if (language.equalsIgnoreCase("c")) {
      command = "gcc c" + studentId + ".c -o c" + studentId;
    }
    return command;
  }

  public String compilationCommand(String language, String studentId, int counter) {
    String command = null;
    if (language.equalsIgnoreCase("java")) {
      command = "javac Main" + counter + ".java";
    } else if (language.equalsIgnoreCase("python")) {
      command = "src/main/resources/temp/python" + studentId + ".py";
    } else if (language.equalsIgnoreCase("cpp")) {
      command = "g++ cpp" + studentId + ".cpp -o cpp" + studentId;
    } else if (language.equalsIgnoreCase("c")) {
      command = "gcc c" + studentId + ".c -o c" + studentId;
    }
    return command;
  }

  public String interpretationCommand(String language, String studentId, int counter) {
    String command = null;
    if (language.equalsIgnoreCase("java")) {
      command = "java Main" + counter + " ";
    } else if (language.equalsIgnoreCase("python")) {
      command = "py python" + studentId + ".py ";
    } else if (language.equalsIgnoreCase("cpp")) {
      command = "src/main/resources/temp/" + "exeofCPP ";
    } else if (language.equalsIgnoreCase("c")) {
      command = "src/main/resources/temp/exeofc ";
    }
    return command;
  }

  public String interpretationCommand(String language, String studentId) {
    String command = null;
    if (language.equalsIgnoreCase("java")) {
      command = "java Main ";
    } else if (language.equalsIgnoreCase("python")) {
      command = "py python" + studentId + ".py ";
    } else if (language.equalsIgnoreCase("cpp")) {
      command = "src/main/resources/temp/" + "exeofCPP ";
    } else if (language.equalsIgnoreCase("c")) {
      command = "src/main/resources/temp/exeofc ";
    }
    return command;
  }

  public String saveCodeTemporary(String code, String language, String studentId, int counter) throws IOException {
    log.info("saveCodeTemporary: started");
    FileWriter fl = null;
    String fileNameInLocal = null;
    if (language.equalsIgnoreCase("java")) {
      fileNameInLocal = "Main" + counter + ".java";
      fl = new FileWriter("src/main/resources/" + fileNameInLocal);
    } else if (language.equalsIgnoreCase("python")) {
      fileNameInLocal = "python" + studentId + ".py";
      fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
    } else if (language.equalsIgnoreCase("cpp")) {
      fileNameInLocal = "cpp" + studentId + ".cpp";
      fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
    } else if (language.equalsIgnoreCase("c")) {
      fileNameInLocal = "c" + studentId + ".c";
      fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
    }
    PrintWriter pr = new PrintWriter(fl);
    pr.write(code);
    pr.flush();
    pr.close();
    log.info("saveCodeTemporary: ended");
    return fileNameInLocal;
  }

  public void saveCodeTemporary(String code, String language, String studentId) throws IOException {
    log.info("saveCodeTemporary: started");
    FileWriter fl = null;
    if (language.equalsIgnoreCase("java")) {
      String fileNameInLocal = "Main.java";
      fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
    } else if (language.equalsIgnoreCase("python")) {
      String fileNameInLocal = "python" + studentId + ".py";
      fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
    } else if (language.equalsIgnoreCase("cpp")) {
      String fileNameInLocal = "cpp" + studentId + ".cpp";
      fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
    } else if (language.equalsIgnoreCase("c")) {
      String fileNameInLocal = "c" + studentId + ".c";
      fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
    }
    PrintWriter pr = new PrintWriter(fl);
    pr.write(code);
    pr.flush();
    pr.close();
    log.info("saveCodeTemporary: ended");
  }

  public String getMessagesFromProcessInputStream(InputStream inputStream) throws IOException {
    log.info("getMessagesFromProcessInputStream :: started");
    String message = "";
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String line = null;
    while ((line = in.readLine()) != null) {
      message += line + "\n";
    }
    log.info("getMessagesFromProcessInputStream :: end");
    return message;
  }
}
