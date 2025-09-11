import java.lang.*;
import java.util.*;

public class Board{
	public static final int initScore = 25000;
	public static final int games = 1;	//1東風戰 2東南戰... {1 Guerra Dongfeng, 2 Guerra Sudeste...}
	public static int wind;	//0東 1南 2西 3北 {0 Leste 1 Sul 2 Oeste 3 Norte}
	public static int game;	//現在的局數-1 {Contagem de jogos atual -1}
	public static String[] actionString = {
	 "",
	 "comer", // comer 吃
	 "ressalto", // ressalto 碰
	 "barra", // barra 槓
	 "Adicionar uma barra", // Adicionar uma barra 加槓
	 "barra escondida", // barra escondida 暗槓
	 "fique em linha reta", // fique em linha reta 立直
	 "glória", // glória 榮
	 "Hu" // Hu 胡
	};
	public static int dealer; //一開始的莊家 {O banqueiro no início}
	private static Shuffler shuffler;
	private static comGUI GUI;
	public static void printTiles(ArrayList<Tile> tiles){
		for(Tile t:tiles){
			System.out.print(t.toString()+t.getSize()+",");
		}		
	}
	public static void main(String args[]){
		wind = 0;
		dealer = 0;	//maybe we should decide this randomly? {talvez devêssemos decidir isso aleatoriamente?}
		game = 0;
		shuffler = new Shuffler();
		GUI = new comGUI();
		Player[] player = new Player[4];
		ArrayList<ArrayList<Tile>> allTiles = new ArrayList<ArrayList<Tile>>();// 0萬 1筒 2條 3字 {0 = Dez mil, 1 = cilindro, 2 = faixa, 3 = Personagem}
		ArrayList<ArrayList<Tile>> table = new ArrayList<ArrayList<Tile>>();
		int[] left = {0, 0, 0, 0};
		GUI.initPlayerGUI("PlayerGUI", initScore, GUI);
		player[0] = GUI.player;
		table.add(new ArrayList<Tile>());	//河底 {fundo do rio}
		for(int i = 1; i < 4 ; i++){
			player[i] = new AI("PlayerAI"+i, initScore);
		}
		for(int i = 0 ; i < 4 ; i++){
			allTiles.add(new ArrayList<Tile>());
			table.add(new ArrayList<Tile>());	//副露 {Vice-exposição}
		}
		while(true){
			
			table.get(0).clear();	//清空河底 {Limpe o fundo do rio}

			//init 4 players' hands and tables
			for(int i = 0 ; i < 4 ; i++){
				table.get(i+1).clear();//清空副露 {efeitos colaterais claros}
				left[i] = 13;//手牌13張 {13 cartas na mão}
				if(i>0)GUI.assignHandNum(i+1, left[i]);
				for(int j = 0 ; j < 4 ; j++){
					allTiles.get(j).clear();
				}
				for(int j = 0 ; j < 13 ; j++){
					Tile tmpTile = shuffler.getNext();
					allTiles.get(tmpTile.suit).add(tmpTile);
				}
				player[i].initHand(allTiles);
			}
			GUI.showWind(wind, game+1);
			GUI.renewGUI();
			GUI.showGUI();
			
			int gameOver = 0;
			int current = (dealer+game)%4;//看第幾局決定輪到誰做莊，莊家開始，抽牌、決定動作 {Determine de quem é a vez de ser o dealer, dependendo da rodada em que o dealer está. O dealer começa, compra cartas e decide as ações.}
			Tile tile = shuffler.getNext();
			Action action = player[current].doSomething(0, tile);
			while(gameOver == 0){
				System.out.println("DEBUG: "+"wind: "+wind+"game: "+game+player[current]+actionString[action.type]+".");
				switch(action.type){//執行動作 {executar a ação}
					case 0:	//摸 {tocar}
					case 1:	//吃 {comer}
					case 2:	//碰 {ressalto}
					case 6:	//立直 {fique em linha reta}
						if(current>0){//手牌減少 {Mãos reduzidas}
							left[current]-= (action.tiles.size()-1);
							GUI.assignHandNum(current+1, left[current]);
						}
						for(int i = 1 ; i < action.tiles.size() ; i++){	//副露 {Vice-exposição}
							table.get(current+1).add(action.tiles.get(i));
						}
						GUI.assignTile(table);
						GUI.renewGUI();
						tile = action.tiles.get(0);	//打出來的牌 {cartas jogadas}
						Action selectAction = null;
						int selectPlayer = -1;
						for(int i = 1 ; i < 4 ; i++){//問另外三家有沒有事情要做 {Pergunte às outras três empresas se elas têm algo a fazer.}
							int p = (current+i)%4;
							System.out.println("wait "+p+" "+tile+" "+tile.getSize());
							action = player[p].doSomething(4-i, tile);
							if(action == null) continue;
							System.out.println(p+" "+actionString[action.type]);
							if(selectPlayer == -1 || action.type > selectAction.type){
								if(selectPlayer != -1)player[selectPlayer].failed();
								selectAction = action;
								selectPlayer = p;
							}
							else player[p].failed();
						}
						if(selectAction != null){//執行最優先動作, 榮>碰>吃, 設定好動作、玩家後continue跳到該玩家執行動作，未考慮同時榮的情形:p {Execute a ação de maior prioridade, glória> toque> comer, após definir a ação e o jogador, continue saltando para o jogador para realizar a ação, sem considerar a situação de glória simultânea:p}
							action = selectAction;
							current = selectPlayer;
							continue;
						}
						else{//換下一家，到switch外面抽牌、決定動作 {Mude para a próxima casa, saia do switch para comprar cartas e decidir a ação}
							table.get(0).add(tile);
							GUI.assignTile(table);
							GUI.renewGUI();
							current = (current+1)%4;
						}
						break;
					case 3:	//槓 {bar}
					case 4:	//加槓 {Adicionar uma barra}
					case 5:	//暗槓 {barra escondida}
						if(current>0){//手牌減少 {Mãos reduzidas}
							left[current]-= (action.tiles.size());
							GUI.assignHandNum(current+1, left[current]);
						}
						for(int i = 0 ; i < action.tiles.size() ; i++){	//槓從0開始算副露 {Kong começa a contar a partir de 0}
							table.get(current+1).add(action.tiles.get(i));
						}
						GUI.assignTile(table);
						GUI.renewGUI();
						shuffler.ackKong();
						break;	//目前玩家補一張，到switch外面抽牌、決定動作 {O jogador atualmente compra uma carta e sai do switch para comprar cartas e decidir ações.}
					case 7:	//榮 {glória}
					case 8:	//自摸 {Toque-se}
						printTiles(action.tiles);
						if(current != (dealer+game)%4){//當局莊家沒有連莊就要輪莊，進入下一局 {Se o banqueiro não tiver uma sucessão de banqueiros, ele recorrerá ao banqueiro e entrará na próxima rodada.}
							game++;
						}
						shuffler.permuteIndex();
						gameOver = 1;
						if(current>0)GUI.flipTile(current-1, action.tiles);
						for(int i = 0 ; i < 4 ; i++){
							if(action.type == 7)
								player[i].GameOver(1, (current-i+4)%4);	//告知player, current榮 {Diga ao jogador, atual}
							else
								player[i].GameOver(2, (current-i+4)%4);	//告知player, current自摸 {Diga ao jogador, a corrente toca nele}
						}
						break;
					default:
						System.out.println("ERROR: "+player[current]+" unknown action "+action.type+".");
						System.exit(1);
				}
				if(gameOver == 1)break;
				tile = shuffler.getNext();//switch外面指的是這裡^^ {A parte externa do switch refere-se aqui ^^}
				if(tile == null){//流局 {Situação perdida}
					gameOver = 1;
					for(int i = 0 ; i < 4 ; i++){
						player[i].GameOver(0, i);	//告知player流局 {Notifique o jogador sobre a situação}
					}
					break;
				}
				System.out.println("self "+current+" "+tile+" "+tile.getSize());
				action = player[current].doSomething(0, tile);
			}
			if(game == 4){	//打滿4局，南(?入 {Depois de jogar 4 partidas, Nan (?}
				wind = wind+1;
				game = 0;
			}
			if(wind == games){	//結束 {Terminar}
				if(GUI.showWind(wind, -1)){
					game = 0;
					wind = 0;
				}
				else{
					break;
				}
			}
		}

	}

}
