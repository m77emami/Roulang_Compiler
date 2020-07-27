package ir.sbu.ac.Parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private TableElement[][] parseTable;
    private int firstNode;
    private String[] headers;           //headers are the set of Terminals and Non-Terminals in the language.

    public Parser(String nptPath) {
        //reading .npt file
        try {
            Scanner npt = new Scanner(new FileInputStream(nptPath));

            String[] rowCol = npt.nextLine().split(" ");
            int rows = Integer.parseInt(rowCol[0]), columns = Integer.parseInt(rowCol[1]);
            parseTable = new TableElement[rows][columns];

            firstNode = Integer.parseInt(npt.nextLine());

            headers = npt.nextLine().split(",");

            //reading parse table from file
            for (int i = 0; i < rows; i++) {
                String[] elements = npt.nextLine().split(",");

                for (int j = 0; j < columns; j++) {
                    String element = elements[j];
                    String[] tmp = element.split(" ");

                    Action action = Action.values()[Integer.parseInt(tmp[0])];
                    int nextNode = Integer.parseInt(tmp[1]);
                    String semantic = tmp[2];

                    parseTable[i][j] = new TableElement(action, nextNode, semantic);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(".npt file not found.");
        }
    }
}
