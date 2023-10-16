// Jeopardy game, Gameshow assignment ICS4U
// By Bala Venkataraman, October 19 2023
// challenge feature: Music
// Challenge feature: timer with interrupt
// challenge feature: previous game logs.



import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
class GameDataHandler {
    // Data to be used in the game
    String[] teamnamnelist = new String[5]; // display names of every team
    Integer[] teampointslist = new Integer[5]; // points that each team has
    String[] categoryNames = new String[5]; // names of each category that has been selected
    String[][] questiontexts = new String[][]; // question texts of each category amount
    Boolean [][] answered;
    private static List<String> getAllFiles(File curDir) {
        // reads all the files in a directory and returns a list of their file names
        List<String> filename = new ArrayList<>();;
        File[] filesList = curDir.listFiles();
        for(File f : filesList){
            if(f.isFile()){
                String name = f.getName();
                if(name.substring(name.length() - 4).equals(".txt")){
                    filename.add(name.substring(0,name.length() - 4));
                }
            }
        }
        return filename;
    }

    public void setCategories(String[] Categorynames, String[] TeamNames){
        this.categoryNames = Categorynames;
        return;
    }
    public String renderCategoryDisplay (){

        String renderedDisp = "";
        for (String categoryName : this.categoryNames) {
            renderedDisp += categoryName;
            renderedDisp += " ";
        }
        renderedDisp += "\n";
        for (int m = 0; m < 5; m++){
            for (int i = 0; i < this.categoryNames.length; i++){
                if ( !answered[m][i] ){
                    renderedDisp += (m+1)*100;
                    renderedDisp += "  ";
                }
                else{
                    renderedDisp += "     ";
                }
            }
            renderedDisp += "\n";
        }

        return renderedDisp;
    }

}



public class Gameshow {
    String[] categories = new String[5];

    public static void main(String[] args) {


        System.out.println();
    }
}


