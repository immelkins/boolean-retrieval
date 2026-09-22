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


  public static String[] Reader() throws Exception {
    Scanner scanner = new Scanner(new File("pg100.txt"),"UTF-8");
    ArrayList<String> lines = new ArrayList<>();
    
    while (scanner.hasNextLine()) 
      {lines.add(scanner.nextLine());}
    
    scanner.close();

    String[] sections = new String[Titles.length - 1];
    int titleCount = 0;
    int startLine = -1;

    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).trim().equals(Titles[0])) {
        titleCount++;
        if (titleCount == 2) 
          { startLine = i; break; }
      }
    }

    if (startLine == -1) {
      System.out.println("Could not find: " + Titles[0]);
      return sections;
    }

    int searchStart = startLine;

    for (int i = 0; i < sections.length; i++) {
      int titleLine = -1;

      for (int j = searchStart; j < lines.size(); j++) {
        if (lines.get(j).trim().equals(Titles[i])) 
          { titleLine = j; break; }
      }

      if (titleLine == -1) 
        { System.out.println("Could not find: " + Titles[i]); continue; }
      
      int endLine = lines.size();

      for (int j = titleLine + 1; j < lines.size(); j++) {
        if (lines.get(j).trim().equals(Titles[i + 1])) 
          { endLine = j; break; }
      }

      StringBuilder section = new StringBuilder();

      for (int j = titleLine; j < endLine; j++) 
        { section.append(lines.get(j)).append("\n"); }

      sections[i] = section.toString();
      searchStart = titleLine + 1;
    }

    return sections;
}
  
  public static String[][] Acts(String[] sections) {
    String[][] acts = new String[sections.length][];
    for (int i = 0; i < sections.length; i++) {

      String[] lines = sections[i].split("\n");
      ArrayList<String> actList = new ArrayList<>();

      int personaLine = -1;
      int startLine = -1;

      for (int j = 0; j < lines.length; j++) {
        if (lines[j].trim().equals("Dramatis Personæ")) 
          { personaLine = j; break; }
      }

      if (personaLine == -1) 
        { acts[i] = new String[0]; continue; }

      for (int j = personaLine + 1; j < lines.length; j++) {
        if (lines[j].trim().equals("ACT I")) 
          { startLine = j; break; }
      }

      if (startLine == -1) 
        { acts[i] = new String[0]; continue; }

      for (int j = startLine + 1; j < lines.length; j++) {

        if (lines[j].trim().matches("ACT [IVX]+")) {
          StringBuilder act = new StringBuilder();
          for (int k = startLine; k < j; k++) 
            { act.append(lines[k]).append("\n"); }

          actList.add(act.toString());
          startLine = j;
        }
      }

      StringBuilder act = new StringBuilder();

      for (int j = startLine; j < lines.length; j++) 
        { act.append(lines[j]).append("\n"); }

      actList.add(act.toString());
      acts[i] = actList.toArray(new String[0]);
    }
    return acts;
  }

  public static boolean[][] PersonaMatrix(String[][] acts) {
    int totalActs = 0;
    for (int i = 0; i < acts.length; i++) 
      { totalActs += acts[i].length; }

    boolean[][] personaMatrix = new boolean[totalActs][Personas.length];
    int row = 0;

    for (int play = 0; play < acts.length; play++) {
      for (int act = 0; act < acts[play].length; act++) {
        String actText = acts[play][act];

        for (int persona = 0; persona < Personas.length; persona++) {
          if (actText.contains(Personas[persona])) 
            { personaMatrix[row][persona] = true; }
          else 
            { personaMatrix[row][persona] = false; }
        }
        row++;
      }
    }
    return personaMatrix;
  }
public static void main(String[] args) throws Exception { 
    String[] sections = Reader(); 
    String[][] acts = Acts(sections); 

    boolean[][] personaMatrix = PersonaMatrix(acts); 

    System.out.println("Number of sections: " + sections.length); 

    for (int i = 0; i < sections.length; i++) { 
        if (sections[i] != null)
            System.out.println(i + " - " + Titles[i] + 
                " (" + sections[i].length() + " characters)"); 
        else
            System.out.println(i + " - " + Titles[i] + " = NULL"); 
    }

    int row = 0;
    for (int play = 0; play < acts.length; play++) {
        for (int act = 0; act < acts[play].length; act++) {
            System.out.println("\n" + Titles[play] + " - ACT " + (act + 1));
            for (int persona = 0; persona < Personas.length; persona++) {
                if (personaMatrix[row][persona]) {
                    System.out.println("  " + Personas[persona] + " = true");
                }
            }
            row++;
        }
    }
}
}