// Jeopardy game, Game show assignment ICS4U
// By Bala Venkataraman, October 19, 2023
// challenge feature: Music
// Challenge feature: timer with interrupt
// challenge feature: previous game logs.
import java.io.RandomAccessFile;
import java.io.File;
import java.io.StreamCorruptedException;
import java.util.*;
import java.util.List;
import java.math.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

class GameDataHandler {
    // Data to be used in the game
    String[] teamnamnelist = new String[5]; // display names of every team
    int numteams = 0;
    Integer[] teampointslist = new Integer[5]; // points that each team has
    String[] categoryNames = new String[5]; // names of each category that has been selected
    int numcategories = 0;
    String[][] questiontexts = new String[5][5]; // question texts of each category amount
    List<String> [][] answers = new List[5][5]; // question texts of each category amount
    Boolean [][] answered = new Boolean[5][5] ;

    public static List<String> getAllFiles(String currPath) {
        try {
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
        catch (Exception e){
            System.out.println("There has been a problem reading the folder");
            return new ArrayList<String>();
        }
        }

    public void setTeamNames(String teamname){
        for(int i = 0; i < this.numteams; i++){
            if(this.teamnamnelist[i].equals(teamname)){
                System.out.println("The name is taken. Please enter a different name");
                return;
            }
        }
        this.teamnamnelist[numteams] = teamname;
        this.teampointslist[numteams] = 0;
        this.numteams += 1;
    }
    public boolean verifyCategory(int categorynumber){
        categorynumber -= 1;
        if(categorynumber < this.numcategories){
            for(int i = 0; i < 5; i++){
                if(!this.answered[categorynumber][i]){
                    return true;
                }
            }

        }
        return false;
    }
    public boolean verifyAmount(int categorynumber, int amount){
        categorynumber -= 1;
        try {
            return !this.answered[categorynumber][((amount / 100) - 1)];
            }
        catch (Exception e){
            return false;
        }
    }
    public boolean verifyAnswer(int categorynumber, int amount, String playeranswer){
        categorynumber -= 1;
        for(String possibleanswer: this.answers[categorynumber][((amount / 100) - 1)]){
            if(playeranswer.equals(possibleanswer)){
                this.answered[categorynumber][((amount / 100) - 1)] = true;
                return true;

            }
        }
        return false;
    }
    public void surrenderProblem(int categorynumber, int amount){
        categorynumber -= 1;
        this.answered[categorynumber][((amount / 100) - 1)] = true;
        for(String possibleanswer: this.answers[categorynumber][((amount / 100) - 1)]){
            System.out.print(possibleanswer + "  ");
        }
        System.out.print("\n");
    }

        public void setCategories(String currPath, String inCategoryname) {
        String str;
        String opcode;
        this.categoryNames[this.numcategories] = inCategoryname;
        int thisindex = 0;

        for(int i = 0; i < this.numcategories; i++){
            if(this.categoryNames[i].equals(inCategoryname)){
                System.out.println("This category is already loaded");
                return;
            }
        }



        try {
            RandomAccessFile file = new RandomAccessFile(currPath + "/" + inCategoryname + ".txt", "r"); // This program was developed on a Unix system (Mac OS), so the path separator is a / On a Windows system, it should be changed to \.


            while (true) { // reads all lines of the problem set file
                str = file.readLine();
                if (str.substring(0,3).equals("END")) {

                    break;
                }

                opcode = str.substring(0, 5);
                if(opcode.equals("$QUES")){
                    this.questiontexts[this.numcategories][thisindex] = str.substring(6, str.length());
                }
                else if (opcode.equals("$ANSW")){
                    this.answers[this.numcategories][thisindex] = new ArrayList<String>();

                    String[] posans = str.substring(6,str.length()).split(";");
                    for(int i = 0; i < posans.length; i++){

                        this.answers[this.numcategories][thisindex].add(posans[i]);
                    }
                    this.answered[this.numcategories][thisindex] = false;
                    thisindex += 1;

                }
            }

        }
        catch (Exception e) {
            System.out.println("There was an error reading the file. The file is likely in incorrect format for this program. Please verify the files in the question directory.");
            return;
        }

        while(thisindex < 5){
            this.answered[numcategories][thisindex] = true;

            thisindex += 1;
        }
        numcategories += 1;

    }



    public String renderDisplay (){ // this renders the state of the game to the screen. Direct X GPU acceleration not implemented yet.
        String renderedDisp = "GAME BOARD STATUS\n ";
        int rightgap;
        int leftgap;
        for (int i = 0; i < this.numcategories; i++) {
            renderedDisp += this.categoryNames[i];
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
        renderedDisp += "\nTEAM SCORE STATUS\n";

        for(int i = 0; i < this.numteams; i++){
            renderedDisp += "Team " + this.teamnamnelist[i] + " " + this.teampointslist[i] + "\n";
        }
        renderedDisp += "\n";

        return renderedDisp;
    }

}



public class Gameshow {
    public static String thisans;
    public static boolean thisswitchmode = false;

    String[] categories = new String[5];

    public static void main(String[] args) {
        GameDataHandler gdh = new GameDataHandler();
        Scanner userinp = new Scanner(System.in);
        String currentinput;
        String curDir = "/home/bala/Documents/school/g12/icsgameproject/src/questions"; // this is the working directory of the program.
        int currentcategoryindex = 1;
        int currentamountindex;
        int thisturn = 0;
        int numberinput = 0;
        boolean stealmode = false;
        int stealteam = 0;
        boolean killGameLoop = false;


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
                gdh.setCategories(curDir, currentinput);
            }
        }

        System.out.println("Similar to the categories, you must now enter the names for the teams. there are maximum 5 teams.");
        while(true){
            currentinput = userinp.nextLine();
            if(currentinput.equals("done")){
                if(gdh.numteams >= 2){
                    break;
                }
                else{
                    System.out.println("You need at least two teams to play this game");
                }
            }
            else if (gdh.numteams == 5){
                System.out.println("There is a maximum of 5 teams");
                break;
            }
            else{
                gdh.setTeamNames(currentinput);
            }
        }

        while(true){
            System.out.println(gdh.renderDisplay());
            System.out.println("It is currently the turn of team " + gdh.teamnamnelist[thisturn % gdh.numteams]);

            System.out.println("Enter the number of the category you want to play (if you want to quit the game, type in $END$) ");
            while (true) {

                currentinput = userinp.nextLine();
                if(currentinput.equals("$END$")){
                    killGameLoop = true;
                    break;
                }

                try {
                    numberinput = Integer.parseInt(currentinput);
                    if(gdh.verifyCategory(numberinput)){
                        currentcategoryindex = numberinput;
                        break;
                    }
                    else{
                        System.out.println("The category number you selected has no answerable questions.");
                    }

                } catch (Exception e) {
                    System.out.println("Input format is invalid. please enter a number.");
                }
            }
            if(killGameLoop){
                break;
            }

            System.out.println("Enter the money amount");
            while (true) {
                currentinput = userinp.nextLine();
                try {
                    numberinput = Integer.parseInt(currentinput);
                    if(gdh.verifyAmount(currentcategoryindex, numberinput)){
                        currentamountindex = numberinput;
                        break;
                    }
                    else{
                        System.out.println("This question in the category is not answerable");
                    }

                } catch (Exception e) {
                    System.out.println("Input format is invalid. Please enter a number (100,200,300,400,500)");
                }
            }
            stealmode = false;
            stealteam = thisturn;

            System.out.println("\n\n" + gdh.questiontexts[currentcategoryindex - 1][(currentamountindex / 100) - 1]);

            while(true) {
                if(stealmode && stealteam % gdh.numteams == thisturn % gdh.numteams){
                    System.out.println("Nobody could solve the problem");
                    System.out.println("Examples of correct answers are : ");
                    gdh.surrenderProblem(currentcategoryindex, currentamountindex);
                }

                thisans = "";
                thisswitchmode = false;

                Timer timer = new Timer();
                timer.schedule(new TimerTask()
                {
                    public void run()
                    {
                        if(thisans.equals(""))
                        {
                            System.out.println( "Answer has not been provided on time. Press enter." );
                            thisswitchmode = true;
                        }
                    }
                }, 10 * 1000);
                System.out.println("Enter your answer: ");
                thisans = userinp.nextLine();
                timer.cancel();

                if (!thisswitchmode) {
                    if(gdh.verifyAnswer(currentcategoryindex, currentamountindex, thisans)) {
                        if (stealmode) {
                            gdh.teampointslist[stealteam % gdh.numteams] += currentamountindex;
                        } else {
                            gdh.teampointslist[thisturn % gdh.numteams] += currentamountindex;
                        }

                        System.out.println("Congratulations, the answer is correct");

                        break;
                    }
                    else{
                        System.out.println("Incorrect answer. ");
                        thisswitchmode = true;
                    }
                }

                if(thisswitchmode) {
                    stealmode = true;
                    stealteam += 1;
                    System.out.println("Team " + gdh.teamnamnelist[stealteam % gdh.numteams] + " can attempt the question now.");
                }


            }
            thisturn += 1;

        }
        System.out.println("THE GAME HAS ENDED!!");
        //thisans = "";
        //thisswitchmode = 0;
        //timer.schedule(task, 10*1000 );
        //thisans = userinp.nextLine();
        //timer.cancel();







    }
}


