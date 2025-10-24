import java.awt.EventQueue;
import java.util.ArrayList;

public class PlayerGUI extends Player
{
	public ArrayList<Tile> myHand = new ArrayList<Tile>();
	private Tile newTile;
	private ArrayList<Tile> discardTile;
	private ArrayList<Tile> pushTile;
	private boolean[] choice;
	private int action;
	
	public comGUI c;
	
	public PlayerGUI(String name, int score)
	{
		super(name, score);
		myHand = new ArrayList<Tile>();
	}
	
	public void failed()
	{
		c.frame.actionFail();
		for(int i = discardTile.size()-1; i > 0; i--)
			hand.add(discardTile.get(i));
		c.renewGUI();
	}
	
	@Override
	public void initHand(ArrayList<ArrayList<Tile>> allTiles)
	{
		c.frame.setFlip(-1, new ArrayList<Tile>());
		hand = new Hand(allTiles);
		getHand();
	}
	
	public Action doSomething(int from, Tile tile)
	{
		c.frame.resetChoice();
		action = -1;
		
		newTile = tile.copy();
		doSelect(from, newTile);
		
		if(action == -1)
			return null;
		return new Action(action, discardTile);
	}
	
	private void doSelect(int from, Tile newTile)
	{
		boolean[] b = {false, false, false, false, false}; /*可做 吃, 碰, 槓, 聽, 胡*/ //{Pode comer, tocar, tocar, ouvir, bagunçar}
		
		int tempType = hand.chowable(newTile);
		ArrayList<Tile> temp = hand.tingable(newTile);
		
		if(tempType != 0 && from == 3){
			b[0] = true;
			c.frame.setChowOption(tempType, getChewChoice(tempType, newTile));
		}
		
		if(from != 0)
			b[1] = hand.pongable(newTile);
		
		b[2] = hand.kongable(newTile);
		
		if(temp == null)
			b[4] = true;
		else if(temp.size() != 0 && from == 0){
			//b[3] = true;
		}
		
		if(from == 0){
			hand.add(newTile);
		}
		c.frame.setThrower(from, newTile);
		
		if(b[0] || b[1] || b[2] || b[3] || b[4]){
			c.frame.setSelect(b);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						c.frame.frameOpen();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			waitOK();
		}
		
		choice = c.frame.getChoice();
		selectProcess(tempType, newTile, from);
	}
	
	private void waitOK()
	{
		while(c.frame.ok == false){}
		pushTile = new ArrayList<Tile>();
		for(Tile t : c.frame.push)
			pushTile.add(t.copy());
		
		c.frame.ok = false;
		c.frame.push = new ArrayList<Tile>();
	}
	
	private void selectProcess(int chewType, Tile newTile, int from)
	{
		discardTile = new ArrayList<Tile>();
		discardTile.add(new Tile(0));
		if(choice[0]){
			action = 1;
			ArrayList<ArrayList<Tile>> chowOption = getChewChoice(chewType, newTile);
			if(chowOption.size() == 1){
				for(int i = 0; i < 3; i++)
					discardTile.add(chowOption.get(0).get(i));
			}
			else{
				for(int i = 0; i < 3; i++)
					discardTile.add(pushTile.get(i).copy());
			}
		}
		else if(choice[1]){
			action = 2;
			for(int i = 0; i < 3; i++)
				discardTile.add(newTile.copy());
		}
		else if(choice[2]){
			action = 3;
			if(from == 0)
				action = 5;
			for(int i = 0; i < 4; i++)
				discardTile.add(newTile.copy());
		}
		else if(choice[3]){
			action = 6;
			discardTile.set(0, pushTile.get(0));
			return;
		}
		else if(choice[4])
		{
			if(from == 0)
				action = 8;
			else{
				action = 7;
				hand.add(newTile);
			}
			getHand();
			discardTile.remove(0);
			for(int i = 0; i < myHand.size(); i++)
				discardTile.add(myHand.get(i));
			return;
		}
		else{
			if(from == 0)
				action = 0;
			else{
				action = -1;
				return;
			}
		}
		
		for(int i = 1; i < discardTile.size() - 1; i++)
			hand.discard(discardTile.get(i));
		c.renewGUI();
		
		if(choice[2]){
			discardTile.remove(0);
		}
		else{
			c.frame.showThrowTile(true);
			waitOK();
			discardTile.set(0, pushTile.get(0));
			hand.discard(discardTile.get(0));
		}
		for(Tile t: discardTile)
			System.out.println(t);
		
		c.frame.resetChoice();
		c.renewGUI();
	}
	
	private ArrayList<ArrayList<Tile>> getChewChoice(int flag, Tile newTile)
	{
		ArrayList<ArrayList<Tile>> temp = new ArrayList<ArrayList<Tile>>();
		if((flag & 0b001) > 0){
			ArrayList<Tile> temp1 = new ArrayList<Tile>();
			temp1.add(newTile.getAdjacentTile(-2));
			temp1.add(newTile.getAdjacentTile(-1));
			temp1.add(newTile.copy());
			temp.add(temp1);
		}
		if((flag & 0b010) > 0){
			ArrayList<Tile> temp1 = new ArrayList<Tile>();
			temp1.add(newTile.getAdjacentTile(-1));
			temp1.add(newTile.getAdjacentTile(1));
			temp1.add(newTile.copy());
			temp.add(temp1);
		}
		if((flag & 0b100) > 0){
			ArrayList<Tile> temp1 = new ArrayList<Tile>();
			temp1.add(newTile.getAdjacentTile(1));
			temp1.add(newTile.getAdjacentTile(2));
			temp1.add(newTile.copy());
			temp.add(temp1);
		}
		return temp;
	}
	
	void getHand()
	{
		int length = myHand.size();
		for(int i = 0; i < length; i++)
			myHand.remove(0);
		for(ArrayList<Tile> temp : hand.getAll())
			for(Tile t : temp)
				for(int i = 0; i < t.getSize(); i++){
					myHand.add(new Tile(t.getIndex()));
				}
	}
	
	public void GameOver(int type, int from)
	{
		c.frame.hu(type, from);
	}
}