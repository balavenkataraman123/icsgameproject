import java.util.ArrayList;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
class GameDataHandler {
    // Data to be used in the game
    String[] teamnamnelist; // display names of every team
    Integer[] teampointslist; // points that each team has
    String[] categoryNames; // names of each category that has been selected
    String[][] questiontexts; // question texts of each category amount
    Boolean [][] answered;

    public static String[] getCategoriesList(String folderpath){
        String[] Categorynames = new String[] {"I", "have", "a", "massive", "cock"}; // this is a place holder. PLEASE REMOVE BEFORE SUBMISSION
        return Categorynames;
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
    public static void main(String[] args) {

    }
}


