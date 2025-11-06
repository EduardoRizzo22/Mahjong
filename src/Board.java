import java.util.*;

/**
 * Classe principal que gerencia o tabuleiro e a lógica central do jogo Mahjong.
 * 
 * <p>Responsável por:
 * <ul>
 *   <li>Controlar o fluxo do jogo e turnos dos jogadores</li>
 *   <li>Gerenciar o embaralhamento e distribuição de peças</li>
 *   <li>Processar ações dos jogadores (draw, chow, pong, kong, riichi, ron, hu)</li>
 *   <li>Determinar condições de vitória e fim de jogo</li>
 *   <li>Atualizar a interface gráfica (GUI) com o estado do jogo</li>
 * </ul>
 * </p>
 * 
 * <p>O jogo segue as regras do Mahjong japonês (Riichi Mahjong), com suporte
 * para 4 jogadores e sistema de ventos (東風戰).</p>
 */
public class Board{
	
	/** Pontuação inicial de cada jogador */
	public static final int initScore = 25000;
	
	/** Número de rodadas do jogo (1 = 東風戰, 2 = 東南戰) */
	public static final int games = 1;
	
	/** Vento atual do jogo (0=Leste, 1=Sul, 2=Oeste, 3=Norte) */
	public static int wind;
	
	/** Número da rodada atual (0-indexed) */
	public static int game;
	
	/** Strings descritivas para cada tipo de ação do jogo */
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
	
	/** Índice do jogador que é o dealer/banqueiro inicial */
	public static int dealer;
	
	/** Embaralhador responsável pela distribuição aleatória das peças */
	private static Shuffler shuffler;
	
	/** Interface gráfica do jogo */
	private static comGUI GUI;

	/**
	 * Imprime as peças em formato legível para debug.
	 * 
	 * @param tiles Lista de peças a serem impressas
	 */
	public static void printTiles(List<Tile> tiles){
		for(Tile t:tiles){
			System.out.print(t.toString()+t.getSize()+",");
		}		
	}

	/**
	 * Adiciona um delay para melhor visualização das jogadas da IA.
	 * 
	 * <p>Jogadores humanos não sofrem delay. Apenas jogadores controlados
	 * por IA (IDs 1, 2, 3) terão uma pausa de 1 segundo antes de cada jogada,
	 * permitindo que o usuário acompanhe melhor a estratégia dos oponentes.</p>
	 * 
	 * @param playerId ID do jogador (0 = humano, 1-3 = IA)
	 */
	private static void addAIDelay(int playerId) {
		if(playerId > 0) { // Apenas para IAs (jogadores 1, 2, 3)
			try {
				Thread.sleep(1000); // 1 segundo de delay
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método principal que inicia e executa o jogo Mahjong.
	 * 
	 * <p>Fluxo de execução:
	 * <ol>
	 *   <li>Inicializa variáveis globais (vento, dealer, rodada)</li>
	 *   <li>Cria 4 jogadores (1 humano + 3 IAs)</li>
	 *   <li>Embaralha e distribui 13 peças para cada jogador</li>
	 *   <li>Inicia loop principal do jogo</li>
	 *   <li>Processa turnos e ações dos jogadores</li>
	 *   <li>Verifica condições de vitória (Ron, Hu) ou empate</li>
	 *   <li>Avança rodadas e ventos conforme regras do Mahjong</li>
	 * </ol>
	 * </p>
	 * 
	 * <p>O jogo continua até que todas as rodadas configuradas sejam completadas.</p>
	 * 
	 * @param args Argumentos de linha de comando (não utilizados)
	 */
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
					allTiles.get(tmpTile.getSuit()).add(tmpTile);
				}
				player[i].initHand(allTiles);
			}
			GUI.showWind(wind, game+1);
			GUI.renewGUI();
			GUI.showGUI();
			
			int gameOver = 0;
			int current = (dealer+game)%4;//看第幾局決定輪到誰做莊，莊家開始，抽牌、決定動作 {Determine de quem é a vez de ser o dealer, dependendo da rodada em que o dealer está. O dealer começa, compra cartas e decide as ações.}
			
			// Atualiza indicador visual do jogador ativo
			GUI.updateActivePlayer(current);

			// Delay para visualização se for IA
			addAIDelay(current);
			
			Tile tile = shuffler.getNext();
			Action action = player[current].doSomething(0, tile);
			while(gameOver == 0){
				System.out.println("DEBUG: "+"wind: "+wind+"game: "+game+player[current]+actionString[action.getType()]+".");
				switch(action.getType()){//執行動作 {executar a ação}
					case 0:	//摸 {tocar}
					case 1:	//吃 {comer}
					case 2:	//碰 {ressalto}
					case 6:	//立直 {fique em linha reta}
						if(current>0){//手牌減少 {Mãos reduzidas}
							left[current]-= (action.getTiles().size()-1);
							GUI.assignHandNum(current+1, left[current]);
						}
						for(int i = 1 ; i < action.getTiles().size() ; i++){	//副露 {Vice-exposição}
							table.get(current+1).add(action.getTiles().get(i));
						}
						GUI.assignTile(table);
						GUI.renewGUI();
						tile = action.getTiles().get(0);	//打出來的牌 {cartas jogadas}
						Action selectAction = null;
						int selectPlayer = -1;
						for(int i = 1 ; i < 4 ; i++){//問另外三家有沒有事情要做 {Pergunte às outras três empresas se elas têm algo a fazer.}
							int p = (current+i)%4;
							System.out.println("wait "+p+" "+tile+" "+tile.getSize());
							action = player[p].doSomething(4-i, tile);
							if(action == null) continue;
							System.out.println(p+" "+actionString[action.getType()]);
							if(selectPlayer == -1 || action.getType() > selectAction.getType()){
								if(selectPlayer != -1)player[selectPlayer].failed();
								selectAction = action;
								selectPlayer = p;
							}
							else player[p].failed();
						}
						if(selectAction != null){//執行最優先動作, 榮>碰>吃, 設定好動作、玩家後continue跳到該玩家執行動作，未考慮同時榮的情形:p {Execute a ação de maior prioridade, glória> toque> comer, após definir a ação e o jogador, continue saltando para o jogador para realizar a ação, sem considerar a situação de glória simultânea:p}
							action = selectAction;
							current = selectPlayer;
							// Atualiza indicador visual do jogador ativo
							GUI.updateActivePlayer(current);
							continue;
						}
						else{//換下一家，到switch外面抽牌、決定動作 {Mude para a próxima casa, saia do switch para comprar cartas e decidir a ação}
							table.get(0).add(tile);
							GUI.assignTile(table);
							GUI.renewGUI();
							current = (current+1)%4;
							// Atualiza indicador visual do jogador ativo
							GUI.updateActivePlayer(current);
						}
						break;
					case 3:	//槓 {bar}
					case 4:	//加槓 {Adicionar uma barra}
					case 5:	//暗槓 {barra escondida}
						if(current>0){//手牌減少 {Mãos reduzidas}
							left[current]-= (action.getTiles().size());
							GUI.assignHandNum(current+1, left[current]);
						}
						for(int i = 0 ; i < action.getTiles().size() ; i++){	//槓從0開始算副露 {Kong começa a contar a partir de 0}
							table.get(current+1).add(action.getTiles().get(i));
						}
						GUI.assignTile(table);
						GUI.renewGUI();
						shuffler.ackKong();
						break;	//目前玩家補一張，到switch外面抽牌、決定動作 {O jogador atualmente compra uma carta e sai do switch para comprar cartas e decidir ações.}
					case 7:	//榮 {glória}
					case 8:	//自摸 {Toque-se}
						printTiles(action.getTiles());
						if(current != (dealer+game)%4){//當局莊家沒有連莊就要輪莊，進入下一局 {Se o banqueiro não tiver uma sucessão de banqueiros, ele recorrerá ao banqueiro e entrará na próxima rodada.}
							game++;
						}
						shuffler.permuteIndex();
						gameOver = 1;
						// Oculta indicador ao fim do jogo
						GUI.updateActivePlayer(-1);
						if(current>0)GUI.flipTile(current-1, action.getTiles());
						for(int i = 0 ; i < 4 ; i++){
							if(action.getType() == 7)
								player[i].GameOver(1, (current-i+4)%4);	//告知player, current榮 {Diga ao jogador, atual}
							else
								player[i].GameOver(2, (current-i+4)%4);	//告知player, current自摸 {Diga ao jogador, a corrente toca nele}
						}
						break;
					default:
						System.out.println("ERROR: "+player[current]+" unknown action "+action.getType()+".");
						System.exit(1);
				}
				if(gameOver == 1)break;

				// Delay para visualização se for IA
				addAIDelay(current);

				tile = shuffler.getNext();//switch外面指的是這裡^^ {A parte externa do switch refere-se aqui ^^}
				if(tile == null){//流局 {Situação perdida}
					gameOver = 1;
					// Oculta indicador ao fim do jogo
					GUI.updateActivePlayer(-1);
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

