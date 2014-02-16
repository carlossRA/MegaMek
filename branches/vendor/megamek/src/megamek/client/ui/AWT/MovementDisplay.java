/**
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import megamek.common.*;

public class MovementDisplay 
	extends AbstractPhaseDisplay
	implements BoardListener,  ActionListener,
	KeyListener, ComponentListener, MouseListener, GameListener
{
	// parent game
	public Client client;
	
	// displays
	private BoardView		bv;
	private Label			statusL;
	
	private	boolean			mechdOn;
	
	// buttons
	private Panel		    panButtons;
	private Button			butWalk;
	private Button			butJump;
	private Button			butBackup;
	private Button			butProne;
	private Button			butNext;
	private Button			butMove;
	
	// let's keep track of what we're moving, too
	private int				cen;	// current entity number
	private MovementData	md;		// movement data
	private MovementData	cmd;	// considering movement data
	
	// what "gear" is our mech in?
	private int				gear;
	
	// is the shift key held?
	private boolean			mouseheld;
	private boolean			shiftheld;
	
	/**
	 * Creates and lays out a new movement phase display 
	 * for the specified client.
	 */
	public MovementDisplay(Client client) {
		this.client = client;
		client.addGameListener(this);
		
		gear = Compute.GEAR_LAND;
		
		shiftheld = false;
		
		bv = client.bv;
		client.game.board.addBoardListener(this);
		bv.addKeyListener(this);
		
		
		statusL = new Label("Waiting to begin Movement phase...", Label.CENTER);
		
		butWalk = new Button("Walk");
		butWalk.setActionCommand("walk");
		butWalk.addActionListener(this);
		butWalk.setEnabled(false);
		
		butJump = new Button("Jump");
		butJump.setActionCommand("jump");
		butJump.addActionListener(this);
		butJump.setEnabled(false);
		
		butBackup = new Button("Backup");
		butBackup.setActionCommand("backup");
		butBackup.addActionListener(this);
		butBackup.setEnabled(false);
		
		butProne = new Button("Go Prone");
		butProne.setActionCommand("prone");
		butProne.addActionListener(this);
		butProne.setEnabled(false);
		
		butNext = new Button("Next Unit");
		butNext.setActionCommand("next");
		butNext.addActionListener(this);
		butNext.setEnabled(false);
		
		butMove = new Button("Move");
		butMove.setActionCommand("move");
		butMove.addActionListener(this);
		butMove.setEnabled(false);
		
		// layout button grid
		panButtons = new Panel();
		panButtons.setLayout(new GridLayout(2, 3));
		panButtons.add(butWalk);
		panButtons.add(butJump);
		panButtons.add(butNext);
		panButtons.add(butBackup);
		panButtons.add(butProne);
		panButtons.add(butMove);
	
		// layout screen
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;	c.weighty = 1.0;
		c.insets = new Insets(1, 1, 1, 1);
		c.gridwidth = GridBagConstraints.REMAINDER;
		addBag(bv, gridbag, c);

		c.weightx = 1.0;	c.weighty = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addBag(statusL, gridbag, c);

		c.gridwidth = 1;
		c.weightx = 1.0;	c.weighty = 0.0;
		addBag(client.cb.getComponent(), gridbag, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;	c.weighty = 0.0;
		addBag(panButtons, gridbag, c);

		// mech display.
		client.mechD.addMouseListener(this);

        client.frame.addComponentListener(this);
	}
	
	private void addBag(Component comp, GridBagLayout gridbag, GridBagConstraints c) {
		gridbag.setConstraints(comp, c);
		add(comp);
	}
	
	/**
	 * Selects an entity, by number, for movement.
	 */
	public void selectEntity(int en) {
        
        if (client.game.getEntity(en) != null) {
            this.cen = en;
            md = new MovementData();
            cmd = new MovementData();
            gear = Compute.GEAR_LAND;
            butWalk.setEnabled(ce().getWalkMP() > 0);
            butJump.setEnabled(ce().getJumpMP() > 0);
            butBackup.setEnabled(ce().getWalkMP() > 0);
		    if (ce().isProne()) {
                butProne.setLabel("Get Up");
		  	    butProne.setEnabled(true);
		    } else {
                butProne.setLabel("Go Prone");
		  	    butProne.setEnabled(false);
            }
            client.game.board.highlight(ce().getPosition());
            client.game.board.select(null);
            client.game.board.cursor(null);
            client.mechD.displayMech(ce());
            client.mechD.showPanel("movement");
            bv.centerOnHex(ce().getPosition());
        } else {
          System.err.println("FiringDisplay: tried to select non-existant entity: " + en);
          System.err.println("FiringDisplay: sending ready signal...");
	    	  client.sendReady(true);
        }
	}
	
	/**
	 * Does turn start stuff
	 */
	private void beginMyTurn() {
        butMove.setLabel("Done");
		butMove.setEnabled(true);
		butNext.setEnabled(true);
		moveMechDisplay();
		client.mechW.setVisible(true);
		moveMechDisplay();
		selectEntity(client.game.getFirstEntityNum(client.getLocalPlayer()));
	}
	
	/**
	 * Does end turn stuff.
	 */
	private void endMyTurn() {
		// end my turn, then.
		cen = Entity.NONE;
		client.game.board.select(null);
		client.game.board.highlight(null);
		client.game.board.cursor(null);
		client.mechW.setVisible(false);
		bv.clearMovementData();
		butMove.setEnabled(false);
		butNext.setEnabled(false);
	}
	
	/**
	 * Clears out the curently selected movement data and
	 * resets it.
	 */
	private void clearAllMoves() {
		client.game.board.select(null);
		client.game.board.cursor(null);
		md = new MovementData();
		cmd = new MovementData();
		bv.clearMovementData();
		butMove.setLabel("Done");;
	}
	
	/**
	 * Sends a data packet indicating the chosen movement.
	 */
	private void moveTo(MovementData md) {
		bv.clearMovementData();
		client.moveEntity(cen, md);
		client.sendReady(true);
	}
	
	/**
	 * Returns the current entity.
	 */
	private Entity ce() {
		return client.game.getEntity(cen);
	}
	
	/**
	 * Returns new MovementData for the currently selected movement type
	 */
	private MovementData currentMove(Coords src, int facing, Coords dest) {
		if (shiftheld) {
		    return Compute.rotatePathfinder(facing, src.direction(dest));
		}
		if (gear == Compute.GEAR_LAND || gear == Compute.GEAR_JUMP) {
			return Compute.lazyPathfinder(src, facing, dest);
		} else if (gear == Compute.GEAR_BACKUP) {
			return Compute.backwardsLazyPathfinder(src, facing, dest);
		}		
		
		return null;
	}
	
	/**
	 * Moves the mech display window to the proper position.
	 */
	private void moveMechDisplay() {
		if (bv.isShowing()) {
			client.mechW.setLocation(bv.getLocationOnScreen().x + bv.getSize().width 
                               - client.mechD.getSize().width - 20, 
                               bv.getLocationOnScreen().y + 20);
		}
	}
	
	//
	// BoardListener
	//
	public void boardHexMoused(BoardEvent b) {
		if (client.isMyTurn() 
            && (b.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
			if (b.getType() == b.BOARD_HEX_DRAGGED) {
				if (!b.getCoords().equals(client.game.board.lastCursor)) {
					client.game.board.cursor(b.getCoords());
					
					// either turn or move
					bv.clearMovementData();
					cmd = md.getAppended(currentMove(md.getFinalCoords(ce().getPosition(), ce().getFacing()), md.getFinalFacing(ce().getFacing()), b.getCoords()));
					bv.drawMovementData(ce(), cmd);
				}
			} else if (b.getType() == b.BOARD_HEX_CLICKED) {
				client.game.board.select(b.getCoords());
				bv.clearMovementData();
				
				Coords moveto = b.getCoords();
				bv.drawMovementData(ce(), cmd);
				md = new MovementData(cmd);
                butMove.setLabel("Move");
				butMove.setEnabled(true);
			}
		}
	}
	
	//
	// GameListener
	//
	public void gameTurnChange(GameEvent ev) {
		if (client.game.phase == Game.PHASE_MOVEMENT) {
			endMyTurn();

      if (client.isMyTurn()) {
				beginMyTurn();
				statusL.setText("It's your turn to move.");
			} else {
				statusL.setText("It's " + ev.getPlayer().getName() + "'s turn to move.");
			}
		}
	}
	public void gamePhaseChange(GameEvent ev) {
		if (client.isMyTurn() && client.game.phase != Game.PHASE_MOVEMENT) {
			endMyTurn();
		}
		if (client.game.phase !=  Game.PHASE_MOVEMENT) {
			client.removeGameListener(this);
			client.game.board.removeBoardListener(this);
			client.bv.removeKeyListener(this);
			client.cb.getComponent().removeKeyListener(this);
			client.mechD.removeMouseListener(this);
			client.frame.removeComponentListener(this);
			
		}
	}

	//
	// ActionListener
	//
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equalsIgnoreCase("ready") && client.isMyTurn()) {
			client.sendEntityReady(cen);
			client.sendReady(true);
		}
		if (ev.getActionCommand().equalsIgnoreCase("move") && client.isMyTurn()) {
			moveTo(md);
		}
		if (ev.getActionCommand().equalsIgnoreCase("next") && client.isMyTurn()) {
			clearAllMoves();
			selectEntity(client.game.getNextEntityNum(client.getLocalPlayer(), cen));
		}
		if (ev.getActionCommand().equalsIgnoreCase("walk") && client.isMyTurn()) {
            if (gear == Compute.GEAR_JUMP) {
			    clearAllMoves();
            }
			gear = Compute.GEAR_LAND;
			butJump.setEnabled(ce().getJumpMP() > 0);
		}
		if (ev.getActionCommand().equalsIgnoreCase("jump") && client.isMyTurn()) {
            if (gear != Compute.GEAR_JUMP) {
			    clearAllMoves();
            }
            if (!md.contains(MovementData.STEP_START_JUMP)) {
                md.addStep(MovementData.STEP_START_JUMP);
            }
			gear = Compute.GEAR_JUMP;
            butWalk.setEnabled(true);
			butBackup.setEnabled(true);
		}
		if (ev.getActionCommand().equalsIgnoreCase("backup") && client.isMyTurn()) {
            if (gear == Compute.GEAR_JUMP) {
			    clearAllMoves();
            }
			gear = Compute.GEAR_BACKUP;
            butWalk.setEnabled(true);
			butJump.setEnabled(ce().getJumpMP() > 0);
		}
		if (ev.getActionCommand().equalsIgnoreCase("prone") && client.isMyTurn()) {
			clearAllMoves();
			gear = Compute.GEAR_LAND;
            if (!md.contains(MovementData.STEP_GET_UP)) {
                md.addStep(MovementData.STEP_GET_UP);
            }
			bv.drawMovementData(ce(), cmd);
            butMove.setLabel("Move");
			butMove.setEnabled(true);
		}
	}
	

	//
	// KeyListener
	//
	public void keyPressed(KeyEvent ev) {
		if (ev.getKeyCode() == ev.VK_ESCAPE) {
			clearAllMoves();
		}
		if (ev.getKeyCode() == ev.VK_ENTER && ev.isControlDown()) {
			if (client.isMyTurn()) {
				moveTo(cmd);
			}
		}
		if (ev.getKeyCode() == ev.VK_SHIFT && !shiftheld) {
			shiftheld = true;
			if (client.isMyTurn() && client.game.board.lastCursor != null && !client.game.board.lastCursor.equals(client.game.board.selected)) {
				// switch to turning
				bv.clearMovementData();
				cmd = md.getAppended(currentMove(md.getFinalCoords(ce().getPosition(), ce().getFacing()), md.getFinalFacing(ce().getFacing()), client.game.board.lastCursor));
				bv.drawMovementData(ce(), cmd);
			}
		}
	}
	public void keyReleased(KeyEvent ev) {
		if (ev.getKeyCode() == ev.VK_SHIFT && shiftheld) {
			shiftheld = false;
			if (client.isMyTurn() && client.game.board.lastCursor != null && !client.game.board.lastCursor.equals(client.game.board.selected)) {
				// switch to movement
				bv.clearMovementData();
				cmd = md.getAppended(currentMove(md.getFinalCoords(ce().getPosition(), ce().getFacing()), md.getFinalFacing(ce().getFacing()), client.game.board.lastCursor));
				bv.drawMovementData(ce(), cmd);
			}
		}
	}
	public void keyTyped(KeyEvent ev) {
		;
	}
	
	//
	// ComponentListener
	//
	public void componentHidden(ComponentEvent ev) {
		client.mechW.setVisible(false);
	}
	public void componentMoved(ComponentEvent ev) {
		moveMechDisplay();
	}
	public void componentResized(ComponentEvent ev) {
		moveMechDisplay();
	}
	public void componentShown(ComponentEvent ev) {
		client.mechW.setVisible(false);
		moveMechDisplay();
	}
	
	//
	// MouseListener
	//
	public void mouseEntered(MouseEvent ev) {
		;
	}
	public void mouseExited(MouseEvent ev) {
		;
	}
	public void mousePressed(MouseEvent ev) {
		;
	}
	public void mouseReleased(MouseEvent ev) {
		;
	}
	public void mouseClicked(MouseEvent ev) {
		;
	}

}