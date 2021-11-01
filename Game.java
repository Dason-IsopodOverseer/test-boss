

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Game extends Canvas {
	private BufferStrategy strategy;   // take advantage of accelerated graphics
	
	private boolean leftPressed = false;  // true if left arrow key currently pressed
    private boolean rightPressed = false; // true if right arrow key currently pressed
    protected boolean upPressed = false; // true if up arrow key currently pressed
    private boolean attackPressed = false; // true if attack key (spacebar) currently pressed
    private boolean forcePressed = false; // true if force key (a) currently pressed
    
    public final int GAMEWIDTH = 1600; // width of game window in px 
    public final int GAMEHEIGHT = 900; // height of game window in px
    private int levelHeight = 1800; // height of the first "level" of the game
    
    protected int tileSize = 50; // width and height of all tiles
    
    private double amountScrolled = 0; // constantly increases to make the platforms rise
    private double scrollSpeed = -2; // speed that amountScrolled increases at
    private boolean superScroll = false; // when luke is in the bottom 2/10 of the screen, scroll faster
    private boolean scrolling = true;
    
    private int lvl = 1;
    private final int ATTACKINTERVAL = 700;
    private final int ATTACKDURATION = 300;
    private final int PAUSEDURATION = 300;
    private int forceAttackInterval = 2000;
    
    public ArrayList<Entity> entities = new ArrayList(); // list of entities
    public ArrayList<Entity> deadEnemies = new ArrayList();
    private Sprite heart = (SpriteStore.get()).getSprite("sprites/heart.png");
    private Sprite lostHeart = (SpriteStore.get()).getSprite("sprites/lostheart.png");
    private Sprite[] hearts = {heart, heart, heart};
    
    private double moveSpeed = 300;
    public Entity luke;
    public Entity enemy;
    
    private boolean inFreefall = true; // true when luke is falling and moving down
    private int jumpTarget;
    private boolean jumping = false; // true when luke is "falling", but moving up
    private boolean facingRight = true;
    public int health = 3;
    public int deathToll = 0;
    private long lastAttack = 0;
    private long lastForceAttack = 0;
    private long lastPause = 0;
    
    private boolean gameStarted = false;
    
    private TileMap map = new TileMap("level1.txt", this);
    
    long lastLoopTime = System.currentTimeMillis();
    
    /*
	 * Construct our game and set it running.
	 */
	public Game() {
		// create a frame to contain game
		JFrame container = new JFrame("Wrong Galaxy");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		panel.setPreferredSize(new Dimension(GAMEWIDTH,GAMEHEIGHT));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setSize(screenSize);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
        // be done using graphics acceleration
		setIgnoreRepaint(true);
		
		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		// make the game appear in the center of the screen
		container.setLocation((int) (screenSize.getWidth()/2.0 - GAMEWIDTH/2.0), (int) (screenSize.getHeight()/2.0 - GAMEHEIGHT/1.8));

        // if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas
		addKeyListener(new KeyInputHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		luke = new LukeEntity(this, "luke", 10, 0);
		entities.add(luke);
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).setMap();
		}

		// start the game
		introScreenLoop();
		gameLoop();
    } // constructor

	private Sprite getSprite(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public TileMap getTileMap() {
		return this.map;
	} // getTileMap
	
	public void introScreenLoop() {
        while(!gameStarted) {
                // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,GAMEWIDTH,GAMEHEIGHT);
            g.setColor(new Color(100,100,175));
            g.setFont(new Font(Font.SERIF,Font.PLAIN,50));
            g.drawString("Wrong Galaxy", GAMEWIDTH / 2 - 110, 60);
            g.setFont(new Font(Font.SERIF,Font.PLAIN,20));
            g.drawString("Luke Groundwalker, testing a new type of hyperdrive, got marooned in the Milky Way when his transwarp coil", 40, 100);
            g.drawString("gave out. Fortunately, a crashed Gorb ship on a nearby planet contains a replacement coil. Unfortunately, the ship's", 40, 125);
            g.drawString("remaining crew don't want Luke to take the coil, nor do the Kronons, who are trying to salvage it themselves.", 40, 150);
            g.setColor(new Color(50,100,225));
            g.drawString("Use the arrow keys to move and space to attack.", 40, 200);
            g.drawString("Press enter to start.", 40, 225);
            // clear graphics and flip buffer
            g.dispose();
            strategy.show();
        } // while
} // introScreenLoop
	
	
	public void gameLoop() {
		
		lastLoopTime = System.currentTimeMillis();

        // keep loop running until game ends
        while (true) {
        	
			// calc. time since last update, will be used to calculate
            // entities movement
            long delta = System.currentTimeMillis() - lastLoopTime;
            lastLoopTime = System.currentTimeMillis();
            
            // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,GAMEWIDTH,GAMEHEIGHT);
            
		
            // remove dead entities
            entities.removeAll(deadEnemies);

            // move entities
            if (!luke.pauseMovement) {
	            for (int i = 0; i < entities.size(); i++) {
	                Entity entity = (Entity) entities.get(i);
	                entity.move(delta);
	            } // for
	           
	            // check superScroll
	            if ((luke.getY() + amountScrolled > (GAMEHEIGHT * 0.8) && scrollSpeed != 0)) {
	            	// if luke is in the bottom 2/10 of the screen
	            	superScroll = true;
	
	            } else if (luke.getY() + amountScrolled < (GAMEHEIGHT * 0.2)) {
	            	// if luke is in the top 2/10 of the screen
	            	superScroll = false;
	            } // else if
	            
	            // scroll screen
	            if (scrolling) {
		            if (superScroll) {
		            	amountScrolled += (-1000 * delta) / 1000;
		            } else {
		            	amountScrolled += (scrollSpeed * delta) / 1000;
		            } // else
		            
		           
		            if ((levelHeight - GAMEHEIGHT) + amountScrolled < 1) {
		        		scrolling = false;
		        		
		        	}
	            }
	            
            } // if movement paused	        
            
            
            Sprite tile = null;
            int topY = (int) amountScrolled / -tileSize;
            int bottomY = (GAMEHEIGHT / tileSize) + topY;
    		for (int i = 0; i < map.getWidth(); i++) { 
    			for (int j = topY; j <= bottomY; j++) {
    				tile = map.getTile(i, j);
    				if (tile != null) {
    					tile.draw(g, (i * tileSize), (int)(j * tileSize + amountScrolled));
    				}
    			}
    		}
    		
            
            
    		// update entity animations to account for movement
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                entity.updateAnimations(delta);
            } // for

            // draw entities
            for (int i = 0; i < entities.size(); i++) {
            	Entity entity = (Entity) entities.get(i);
            	entity.draw(g, amountScrolled);
            }
            
            for (int i = 0; i < entities.size(); i++) {
    	        if (luke.lightsaberHit(entities.get(i), facingRight) && entities.get(i) != luke) {
    	        	if (luke.attacking) {
    	        		deadEnemies.add(entities.get(i));
    	 				forceAttackInterval += 250;
    	        	}
    	        }
    	        
    	        if (entities.get(i).collidesWith(luke) && entities.get(i) != luke) {
	        		health--;
	        		hearts[health] = lostHeart;
	        		luke.collidedWith(entities.get(i));
	        		lastPause = System.currentTimeMillis();
	        		luke.pauseMovement = true;
	        				
	        	}
    	    }
            
            if (System.currentTimeMillis() - lastPause > PAUSEDURATION) {
    			luke.pauseMovement = false;
    		}
            
            // check if luke should be moving vertically
           
            if (jumping) {
            	// check if luke has reached the peak of his jump
            	if (luke.getY() < jumpTarget || (luke.getY() + amountScrolled < 0)) {
            		jumping = false;
            		inFreefall = true; 
            	} // if
            	
            } else { 
	        	inFreefall = true;
            } // else
            
            // draw force bar
            if (System.currentTimeMillis() - lastForceAttack > forceAttackInterval) {
            	g.setColor(new Color(20,170,255));
            	g.fillRect(GAMEWIDTH - forceAttackInterval / 30, 0, forceAttackInterval / 30, 40);
            } else {
            	g.fillRect(GAMEWIDTH - forceAttackInterval / 30, 0, forceAttackInterval / 30, 40);
            	if (deadEnemies.isEmpty()) {
            		g.setColor(new Color(20,170,255));
            	} else {
            		g.setColor(new Color(150,0,0));
            	} // else
            	
            	g.fillRect(GAMEWIDTH - forceAttackInterval / 30, 0, (int) (System.currentTimeMillis() - lastForceAttack) / 30, 40);
            } // else
            
            // draw health bar
            for (int i = 1; i <= 3; i++) {
            	hearts[i - 1].draw(g, GAMEWIDTH - (40 * i), 60);
            }
           
            // clear graphics and flip buffer
            g.dispose();
            strategy.show();
            
            // revert luke to default movement
            if (inFreefall) {
            	luke.setVerticalMovement(600);
            }
            luke.setHorizontalMovement(0);
            	
        	// Handle input
 			if (rightPressed && !leftPressed) {
 				luke.setHorizontalMovement(moveSpeed);
 				facingRight = true;
 			} else if (leftPressed) {
 				luke.setHorizontalMovement(-moveSpeed);
 				facingRight = false;
 			} if (upPressed && luke.isTileBelow(delta)) {
 				luke.setVerticalMovement(-600);
 				jumpTarget = luke.getY() - 200;
 				jumping = true;
 				inFreefall = false;
 			} // if
 			
 			// check if luke can attack
 			if (attackPressed && (System.currentTimeMillis() - lastAttack > ATTACKINTERVAL)) {
 				lastAttack = System.currentTimeMillis();
 				luke.attacking = true;
 			}
 			// stop attack once attack duration is over
 			if (System.currentTimeMillis() - lastAttack > ATTACKDURATION) {
 				luke.attacking = false; 
 			}
 			
 			// check if luke can use the force
 			if (forcePressed && (System.currentTimeMillis() - lastForceAttack > forceAttackInterval)) {
 				lastForceAttack = System.currentTimeMillis();
 				
 				// push away all enemies in front of luke
 				for (int i = 0; i < entities.size(); i++) {
 					Entity e = entities.get(i);
 					if (e.getY() < luke.getY() + 30 && e.getY() > luke.getY() - 30) {
 						if (facingRight && e.getX() > luke.getX()) {
 							e.pushBackTo(e.getX() + 100, true);
 						} else if (!facingRight && e.getX() < luke.getX()) {
 							e.pushBackTo(e.getX() - 100, false);
 						} // else if
 					} // if
 				} // for
 			} // if
 			
 			// see if luke is dead
 			if (health == 0) {
 				lose();
 			}
 			if ((luke.getY() + amountScrolled) < 0 && luke.isTileBelow(delta)) {
            	lose();
            } // if
 			
 			// if luke is at the bottom region of the level, detect tiles below
 			if (luke.y > (levelHeight - 200)) {
	 			if (((LukeEntity) luke).getTileDirectlyBelow() == 'N') {
	 				lvl++;
	 				goToNextLevel();
	 				((LukeEntity) luke).setMap(map);
	 				scrolling = true;
	 			}
 			}
        
         } // while
	} // gameLoop
	
	private void goToNextLevel() {
			entities.clear();
		    health = 3;
		    hearts[0] = heart;
		    hearts[1] = heart;
		    hearts[2] = heart;
			
            // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,GAMEWIDTH,GAMEHEIGHT);
            
            loadLvlMap();
            entities.add(luke);
            
            for (int i = 1; i <= 3; i++) {
            	hearts[i - 1].draw(g, GAMEWIDTH - (40 * i), 60);
            }
            
            amountScrolled = 0;
            superScroll = false;
            
            luke.x = 10;
            luke.y = 10;
            
            luke.attacking = false;
            luke.pauseMovement = false;
            
            //clear graphics and flip buffer
            g.dispose();
            strategy.show();
            
            lastLoopTime = System.currentTimeMillis();
            
            
		//} // while
	} // goToNextLevel
	
	public void stopJumping() {
		jumping = false;
	}
	
	public boolean getJumping() {
		return jumping;
	}
	
	public static boolean enemyOnPlatform(Entity e) {
		return false;
	} // enemyOnPlatform
	
	public void lose() {
		// draw lose screen and wait for input
        gameStarted = false;
        while (!gameStarted) {
            // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,GAMEWIDTH,GAMEHEIGHT);
            g.setColor(new Color(100,100,175));
            g.setFont(new Font(Font.SERIF,Font.PLAIN,50));
            g.drawString("You Died.", GAMEWIDTH / 2 - 110, 60);
            g.setColor(new Color(50,100,225));
            g.setFont(new Font(Font.SERIF,Font.PLAIN,20));
            g.drawString("Press enter to try again.", 40, 150);

	            // clear graphics and flip buffer
            g.dispose();
            strategy.show();
        } // while
		                
        // reset to start of level

        goToNextLevel();
        //gameStarted = true;
        
	} // lose
	
	private void loadLvlMap() {
		if (lvl == 1) {
			map = new TileMap("level1.txt", this);
			levelHeight = map.getHeight() * tileSize;
			for (int i = 0; i < entities.size(); i++) {
				entities.get(i).setMap();
			}
		}
		else if (lvl == 2) {
			map = new TileMap("level2.txt", this);
			levelHeight = map.getHeight() * tileSize;
			for (int i = 0; i < entities.size(); i++) {
				entities.get(i).setMap();
			}
		}
		else if (lvl == 3) {
			
		}
	}
	
	private class KeyInputHandler extends KeyAdapter {
        
       /* The following methods are required
        * for any class that extends the abstract
        * class KeyAdapter.  They handle keyPressed,
        * keyReleased and keyTyped events.
        */
        public void keyPressed(KeyEvent e) {
	         
	         // respond to move left, right or fire
	         if (e.getKeyCode() == KeyEvent.VK_LEFT) {
	        	 leftPressed = true;
	         } // if
	
	         if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
	        	 rightPressed = true;
	         } // if
	       
	         if (e.getKeyCode() == KeyEvent.VK_UP) {
	       	  	upPressed = true;
	         } // if
	         
	         if (e.getKeyCode() == KeyEvent.VK_SPACE) {
	        	 attackPressed = true;
	         }
	         if (e.getKeyChar() == KeyEvent.VK_ENTER) {
	        	 gameStarted = true;
	        	 luke.x = 10;
	        	 luke.y = 10;
	         }
	         if (e.getKeyCode() == KeyEvent.VK_X) {
	        	 forcePressed = true;
	         }
        } // keyPressed

        public void keyReleased(KeyEvent e) {
	         
	         // respond to move left, right or fire
	         if (e.getKeyCode() == KeyEvent.VK_LEFT) {
	        	 leftPressed = false;
	         } // if
	
	         if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
	        	 rightPressed = false;
	         } // if
	
	         if (e.getKeyCode() == KeyEvent.VK_UP) {
	       	  	upPressed = false;
	         } // if
	         
	         if(e.getKeyCode() == KeyEvent.VK_SPACE) {
	        	 attackPressed = false;
	         }
	         if (e.getKeyCode() == KeyEvent.VK_X) {
	        	 forcePressed = false;
	         }
		} // keyReleased

	    public void keyTyped(KeyEvent e) {
	          // if escape is pressed, end game
	          if (e.getKeyChar() == 27) {
	            System.exit(0);
	          } // if escape pressed
	    } // keyTyped

	} // class KeyInputHandler
	
	public static void main(String[] args) {
		new Game();
	} // main
} // class Game