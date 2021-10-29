public class BorgEntity extends EnemyEntity {
	private final int speed = 10;
	private boolean isQueen = false;
	private int interval = 0;
	private int count = 0;
	
    public BorgEntity(final Game g, final String r, final int newX, final int newY) {
        super(g, r, newX, newY, 50);
        
        if (r.equals("queen")) {
        	isQueen = true;
        }
    }
    
    public void move(long delta) {
    	super.move(delta);
    	interval++;
    	if (interval == 300) {
    		interval = 0;
   	 	}
    	
    	// check if this entity should turn around
		if (isTileBelow() && !isTileCompletelyBelow()) {
			dx = -dx;
			x += (delta * dx) / 500;
		} // if
		
		// configure additional movements depending on type of enemy
		if (isQueen && interval == 0) {
			count++;
			
			// queen sometimes turns around randomly
			int random = (int)(Math.random() * 100);
			if (random == 0) {
				dx = -dx;
			}
			
			// queen sometimes spawns a drone
			if (count % 20 == 0) {
				Entity b = new BorgEntity(game, "borg", this.getX() + 100, this.getY());
				game.entities.add(b);
				b.setMap();
				
			}
		}
    } // move
    
    public void collidedWith(final Entity other) {
    }
}
