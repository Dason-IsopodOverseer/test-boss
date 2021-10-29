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
    		// if luke is on the enemy's left
    		if (x + (this.getWidth() / 2) < other.x + (other.getWidth() / 2)) {
    			// if luke would hit left edge
    			if (x - 50 < 1) {
    				x = 1;
    			}
    			// if luke would hit a tile to the left
    			else if (map.getTile(((int) x - 51) / 96, bottom / 96) != null) {
    				x = ((int) (x - 50) / 96 * 96) + 96;
    			}
    			else {
    				x = x - 50;
    			}
    		}
    		// if luke is on the enemy's right
    		else {
    			// if luke would hit right edge
    			if (other.x + other.getWidth() + 51 > 960 - this.getWidth() - 1) {
    				x = 960 - this.getWidth() - 1;
    			}
    			// if luke would hit a tile
    			else if (map.getTile(((int) other.x + other.getWidth() + 51) / 96, bottom / 96) != null) {
    				x = (int) (other.x + other.getWidth() + 50) / 96 * 96;
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
    	if (map.getTile(right / 96, (bottom + 1) / 96) != null && map.getTile(left / 96, (bottom + 1) / 96) != null) {
    		if (map.getTile(right / 96, (bottom + 1) / 96) == map.getTile(left / 96, (bottom + 1) / 96)) {
    			s = map.tileConfig.get((bottom + 1) / 96);
	    		return s.charAt(right / 96);
    		}
    	}
    	return 'x';
    }
   
}