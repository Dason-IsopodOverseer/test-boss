public class KlingonEntity extends EnemyEntity {
	private double moveSpeed = 50;
    private String type = "";
    private byte count = 0;
	
    public KlingonEntity(final Game g, final String r, final int newX, final int newY) {
        super(g, r, newX, newY, 50);
       
        type = r;
        if (!type.equals("kling")) {
        	// warriors and masters are very fast
        	moveSpeed *= 1.5;
        }
        
        dx = moveSpeed;
    }
    
    public void move(long delta) {
    	if (frame % 5 == 0) {
    		count++;
    	}
    	if (count > 100) {
    		count = 0;
    	}
    	
    	if (type.equals("kling")) {
    		super.move(delta);
    		if (count == 0) {
    			
    		}
    	} else if (game.luke.getY() + 100 >= y) {
    		
    		// warriors and dahar masters chase after player
    		if (game.luke.getX() < x) {
    			dx = -Math.abs(dx);
    		} else {
    			dx = Math.abs(dx);
    		}
    		super.move(delta);
    		
    		if (type.equals("warrior") && frame % 2 == 0) {
    			
    			// configure additional movements depending on type of enemy
        	} else if (type.equals("warrior") && frame % 2 == 0) {
        		
        	}
    	}
    } // move
    
    public void collidedWith(final Entity other) {
    }
}