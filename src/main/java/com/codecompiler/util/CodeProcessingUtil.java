package com.codecompiler.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class CodeProcessingUtil {
	
	public static final String PATH="/home/server/SERVERS/Testengine Jar/temp/";
//	public static final String PATH="src/main/resources/temp/";

  public String compilationCommand(String language, String studentId) {
    String command = null;
    if (language.equalsIgnoreCase("java")) {
      command = "javac Main.java";
    } else if (language.equalsIgnoreCase("python")) {
      command = PATH + studentId + ".py";
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
      command = "python Main" + counter + ".py";
    } else if (language.equalsIgnoreCase("cpp")) {
      command = "g++ Main" + counter + ".cpp -o Main" + counter;
    } else if (language.equalsIgnoreCase("c")) {
      command = "gcc Main" + counter + ".c -o Main"+counter;
    }
    return command;
  }

  public String interpretationCommand(String language, String studentId, int counter) {
    String command = null;
    if (language.equalsIgnoreCase("java")) {
      command = "java Main" + counter + " ";
    } else if (language.equalsIgnoreCase("python")) {
      command = "py Main" + counter + ".py ";
    } else if (language.equalsIgnoreCase("cpp")) {
      command = PATH + "Main"+counter+".exe";
    } else if (language.equalsIgnoreCase("c")) {
      command = PATH+"Main"+counter+".exe";
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
      command = PATH + "exeofCPP ";
    } else if (language.equalsIgnoreCase("c")) {
      command = PATH+"exeofc ";
    }
    return command;
  }

  public String saveCodeTemporary(String code, String language, String studentId, int counter) throws IOException {
    log.info("saveCodeTemporary: started");
    FileWriter fl = null;
    String fileNameInLocal = null;
    if (language.equalsIgnoreCase("java")) {
      fileNameInLocal = "Main" + counter + ".java";
      fl = new FileWriter(PATH + fileNameInLocal);
    } else if (language.equalsIgnoreCase("python")) {
      fileNameInLocal = "Main" + counter + ".py";
      fl = new FileWriter(PATH+ fileNameInLocal);
    } else if (language.equalsIgnoreCase("cpp")) {
      fileNameInLocal = "Main" + counter + ".cpp";
      fl = new FileWriter(PATH + fileNameInLocal);
    } else if (language.equalsIgnoreCase("c")) {
      fileNameInLocal = "Main" + counter + ".c";
      fl = new FileWriter(PATH+ fileNameInLocal);
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
      fl = new FileWriter(PATH+ fileNameInLocal);
    } else if (language.equalsIgnoreCase("python")) {
      String fileNameInLocal = "python" + studentId + ".py";
      fl = new FileWriter(PATH+ fileNameInLocal);
    } else if (language.equalsIgnoreCase("cpp")) {
      String fileNameInLocal = "cpp" + studentId + ".cpp";
      fl = new FileWriter(PATH + fileNameInLocal);
    } else if (language.equalsIgnoreCase("c")) {
      String fileNameInLocal = "c" + studentId + ".c";
      fl = new FileWriter(PATH+ fileNameInLocal);
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
