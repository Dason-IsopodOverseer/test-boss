/* Entity.java
 * An entity is any object that appears in the game.
 * It is responsible for resolving collisions and movement.
 */
 
 import java.awt.*;
 
 public abstract class Entity {

    // Java Note: the visibility modifier "protected"
    // allows the variable to be seen by this class,
    // any classes in the same package, and any subclasses
    // "private" - this class only
    // "public" - any class can see it
    protected double x;   // current x location
    protected double y;   // current y location
    protected Sprite sprite; // this entity's sprite
    protected double dx; // horizontal speed (px/s)  + -> right
    protected double dy; // vertical speed (px/s) + -> down
    protected int left;
    protected int right;
    protected int top;
    protected int bottom;
    protected TileMap map;
    protected Game game;
    protected boolean beingPushed = false;
    protected int pushTarget = 0;
    protected boolean pushedRight = false;
    
    public boolean pauseMovement = false;
    public boolean attacking = false;

    // the following variables control animations for this entity
    private boolean animated = false;
	private String[] moveLeft = new String[3]; // stores an array of images for moving left
	private String[] moveRight = new String[3]; // stores an array of images for moving right
	private String[] jump = new String[3]; // stores an array of images for jumping
	private String[] attack = new String[3]; // stores an array of images for attacking
	private String[] hurt = new String[3]; // stores an array of images for being hurt
	protected boolean isFacingRight = true; // true if sprite is facing right
	protected int frame = 0; // stores the specific frame the game is on (refreshes every 600 frames)
    
    private Rectangle me = new Rectangle(); // bounding rectangle of
                                            // this entity
    private Rectangle him = new Rectangle(); // bounding rect. of other
                                             // entities
    // higher values result in slower FPS
    private int refreshRate = 500;
                                             
    /* Constructor
     * input: reference to the image for this entity,
     *        initial x and y location to be drawn at
     */
     public Entity(String r, int newX, int newY, boolean animated) {
       x = newX;
       y = newY;
       if (animated) {
    	   this.animated = true;
    	   setAnimations(r);
       } else {
    	   sprite = (SpriteStore.get()).getSprite(r);
       }
     } // constructor
     
     /* determines and sets entity animation variables
      * selects direction of entity
      * selects correct image to use
      */
     private void setAnimations(String entityName) {
    	 
    	 // configure all moveLeft images
    	 for (int i = 0; i < moveLeft.length; i++) {
    		 moveLeft[i] = "sprites/" + entityName + "/L" + i + ".png";
    	 }
    	 
    	 // configure all moveRight images
    	 for (int i = 0; i < moveRight.length; i++) {
    		 moveRight[i] = "sprites/" + entityName + "/R" + i + ".png";
    	 }
    	 
    	// configure all moveUp images
    	 for (int i = 0; i < jump.length; i++) {
    		 jump[i] = "sprites/" + entityName + "/U" + i + ".png";
    	 }
    	 
    	 // configure all attack images
    	 for (int i = 0; i < attack.length; i++) {
    		 attack[i] = "sprites/" + entityName + "/AT" + i + ".png";
    	 }
    	 
    	 for (int i = 0; i < hurt.length; i++) {
    		 hurt[i]= "sprites/" + entityName + "/DMG" + i + ".png";
    	 }
    	 
    	 // configure initial state of entity
    	 sprite = (SpriteStore.get()).getSprite(moveRight[0]);
     }
    
     /* Takes an entity state (jumping, moving, attacking) and
      * changes the sprite to match the given animation.
      * 
      */
     public void updateAnimations() {
    	 frame++;
    	 if (frame > (moveRight.length) * refreshRate) {
    		 frame = 0;
    	 }
    	 if (frame != 0 && (frame - refreshRate) % refreshRate == 0 && animated) {
    		 int index = (frame - refreshRate) / refreshRate;
    		 
             // update direction
    		 if (pauseMovement) {
    			 refreshRate = 50;
				 sprite = (SpriteStore.get()).getSprite(hurt[index]);
			 }
    		 else {
    			 refreshRate = 500;
    			 if (attacking) {
    				 sprite = (SpriteStore.get()).getSprite(attack[0]);
    			 } 
    			 
    			 else if (dy < 0) {
        			 sprite = (SpriteStore.get()).getSprite(jump[index]);
        		 } else {
        			 if (dx < 0) {
            			 isFacingRight = false;
            			 sprite = (SpriteStore.get()).getSprite(moveLeft[index]);
            		 } else if (dx > 0) {
            			 isFacingRight = true;
            			 sprite = (SpriteStore.get()).getSprite(moveRight[index]);
            		 } else {
            			 String idle =  isFacingRight ? moveRight[0] : moveLeft[0];
            			 sprite = (SpriteStore.get()).getSprite(idle);
            		 }
        		 } // elseS
    		 }
			 
    	 }
     }
     

     public void setMap() {}
     /* move
      * input: delta - the amount of time passed in ms
      * output: none
      * purpose: after a certain amout of time has passed,
      *          update the location
      */
     public void move(long delta) {
    	 left = (int) x;
         right = left + sprite.getWidth();
         top = (int) y;
         bottom = top + sprite.getHeight();
    	 if (!beingPushed) {
	    	 // check if it'll hit a platform
	         if (dx < 0 && x < 0) {
	             dx = -dx;
	         } else if (dx > 0 && x > 950) {
	             dx = -dx;
	         } else if (dx < 0 && isTileLeft(delta)) {
	             dx = -dx;
	         } else if (dx > 0 && isTileRight(delta)) {
	             dx = -dx;
	         }    
	         
    	 	} else {
	    	if (pushedRight) {
	    		x += delta * 100.0 / 1000;
	    		if (x > pushTarget) {
	    			beingPushed = false;
	    		} // if
	    	} else {
	    		x -= delta * 100.0 / 1000;
	    		if (x < pushTarget) {
	    			beingPushed = false;
	    		} // if
	    	} // else
	    } // else
    	 
    	 if (dy < 0 && isTileAbove(delta)) {
         	dy = 0;
         	game.stopJumping();
         } else if (isTileBelow(delta) && (!game.getJumping() || !(this instanceof LukeEntity))) {
             dy = 0;
         }
    	 
    	 // update location of entity based on move speeds
    	 // update location of entity based on move speeds
         x += (delta * dx) / 1000;
    	 y += (delta * dy) / 1000;
     } // move

     // get and set velocities
     public void setHorizontalMovement(double newDX) {
       dx = newDX;
     } // setHorizontalMovement

     public void setVerticalMovement(double newDY) {
       dy = newDY;
     } // setVerticalMovement

     public double getHorizontalMovement() {
       return dx;
     } // getHorizontalMovement

     public double getVerticalMovement() {
       return dy;
     } // getVerticalMovement

     // get position
     public int getX() {
       return (int) x;
     } // getX

     public int getY() {
       return (int) y;
     } // getY
     
     public int getHeight() {
    	 return sprite.getHeight();
     }
     
     public int getWidth() {
    	 return sprite.getWidth();
     }
     
     public void pushBackTo(int target, boolean pushedRight) {
    	 beingPushed = true;
    	 pushTarget = target;
    	 this.pushedRight = pushedRight;
     }
     
     protected boolean isTileAbove(long delta) {

         // if entity's top-left or top-right corner is in a tile
         return map.getTile (right / 96, (int) (top + (delta * dy) / 1000 - 1) / 96) != null || map.getTile(left / 96, (int) (top + (delta * dy) / 1000 - 1) / 96) != null;
     }
     
     public boolean isTileBelow(long delta) {
     	
     	// if entity's bottom-left or bottom-right corner is in a tile
     	try {
     		return map.getTile(right / 96, (int) (bottom + (delta * dy) / 1000 + 1) / 96) != null || map.getTile(left / 96, (int) (bottom + (delta * dy) / 1000 + 1) / 96) != null;
     	} catch (Exception e) {
     		return false;
     	}
     	
     }
     
     protected boolean isTileLeft(long delta) {
         
     	// if entity's top-left or bottom-left corner is in a tile
         return map.getTile((int) (left + (delta * dx) / 1000 - 1) / 96, top / 96) != null || map.getTile((int) (left + (delta * dx) / 1000 - 1) / 96, bottom / 96) != null;
     }
     
     protected boolean isTileRight(long delta) {
         
     	// if entity's top-right or bottom-right corner is in a tile
     	try {
     		return map.getTile((int) (right + (delta * dx) / 1000 + 1) / 96, top / 96) != null || map.getTile((int) (right + (delta * dx) / 1000 + 1) / 96, bottom / 96) != null;
     	} catch (Exception e) {
     		return true;
     	}
     }
     

    /*
     * Draw this entity to the graphics object provided at (x,y)
     */
     public void draw (Graphics g, double translateY) {
       sprite.draw(g,(int)x,(int) (y + translateY));
     }  // draw
     
    /* Do the logic associated with this entity.  This method
     * will be called periodically based on game events.
     */
     public void doLogic() {}
     
     /* collidesWith
      * input: the other entity to check collision against
      * output: true if entities collide
      * purpose: check if this entity collides with the other.
      */
     public boolean collidesWith(Entity other) {
       me.setBounds((int)x, (int)y, sprite.getWidth(), sprite.getHeight());
       him.setBounds(other.getX(), other.getY(), 
                     other.sprite.getWidth(), other.sprite.getHeight());
       return me.intersects(him);
     } // collidesWith
     
     /* collidedWith
      * input: the entity with which this has collided
      * purpose: notification that this entity collided with another
      * Note: abstract methods must be implemented by any class
      *       that extends this class
      */
      public abstract void collidedWith(Entity other);

 } // Entity class