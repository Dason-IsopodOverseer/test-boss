public class LukeEntity extends Entity
{   
    
    public LukeEntity(final Game g, final String r, final int newX, final int newY) {
        super(r, newX, newY, true);
        this.game = g;
        this.map = g.getTileMap();
    }
    
    public void setMap (TileMap m) {
    	map = m;
    }
    
    public void collidedWith(final Entity other) {
    	if (other instanceof EnemyEntity ) {
    		other.setHorizontalMovement(-other.getHorizontalMovement());
    		// if luke is on the enemy's left
    		if (x + (this.getWidth() / 2) < other.x + (other.getWidth() / 2)) {
    			// if luke would hit left edge
    			if (x - 50 < 1) {
    				x = 1;
    			}
    			// if luke would hit a tile to the left
    			else if (map.getTile(((int) x - 51) / tileSize, bottom / tileSize) != null || map.getTile(((int) x - 51) / tileSize, top / tileSize) != null) {
    				x = ((int) (x - 50) / tileSize * tileSize) + tileSize;
    			}
    			else {
    				x = x - 50;
    			}
    		}
    		// if luke is on the enemy's right
    		else {
    			// if luke would hit right edge
    			if (other.x + other.getWidth() + 51 > (tileSize * mapWidth - 10) - this.getWidth() - 1) {
    				x = (tileSize * mapWidth - 10) - this.getWidth() - 1;
    			}
    			// if luke would hit a tile
    			else if (map.getTile(((int) other.x + other.getWidth() + 51) / tileSize, bottom / tileSize) != null || map.getTile(((int) this.x + this.getWidth() + 51) / tileSize, top / tileSize) != null) {
    				x = (int) (other.x + other.getWidth() + 50) / tileSize * tileSize;
    			}
    			else {
    				x = other.x + other.getWidth() + 50;
    			}
    		}
    		
    		//System.out.println(game.health);
    	}
    }
    
    protected char getTileDirectlyBelow() {
    	String s = "test";
    	if (map.getTile(right / tileSize, (bottom + 1) / tileSize) != null && map.getTile(left / tileSize, (bottom + 1) / tileSize) != null) {
    		if (map.getTile(right / tileSize, (bottom + 1) / tileSize) == map.getTile(left / tileSize, (bottom + 1) / tileSize)) {
    			s = map.tileConfig.get((bottom + 1) / tileSize);
	    		return s.charAt(right / tileSize);
    		}
    	}
    	return 'x';
    }
}