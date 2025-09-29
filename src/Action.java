import java.util.*;

public class Action{
	public int type;
	public ArrayList<Tile> tiles;

	// Enum para os tipos de ação
	public enum ActionType {
		DRAW(0),          // 摸 - toque
		CHOW(1),          // 吃 - comer
		PONG(2),          // 碰 - toque
		KONG(3),          // 槓 - Kong
		ADDED_KONG(4),    // 加槓 - Adicione a barra
		CONCEALED_KONG(5), // 暗槓 - Barra escondida
		RIICHI(6),        // 立直 - Recupere-se
		RON(7),           // 榮 - Rong
		HU(8);            // 胡 - Hu

		private final int value;

		ActionType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		// Método para obter o ActionType a partir do valor inteiro
		public static ActionType fromValue(int value) {
			for (ActionType actionType : ActionType.values()) {
				if (actionType.getValue() == value) {
					return actionType;
				}
			}
			throw new IllegalArgumentException("Valor inválido para ActionType: " + value);
		}
	}

	public Action(int i, ArrayList<Tile> t){
		type = i;
		tiles = t;
	}

	// Construtor adicional usando o Enum
	public Action(ActionType actionType, ArrayList<Tile> t){
		type = actionType.getValue();
		tiles = t;
	}

	// Método para obter o ActionType atual
	public ActionType getActionType() {
		return ActionType.fromValue(type);
	}

	// Método para definir o tipo usando Enum
	public void setActionType(ActionType actionType) {
		this.type = actionType.getValue();
	}
}