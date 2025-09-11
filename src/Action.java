import java.util.*;

public class Action{
	public int type;
	/*
	 * 0:摸 {0: toque}
	 * 1:吃 {1: comer}
	 * 2:碰 {2: toque}
	 * 3:槓 {3: Kong}
	 * 4:加槓 {4: Adicione a barra}
	 * 5:暗槓 {5: Barra escondida}
	 * 6:立直 {6: Recupere-se}
	 * 7:榮 {7: Rong}
	 * 8:胡 {8: Hu}
	 */
	public ArrayList<Tile> tiles;	//board會把第0張當作是要打掉的牌，其他看要放吃碰槓的傳給GUI用還是幹嘛都可以(?
	/* A placa considerará a 0ª carta como a carta a ser destruída, e o restante pode ser passado para a GUI para uso ou qualquer outra coisa (? */
	public Action(int i, ArrayList<Tile> t){
		type = i;
		tiles = t;
	}

}
