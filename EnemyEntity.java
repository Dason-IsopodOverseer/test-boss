public abstract class EnemyEntity extends Entity
{    
    protected final int fallingSpeed = 300;
	
    public EnemyEntity(final Game g, final String r, final int newX, final int newY, int speed) {
        super(r, newX, newY, true);
        game = g;
        dx = speed; // uses parent's speed
    }
    
    public void setMap() {
    	map = game.getTileMap();
    }
    
    public void move(long delta) {
    	dy = fallingSpeed;
    	super.move(delta);
    } // move
    
    protected boolean isTileCompletelyBelow(long delta) {
    	
    	// if entity's bottom-left or bottom-right corner is in a tile
     	try {
     		return map.getTile(right / 96, (int) (bottom + (delta * dy) / 1000 + 1) / 96) != null && map.getTile(left / 96, (int) (bottom + (delta * dy) / 1000 + 1) / 96) != null;
     	} catch (Exception e) {
     		return false;
     	} // catch
    } // isTileCompletelyBelow
    
    public void collidedWith(final Entity other) {
    }
}