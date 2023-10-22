// Jeopardy game, Game show assignment ICS4U
// By Bala Venkataraman, October 19, 2023
// challenge feature: Music
// Challenge feature: timer with interrupt
// challenge feature: previous game logs.
import java.io.RandomAccessFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.math.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

class GameDataHandler {
    // Data to be used in the game
    String[] teamnamnelist = new String[5]; // display names of every team
    Integer[] teampointslist = new Integer[5]; // points that each team has
    String[] categoryNames = new String[5]; // names of each category that has been selected
    int numcategories = 0;
    String[][] questiontexts = new String[5][5]; // question texts of each category amount
    Boolean [][] answered;
    public static List<String> getAllFiles(File curDir) {
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

    public int setCategories(String inCategoryname){

        this.categoryNames[numcategories] = inCategoryname;
        numcategories += 1;
        try {
            RandomAccessFile file = new RandomAccessFile(inCategoryname + ".txt", "r");
            return(0);

        }
        catch (Exception e) {
            System.out.println("There was an error reading the file. The file is likely in incorrect format for this program. Please verify the files in the question directory.");
            return(1);
        }


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
        GameDataHandler gdh = new GameDataHandler();
        Scanner userinp = new Scanner(System.in);
        String currentinput;
        File curDir;

        System.out.println("Enter the file path to the questions folder");
        currentinput = userinp.nextLine();
        curDir = new File(currentinput);

        List<String> qFilesList = GameDataHandler.getAllFiles(curDir);
        System.out.println("Loaded problem sets\n\nThe possible categories to play are: \n");
        for (String fname : qFilesList) {
            System.out.println(fname); // displays problem set filenames for selection
        }

        System.out.println("you can select up to 5 categories. Please enter the names of each category. The input is case sensitive. enter \"done\" when you are done.\n");

        while(true){
            boolean valid = false;
            currentinput = userinp.nextLine();
            if(currentinput.equals("done")){
                if(gdh.numcategories >= 1){
                    break;
                }
                else{
                    System.out.println("You need at least one category to play this game");
                }
            }
            for (String s : qFilesList) {
                if (s.equals(currentinput)) {
                    valid = true;
                    break;
                }
            }
            if(!valid){
                System.out.println("That was not any of the categories. Please enter a category");
            }
            else if (gdh.numcategories == 5){
                System.out.println("There is a maximum of 5 categories");
                break;
            }
            else{
                int ret = gdh.setCategories(currentinput);
            }
        }





    }
}


