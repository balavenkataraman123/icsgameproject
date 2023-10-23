// Jeopardy game, Game show assignment ICS4U
// By Bala Venkataraman, October 19, 2023
// challenge feature: Music
// Challenge feature: timer with interrupt
// challenge feature: previous game logs.
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.math.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

class GameDataHandler {
    // Data to be used in the game
    String[] teamnamnelist = new String[5]; // display names of every team
    int numteams = 0;
    int answeredquestions = 0;
    int totalquestions = 0;
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
                    if(name.substring(name.length() - 4).equals(".txt") && !name.equals("playhistory.txt")){
                        filename.add(name.substring(0,name.length() - 4));
                    }
                }
            }
            return filename;
        }
        catch (Exception e){
            System.out.println("No valid question files could be found ");
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
                answeredquestions += 1;

                return true;

            }
        }
        return false;
    }
    public void surrenderProblem(int categorynumber, int amount){
        categorynumber -= 1;
        this.answered[categorynumber][((amount / 100) - 1)] = true;
        answeredquestions += 1;
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
                    totalquestions += 1;

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
        Scanner userinp = new Scanner(System.in);
        String currentinput;
        String curDir = "/home/bala/Documents/school/g12/icsgameproject/src/questions"; // this is the working directory of the program.
        int option;



            // Main Menu and rules screen.

            try{
                RandomAccessFile file = new RandomAccessFile(curDir + "/playhistory.txt", "r"); // This program was developed on a Unix system (Mac OS), so the path separator is a / On a Windows system, it should be changed to \.
            }
            catch(Exception e){
                System.out.println(e);
                System.out.println("WELCOME TO JEOPARDY.");
                System.out.println("SINCE THIS IS THE FIRST TIME YOU ARE RUNNING THIS GAME, THE RULES ARE BEING DISPLAYED.");
                System.out.println("THIS GAME RUNS LIKE THE GAME SHOW JEOPARDY, WHERE TEAMS ANSWER A SET OF QUESTIONS IN DIFFERENT CATEGORIES.");
                System.out.println("IF THE TEAM WHOSE TURN IT IS TO ANSWER PROVIDES AN INCORRECT ANSWER, OR DOES NOT ANSWER IN TIME, THE CHANCE IS PASSED ONTO THE NEXT TEAM.");
                System.out.println("THE PROBLEM SET CATEGORIES STORED IN YOUR PROGRAM DATA DIRECTORY ARE DISPLAYED. THERE ARE TWO EXAMPLE PROBLEM SETS FOR NOW");
                System.out.println("YOU ARE ALSO GIVEN AN OPTION TO CREATE PROBLEM SETS IN ADDITIONAL CATEGORIES (after doing so, restart the game before being able to play that category)");
                System.out.println("PRESS ENTER TO CONTINUE TO MAIN MENU.");
                currentinput = userinp.nextLine();
                try {
                    File lbObj1 = new File(curDir + "/playhistory.txt");
                    if(!lbObj1.createNewFile()){
                        throw new Exception("file not made");
                    }

                    FileWriter fr1 = new FileWriter(lbObj1, true);
                    fr1.write("\nJeopardy game result log\n\n");
                    fr1.close();


                }
                catch (Exception ez) {
                    System.out.print(ez);
                    System.out.println("An error occurred when using the working directory. Please make sure it has been set properly. .");
                    userinp.close();
                    return;
                }

            }
            while (true) {

            System.out.println("    /$$$$$                                                         /$$                           /$$$$$$$  /$$$$$$$$ /$$        /$$$$$$   /$$$$$$  /$$$$$$$  /$$$$$$$$ /$$$$$$$ \n" +
                    "   |__  $$                                                        | $$                          | $$__  $$| $$_____/| $$       /$$__  $$ /$$__  $$| $$__  $$| $$_____/| $$__  $$\n" +
                    "      | $$  /$$$$$$   /$$$$$$   /$$$$$$   /$$$$$$   /$$$$$$   /$$$$$$$ /$$   /$$       /$$      | $$  \\ $$| $$      | $$      | $$  \\ $$| $$  \\ $$| $$  \\ $$| $$      | $$  \\ $$\n" +
                    "      | $$ /$$__  $$ /$$__  $$ /$$__  $$ |____  $$ /$$__  $$ /$$__  $$| $$  | $$      |__/      | $$$$$$$/| $$$$$   | $$      | $$  | $$| $$$$$$$$| $$  | $$| $$$$$   | $$  | $$\n" +
                    " /$$  | $$| $$$$$$$$| $$  \\ $$| $$  \\ $$  /$$$$$$$| $$  \\__/| $$  | $$| $$  | $$                | $$__  $$| $$__/   | $$      | $$  | $$| $$__  $$| $$  | $$| $$__/   | $$  | $$\n" +
                    "| $$  | $$| $$_____/| $$  | $$| $$  | $$ /$$__  $$| $$      | $$  | $$| $$  | $$       /$$      | $$  \\ $$| $$      | $$      | $$  | $$| $$  | $$| $$  | $$| $$      | $$  | $$\n" +
                    "|  $$$$$$/|  $$$$$$$|  $$$$$$/| $$$$$$$/|  $$$$$$$| $$      |  $$$$$$$|  $$$$$$$      |__/      | $$  | $$| $$$$$$$$| $$$$$$$$|  $$$$$$/| $$  | $$| $$$$$$$/| $$$$$$$$| $$$$$$$/\n" +
                    " \\______/  \\_______/ \\______/ | $$____/  \\_______/|__/       \\_______/ \\____  $$                |__/  |__/|________/|________/ \\______/ |__/  |__/|_______/ |________/|_______/ \n" +
                    "                              | $$                                     /$$  | $$                                                                                                \n" +
                    "                              | $$                                    |  $$$$$$/                                                                                                \n" +
                    "                              |__/                                     \\______/                                                                                                 ");
            System.out.println("To play the game, press 1. To create a problem set, press 2. To display past scores, press 3. To exit the game, press 4.");
            while(true) {
                currentinput = userinp.nextLine();
                try {
                    option = Integer.parseInt(currentinput);
                    if(1 <= option && option <= 4){
                        break;
                    }
                }
                catch (Exception e) {

                }
                System.out.println("Input format is invalid. please enter a number between 1 and 4.");
            }
            if(option == 4){
                userinp.close();
                break;
            }
            else if (option == 2){
                File questionobj;
                while(true){
                    System.out.println("You are now creating a new category of questions. Enter the name of the category (however, it can't be named playerhistory or $END$) ");

                    currentinput = userinp.nextLine();
                    if(!currentinput.equals("$END$") && !currentinput.equals("playhistory")){
                        questionobj = new File(curDir + "/" + currentinput +  ".txt");
                        try {
                            if (questionobj.createNewFile()) {
                                break;
                            }
                            else{
                                System.out.println("That category already exists");
                            }
                        }
                        catch(Exception e){
                            System.out.println("There was an error writing the file.");
                        }
                    }


                }
                try {
                    FileWriter fr = new FileWriter(questionobj, true);

                    System.out.println("To write the question file, enter the question text and answers when prompted. If there is more than one possible answer, enter them all separated by semicolons. To end without a full 5 questions, type $END$ when prompted for the question.");
                    for(int i = 0; i < 5; i++){
                        System.out.print("Question " + (i+1) + ": " );
                        currentinput = userinp.nextLine();
                        if(currentinput.equals("$END$")){
                            break;
                        }
                        fr.write("$QUES " + currentinput + "\n");
                        System.out.print("Answers: ");
                        currentinput = userinp.nextLine();
                        fr.write("ANSW " + currentinput + "\n");
                    }
                    fr.write("END");
                    fr.close();
                }
                catch (Exception e){
                    System.out.print(e);
                    System.out.println("Could not save scores");
                }

            }
            else if (option == 3){
                try {
                    RandomAccessFile file = new RandomAccessFile(curDir + "/playhistory.txt", "r");
                    while(true){
                        String a = file.readLine();
                        if(a == null){
                            break;
                        }
                        System.out.println(a);
                    }

                }
                catch(Exception e){
                    System.out.println("Could not read leaderboard file.");
                }
                System.out.println("Press enter to continue to the main menu");
                currentinput = userinp.nextLine();


            }
            else if(option == 1) {
            // game mode

            GameDataHandler gdh = new GameDataHandler();
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

            while (true) {
                boolean valid = false;
                currentinput = userinp.nextLine();
                if (currentinput.equals("done")) {
                    if (gdh.numcategories >= 1) {
                        break;
                    } else {
                        System.out.println("You need at least one category to play this game");
                    }
                }
                for (String s : qFilesList) {
                    if (s.equals(currentinput)) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    System.out.println("That was not any of the categories. Please enter a category");
                } else if (gdh.numcategories == 5) {
                    System.out.println("There is a maximum of 5 categories");
                    break;
                } else {
                    gdh.setCategories(curDir, currentinput);
                }
            }

            System.out.println("Similar to the categories, you must now enter the names for the teams. there are maximum 5 teams.");
            while (true) {
                currentinput = userinp.nextLine();
                if (currentinput.equals("done")) {
                    if (gdh.numteams >= 2) {
                        break;
                    } else {
                        System.out.println("You need at least two teams to play this game");
                    }
                } else if (gdh.numteams == 5) {
                    System.out.println("There is a maximum of 5 teams");
                    break;
                } else {
                    gdh.setTeamNames(currentinput);
                }
            }

            while (true) {
                System.out.println(gdh.renderDisplay());
                System.out.println("It is currently the turn of team " + gdh.teamnamnelist[thisturn % gdh.numteams]);

                System.out.println("Enter the number of the category you want to play (if you want to quit the game, type in $END$) ");
                while (true) {
                    currentinput = userinp.nextLine();
                    if (currentinput.equals("$END$")) {
                        killGameLoop = true;
                        break;
                    }

                    try {
                        numberinput = Integer.parseInt(currentinput);
                        if (gdh.verifyCategory(numberinput)) {
                            currentcategoryindex = numberinput;
                            break;
                        } else {
                            System.out.println("The category number you selected has no answerable questions.");
                        }

                    } catch (Exception e) {
                        System.out.println("Input format is invalid. please enter a number.");
                    }
                }
                if (killGameLoop) {
                    break;
                }

                System.out.println("Enter the money amount");
                while (true) {
                    currentinput = userinp.nextLine();
                    try {
                        numberinput = Integer.parseInt(currentinput);
                        if (gdh.verifyAmount(currentcategoryindex, numberinput)) {
                            currentamountindex = numberinput;
                            break;
                        } else {
                            System.out.println("This question in the category is not answerable");
                        }

                    } catch (Exception e) {
                        System.out.println("Input format is invalid. Please enter a number (100,200,300,400,500)");
                    }
                }
                stealmode = false;
                stealteam = thisturn;

                System.out.println("\n\n" + gdh.questiontexts[currentcategoryindex - 1][(currentamountindex / 100) - 1]);

                while (true) {
                    if (stealmode && stealteam % gdh.numteams == thisturn % gdh.numteams) {
                        System.out.println("Nobody could solve the problem");
                        System.out.println("Examples of correct answers are : ");
                        gdh.surrenderProblem(currentcategoryindex, currentamountindex);
                        break;
                    }

                    thisans = "";
                    thisswitchmode = false;

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            if (thisans.equals("")) {
                                System.out.println("Answer has not been provided on time. Press enter.");
                                thisswitchmode = true;
                            }
                        }
                    }, 10 * 1000);
                    System.out.println("Enter your answer: ");
                    thisans = userinp.nextLine();
                    timer.cancel();

                    if (!thisswitchmode) {
                        if (gdh.verifyAnswer(currentcategoryindex, currentamountindex, thisans)) {
                            if (stealmode) {
                                gdh.teampointslist[stealteam % gdh.numteams] += currentamountindex;
                            } else {
                                gdh.teampointslist[thisturn % gdh.numteams] += currentamountindex;
                            }

                            System.out.println("Congratulations, the answer is correct");

                            break;
                        } else {
                            System.out.println("Incorrect answer. ");
                            thisswitchmode = true;
                        }
                    }

                    if (thisswitchmode) {

                        stealmode = true;
                        stealteam += 1;
                        if (stealmode && stealteam % gdh.numteams != thisturn % gdh.numteams) {

                            System.out.println("Team " + gdh.teamnamnelist[stealteam % gdh.numteams] + " can attempt the question now.");
                        }
                    }


                }
                thisturn += 1;
                if (gdh.answeredquestions == gdh.totalquestions) {
                    break;
                }

                }
                System.out.println("THE GAME HAS ENDED!!");
                System.out.println("FINAL STANDINGS ARE AS FOLLOWS \n\n");
                currentinput = gdh.renderDisplay();
                System.out.println(currentinput);

                try {

                    File lbObj = new File(curDir + "/playhistory.txt");
                    FileWriter fr = new FileWriter(lbObj, true);
                    fr.write("\nGAME LOG\n\n" + currentinput + "\n\n");
                    fr.close();

                }
                catch (Exception e){
                    System.out.print(e);
                    System.out.println("Could not save scores");
                }

                System.out.print("Press any key to exit to main menu");
                currentinput = userinp.nextLine();





        }
        }

    }
}



