package userInterface;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import solve.Solver;

public class Window {
	ArrayList<String> answers = new ArrayList<String>();
	ArrayList<String> floats = new ArrayList<String>();
	int tableRows = 0;
	JPanel centerPanel, southPanel;
	JFrame window;
	JMenuBar menuBar;
	JMenu fileMenu, optionsMenu, helpMenu;
	JMenuItem exitAction, exportToFileAction, clearTableAction, showHelpAction;
	JCheckBox rowsCheckBox,formulasCheckBox,solutionsCheckBox,floatsCheckBox;
	JCheckBoxMenuItem floatCheckButton;
	JButton enterButton, exportFileTextButton, exportButton;
	JTextField textField, exportFileTextField;
	JTable table;
	JScrollPane textScroll;
	String lastInput;
	DefaultTableModel model;
	JDialog exportOptionsDialog, fileChooserDialog;

	/**
	 * Constructs a window that is customized to solve math expressions and output them to a table.
	 */
	public Window(){
		window = new JFrame();
		initMenuBar();
		initTable();
		initPanel();
		addListeners();
		setWindowPreferences();
	}
	
	/**
	 * Handles setting all the preferences to building the window in regard to size, title, and default close operations.
	 * @Author Chris Moscoso
	 */
	private void setWindowPreferences(){
		window.setTitle("Formula Solver");
		window.setSize(750, 450);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Handles constructing and arranging all the components that makes up the menu bar.
	 * @Author Chris Moscoso
	 */
	private void initMenuBar(){
		//Construct the menu bar
		menuBar = new JMenuBar();
		
		//Set the menu bar to the window
		window.setJMenuBar(menuBar);
		
		// Define and add two drop down menu to the menu bar
        fileMenu = new JMenu("File");
        optionsMenu = new JMenu("Options");
        helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);
        
        // Create and add simple menu item to one of the drop down menu
        exportToFileAction = new JMenuItem("Export to .txt");
        exitAction = new JMenuItem("Exit");
        clearTableAction = new JMenuItem("Clear Answer History");
        showHelpAction = new JMenuItem("Show Help");
        
        // Create and add CheckButton as a menu item to one of the drop down
        // menu
        floatCheckButton = new JCheckBoxMenuItem("Show float");
        
        //Add the actions and buttons to their appropriate menus
        fileMenu.add(exportToFileAction);
        fileMenu.add(exitAction);
        optionsMenu.add(floatCheckButton);
        optionsMenu.add(clearTableAction);
        helpMenu.add(showHelpAction);
	}
	
	/**
	 * Handles constructing and arranging all the components that makes up the panel that contains all the windows content.
	 * @Author Chris Moscoso
	 */
	private void initPanel(){
	        centerPanel = new JPanel(); //Will contain a table for output
	        southPanel = new JPanel();  //Will contain a text field for input
	        
	        //Adds the text scroll that is constructed when the table is initialized.
	        centerPanel.setLayout(new BorderLayout());
	        centerPanel.add(textScroll);

	        //Construct the text field and button for the south panel
	        textField = new JTextField(30);
	        enterButton = new JButton("Enter");
	        southPanel.add(textField);
	        southPanel.add(enterButton);
	        
	        //Constructing the container that contains all the panels
	        Container content = window.getContentPane();
	        content.add(centerPanel, BorderLayout.CENTER);
	        content.add(southPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Handles constructing and initializing all the components required by our custom table to output the data.
	 * @Author Chris Moscoso
	 */
	private void initTable(){
		Object[][] initData = {{""+tableRows, "", ""}};
		String[] initColumns = {"Ans", "Formula", "Solution"};
		model = new DefaultTableModel(initData,initColumns);
        table = new JTable(model);
        table.setEnabled(false); //Disables the JTable from editing
        
        //Label Columns
        table.getColumnModel().getColumn(0).setHeaderValue("Ans");
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setHeaderValue("Formula");
        table.getColumnModel().getColumn(2).setHeaderValue("Solution");
        //Set text alignment for the columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.setValueAt("1", 0, 0);

        //Construct a JScrollPane that contains the JTable
        textScroll = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * Adds mouse click and keyboard listeners to their respective components.
	 * @Author Chris Moscoso
	 */
	private void addListeners(){
		exportToFileAction.addActionListener(new ClickListener());
		exitAction.addActionListener(new ClickListener()); 	
                floatCheckButton.addActionListener(new ClickListener());
                enterButton.addActionListener(new ClickListener());	 
                clearTableAction.addActionListener(new ClickListener());
                showHelpAction.addActionListener(new ClickListener());
                textField.addKeyListener(new KeyboardListener());
	}
	
	/**
	 * @param str a formula with no spaces.
	 * @return a longer formula with no spaces.
	 * @Author Chris Moscoso
	 */
	private String replaceAns(String str){
		while(str.contains("ans")){
			int ansNum = findAnswerNumber(str);
			if(ansNum == -1){
				try{
					while(str.contains("ans")){
						str = "(" + str.replaceFirst("ans", answers.get(tableRows - 1) + ")");
					}
				}catch(Exception e){
					throw new IllegalArgumentException("No answers exist yet.");
				}
			}else{
				try{
					str = "(" + str.replaceFirst("ans"+ansNum, answers.get(ansNum - 1) + ")");
				}catch(Exception e){
					throw new IllegalArgumentException("ans" + ansNum + " does not exist.");
				}
			}
		}
		return str;
	}
	
	/**
	 * @param str a string with no spaces
	 * @return the expression for the ans keyword. Null if there is none
	 * @Author Sarah Schruster
	 */
	private int findAnswerNumber(String str){
		String answer = "ans";
		for(int i = 0; i+3 <= str.length(); i++){
			String substring = str.substring(i,i+3);
			if(answer.equalsIgnoreCase(substring)){
				String number = "";				
				if(str.length() <= 3){
					return -1;
				}
				while(i+3 < str.length() && Character.isDigit(str.charAt(i+3))){
					number = number + str.charAt(i+3);
					i++;
					if(i + 3 >= str.length()){
						return Integer.parseInt(number);
					}
					if(!Character.isDigit(str.charAt(i+3))){
						return Integer.parseInt(number);
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * Clears all the data of the table. Deletes the Answer History by removing all elements in the answer arraylist.
	 * @Author Chris Moscoso
	 */
	private void clearTable(){
		for(int i = tableRows - 1; i >= 0; i--){
			if(i > 0){
				model.removeRow((i));
			}
			else{
				table.setValueAt("", 0, 1);
				table.setValueAt("", 0, 2);
			}
			tableRows = 0;
		}
		answers = new ArrayList<String>();
		floats = new ArrayList<String>();
	}
	
	/**
	 * @param a string with no spaces 
	 * @return a string with all sqrt replaced with 2rt.
	 */
	private String replaceSqrt(String str){
		for(int i = 0; i < str.length() - 2; i++){
			if(Character.isDigit(str.charAt(i)) && str.charAt(i + 1) == 's' ){
				throw new IllegalArgumentException("Need operation between " + str.charAt(i) + " and sqrt");
			}
		}
		while(str.contains("sq")){
			str = str.replaceFirst("sq", "2");
		}
		return str;
	}
	
	/**
	 * Takes the input String and manipulates it into a proper formula to be solved by the calculator and then outputs it 
	 * to the table.
	 * @Author Chris Moscoso
	 */
	private void enterFormula(){
		lastInput = textField.getText();
		lastInput = lastInput.toLowerCase();
		String solutionOutput = lastInput;
		
		if(solutionOutput.length() == 0){
			JOptionPane.showMessageDialog(window, "The textfield is blank", "Input Error", JOptionPane.ERROR_MESSAGE);
		}
		else if(checkIfValid(removeSpaces(lastInput))){
			solutionOutput = this.removeSpaces(solutionOutput);
			try{
				solutionOutput = this.replaceSqrt(solutionOutput);
			}catch(Exception e){
				JOptionPane.showMessageDialog(window, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try{
				solutionOutput = this.replaceAns(solutionOutput);
			}catch(Exception e){
				JOptionPane.showMessageDialog(window, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			solutionOutput = this.removeSpaces(solutionOutput);
			solutionOutput = this.fixSigns(solutionOutput);
			try{
				solutionOutput = this.addSpaces(solutionOutput);
			}catch(Exception e){
				JOptionPane.showMessageDialog(window, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try{
				solutionOutput = this.replaceRoots(solutionOutput);
			}catch(Exception e){
				JOptionPane.showMessageDialog(window, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			solutionOutput = this.removeSpaces(solutionOutput);
			try{
				solutionOutput = this.addSpaces(solutionOutput);
			}catch(Exception e){
				JOptionPane.showMessageDialog(window, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			solutionOutput = this.replaceUnicodeCharacters(solutionOutput);
			solutionOutput = this.parseImplicitMulitplication(solutionOutput);
			try{
				String floatOutput = "";
				try{
					floatOutput = ""+Solver.solveFloat(solutionOutput);
				}catch(Exception e){
					//Float solver failed. Most likely ArrayIndexOutOfBoundsException. Main solve won't work. but return it's input.
				}
				solutionOutput = Solver.solve(solutionOutput);
				solutionOutput = this.replaceNormalCharacters(solutionOutput);
				floats.add(floatOutput);
				answers.add(solutionOutput);
				if(tableRows == 0){
					table.setValueAt(lastInput, 0, 1);
					table.setValueAt(solutionOutput, 0, 2);
					tableRows++;				
				}else{
					model.insertRow(table.getRowCount(),new Object[]{++tableRows, lastInput, solutionOutput});
				}
				if(floatCheckButton.isSelected()){
					changeToFloats();
				}
				textField.setText("");
			}catch(ArrayIndexOutOfBoundsException e){
				JOptionPane.showMessageDialog(window, "Could not solve: " + solutionOutput + "\n" + e.getMessage(), "Unsolvable input", JOptionPane.ERROR_MESSAGE);
			}catch(Exception e){
				JOptionPane.showMessageDialog(window, "Could not solve: " + solutionOutput + "\n" + e.getMessage(), "Unsolvable input", JOptionPane.ERROR_MESSAGE);
			}
		}else{
			JOptionPane.showMessageDialog(window, "Formula contains invalid characters [" + lastInput + "]", "Input Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Checks if a string has any invalid characters and returns false if it does.
	 * @param a string without spaces that should represent a valid formula
	 * @return true if the string is a valid formula.
	 * @Author Chris Moscoso
	 */
	private boolean checkIfValid(String str){
		//Convert all allowable characters to '$'
		for(int i = 0; i < Main.allowableChars.length; i++){
			for(int j = 0; j < str.length(); j++){
				if(str.charAt(j) == Main.allowableChars[i]){
					str = str.replace(Main.allowableChars[i], '$');
				}
			}			
		}
		//Convert all allowable strings to "$"
		for(int i = 0; i < Main.allowableStrings.length; i++){
			while(str.contains(Main.allowableStrings[i])){
				//Check if the string is at the end 
				int position = str.indexOf(Main.allowableStrings[i]);
				if(position == str.length() - Main.allowableStrings[i].length()){
					//Just split string and add $ to the end
					str = str.substring(0, str.indexOf(Main.allowableStrings[i])) + "$";
				//String must be in the middle
				}else{
					//Split string and append a $ in between
					str = str.substring(0, position) + "$" + str.substring(position + Main.allowableStrings[i].length(), str.length());
				}
			}
		}
		//Check to see if there are any characters that are not '$'.
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) != '$'){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Puts a space appropriately between each operator.
	 * @param a formula with no spaces.
	 * @return a formula with proper spacing to be solved.
	 * @author Chris Moscoso
	 */
	private String addSpaces(String str){
		StringTokenizer tokenizer = new StringTokenizer(str, "+/*^()-"+Main.getHypen(), true);
		int tokens = tokenizer.countTokens();
		for(int i = 0; i < tokens; i++){			
			String currentToken = tokenizer.nextToken();
			if(i == 0){
				str = currentToken + " ";
			}
			else if(i < tokens - 1){
				str = str + currentToken + " ";
			}else{
				str = str + currentToken;
			}
		}
		//If the power for root comes from a parenthesis, the ')' must be attached to the 'r' (no space in between). 
		//This is required by changeRoots(String str);
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == ')'){
				if((i+1) < str.length() && str.charAt(i+2) == 'r'){
					str = str.substring(0,i+1) + str.substring(i+2);
				}
			}
		}
		//If the base of the root has parenthesis, the ':' must be attached to the '(' (no space in between).
		//This is required by changeRoots(String str);
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == ':'){
				if(!(i+2 >= str.length())) {
					if((i+1) < str.length() && (str.charAt(i+2) == '(' || str.charAt(i+2) == '-')){
						str = str.substring(0,i+1) + str.substring(i+2);
					}
				}
			}
		}
		//If the first character is a negative and followed by a number than don't put a space in between the number and negative.	
		if(str.charAt(0) == '-' || str.charAt(0) == '+'){
			try{
				if(Character.isDigit(str.charAt(2)) || str.charAt(2) == 'e' || str.charAt(2) == 'p' || str.charAt(2) == Main.getE() ||str.charAt(2) == Main.getPi()){
					str = str.substring(0,1) + str.substring(2);
				}
			}catch(StringIndexOutOfBoundsException e){
				throw new ArrayIndexOutOfBoundsException("Need expressions on both sides of: " + str.charAt(0));
			}			
		}
		//If a negative number is in front of an operator other than + or - than make the following number negative.
		for(int i = 2; i < str.length() - 1; i++){
			if(str.charAt(i) == '-' && (str.charAt(i-2) == '*' || str.charAt(i-2) == '/' || str.charAt(i-2) == '^' || str.charAt(i-2) == '(')){
				str = str.substring(0, i+1) + str.substring(i+2);
			}
		}
		return str;	
	}

	/**
	 * Erases all white spaces from the string that the user has entered.
	 * @param any formula 
	 * @return a string with no white spaces.
	 * @Author Chris Moscoso
	 */
	private String removeSpaces(String str){
		//Remove spaces.
		while(str.contains(" ")){
			int pos = str.indexOf(" ");
			str = str.substring(0,pos)+str.substring(pos+1,str.length());
		}
		//Remove tab spaces.
		while(str.contains("\t")){
			int pos = str.indexOf("\t");
			str = str.substring(0,pos)+str.substring(pos+1,str.length());
		}
		return str;
	}
	
	/**
	 * @param a string formula
	 * @return a string with unicode e replaced with e and unicode pi replace with p
	 * @Author Chris Moscoso
	 */
	private String replaceUnicodeCharacters(String str){
		str = str.replaceAll("" + Main.getE(), "e");
		str = str.replaceAll("pi", "p");
		str = str.replaceAll("" + Main.getPi(), "p");
		return str;
	}

	private String parseImplicitMulitplication(String str){
		str = removeSpaces(str);
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == 'p' || str.charAt(i) == 'e'){
				if(i == 0){
					if(Character.isDigit(str.charAt(i+1))){
						str = str.charAt(i) + "*" + str.substring(i+1);
					}
				}else if(i < str.length() - 2){
					if(Character.isDigit(str.charAt(i+1))){
						str = str.substring(0, i+1) + "*" + str.substring(i+1);
					}
					if(Character.isDigit(str.charAt(i-1))){
						str = str.substring(0,i) + "*" +str.substring(i);
					}
				}else{
					if(Character.isDigit(str.charAt(i-1))){
						str = str.substring(0,i) + "*" +str.substring(i);
					}
				}
			}
		}
		str = addSpaces(str);
		return str;
	}
	/**
	 * @param a string formula
	 * @return a string with e and p replaced with unicode e and unicode pi respectively.
	 * @Chris Moscoso
	 */
	private String replaceNormalCharacters(String str){
		str = str.replaceAll("e", ""+Main.getE());
		str = str.replaceAll("p", ""+Main.getPi());
		return str;
	}
	
	/**
	 * Replaces all rt: operators to base^1/root.
	 * @param a properly spaced formula.
	 * @return a string that is properly spaced with no roots only bases raised to a power using the ^ operator.
	 * @Author Justin Stauber (Modified by Chris Moscoso)
	 */
	String replaceRoots(String str){
		while(str.contains("rt:")){
			int pos = str.indexOf("rt:");
			int posBegin = pos - 1;
			int posEnd = pos + 3;
			String root = "";
			String base = "";
			if(str.charAt(posBegin) == ')'){
				int count = 1;
				posBegin--;
				for(; count > 0; posBegin--){
					if(posBegin < 0){
						throw new IllegalArgumentException("Missing begin-paren");
					}
					if(str.charAt(posBegin) == ')') count++;
					if(str.charAt(posBegin) == '(') count--;
				}
				root = str.substring(posBegin+1, pos);
			}
			else{
				while(posBegin >= 0 && Character.isDigit(str.charAt(posBegin))){
					root = str.charAt(posBegin--) + root;
				}
				posBegin++;
				if(posBegin >= 0 && str.charAt(posBegin) == '-'){
					root = '-'+root;
				}
			}
			if(str.charAt(posEnd) == '('){
				int count = 1;
				posEnd++;
				for(; count > 0; posEnd++){
					if(posEnd >= str.length()){
						throw new IllegalArgumentException("Missing end-paren");
					}
					if(str.charAt(posEnd) == '(') count++;
					if(str.charAt(posEnd) == ')') count--;
				}
				base = str.substring(pos + 3, posEnd);
			}
			else{
				if(str.charAt(posEnd) == ' '){
					throw new IllegalArgumentException("Formula is blank after 'rt:' symbol.");
				}else if(!Character.isDigit(str.charAt(posEnd)) && !(str.charAt(posEnd) == 'e') && !(str.charAt(posEnd) == 'p') && !(str.charAt(posEnd) == '-')){
					throw new IllegalArgumentException("'" + str.charAt(posEnd) + "' is not an acceptable character after a 'rt:' symbol.");
				}
				if(str.charAt(posEnd) == 'p' || str.charAt(posEnd) == '-'){
					base += " " + str.charAt(posEnd++) + str.charAt(posEnd++);
				}
				while(posEnd < str.length() && Character.isDigit(str.charAt(posEnd))){
					base += str.charAt(posEnd++);
				}
			}
			str = str.substring(0, posBegin) + base + " ^ ( 1 / " + root + " )" + str.substring(posEnd, str.length());
		}
		return str.trim();
	}
	
	/**
	 * Changes ++ and -- to +. Changes +- and -+ to -.
	 * @param a string with no spaces
	 * @return a string with no ++,+-,-+,--
	 * @Author Justin Stauber
	 */
	private String fixSigns(String str){
		while(str.contains("++")){
			str = str.replace("++", "+");
		}
		while(str.contains("+-")){
			str = str.replace("+-", "-");
		}
		while(str.contains("-+")){
			str = str.replace("-+", "-");
		}
		while(str.contains("--")){
			str = str.replace("--", "+");
		}
		return str;
	}
	
	/**
	 * Changes the expressions in the table to floats.
	 * @Author Chris Moscoso
	 */
	private void changeToFloats(){
		for(int i = 0; i < tableRows; i++){
			table.setValueAt(floats.get(i),i,2);
		}
	}
	
	/**
	 * Changes the floats in the table to expressions.
	 * @Author Chris Moscoso
	 */
	private void changeToExpressions(){
		for(int i = 0; i < tableRows; i++){
			table.setValueAt(answers.get(i),i,2);
		}
	}
	
	/**
	 * Displays a dialog with options to export the data in the table to a text file.
	 * @author Chris Moscoso
	 */
	private void showExportOptionsDialog(){
		if(exportOptionsDialog == null){
			//Construct Dialog Components
			exportOptionsDialog = new JDialog(window, "Export Options", false);
			Container content = exportOptionsDialog.getContentPane();
			rowsCheckBox = new JCheckBox("Include ans #");
			formulasCheckBox = new JCheckBox("Include formulas");
			solutionsCheckBox = new JCheckBox("Include solutions");
			floatsCheckBox = new JCheckBox("Include float");
			JLabel label = new JLabel("Save to");
			exportFileTextField = new JTextField(30);
			exportFileTextButton = new JButton("...");
	        exportFileTextButton.addActionListener(new ClickListener());
			exportButton = new JButton("Export");
			exportButton.addActionListener(new ClickListener());
			
			JPanel top = new JPanel(); //Writer options
			JPanel middle = new JPanel(); //File output
			JPanel bottom = new JPanel(); //Export Button
						
			//Set export default options.
			rowsCheckBox.setSelected(true);
			formulasCheckBox.setSelected(true);
			solutionsCheckBox.setSelected(true);
			floatsCheckBox.setSelected(true);
			
			//Add components to dialog.
			content.setLayout(new GridLayout(3,1));
			top.add(rowsCheckBox);
			top.add(formulasCheckBox);
			top.add(solutionsCheckBox);
			top.add(floatsCheckBox);
			middle.add(label);
			middle.add(exportFileTextField);
			middle.add(exportFileTextButton);
			bottom.add(exportButton);
			content.add(top);
			content.add(middle);
			content.add(bottom);
			exportOptionsDialog.pack();
		}
		exportOptionsDialog.setVisible(true);
	}
	
	/**
	 * Displays the file chooser to set the file path for the export text file.
	 */
	private void showFileChooser(){
		JDialog fileChooserDialog = new JDialog(exportOptionsDialog, "Export Options", false);
		fileChooserDialog.setSize(300,300);
		JFileChooser chooser = new JFileChooser();
		fileChooserDialog.add(chooser);
		int option = chooser.showSaveDialog(exportOptionsDialog);  
		if(option == JFileChooser.APPROVE_OPTION){  
			exportFileTextField.setText(chooser.getSelectedFile().toString());
			if(exportFileTextField.getText().indexOf(".txt") == exportFileTextField.getText().length() - 4){
				exportFileTextField.setText(exportFileTextField.getText().substring(0,exportFileTextField.getText().length() - 4));
			}
		}
	}
	
	/**
	 * Exports the table to a text file
	 * @param rows if true includes rows in the export data.
	 * @param formulas if true includes formulas in the export data.
	 * @param solutions if true includes solutions in the export data.
	 * @param floats if true includes floats in the export data.
	 */
	private void export(boolean rows, boolean formulas, boolean solutions, boolean floats){
		exportOptionsDialog.setVisible(false);
		String fileName = exportFileTextField.getText();
		File file = new File(fileName);
		PrintWriter writer = null;
		if(fileName.charAt(0) == '.'){
			JOptionPane.showMessageDialog(exportOptionsDialog, "File names can't begin with a '.'", "File name error", JOptionPane.ERROR_MESSAGE);
		}
		try {
			//If file exists it appends the data
			if(fileName.length() > 5 && fileName.charAt(fileName.length() - 4) == '.' && fileName.charAt(fileName.length() - 3) == 't' && fileName.charAt(fileName.length() - 2) == 'x' && fileName.charAt(fileName.length() - 1) == 't'){
				writer = new PrintWriter(new FileWriter((fileName), true));
			}else{
				writer = new PrintWriter(new FileWriter((fileName + ".txt"), true));
			}
			if(rows || formulas || solutions || floats){
				if(rows){
					if(rows){
						writer.printf("%-6s|", "ROW");
					}
					if(formulas){
						writer.printf("%50s|", "FORMULA");
					}
					if(solutions){
						writer.printf("%50s|", "SOLUTION");
					}
					if(floats){
						writer.printf("%25s|", "FLOAT");
					}
					writer.println();
					for(int i = 0; i <= 133; i++){
						writer.print("*");
					}
					writer.println();
				}
				for(int i = 0; i < tableRows; i++){
					if(rows){
						writer.printf("%-6s|", i+1);
					}
					if(formulas){
						writer.printf("%50s|", this.replaceUnicodeCharacters(""+table.getValueAt(i, 1)));
					}
					if(solutions){						
						writer.printf("%50s|", this.replaceUnicodeCharacters(answers.get(i)));
					}
					if(floats){
						writer.printf("%25s|", this.floats.get(i));
					}
					writer.println();				
				}
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JOptionPane.showMessageDialog(window, "Export Complete.\nExported to " + file.getAbsolutePath() );
		if(fileName.length() > 5 && fileName.charAt(fileName.length() - 4) == '.' && fileName.charAt(fileName.length() - 3) == 't' && fileName.charAt(fileName.length() - 2) == 'x' && fileName.charAt(fileName.length() - 1) == 't'){
			file.renameTo(new File(fileName + ".txt"));
		}
	}
	
	
	/**
	 * This class is responsible for handling events triggered by clicking of buttons or menu items.
	 * @author Chris Moscoso
	 */
	private class ClickListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if(source.equals(exitAction)){
				System.exit(0);
			}
			else if(source.equals(exportToFileAction)){
				showExportOptionsDialog();
			}
			else if(source.equals(exportFileTextButton)){
				showFileChooser();
			}
			else if(source.equals(exportButton)){
				export(rowsCheckBox.isSelected(),formulasCheckBox.isSelected(), solutionsCheckBox.isSelected(), floatsCheckBox.isSelected());
			}
			else if(source.equals(floatCheckButton)){
				if(floatCheckButton.isSelected()){
					changeToFloats();
				}else{
					changeToExpressions();
				}
			}
			else if(source.equals(clearTableAction)){
				clearTable();
			}
			else if(source.equals(showHelpAction)){
				JOptionPane.showMessageDialog(window, "HOW TO INPUT FORMULAS\n" +
						"-Spaces are permitted but not required.\n" +
						"-Implicit multipication is allowed between e, pi, and digits (0-9). (e.g. 2e allowed.)\n" +
						"but not allowed between expressions and parenthesis (e.g. (2)(3+2) not allowed.)\n" +
						"-Cannot support numbers bigger than 2^30\n" + 
						"-For " + Main.getPi() + " type pi or p\n" +
						"-For " + Main.getE() + " type e\n" +
						"-'n' roots are supported. Type sqrt:, 'n'rt:, or base^(1/n)\n" +
						"Note: Non rational roots such as " + Main.getPi() + " or " + Main.getE() + " roots must be in base^(1/" + Main.getPi() + ") or base^(1/" + Main.getE() + ") format.\n" +
						"Roots that are 32 or bigger might not work.\n" +
						"-Type ans in the expression to include the previous expression for each instance of ans.\n" +
						"-Type ans# to include the expression from that row #\n" +
						"\nHOW TO EXPORT DATA TO .TXT FILE\n" +
						"-Go to File > Export table to .txt\n" +
						"-Check which data you would like to include\n" +
						"-File names without a path will be sent to user's default directory\n" +
						"-Choosing a pre existing .txt file will append the data to the file (instead of overwriting it).\n" +
						"-Formulas and solutions bigger than 50 characters will not be properly formatted.\n"
						, "HELP", JOptionPane.QUESTION_MESSAGE);
			}
			else if(source.equals(enterButton)){
				enterFormula();
			}
		}	
	}
	

	/**
	 * This class is responsible for handling all keyboard events
	 * @author Chris Moscoso
	 *
	 */
	private class KeyboardListener implements KeyListener{
		public void keyPressed(KeyEvent e) {}

		public void keyReleased(KeyEvent e){
			//Check for i to autoconvert to pi to its special unicode character.
			if(e.getKeyChar() == 'i'){
				int positionBeforeI = textField.getText().length() - 2;
				//If p is the previous letter
				try{
					if(textField.getText().charAt(positionBeforeI) == 'p'){
						textField.setText(textField.getText().substring(0, positionBeforeI) + Main.getPi());
					}
				}catch(Exception ex){
					
				}
			}
			if(e.getKeyChar() == 'e'){
				int positionBeforeE = textField.getText().length() - 1;
				try{
					if(textField.getText().charAt(positionBeforeE) == 'e'){
						textField.setText(textField.getText().substring(0, positionBeforeE) + Main.getE());
					}
				}catch(Exception ex){
					
				}
			}
		}
		
		public void keyTyped(KeyEvent e) {
			//Check when enter is pressed to solve the formula.
			if(e.getKeyChar() == KeyEvent.VK_ENTER){
				enterFormula();
			}
			if(e.getKeyChar() == '+' || e.getKeyChar() == '-' || e.getKeyChar() == '*' || e.getKeyChar() == '/' || e.getKeyChar() == '^'){
				if(textField.getText().length() == 0){
					textField.setText("ans");
				}
			}
		}
	}
}