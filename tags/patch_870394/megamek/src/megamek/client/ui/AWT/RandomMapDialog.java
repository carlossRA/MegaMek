/*
 * MegaMek - Copyright (C) 2000-2003 Ben Mazur (bmazur@sev.org)
 * 
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License as published by the Free 
 *  Software Foundation; either version 2 of the License, or (at your option) 
 *  any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 *  for more details.
 */

package megamek.client;

import megamek.client.util.*;
import megamek.common.*;
import java.awt.*;
import java.awt.event.*;

public class RandomMapDialog
    extends Dialog implements ActionListener, FocusListener
{
	private static final String NONE   = "None";
	private static final String LOW    = "Low";
	private static final String MEDIUM = "Medium";
	private static final String HIGH   = "High";
	
	private static final int NORMAL_LINE_WIDTH = 195;
	private static final int ADVANCED_LINE_WIDTH = 295;

    private Button butOK = null;
    private Button butAdvanced = null;
    
    private Panel panButtons = null;
    private Panel panOptions = null;

    private Label labBoardSize = null;
    private Label labBoardDivider = null;
    private TextField texBoardWidth = null;
    private TextField texBoardHeight = null;

    private Choice choElevation = null;
    private Choice choWoods = null;
    private Choice choLakes = null;
    private Choice choRough = null;
    private Choice choRoads = null;
    private Choice choRivers = null;

    private Label labElevation = null;
    private Label labWoods = null;
    private Label labLakes = null;
    private Label labRough = null;
    private Label labRoads = null;
    private Label labRivers = null;
    
    private SimpleLine slElevation = null;
    private SimpleLine slWoods = null;
    private SimpleLine slLakes = null;
    private SimpleLine slRough = null;
    private SimpleLine slRoads = null;
    private SimpleLine slRivers = null;
    private SimpleLine slBoardSize = null;

    private SimpleLine slElevationAd = null;
    private SimpleLine slWoodsAd = null;
    private SimpleLine slLakesAd = null;
    private SimpleLine slRoughAd = null;
    private SimpleLine slRoadsAd = null;
    private SimpleLine slRiversAd = null;
    private SimpleLine slBoardSizeAd = null;
    private SimpleLine slCratersAd = null;

	/** how much hills there should be, Range 0..100 */
	private Label labHilliness;
	private TextField texHilliness;
	/** Maximum level of the map */
	private Label labRange;
	private TextField texRange;
	private Label labProbInvert;
	private TextField texProbInvert;
	
	/** how much Lakes at least */
	private Label labWaterSpots;
	private TextField texMinWaterSpots;
	/** how much Lakes at most */
	private TextField texMaxWaterSpots;
	/** minimum size of a lake */
	private Label labWaterSize;
	private TextField texMinWaterSize;
	/** maximum Size of a lake */
	private TextField texMaxWaterSize;
	/** probability for water deeper than lvl1, Range 0..100 */
	private Label labProbDeep;
	private TextField texProbDeep;
	
	/** how much forests at least */
	private Label labForestSpots;
	private TextField texMinForestSpots;
	/** how much forests at most */
	private TextField texMaxForestSpots;
	/** minimum size of a forest */
	private Label labForestSize;
	private TextField texMinForestSize;
	/** maximum Size of a forest */
	private TextField texMaxForestSize;
	/** probability for heavy wood, Range 0..100 */
	private Label labProbHeavy;
	private TextField texProbHeavy;
	
	/** rough */
	private Label labRoughSpots;
	private TextField texMinRoughSpots;
	private TextField texMaxRoughSpots;
	private Label labRoughSize;
	private TextField texMinRoughSize;
	private TextField texMaxRoughSize;
	
	/** probability for a road, range 0..100 */
	private Label labProbRoad;
	private TextField texProbRoad;
	/** probability for a river, range 0..100 */
	private Label labProbRiver;
	private TextField texProbRiver;
	
	/* Craters */
	private Label labProbCrater;
	private TextField texProbCrater;
	private Label labRadius;
	private TextField texMinRadius;
	private TextField texMaxRadius;
	private Label labMaxCraters;
	private TextField texMaxCraters;
	private TextField texMinCraters;
	
	/** Algorithm */
	private Label labAlgorithmToUse;
	private TextField texAlgorithmToUse;

	GridBagLayout gridbag;
	
	private MapSettings mapSettings = null;
	private Frame frame = null;
	private BoardSelectionDialog bsd = null;

    private boolean advanced = false;
    private boolean initiated = false;

    public RandomMapDialog(Frame parent, BoardSelectionDialog bsd, MapSettings mapSettings) {
        super(parent, "Random map settings", true);
        this.mapSettings = mapSettings;
        this.frame = parent;
        this.bsd = bsd;
        setResizable(false);
        
        createComponents();
        loadValues();

        setLayout(new BorderLayout());
        setupOptions();
        add(panOptions, BorderLayout.CENTER);
        setupButtons();
        add(panButtons, BorderLayout.SOUTH);

		validate();
		pack();
		
        butOK.requestFocus();

        setLocation(getParent().getLocation().x + getParent().getSize().width/2 - getSize().width/2,
                    getParent().getLocation().y + getParent().getSize().height/2 - getSize().height/2);
        initiated = true;
    }
    
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource().equals(butOK)) {
	        if (applyValues()) {
	        	this.setVisible(false);
	        }
    	} else {
    		advanced = !advanced;
    		if (advanced) {
    			butAdvanced.setLabel("Normal");
    		} else {
    			butAdvanced.setLabel("Advanced");
    		}
    		setupOptions();
    	}
    }
    
    private void setupOptions() {
    	panOptions.removeAll();

		addLabelTextField(labBoardSize, texBoardWidth, texBoardHeight, "x");
		texBoardWidth.requestFocus();

		if (!advanced) {
			
			addSeparator(slBoardSize);

			addOption(labElevation, choElevation, slElevation);
			addOption(labWoods, choWoods, slWoods);
			addOption(labRough, choRough, slRough);
			addOption(labRoads, choRoads, slRoads);
			addOption(labLakes, choLakes, slLakes);
			addOption(labRivers, choRivers, slRivers);

		} else {

			addSeparator(slBoardSizeAd);

			addLabelTextField(labHilliness, texHilliness);
			addLabelTextField(labRange, texRange);
			addLabelTextField(labProbInvert, texProbInvert);
			addLabelTextField(labAlgorithmToUse, texAlgorithmToUse);

			addSeparator(slElevationAd);
			
			addLabelTextField(labForestSpots, texMinForestSpots, texMaxForestSpots, "-");
			addLabelTextField(labForestSize, texMinForestSize, texMaxForestSize, "-");
			addLabelTextField(labProbHeavy, texProbHeavy);

			addSeparator(slWoodsAd);

			addLabelTextField(labRoughSpots, texMinRoughSpots, texMaxRoughSpots, "-");
			addLabelTextField(labRoughSize, texMinRoughSize, texMaxRoughSize, "-");
		
			addSeparator(slRoughAd);

			addLabelTextField(labProbRoad, texProbRoad);
		
			addSeparator(slRoadsAd);

			addLabelTextField(labWaterSpots, texMinWaterSpots, texMaxWaterSpots, "-");
			addLabelTextField(labWaterSize, texMinWaterSize, texMaxWaterSize, "-");
			addLabelTextField(labProbDeep, texProbDeep);

			addSeparator(slLakesAd);

			addLabelTextField(labProbRiver, texProbRiver);

			addSeparator(slRiversAd);

			addLabelTextField(labProbCrater, texProbCrater);
			addLabelTextField(labMaxCraters, texMinCraters, texMaxCraters, "-");
			addLabelTextField(labRadius, texMinRadius, texMaxRadius, "-");

			addSeparator(slCratersAd);

	    }
	    
	    if (initiated) {
	  		pack();
	        setLocation(getParent().getLocation().x + getParent().getSize().width/2 - getSize().width/2,
                    getParent().getLocation().y + getParent().getSize().height/2 - getSize().height/2);
	  	}
	}

    private void setupButtons() {

		panButtons.add(butOK);
		panButtons.add(butAdvanced);
		
	}
	
	private void createComponents() {

	    butOK = new Button("OK");
		butOK.addActionListener(this);
		
	    butAdvanced = new Button("Advanced");
		butAdvanced.addActionListener(this);

	    panButtons = new Panel();	    
        panButtons.setLayout(new FlowLayout());

	    panOptions = new Panel();
        gridbag = new GridBagLayout();
        panOptions.setLayout(gridbag);

	    labBoardSize = new Label("Board size (hexes):", Label.LEFT);
	    labBoardDivider = new Label("x", Label.CENTER);
	    texBoardWidth = new TextField(2);
   		texBoardWidth.addFocusListener(this);
	    texBoardHeight = new TextField(2);
   		texBoardHeight.addFocusListener(this);
	    slBoardSize = new SimpleLine(NORMAL_LINE_WIDTH);

		// Normal setting components...
		labElevation = new Label("Elevation:", Label.LEFT);
		choElevation = new Choice();
		fillChoice(choElevation);
		slElevation = new SimpleLine(NORMAL_LINE_WIDTH);

		labWoods = new Label("Woods:", Label.LEFT);
		choWoods = new Choice();
		fillChoice(choWoods);
		slWoods = new SimpleLine(NORMAL_LINE_WIDTH);

		labLakes = new Label("Lakes:", Label.LEFT);
		choLakes = new Choice();
		fillChoice(choLakes);
		slLakes = new SimpleLine(NORMAL_LINE_WIDTH);

		labRough = new Label("Roughs:", Label.LEFT);
		choRough = new Choice();
		fillChoice(choRough);
		slRough = new SimpleLine(NORMAL_LINE_WIDTH);

		labRivers = new Label("River:", Label.LEFT);
		choRivers = new Choice();
		fillChoice(choRivers);
		slRivers = new SimpleLine(NORMAL_LINE_WIDTH);

		labRoads = new Label("Road:", Label.LEFT);
		choRoads = new Choice();
		fillChoice(choRoads);
		slRoads = new SimpleLine(NORMAL_LINE_WIDTH);

		// Advanced setting components...
		/** how much hills there should be, Range 0..1000 */
		labHilliness = new Label("Amount of elevation(0-1000):", Label.LEFT);
		texHilliness = new TextField(2);
		texHilliness.addFocusListener(this);
		/** Maximum level of the map */
		labRange = new Label("Elevation range: ", Label.LEFT);
		texRange = new TextField(2);
		texRange.addFocusListener(this);
		labProbInvert = new Label("Probability of inverting the map: ", Label.LEFT);
		texProbInvert = new TextField(2);
		texProbInvert.addFocusListener(this);
		
		/** how much Lakes at least */
		labWaterSpots= new Label("Number of lakes(min-max):", Label.LEFT);
		texMinWaterSpots= new TextField(2);
		texMinWaterSpots.addFocusListener(this);
		/** how much Lakes at most */
		texMaxWaterSpots= new TextField(2);
		texMaxWaterSpots.addFocusListener(this);
		/** minimum size of a lake */
		labWaterSize= new Label("Lake size in hexes(min-max):", Label.LEFT);
		texMinWaterSize= new TextField(2);
		texMinWaterSize.addFocusListener(this);
		/** maximum Size of a lake */
		texMaxWaterSize= new TextField(2);
		texMaxWaterSize.addFocusListener(this);
		/** probability for water deeper than lvl1, Range 0..100 */
		labProbDeep= new Label("Probability for deep water:", Label.LEFT);
		texProbDeep= new TextField(2);
		texProbDeep.addFocusListener(this);
		
		/** how much forests at least */
		labForestSpots = new Label("Number of woods(min-max):", Label.LEFT);
		texMinForestSpots = new TextField(2);
		texMinForestSpots.addFocusListener(this);
		/** how much forests at most */
		texMaxForestSpots= new TextField(2);
		texMaxForestSpots.addFocusListener(this);
		/** minimum size of a forest */
		labForestSize= new Label("Wood size in hexes(min-max):", Label.LEFT);
		texMinForestSize= new TextField(2);
		texMinForestSize.addFocusListener(this);
		/** maximum Size of a forest */
		texMaxForestSize= new TextField(2);
		texMaxForestSize.addFocusListener(this);
		/** probability for heavy wood, Range 0..100 */
		labProbHeavy = new Label("Probability for heavy wood:", Label.LEFT);
		texProbHeavy = new TextField(2);
		texProbHeavy.addFocusListener(this);
		
		/** rough */
		labRoughSpots= new Label("Number of roughs(min-max):", Label.LEFT);
		texMinRoughSpots= new TextField(2);
		texMinRoughSpots.addFocusListener(this);
		texMaxRoughSpots= new TextField(2);
		texMaxRoughSpots.addFocusListener(this);
		labRoughSize= new Label("Rough size in hexes(min-max):", Label.LEFT);
		texMinRoughSize= new TextField(2);
		texMinRoughSize.addFocusListener(this);
		texMaxRoughSize= new TextField(2);
		texMaxRoughSize.addFocusListener(this);
		
		/** probability for a road, range 0..100 */
		labProbRoad= new Label("Probability for a road:", Label.LEFT);
		texProbRoad= new TextField(2);
		texProbRoad.addFocusListener(this);
		/** probability for a river, range 0..100 */
		labProbRiver= new Label("Probability for a river", Label.LEFT);
		texProbRiver= new TextField(2);
		texProbRiver.addFocusListener(this);
		
		/* Craters */
		labProbCrater= new Label("Probability for craters:", Label.LEFT);
		texProbCrater= new TextField(2);
		texProbCrater.addFocusListener(this);
		labRadius= new Label("Crater radius(min-max):", Label.LEFT);
		texMinRadius= new TextField(2);
		texMinRadius.addFocusListener(this);
		texMaxRadius= new TextField(2);
		texMaxRadius.addFocusListener(this);
		labMaxCraters= new Label("Number of craters(min-max):", Label.LEFT);
		texMaxCraters= new TextField(2);    
		texMaxCraters.addFocusListener(this);
		texMinCraters= new TextField(2);
		texMinCraters.addFocusListener(this);
		
		/** Algorithm */
		labAlgorithmToUse = new Label("Algorithm:", Label.LEFT);
		texAlgorithmToUse = new TextField(2);
	
	    slElevationAd = new SimpleLine(ADVANCED_LINE_WIDTH);
	    slWoodsAd = new SimpleLine(ADVANCED_LINE_WIDTH);
	    slLakesAd = new SimpleLine(ADVANCED_LINE_WIDTH);
	    slRoughAd = new SimpleLine(ADVANCED_LINE_WIDTH);
	    slRoadsAd = new SimpleLine(ADVANCED_LINE_WIDTH);
	    slRiversAd = new SimpleLine(ADVANCED_LINE_WIDTH);
	    slBoardSizeAd = new SimpleLine(ADVANCED_LINE_WIDTH);
	    slCratersAd = new SimpleLine(ADVANCED_LINE_WIDTH);

	}
	
	private void addOption(Label label, Choice choice, SimpleLine sl) {
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;    c.weighty = 1;
        c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(label, c);
        panOptions.add(label);

		c.gridwidth = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(choice, c);
		panOptions.add(choice);

		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(2, 0, 2, 0);
		gridbag.setConstraints(sl, c);
		panOptions.add(sl);
	
	}
	
	private void fillChoice(Choice c) {
		c.add(NONE);
		c.add(LOW);
		c.add(MEDIUM);
		c.add(HIGH);
	}
	
	private void addLabelTextField(Label label, TextField text) {
		GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;    c.weighty = 0;
        c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(label, c);
        panOptions.add(label);

        c.weightx = 0;    c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(text, c);
        panOptions.add(text);
	}

	private void addLabelTextField(Label label, TextField text, TextField text2, String separator) {
		GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;    c.weighty = 0;
        c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(label, c);
        panOptions.add(label);

        c.weightx = 0;    c.weighty = 0;
        c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(text, c);
        panOptions.add(text);
        
        Label l = new Label(separator, Label.CENTER);
        gridbag.setConstraints(l, c);
        panOptions.add(l);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(text2, c);
        panOptions.add(text2);
	}
	
	private void addSeparator(SimpleLine sl) {
		GridBagConstraints c = new GridBagConstraints();
		
        c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(2, 0, 2, 0);
        gridbag.setConstraints(sl, c);
        panOptions.add(sl);
	}
	
	private void loadValues() {
        texBoardWidth.setText(new Integer(mapSettings.getBoardWidth()).toString());
        texBoardHeight.setText(new Integer(mapSettings.getBoardHeight()).toString());

		texHilliness.setText(new Integer(mapSettings.getHilliness()).toString());
		texRange.setText(new Integer(mapSettings.getRange()).toString());
		texProbInvert.setText(new Integer(mapSettings.getProbInvert()).toString());
		texMinWaterSpots.setText(new Integer(mapSettings.getMinWaterSpots()).toString());
		texMaxWaterSpots.setText(new Integer(mapSettings.getMaxWaterSpots()).toString());
		texMinWaterSize.setText(new Integer(mapSettings.getMinWaterSize()).toString());
		texMaxWaterSize.setText(new Integer(mapSettings.getMaxWaterSize()).toString());
		
		texMinForestSpots.setText(new Integer(mapSettings.getMinForestSpots()).toString());
		texMaxForestSpots.setText(new Integer(mapSettings.getMaxForestSpots()).toString());
		texMinForestSize.setText(new Integer(mapSettings.getMinForestSize()).toString());
		texMaxForestSize.setText(new Integer(mapSettings.getMaxForestSize()).toString());
		
		texMinRoughSpots.setText(new Integer(mapSettings.getMinRoughSpots()).toString());
		texMaxRoughSpots.setText(new Integer(mapSettings.getMaxRoughSpots()).toString());
		texMinRoughSize.setText(new Integer(mapSettings.getMinRoughSize()).toString());
		texMaxRoughSize.setText(new Integer(mapSettings.getMaxRoughSize()).toString());
		
		texProbDeep.setText(new Integer(mapSettings.getProbDeep()).toString());
		texProbHeavy.setText(new Integer(mapSettings.getProbHeavy()).toString());
		texProbRiver.setText(new Integer(mapSettings.getProbRiver()).toString());
		texProbRoad.setText(new Integer(mapSettings.getProbRoad()).toString());
		texProbCrater.setText(new Integer(mapSettings.getProbCrater()).toString());
		texMinRadius.setText(new Integer(mapSettings.getMinRadius()).toString());
		texMaxRadius.setText(new Integer(mapSettings.getMaxRadius()).toString());
		texMaxCraters.setText(new Integer(mapSettings.getMaxCraters()).toString());
		texMinCraters.setText(new Integer(mapSettings.getMinCraters()).toString());
		texAlgorithmToUse.setText(new Integer(mapSettings.getAlgorithmToUse()).toString());
	}
	
	private boolean applyValues() {
        int boardWidth;
        int boardHeight;
		int hilliness, range;
		int minWaterSpots, maxWaterSpots, minWaterSize, maxWaterSize, probDeep;
		int minForestSpots, maxForestSpots, minForestSize, maxForestSize, probHeavy;
		int minRoughSpots, maxRoughSpots, minRoughSize, maxRoughSize;
		int probRoad, probRiver, probInvert;
		int minRadius, maxRadius, minCraters, maxCraters, probCrater;
		int algorithmToUse;

		try {
			boardWidth = Integer.parseInt(texBoardWidth.getText());
			boardHeight = Integer.parseInt(texBoardHeight.getText());
		} catch (NumberFormatException ex) {
            new AlertDialog(frame, "Invalid Setting", "Only integers are allowed!").show();
            return false;
        }
        
        if (boardHeight <= 0 || boardHeight <= 0) {
            new AlertDialog(frame, "Invalid Setting", "Board size settings must be greater than 0.").show();
            return false;
        }

		if (advanced) {
			try {
				hilliness = Integer.parseInt(texHilliness.getText());
				range = Integer.parseInt(texRange.getText());
				probInvert = Integer.parseInt(texProbInvert.getText());
				minWaterSpots = Integer.parseInt(texMinWaterSpots.getText());
				maxWaterSpots = Integer.parseInt(texMaxWaterSpots.getText());
				minWaterSize = Integer.parseInt(texMinWaterSize.getText());
				maxWaterSize = Integer.parseInt(texMaxWaterSize.getText());
				minForestSpots = Integer.parseInt(texMinForestSpots.getText());
				maxForestSpots = Integer.parseInt(texMaxForestSpots.getText());
				minForestSize = Integer.parseInt(texMinForestSize.getText());
				maxForestSize = Integer.parseInt(texMaxForestSize.getText());
				minRoughSpots = Integer.parseInt(texMinRoughSpots.getText());
				maxRoughSpots = Integer.parseInt(texMaxRoughSpots.getText());
				minRoughSize = Integer.parseInt(texMinRoughSize.getText());
				maxRoughSize = Integer.parseInt(texMaxRoughSize.getText());
				probRoad = Integer.parseInt(texProbRoad.getText());
				probRiver = Integer.parseInt(texProbRiver.getText());
				probHeavy = Integer.parseInt(texProbHeavy.getText());
				probDeep = Integer.parseInt(texProbDeep.getText());
				probCrater = Integer.parseInt(texProbCrater.getText());
				minRadius = Integer.parseInt(texMinRadius.getText());
				maxRadius = Integer.parseInt(texMaxRadius.getText());
				maxCraters = Integer.parseInt(texMaxCraters.getText());
				minCraters = Integer.parseInt(texMinCraters.getText());
				algorithmToUse = Integer.parseInt(texAlgorithmToUse.getText());
			} catch (NumberFormatException ex) {
	            new AlertDialog(frame, "Invalid Setting", "Only integers are allowed!").show();
	            return false;
	        }
	        
	        if (hilliness < 0 || hilliness > 99) {
	            new AlertDialog(frame, "Invalid Setting", "Amount of elevation must be 0-99.").show();
	            return false;
	        }
	        if (range < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Elevation range must be greater than -1.").show();
	            return false;
	        }
	        if (probInvert < 0 || probInvert > 100) {
	            new AlertDialog(frame, "Invalid Setting", "Depression probability must be 0-100.").show();
	            return false;
	        }
	        if (minWaterSpots < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min number of lakes must be greater than -1.").show();
	            return false;
	        }
	        if (maxWaterSpots < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of lakes must be greater than -1.").show();
	            return false;
	        }
	        if (maxWaterSpots < minWaterSpots) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of lakes must not be less than min.").show();
	            return false;
	        }
	        if (minWaterSize < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min lake size must be greater than -1.").show();
	            return false;
	        }
	        if (maxWaterSize < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max lake size must be greater than -1.").show();
	            return false;
	        }
	        if (maxWaterSize < minWaterSize) {
	            new AlertDialog(frame, "Invalid Setting", "Max lake size must not be less than min.").show();
	            return false;
	        }
	        if (probDeep < 0 || probDeep > 100) {
	            new AlertDialog(frame, "Invalid Setting", "Probability for deep water must be 0-100.").show();
	            return false;
	        }
	        if (minForestSpots < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min number of forests must be greater than -1.").show();
	            return false;
	        }
	        if (maxForestSpots < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of forests must be greater than -1.").show();
	            return false;
	        }
	        if (maxForestSpots < minForestSpots) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of forests must not be less than min.").show();
	            return false;
	        }
	        if (minForestSize < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min forest size must be greater than -1.").show();
	            return false;
	        }
	        if (maxForestSize < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max forest size must be greater than -1.").show();
	            return false;
	        }
	        if (maxForestSize < minForestSize) {
	            new AlertDialog(frame, "Invalid Setting", "Max forest size must not be less than min.").show();
	            return false;
	        }
	        if (probHeavy < 0 || probHeavy > 100) {
	            new AlertDialog(frame, "Invalid Setting", "Probability for heavy forest must be 0-100.").show();
	            return false;
	        }
	        if (minRoughSpots < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min number of roughs must be greater than -1.").show();
	            return false;
	        }
	        if (maxRoughSpots < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of roughs must be greater than -1.").show();
	            return false;
	        }
	        if (maxRoughSpots < minRoughSpots) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of roughs must not be less than min.").show();
	            return false;
	        }
	        if (minRoughSize < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min rough size must be greater than -1.").show();
	            return false;
	        }
	        if (maxRoughSize < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max rough size must be greater than -1.").show();
	            return false;
	        }
	        if (maxRoughSize < minRoughSize) {
	            new AlertDialog(frame, "Invalid Setting", "Max rough size must not be less than min.").show();
	            return false;
	        }
	        if (probRiver < 0 || probRiver > 100) {
	            new AlertDialog(frame, "Invalid Setting", "Probability for a river must be 0-100.").show();
	            return false;
	        }
	        if (probRoad < 0 || probRoad > 100) {
	            new AlertDialog(frame, "Invalid Setting", "Probability for a road must be 0-100.").show();
	            return false;
	        }
	        if (probCrater < 0 || probCrater > 100) {
	            new AlertDialog(frame, "Invalid Setting", "Probability for craters must be 0-100.").show();
	            return false;
	        }
	        if (minRadius < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min crater radius must be greater than -1.").show();
	            return false;
	        }
	        if (maxRadius < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max crater radius must be greater than -1.").show();
	            return false;
	        }
	        if (maxRadius < minRadius) {
	            new AlertDialog(frame, "Invalid Setting", "Max crater radius must not be less than min.").show();
	            return false;
	        }
	        if (maxCraters < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of craters must be greater than -1.").show();
	            return false;
	        }
	        if (minCraters < 0) {
	            new AlertDialog(frame, "Invalid Setting", "Min number of craters must be greater than -1.").show();
	            return false;
	        }
	        if (maxCraters < minCraters) {
	            new AlertDialog(frame, "Invalid Setting", "Max number of craters must not be less than min.").show();
	            return false;
	        }
	        if (algorithmToUse < 0 || algorithmToUse > 2) {
	            new AlertDialog(frame, "Invalid Setting", "Algorithm to use must be 0-2.").show();
	            return false;
	        }
	        
	        
		} else {
			String s = choElevation.getSelectedItem();
			if (s.equals(NONE)) {
				hilliness = 0;
				range = 0;
			} else if (s.equals(LOW)) {
				hilliness = 25;
				range = 3;
			} else if (s.equals(MEDIUM)) {
				hilliness = 50;
				range = 5;
			} else {
				hilliness = 75;
				range = 8;
			}
			s = choWoods.getSelectedItem();
			if (s.equals(NONE)) {
				minForestSize = 0;
				maxForestSize = 0;
				minForestSpots = 0;
				maxForestSpots = 0;
				probHeavy = 0;
			} else if (s.equals(LOW)) {
				minForestSize = 3;
				maxForestSize = 6;
				minForestSpots = 3;
				maxForestSpots = 6;
				probHeavy = 20;
			} else if (s.equals(MEDIUM)) {
				minForestSize = 3;
				maxForestSize = 10;
				minForestSpots = 4;
				maxForestSpots = 8;
				probHeavy = 30;
			} else {
				minForestSize = 8;
				maxForestSize = 13;
				minForestSpots = 6;
				maxForestSpots = 10;
				probHeavy = 45;
			}
			s = choLakes.getSelectedItem();
			if (s.equals(NONE)) {
				minWaterSize = 0;
				maxWaterSize = 0;
				minWaterSpots = 0;
				maxWaterSpots = 0;
				probDeep = 0;
			} else if (s.equals(LOW)) {
				minWaterSize = 1;
				maxWaterSize = 5;
				minWaterSpots = 1;
				maxWaterSpots = 5;
				probDeep = 20;
			} else if (s.equals(MEDIUM)) {
				minWaterSize = 6;
				maxWaterSize = 10;
				minWaterSpots = 2;
				maxWaterSpots = 5;
				probDeep = 30;
			} else {
				minWaterSize = 8;
				maxWaterSize = 15;
				minWaterSpots = 3;
				maxWaterSpots = 6;
				probDeep = 45;
			}
			s = choRough.getSelectedItem();
			if (s.equals(NONE)) {
				minRoughSize = 0;
				maxRoughSize = 0;
				minRoughSpots = 0;
				maxRoughSpots = 0;
			} else if (s.equals(LOW)) {
				minRoughSize = 1;
				maxRoughSize = 2;
				minRoughSpots = 2;
				maxRoughSpots = 6;
			} else if (s.equals(MEDIUM)) {
				minRoughSize = 2;
				maxRoughSize = 5;
				minRoughSpots = 3;
				maxRoughSpots = 8;
			} else {
				minRoughSize = 3;
				maxRoughSize = 7;
				minRoughSpots = 5;
				maxRoughSpots = 10;
			}
			s = choRoads.getSelectedItem();
			if (s.equals(NONE)) {
				probRoad = 0;
			} else if (s.equals(LOW)) {
				probRoad = 25;
			} else if (s.equals(MEDIUM)) {
				probRoad = 50;
			} else {
				probRoad = 75;
			}
			s = choRivers.getSelectedItem();
			if (s.equals(NONE)) {
				probRiver = 0;
			} else if (s.equals(LOW)) {
				probRiver = 25;
			} else if (s.equals(MEDIUM)) {
				probRiver = 50;
			} else {
				probRiver = 75;
			}
			
			probCrater = 0;
			minRadius = 0;
			maxRadius = 0;
			minCraters = 0;
			maxCraters = 0;
			algorithmToUse = 0;
			probInvert = 0;
		}
		
        mapSettings.setBoardSize(boardWidth, boardHeight);
		mapSettings.setElevationParams(hilliness, range, probInvert);
		mapSettings.setWaterParams(minWaterSpots, maxWaterSpots, 
									minWaterSize, maxWaterSize, probDeep);
		mapSettings.setForestParams(minForestSpots, maxForestSpots,
									minForestSize, maxForestSize, probHeavy);
		mapSettings.setRoughParams(minRoughSpots, maxRoughSpots,
									minRoughSize, maxRoughSize);
		mapSettings.setRiverParam(probRiver);
		mapSettings.setRoadParam(probRoad);
		mapSettings.setCraterParam(probCrater, minCraters, maxCraters, minRadius, maxRadius);
		mapSettings.setAlgorithmToUse(algorithmToUse);
		
		if (!advanced) {
			loadValues();
		}

		bsd.updateMapSettings(mapSettings);

		return true;
	}
	
	public void focusGained(FocusEvent fe) {
		if (fe.getSource() instanceof TextField) {
			TextField tf = (TextField) fe.getSource();
			tf.selectAll();
		}
	}

	public void focusLost(FocusEvent fe) {
		if (fe.getSource() instanceof TextField) {
			TextField tf = (TextField) fe.getSource();
			tf.select(0, 0);
		}
	}
	
	public void setMapSettings(MapSettings mapSettings) {
		this.mapSettings = mapSettings;
		loadValues();
	}
}
