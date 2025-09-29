import java.util.ArrayList;

public class comGUI
{
	public static final int tableIndex = 0;
	public static final int myPlayerOpenIndex = 1;
	public static final int rightPlayerIndex = 2;
	public static final int upPlayerIndex = 3;
	public static final int leftPlayerIndex = 4;
	public static final int myPlayerHandIndex = 5;
	
	private int numRightPlayer, numUpPlayer, numLeftPlayer;
	private ArrayList<Tile> rightPlayerOpen;
	private ArrayList<Tile> upPlayerOpen;
	private ArrayList<Tile> leftPlayerOpen;
	private ArrayList<Tile> myPlayerOpen;
	private ArrayList<Tile> table;
	public mainGUI frame;
	
	public PlayerGUI player;
	
	public comGUI()
	{
		numLeftPlayer = 0;
		numRightPlayer = 0;
		numUpPlayer = 0;
		
		leftPlayerOpen = new ArrayList<Tile>();
		upPlayerOpen = new ArrayList<Tile>();
		rightPlayerOpen = new ArrayList<Tile>();
		myPlayerOpen = new ArrayList<Tile>();
		table = new ArrayList<Tile>();
		
		frame = new mainGUI();
	}
	
	public void initPlayerGUI(String name, int score, comGUI _c)
	{
		player = new PlayerGUI(name, score);
		player.c = _c;
	}
	
	public void renewGUI()
	{
		ArrayList<ArrayList<Tile>> temp = new ArrayList<ArrayList<Tile>>();
		temp.add(table);
		temp.add(myPlayerOpen);
		temp.add(rightPlayerOpen);
		temp.add(upPlayerOpen);
		temp.add(leftPlayerOpen);
		
		player.getHand();
		temp.add(player.myHand);
		
		int[] tempNum = new int[3];
		tempNum[0] = numRightPlayer;
		tempNum[1] = numUpPlayer;
		tempNum[2] = numLeftPlayer;
		
		frame.setAllContent(temp, tempNum);
		frame.reset();
	}
	
	public void assignTile(ArrayList<ArrayList<Tile>> allTile)
	{
		table = allTile.get(tableIndex);
		rightPlayerOpen = allTile.get(rightPlayerIndex);
		leftPlayerOpen = allTile.get(leftPlayerIndex);
		upPlayerOpen = allTile.get(upPlayerIndex);
		myPlayerOpen = allTile.get(myPlayerOpenIndex);
	}
	
	public void assignTile(int which, ArrayList<Tile> allTile)
	{
		if(which == tableIndex)
			table = allTile;
		else if(which == rightPlayerIndex)
			rightPlayerOpen = allTile;
		else if(which == leftPlayerIndex)
			leftPlayerOpen = allTile;
		else if(which == upPlayerIndex)
			upPlayerOpen = allTile;
		else if(which == myPlayerOpenIndex)
			myPlayerOpen = allTile;
	}
	
	public void assignHandNum(int which, int num)
	{
		if(which == rightPlayerIndex)
			numRightPlayer = num;
		else if(which == leftPlayerIndex)
			numLeftPlayer = num;
		else
			numUpPlayer = num;
	}
	
	public void assignHandNum(int[] num)
	{
		numRightPlayer = num[0];
		numUpPlayer = num[1];
		numLeftPlayer = num[2];
	}
	
	public void assignExposedKongNum(int which, int num)
	{
		if(which == rightPlayerIndex) {
		} else if(which == leftPlayerIndex) {
		} else {
		}
	}
	
	public void flipTile(int index, ArrayList<Tile> tile)
	{
		ArrayList<Tile> temp = new ArrayList<Tile>();
		for(Tile t : tile)
			for(int i = 0; i < t.getSize(); i++){
				temp.add(new Tile(t.index));
			}
		
		if(index == 0)
			frame.setFlip(index, temp);
		else if(index == 1)
			frame.setFlip(index, temp);
		else if(index == 2)
			frame.setFlip(index, temp);
		renewGUI();
	}
	
	public boolean showWind(int wind, int game)
	{
		frame.showWind(wind, game);
		frame.nok = false;
		if(game == -1){
			while(frame.nok == false){}
			System.out.println("restart");
		}
		else return false;
		return frame.restart;
	}
	
	public void showGUI()
	{
		frame.start();
	}
}