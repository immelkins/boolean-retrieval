import java.io.*;
import java.util.*;

public class Main {

  static final String[] Titles = {
    "AS YOU LIKE IT",
    "THE COMEDY OF ERRORS",
    "THE TRAGEDY OF CORIOLANUS",

    // cutoff this is not a section
    "CYMBELINE"
  };

  static final String[] Personas = {
    // AS YOU LIKE IT
      "ORLANDO", "OLIVER", "JAQUES DE BOYS", "ADAM", "DENNIS", "ROSALIND", "CELIA", "TOUCHSTONE", "DUKE SENIOR", "JAQUES", "AMIENS", "DUKE FREDERICK", "CHARLES",
      "LE BEAU", "CORIN", "SILVIUS", "PHOEBE", "AUDREY", "WILLIAM", "SIR OLIVER MARTEXT", "HYMEN",

    // THE COMEDY OF ERRORS
      "SOLINUS", "EGEON", "ANTIPHOLUS OF EPHESUS", "ANTIPHOLUS OF SYRACUSE", "DROMIO OF EPHESUS", "DROMIO OF SYRACUSE", "BALTHASAR", "ANGELO", "MERCHANT", "PINCH",
      "EMILIA", "ADRIANA", "LUCIANA", "LUCE", "COURTESAN",

    // THE TRAGEDY OF CORIOLANUS
      "CAIUS MARTIUS CORIOLANUS", "VOLUMNIA", "VIRGILIA", "YOUNG MARTIUS", "VALERIA", "GENTLEWOMAN", "MENENIUS AGRIPPA", "COMINIUS", "TITUS LARTIUS", "SICINIUS VELUTUS", 
      "JUNIUS BRUTUS", "ROMAN HERALD", "TULLUS AUFIDIUS", "LIEUTENANT", "CITIZEN"
  };

  boolean [][] PersonaMatrix;


  public static String Reader(File filename) throws Exception {
    Scanner scanner = new Scanner(filename, "UTF-8");
    StringBuilder text = new StringBuilder();
    
    while (scanner.hasNextLine()) 
      {text.append(scanner.nextLine()).append("\n");}
    
    scanner.close();
    return text.toString();
  }

  public static String[] TextSplitter(String text) throws Exception {
    // split into plays and acts
    String[] lines = text.toString().split("\n");
    ArrayList<String> play_acts = new ArrayList<>();

    // second occurence 
    int titleCount = 0;
    int startLine = 0;

    for (int i = 0; i < lines.length; i++) {
      if (lines[i].trim().equals(Titles[0])) {
        titleCount++;
        if (titleCount == 2) 
          { startLine = i; break; }
      }
    }

    // split into plays
    for (int play = 0; play < Titles.length - 1; play++) {
      int titleLine = -1;
      // find play title
      for (int i = startLine; i < lines.length; i++) {
        if (lines[i].trim().equals(Titles[play])) 
          { titleLine = i; break; }
      }

      if (titleLine == -1) 
        { System.out.println("Could not find: " + Titles[play]); continue; }
      
      // start of next play
      int endLine = lines.length;

      for (int i = titleLine + 1; i < lines.length; i++) {
        if (lines[i].trim().equals(Titles[play + 1])) 
          { endLine = i; break; }
      }

      // find the first act within the play
      int startAct = -1;
      for (int i = titleLine; i < endLine; i++) { 
        if (lines[i].trim().matches("ACT [IVX]+")) 
          if (lines[i+2].trim().matches("SCENE [IVX]+.*")) 
            { startAct = i; break; }
      }

      if (startAct == -1) 
        { System.out.println("Could not find ACT in: " + Titles[play]); continue; }

      // other acts within the play
      for (int i = startAct + 1; i < endLine; i++) {
        if (lines[i].trim().matches("ACT [IVX]+")) {
          StringBuilder act = new StringBuilder();
          act.append(Titles[play]).append(" - " + lines[startAct].trim() + "\n");

          for (int j = startAct + 1; j < i; j++) 
            { act.append(lines[j]).append("\n"); }
          play_acts.add(act.toString());
          startAct = i;
        }
      }

      StringBuilder act = new StringBuilder();
      act.append(Titles[play]).append(" - " + lines[startAct].trim() + "\n");

      for (int i = startAct + 1; i < endLine; i++) 
        { act.append(lines[i]).append("\n"); }
      play_acts.add(act.toString());
      startLine = titleLine + 1;
    }
    return play_acts.toArray(new String[0]);
  }
  
  public static boolean[][] PersonaMatrix(String[] play_acts) {
    boolean[][] personaMatrix = new boolean[play_acts.length][Personas.length];

    for (int act = 0; act < play_acts.length; act++) {
      for (int persona = 0; persona < Personas.length; persona++) {
        if (play_acts[act].contains(Personas[persona])) 
          { personaMatrix[act][persona] = true; }
        else 
          { personaMatrix[act][persona] = false; }
      }
    }
    return personaMatrix;
  }
  public static void main(String[] args) throws Exception { 
    String[] play_acts = TextSplitter(Reader(new File("pg100.txt"))); 
    boolean[][] personaMatrix = PersonaMatrix(play_acts); 

    System.out.println("Number of sections: " + play_acts.length); 

    for (int person = 0; person < Personas.length; person++) { 
      System.out.println("\n" + Personas[person]);
      for (int act = 0; act < play_acts.length; act++) {
        if (personaMatrix[act][person]) { 
          String[] actHeaders = play_acts[act].split("\n", 2);
          System.out.println("  " + actHeaders[0] + " = true"); 
        }
      }
    }
  }
}