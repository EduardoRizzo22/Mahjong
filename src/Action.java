import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Action {
    private int type;
    private List<Tile> tiles;

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

    // Construtores
    public Action(int type, List<Tile> tiles) {
        setType(type);
        setTiles(tiles);
    }

    public Action(ActionType actionType, List<Tile> tiles) {
        setActionType(actionType);
        setTiles(tiles);
    }

    // Getters
    public int getType() {
        return type;
    }

    public ActionType getActionType() {
        return ActionType.fromValue(type);
    }

    public List<Tile> getTiles() {
        return Collections.unmodifiableList(tiles); // Retorna lista imutável
    }

    // Setters com validação
    public void setType(int type) {
        // Valida se o tipo está no range válido
        if (type < 0 || type > 8) {
            throw new IllegalArgumentException("Tipo de ação inválido: " + type);
        }
        this.type = type;
    }

    public void setActionType(ActionType actionType) {
        this.type = actionType.getValue();
    }

    public void setTiles(List<Tile> tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("Lista de tiles não pode ser nula");
        }
        this.tiles = new ArrayList<>(tiles); // Cria uma cópia defensiva
    }

    // Métodos utilitários
    public void addTile(Tile tile) {
        if (tile == null) {
            throw new IllegalArgumentException("Tile não pode ser nulo");
        }
        this.tiles.add(tile);
    }

    public boolean removeTile(Tile tile) {
        return this.tiles.remove(tile);
    }

    public Tile getTile(int index) {
        if (index < 0 || index >= tiles.size()) {
            throw new IndexOutOfBoundsException("Índice inválido: " + index);
        }
        return tiles.get(index);
    }

    public int getTileCount() {
        return tiles.size();
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    // Método para obter uma cópia defensiva dos tiles
    public List<Tile> getTilesCopy() {
        List<Tile> copy = new ArrayList<>();
        for (Tile tile : tiles) {
            copy.add(tile.copy());
        }
        return copy;
    }

    // Métodos de verificação de tipo de ação
    public boolean isDraw() {
        return type == ActionType.DRAW.getValue();
    }

    public boolean isChow() {
        return type == ActionType.CHOW.getValue();
    }

    public boolean isPong() {
        return type == ActionType.PONG.getValue();
    }

    public boolean isKong() {
        return type == ActionType.KONG.getValue() || 
               type == ActionType.ADDED_KONG.getValue() || 
               type == ActionType.CONCEALED_KONG.getValue();
    }

    public boolean isWin() {
        return type == ActionType.RON.getValue() || type == ActionType.HU.getValue();
    }

    @Override
    public String toString() {
        return String.format("Action{type=%s, tiles=%s}", getActionType(), tiles);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Action action = (Action) obj;
        return type == action.type && tiles.equals(action.tiles);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(type);
        result = 31 * result + tiles.hashCode();
        return result;
    }
}
