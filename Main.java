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

  static boolean [][] personaMatrix;
  static HashMap<String, ArrayList<String>> personaMap = new HashMap<>();

  public static String Reader(File filename) throws Exception {
    Scanner scanner = new Scanner(filename, "UTF-8");
    StringBuilder text = new StringBuilder();
    
    while (scanner.hasNextLine()) 
      {text.append(scanner.nextLine()).append("\n");}
    
    scanner.close();
    return text.toString();
  }

  public static String[] TextSplitter(String text) throws Exception {
    String[] lines = text.split("\n");
    ArrayList<String> playacts = new ArrayList<>();

    // split into plays
    for (int play = 0; play < Titles.length - 1; play++) {

      int titleCount = 0;
      int titleLine = 0;

      // second occurence of each title
      for (int i = 0; i < lines.length; i++) {
        if (lines[i].trim().equals(Titles[play])) {
          titleCount++;
          if (titleCount == 2) 
            { titleLine = i; break; }
        }
      }
      
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

      // other acts in the play
      for (int i = startAct + 1; i < endLine; i++) {
        if (lines[i].trim().matches("ACT [IVX]+")) {
          StringBuilder act = new StringBuilder();
          act.append(Titles[play]).append(" - " + lines[startAct].trim() + "\n");

          for (int j = startAct + 1; j < i; j++) 
            { act.append(lines[j]).append("\n"); }
          playacts.add(act.toString());
          startAct = i;
        }
      }

      StringBuilder act = new StringBuilder();
      act.append(Titles[play]).append(" - " + lines[startAct].trim() + "\n");

      for (int i = startAct + 1; i < endLine; i++) 
        { act.append(lines[i]).append("\n"); }
      playacts.add(act.toString());
    }
    return playacts.toArray(new String[0]);
  }
  
  public static boolean[][] PersonaMatrix(String[] playacts) {
    boolean[][] personaMatrix = new boolean[playacts.length][Personas.length];

    for (int act = 0; act < playacts.length; act++) {
      for (int persona = 0; persona < Personas.length; persona++) {
        if (playacts[act].contains(Personas[persona])) 
          { personaMatrix[act][persona] = true; }
        else 
          { personaMatrix[act][persona] = false; }
      }
    }
    return personaMatrix;
  }

  public static void PersonaSearch(String inputPersona) {
    String persona = inputPersona.toUpperCase();
    ArrayList<String> acts1 = personaMap.get(persona);
    System.out.println("\nActs that have " + persona + ": ");
    for (String act : acts1) 
      { System.out.println(act); }
  }

  public static void PersonaIncludedSearch(String inputPersona1, String inputPersona2) {
    String persona1 = inputPersona1.toUpperCase();
    String persona2 = inputPersona2.toUpperCase();
    ArrayList<String> acts1 = personaMap.get(persona1);
    ArrayList<String> acts2 = personaMap.get(persona2);
    System.out.println("\nActs that have both " + persona1 + " & " + persona2 + ": ");
    for (String act : acts1) {
      if (acts2.contains(act)) 
        { System.out.println(act); }
    }
  }

  public static void PersonaExcludedSearch(String inputPersona1, String inputPersona2) {
    String persona1 = inputPersona1.toUpperCase();
    String persona2 = inputPersona2.toUpperCase();
    ArrayList<String> acts1 = personaMap.get(persona1);
    ArrayList<String> acts2 = personaMap.get(persona2);
    System.out.println("\nActs that have " + persona1 + " & NOT " + persona2 + ": ");

    for (String act : acts1) {
      if (!(acts2.contains(act))) 
        { System.out.println(act); }
    }
  }

  public static void main(String[] args) throws Exception { 
    String[] playacts = TextSplitter(Reader(new File("pg100.txt"))); 
    boolean[][] personaMatrix = PersonaMatrix(playacts); 

    for (int persona = 0; persona < Personas.length; persona++) { 
      ArrayList<String> acts = new ArrayList<>();
      for (int act = 0; act < playacts.length; act++) {
        if (personaMatrix[act][persona]) { 
          String[] actHeaders = playacts[act].split("\n", 2);
          acts.add(actHeaders[0]); 
        }
      }
      personaMap.put(Personas[persona], acts);
    }

    for (String persona : personaMap.keySet()) {
      System.out.println("\n" + persona);
      for (String act : personaMap.get(persona)) 
        { System.out.println("  " + act); }
    }

    PersonaSearch("citizen");
    PersonaIncludedSearch("citizen", "orlando");
    PersonaExcludedSearch("citizen", "orlando");
  }
}