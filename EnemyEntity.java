public abstract class EnemyEntity extends Entity {    
    protected final int fallingSpeed = 300;
    private String type = "";
    
	// the following variables control entity status
	protected byte health = 1;
	protected double moveSpeed = 0;
	
    public EnemyEntity(final Game g, final String r, final int newX, final int newY, int speed) {
        super(r, newX, newY, true);
        game = g;
        type = r;
        dx = speed; // uses parent's speed
    }
    
    public void setMap() {
    	map = game.getTileMap();
    }
    
    public void move(long delta) {
    	if (type != "master") {
    		dy = fallingSpeed;
    	}
    	super.move(delta);
    } // move
    
    protected boolean isTileCompletelyBelow(long delta) {
    	
    	// if entity's bottom-left or bottom-right corner is in a tile
     	try {
     		return map.getTile(right / tileSize, (int) (bottom + (delta * dy) / 1000 + 1) / tileSize) != null && map.getTile(left / tileSize, (int) (bottom + (delta * dy) / 1000 + 1) / tileSize) != null;
     	} catch (Exception e) {
     		return false;
     	} // catch
    } // isTileCompletelyBelow
    
    public void collidedWith(final Entity other) {
    }
}