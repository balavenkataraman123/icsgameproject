// Jeopardy game, Game show assignment ICS4U
// By Bala Venkataraman, October 19, 2023
// challenge feature: Music
// Challenge feature: timer with interrupt
// challenge feature: previous game logs.
import java.io.RandomAccessFile;
import java.io.File;
import java.util.*;
import java.util.List;
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
    List<String> [][] answers = new List[5][5]; // question texts of each category amount
    Boolean [][] answered = new Boolean[5][5] ;

    public static List<String> getAllFiles(String currPath) {
        File curDir =  new File(currPath);
        // reads all the files in a directory and returns a list of their file names
        List<String> filename = new ArrayList<>();
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

    public int setCategories(String currPath, String inCategoryname) {
        String str;
        String opcode;
        this.categoryNames[numcategories] = inCategoryname;
        int thisindex = 0;

        try {
            RandomAccessFile file = new RandomAccessFile(currPath + "/" + inCategoryname + ".txt", "r"); // This program was developed on a Unix system (Mac OS), so the path separator is a / On a Windows system, it should be changed to \.


            while (true) { // reads all lines of the problem set file
                str = file.readLine();
                if (str.substring(0,3).equals("END")) {

                    break;
                }

                opcode = str.substring(0, 5);
                if(opcode.equals("$QUES")){
                    questiontexts[numcategories][thisindex] = str.substring(6, str.length());
                }
                else if (opcode.equals("$ANSW")){
                    answers[numcategories][thisindex] = new ArrayList<String>();

                    String[] posans = str.substring(6,str.length()).split(";");
                    for(int i = 0; i < posans.length; i++){

                        answers[numcategories][thisindex].add(posans[i]);
                    }
                    this.answered[numcategories][thisindex] = false;
                    thisindex += 1;

                }
            }

        }
        catch (Exception e) {
            System.out.println("There was an error reading the file. The file is likely in incorrect format for this program. Please verify the files in the question directory.");
            return (1);
        }

        while(thisindex < 5){
            this.answered[numcategories][thisindex] = true;

            thisindex += 1;
        }
        numcategories += 1;
        return(0);

    }



    public String renderDisplay (){ // this renders the state of the game to the screen. Direct X GPU acceleration not implemented yet.

        String renderedDisp = " ";
        int rightgap;
        int leftgap;
        for (String categoryName : this.categoryNames) {
            renderedDisp += categoryName;
            renderedDisp += " ";
        }
        renderedDisp += "\n";
        for (int m = 0; m < 5; m++){
            for (int i = 0; i < this.numcategories; i++){


                if ( !this.answered[i][m] ){
                    rightgap = Math.max(0, (int) Math.floor( (this.categoryNames[i].length() - 3) / 2.0 ));
                    leftgap = Math.max(0, (int) Math.ceil( (this.categoryNames[i].length() - 3) / 2.0 ) + 1);
                    renderedDisp += " ".repeat(leftgap);
                    renderedDisp += (m + 1) * 100;
                    renderedDisp += " ".repeat(rightgap);
                }
                else{
                    renderedDisp += " ".repeat(this.categoryNames[i].length() + 1);
                }
            }
            renderedDisp += "\n";
        }

        return renderedDisp;
    }

}



public class Gameshow {
    public static String thisans;
    public static int thisswitchmode = 1;
    static TimerTask task = new TimerTask()
    {
        public void run()
        {
            if(thisans.equals(""))
            {
                System.out.println( "you input nothing. exit..." );
                thisswitchmode = 0;
            }
        }
    };
    static Timer timer = new Timer();

    String[] categories = new String[5];

    public static void main(String[] args) {
        GameDataHandler gdh = new GameDataHandler();
        Scanner userinp = new Scanner(System.in);
        String currentinput;
        String curDir;

        System.out.println("Enter the file path to the questions folder");
        curDir = userinp.nextLine();
        List<String> qFilesList = gdh.getAllFiles(curDir);
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
                int ret = gdh.setCategories(curDir, currentinput);
            }
        }
        thisans = "";
        thisswitchmode = 0;
        timer.schedule(task, 10*1000 );
        Scanner sc = new Scanner(System.in);
        thisans = userinp.nextLine();
        timer.cancel();

        System.out.println(gdh.renderDisplay());





    }
}


