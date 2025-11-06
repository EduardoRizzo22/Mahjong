import java.util.ArrayList;
import java.util.List;

/**
 * Classe de comunicação entre a lógica do jogo (Board) e a interface gráfica (mainGUI).
 * 
 * <p>Responsabilidades principais:
 * <ul>
 *   <li>Gerenciar o estado visual das peças de todos os jogadores</li>
 *   <li>Sincronizar dados entre o modelo de jogo e a apresentação visual</li>
 *   <li>Controlar a atualização da GUI quando o estado do jogo muda</li>
 *   <li>Gerenciar o jogador humano (PlayerGUI) e sua interação com a interface</li>
 * </ul>
 * </p>
 * 
 * <p><b>Índices dos Jogadores:</b></p>
 * <ul>
 *   <li>0: Mesa (peças descartadas)</li>
 *   <li>1: Peças expostas do jogador humano</li>
 *   <li>2: Jogador da direita</li>
 *   <li>3: Jogador oponente (cima)</li>
 *   <li>4: Jogador da esquerda</li>
 *   <li>5: Mão do jogador humano</li>
 * </ul>
 */
public class comGUI
{
	/** Índice para as peças na mesa (descartadas) */
	public static final int tableIndex = 0;
	
	/** Índice para as peças expostas do jogador humano */
	public static final int myPlayerOpenIndex = 1;
	
	/** Índice para o jogador da direita */
	public static final int rightPlayerIndex = 2;
	
	/** Índice para o jogador oponente (cima) */
	public static final int upPlayerIndex = 3;
	
	/** Índice para o jogador da esquerda */
	public static final int leftPlayerIndex = 4;
	
	/** Índice para a mão do jogador humano */
	public static final int myPlayerHandIndex = 5;
	
	/** Número de peças na mão do jogador da direita */
	private int numRightPlayer;
	
	/** Número de peças na mão do jogador oponente */
	private int numUpPlayer;
	
	/** Número de peças na mão do jogador da esquerda */
	private int numLeftPlayer;
	
	/** Peças expostas do jogador da direita */
	private ArrayList<Tile> rightPlayerOpen;
	
	/** Peças expostas do jogador oponente */
	private ArrayList<Tile> upPlayerOpen;
	
	/** Peças expostas do jogador da esquerda */
	private ArrayList<Tile> leftPlayerOpen;
	
	/** Peças expostas do jogador humano */
	private ArrayList<Tile> myPlayerOpen;
	
	/** Peças na mesa (descartadas por todos os jogadores) */
	private ArrayList<Tile> table;
	
	/** Instância da interface gráfica principal */
	public mainGUI frame;
	
	/** Instância do jogador humano com capacidades de GUI */
	public PlayerGUI player;
	
	/**
	 * Construtor que inicializa o sistema de comunicação com a GUI.
	 * Cria todas as estruturas de dados necessárias e instancia a janela principal.
	 */
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
	
	/**
	 * Inicializa o jogador humano com interface gráfica.
	 * 
	 * @param name Nome do jogador
	 * @param score Pontuação inicial
	 * @param _c Referência ao próprio comGUI para comunicação bidirecional
	 */
	public void initPlayerGUI(String name, int score, comGUI _c)
	{
		player = new PlayerGUI(name, score);
		player.c = _c;
	}
	
	/**
	 * Atualiza a interface gráfica com o estado atual de todas as peças.
	 * 
	 * <p>Este método:
	 * <ol>
	 *   <li>Coleta todas as peças visíveis (mesa, peças expostas, mão do jogador)</li>
	 *   <li>Envia os dados para a GUI através de {@link mainGUI#setAllContent}</li>
	 *   <li>Reseta o estado visual da interface</li>
	 * </ol>
	 * </p>
	 */
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
	
	public void flipTile(int index, List<Tile> tile)
	{
		ArrayList<Tile> temp = new ArrayList<Tile>();
		for(Tile t : tile)
			for(int i = 0; i < t.getSize(); i++){
				temp.add(new Tile(t.getIndex()));
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
	
	/**
	 * Atualiza o indicador visual do jogador ativo na GUI
	 * playerId ID do jogador ativo (0 = jogador humano, 1 = direita, 2 = oponente, 3 = esquerda)
	 */
	public void updateActivePlayer(int playerId)
	{
		frame.updateActivePlayerDisplay(playerId);
	}
}
