// Jeopardy game, Game show assignment ICS4U
// By Bala Venkataraman, October 23, 2023
// Challenge feature: timer with interrupt
// challenge feature: previous game logs.



import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.*;
import java.util.List;

class GameDataHandler {
    // This class handles the data that is used in a game
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

    public static List<String> getAllFiles(String currPath) { // lists categories in the working directory
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
        catch (Exception e){ // when there is an error reading the directory
            System.out.println("No valid question files could be found ");
            return new ArrayList<String>();
        }
        }

    public void setTeamNames(String teamname){ // Initializes teams in the game
        for(int i = 0; i < this.numteams; i++){
            if(this.teamnamnelist[i].equals(teamname)){
                System.out.println("The name is taken. Please enter a different name");
                return;
            }
        }
        this.teamnamnelist[numteams] = teamname; // sets the name and score
        this.teampointslist[numteams] = 0;
        this.numteams += 1;
    }
    public boolean verifyCategory(int categorynumber){ // checks if a category is one of the categories in the game, and if there are questions remaining in it.
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
    public boolean verifyAmount(int categorynumber, int amount){ // verifies if a particular question is not taken.
        categorynumber -= 1;
        try {
            return !this.answered[categorynumber][((amount / 100) - 1)]; // checks if the problem has been answered and returns true if it is unanswered
            }
        catch (Exception e){ // in case of index error
            return false;
        }
    }
    public boolean verifyAnswer(int categorynumber, int amount, String playeranswer){ // checks the answer
        categorynumber -= 1;
        for(String possibleanswer: this.answers[categorynumber][((amount / 100) - 1)]){ // checks if the answer matches any possible answer (case sensitivesly)
            if(playeranswer.equals(possibleanswer)){
                this.answered[categorynumber][((amount / 100) - 1)] = true;
                answeredquestions += 1;

                return true;

            }
        }
        return false;
    }
    public void surrenderProblem(int categorynumber, int amount){ // displays the answer to a problem if players can't answer it.
        categorynumber -= 1;
        this.answered[categorynumber][((amount / 100) - 1)] = true; // marks the problem as answered so it cant be picked again.
        answeredquestions += 1;
        for(String possibleanswer: this.answers[categorynumber][((amount / 100) - 1)]){ // prints out all possible answers

            System.out.print(possibleanswer + "  ");
        }
        System.out.print("\n");
    }

        public void setCategories(String currPath, String inCategoryname) {  // sets the categories that the game will use
        String str;
        String opcode;
        this.categoryNames[this.numcategories] = inCategoryname;
        int thisindex = 0;

        for(int i = 0; i < this.numcategories; i++){
            if(this.categoryNames[i].equals(inCategoryname)){ // makes sure you dont pick the same category twice.
                System.out.println("This category is already loaded");
                return;
            }
        }



        try { // checks if the file exists, and reads the data if it does
            RandomAccessFile file = new RandomAccessFile(currPath + "/" + inCategoryname + ".txt", "r"); // This program was developed on a Unix system (Mac OS), so the path separator is a / On a Windows system, it should be changed to \.


            while (true) { // reads all lines of the problem set file
                str = file.readLine();
                if (str.substring(0,3).equals("END")) { // quit if file has finished

                    break;
                }

                opcode = str.substring(0, 5);
                if(opcode.equals("$QUES")){ // adds to question or answer lists based on opcode
                    this.questiontexts[this.numcategories][thisindex] = str.substring(6, str.length());
                }
                else if (opcode.equals("$ANSW")){
                    this.answers[this.numcategories][thisindex] = new ArrayList<String>();

                    String[] posans = str.substring(6,str.length()).split(";"); // takes possible answers as a list
                    for(int i = 0; i < posans.length; i++){

                        this.answers[this.numcategories][thisindex].add(posans[i]); // saves possible answer for that question

                    }
                    this.answered[this.numcategories][thisindex] = false;
                    thisindex += 1; // updates number of questions scanned.
                    totalquestions += 1;

                }
            }

        }
        catch (Exception e) {
            System.out.println("There was an error reading the file. The file is likely in incorrect format for this program. Please verify the files in the question directory.");
            return;
        }

        while(thisindex < 5){
            this.answered[numcategories][thisindex] = true; // if there are any less than 5 questions, the remaining ones are flagged as answered so they can't be picked in game.
            thisindex += 1;
        }
        numcategories += 1; // updates categories

    }



    public String renderDisplay (){ // this renders the state of the game to the screen. Direct X GPU acceleration not implemented yet.
        String renderedDisp = "GAME BOARD STATUS\n ";
        int rightgap;
        int leftgap;
        for (int i = 0; i < this.numcategories; i++) { // list categories in the match
            renderedDisp += this.categoryNames[i];
            renderedDisp += " ";
        }
        renderedDisp += "\n";
        for (int m = 0; m < 5; m++){
            for (int i = 0; i < this.numcategories; i++){ // lists prize amounts if the question has not been answered


                if ( !this.answered[i][m] ){ // renders prize amount if problem is not answered yet
                    rightgap = Math.max(0, (int) Math.floor( (this.categoryNames[i].length() - 3) / 2.0 )); // aligns the prize amount in teh center and keeps appropriate spacing.
                    leftgap = Math.max(0, (int) Math.ceil( (this.categoryNames[i].length() - 3) / 2.0 ) + 1);
                    renderedDisp += " ".repeat(leftgap);
                    renderedDisp += (m + 1) * 100;
                    renderedDisp += " ".repeat(rightgap);
                }
                else{
                    renderedDisp += " ".repeat(this.categoryNames[i].length() + 1); // renders blank if problem is marked as answered
                }
            }
            renderedDisp += "\n";
        }
        renderedDisp += "\nTEAM SCORE STATUS\n"; // displays the statuses of the teams scores

        for(int i = 0; i < this.numteams; i++){
            renderedDisp += "Team " + this.teamnamnelist[i] + " " + this.teampointslist[i] + "\n"; // writes each team name and score
        }
        renderedDisp += "\n";

        return renderedDisp;
    }

}



public class Gameshow {
    public static String thisans; // global variables to be used by the timer.
    public static boolean thisswitchmode = false;

    public static void main(String[] args) {
        Scanner userinp = new Scanner(System.in); // Defines scanner and variable for scanner input.
        String currentinput;
        String curDir = "/Users/balavenkataraman/Documents/academics/MidtermAssignment/src/questions"; // this is the working directory of the program. to update before submission.
        int option; //  variable for selected user option


            // Main Menu and rules screen.

            try{ // checks if a leaderboard history file has been created, which indicates the program has been run before
                RandomAccessFile file = new RandomAccessFile(curDir + "/playhistory.txt", "r"); // This program was developed on a Unix system (Mac OS), so the path separator is a / On a Windows system, it should be changed to \.
            }
            catch(Exception e){ // if the program has not been run before, displays the rules
                System.out.println("WELCOME TO JEOPARDY.");
                System.out.println("SINCE THIS IS THE FIRST TIME YOU ARE RUNNING THIS GAME, THE RULES ARE BEING DISPLAYED.");
                System.out.println("THIS GAME RUNS LIKE THE GAME SHOW JEOPARDY, WHERE TEAMS ANSWER A SET OF QUESTIONS IN DIFFERENT CATEGORIES.");
                System.out.println("IF THE TEAM WHOSE TURN IT IS TO ANSWER PROVIDES AN INCORRECT ANSWER, OR DOES NOT ANSWER IN TIME, THE CHANCE IS PASSED ONTO THE NEXT TEAM.");
                System.out.println("THE PROBLEM SET CATEGORIES STORED IN YOUR PROGRAM DATA DIRECTORY ARE DISPLAYED. THERE ARE TWO EXAMPLE PROBLEM SETS FOR NOW");
                System.out.println("YOU ARE ALSO GIVEN AN OPTION TO CREATE PROBLEM SETS IN ADDITIONAL CATEGORIES (after doing so, restart the game before being able to play that category)");
                System.out.println("PRESS ENTER TO CONTINUE TO MAIN MENU.");
                currentinput = userinp.nextLine();
                try { // create the leaderboard history file.
                    File lbObj1 = new File(curDir + "/playhistory.txt");
                    if(!lbObj1.createNewFile()){
                        throw new Exception("file not made");
                    }

                    FileWriter fr1 = new FileWriter(lbObj1, true);
                    fr1.write("\nJeopardy game result log\n\n");
                    fr1.close();


                }
                catch (Exception e2) {
                    System.out.print(e2);
                    System.out.println("An error occurred when using the working directory. Please make sure it has been set properly. .");
                    userinp.close();
                    return; // closes the program, since the working directory is to be set from the program itself.
                }

            }
            while (true) { // main game loop
                // display splash screen
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
                while(true) { // inputs option
                    currentinput = userinp.nextLine();
                    try {
                        option = Integer.parseInt(currentinput);
                        if(1 <= option && option <= 4){ // ends the loop if a valid input is found
                            break;
                        }
                    }
                    catch (Exception e) { // in case if not a valid number

                    }
                System.out.println("Input format is invalid. please enter a number between 1 and 4.");
                    }
                if(option == 4){ // end the program
                    userinp.close(); // close the scanner before ending the program
                    return;
                }
                else if (option == 2){ // write new question category
                    File questionobj;
                    while(true){ // reads input, and proceeeds when it is valid
                        System.out.println("You are now creating a new category of questions. Enter the name of the category (however, it can't be named playerhistory, done or $END$ since those are reserved operation codes.) "); //
                        currentinput = userinp.nextLine();
                        if(!currentinput.equals("$END$") && !currentinput.equals("playhistory") && !currentinput.equals("done")){ // verifies that the name is not an opcode
                            questionobj = new File(curDir + "/" + currentinput +  ".txt");
                            try {
                                if (questionobj.createNewFile()) { // verifies that the name is not taken and that the file has been made.
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
                        // reads questions and answers and writes into file.
                        System.out.println("To write the question file, enter the question text and answers when prompted. If there is more than one possible answer, enter them all separated by semicolons. To end without a full 5 questions, type $END$ when prompted for the question.");
                        for(int i = 0; i < 5; i++){
                            System.out.print("Question " + (i+1) + ": " ); // prompts user for each question with an option to exit prematurely.
                            currentinput = userinp.nextLine();
                            if(currentinput.equals("$END$")){
                                break;
                            }
                            fr.write("$QUES " + currentinput + "\n");
                            System.out.print("Answers: "); // prompts user for possible answers.
                            currentinput = userinp.nextLine();
                            fr.write("$ANSW " + currentinput + "\n");
                        }
                        fr.write("END");
                        fr.close();
                    }
                    catch (Exception e){ // in case the file can't be read
                        System.out.print(e);
                        System.out.println("Could not save file");
                    }

                }
            else if (option == 3){
                try {
                    RandomAccessFile file = new RandomAccessFile(curDir + "/playhistory.txt", "r"); // loads leaderboard file.
                    while(true){ // reads every line of playhistory file and prints, breaks loop when file ends
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
            else if(option == 1) {// play a match
            GameDataHandler gdh = new GameDataHandler(); // initialize game variables. declared here instead of at start of main function to separate variables of different game modes.
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

            while (true) { // prompts user for up to 5 valid category names.
                boolean valid = false;
                currentinput = userinp.nextLine();
                if (currentinput.equals("done")) { // ends if user has selected more than one category.
                    if (gdh.numcategories >= 1) {
                        break;
                    } else {
                        System.out.println("You need at least one category to play this game");
                    }
                }
                for (String s : qFilesList) { // makes sure the category is one of the files.
                    if (s.equals(currentinput)) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    System.out.println("That was not any of the categories. Please enter a category");
                } else if (gdh.numcategories == 5) { // when all categories are filled.
                    System.out.println("There is a maximum of 5 categories");
                    break;
                } else {
                    gdh.setCategories(curDir, currentinput); // loads questions and answers of the category into the data handler.
                }
            }

            System.out.println("Similar to the categories, you must now enter the names for the teams. there are maximum 5 teams. enter \"done\" when you are done."); // prompts the user for teams similar to categories. Makes sure there is at least 2 and at most 5, and no team name is entered twice.
            while (true) {
                currentinput = userinp.nextLine();
                if (currentinput.equals("done")) {
                    if (gdh.numteams >= 2) {
                        break;
                    } else {
                        System.out.println("You need at least two teams to play this game. It becomes lonely otherwise.");
                    }
                } else if (gdh.numteams == 5) {
                    System.out.println("There is a maximum of 5 teams");
                    break;
                } else {
                    gdh.setTeamNames(currentinput); // initializes the variable for the team.
                }
            }

            while (true) {
                System.out.println(gdh.renderDisplay()); // displays board and scores
                System.out.println("It is currently the turn of team " + gdh.teamnamnelist[thisturn % gdh.numteams]);

                System.out.println("Enter the number of the category you want to play (if you want to quit the game, type in $END$) "); //prompts user for and validates category and amount of question to answer. Makes sure the category exists, and the question with that amount is not taken.
                while (true) {
                    currentinput = userinp.nextLine();
                    if (currentinput.equals("$END$")) { // gives a chance to exit the game prematurely, but still decide winning team.
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
                stealmode = false; // determines whether the point goes to a different team, and which team it goes to
                stealteam = thisturn;

                System.out.println("\n\n" + gdh.questiontexts[currentcategoryindex - 1][(currentamountindex / 100) - 1]); // prints question statement.

                while (true) {
                    if (stealmode && stealteam % gdh.numteams == thisturn % gdh.numteams) { // display answers if the steal has gone all the way around and every team has failed
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
                                thisswitchmode = true; // determines that the answering team is switched, for a steal.
                            }
                        }
                    }, 10 * 1000);  // interrupts input within 10 seconds.
                    System.out.println("Enter your answer: "); // promots for answer
                    thisans = userinp.nextLine();
                    timer.cancel(); // cancels timer only if question is answered

                    if (!thisswitchmode) { // in case the problem was not timed out.
                        if (gdh.verifyAnswer(currentcategoryindex, currentamountindex, thisans)) { // provides points to the team that answered it, if answer is correct
                            if (stealmode) {
                                gdh.teampointslist[stealteam % gdh.numteams] += currentamountindex;
                            } else {
                                gdh.teampointslist[thisturn % gdh.numteams] += currentamountindex;
                            }

                            System.out.println("Congratulations, the answer is correct");

                            break;
                        } else {
                            System.out.println("Incorrect answer. "); // sets problem for steal in case problem is incorrectly answered.
                            thisswitchmode = true;
                        }
                    }

                    if (thisswitchmode) {

                        stealmode = true; // sets up team that will attempt the question next.
                        stealteam += 1;
                        if (stealmode && stealteam % gdh.numteams != thisturn % gdh.numteams) {

                            System.out.println("Team " + gdh.teamnamnelist[stealteam % gdh.numteams] + " can attempt the question now.");
                        }
                    }


                }
                thisturn += 1; // increases the turn in the match, giving the next team chance to answer.
                if (gdh.answeredquestions == gdh.totalquestions) { // ends if every question is answered.
                    break;
                }

                }
                System.out.println("THE GAME HAS ENDED!!"); // displays final standings
                System.out.println("FINAL STANDINGS ARE AS FOLLOWS \n\n");
                currentinput = gdh.renderDisplay();
                System.out.println(currentinput);

                try { // updates leaderboard file with new game statistics.

                    File lbObj = new File(curDir + "/playhistory.txt");
                    FileWriter fr = new FileWriter(lbObj, true);
                    fr.write("\nGAME LOG\n\n" + currentinput + "\n\n");
                    fr.close();

                }
                catch (Exception e){
                    System.out.print(e);
                    System.out.println("Could not save scores");
                }

                System.out.print("Press enter key to exit to main menu"); //end of game routine,  returns to main menu.
                currentinput = userinp.nextLine();

        }
        }

    }
}