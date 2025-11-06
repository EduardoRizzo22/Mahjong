import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa uma ação realizada por um jogador no jogo Mahjong.
 * 
 * <p>Uma ação consiste em:
 * <ul>
 *   <li><b>Tipo (type)</b>: Define qual ação está sendo executada</li>
 *   <li><b>Peças (tiles)</b>: Lista de peças envolvidas na ação</li>
 * </ul>
 * </p>
 * 
 * <p>Tipos de ação suportados:
 * <ul>
 *   <li>DRAW (0): Comprar uma peça do monte</li>
 *   <li>CHOW (1): Formar sequência com peça descartada</li>
 *   <li>PONG (2): Formar trinca com peça descartada</li>
 *   <li>KONG (3): Formar quadra com peça descartada</li>
 *   <li>ADDED_KONG (4): Adicionar à quadra existente</li>
 *   <li>CONCEALED_KONG (5): Formar quadra oculta</li>
 *   <li>RIICHI (6): Declarar Riichi (mão pronta)</li>
 *   <li>RON (7): Vitória com peça descartada por oponente</li>
 *   <li>HU (8): Vitória por auto-compra (tsumo)</li>
 * </ul>
 * </p>
 * 
 */
public class Action {
    /** Tipo da ação (0-8) */
    private int type;
    
    /** Lista de peças envolvidas na ação */
    private List<Tile> tiles;

    /**
     * Enumeração dos tipos de ação possíveis no jogo.
     * Cada tipo possui um valor inteiro associado para compatibilidade com código legado.
     */
    public enum ActionType {
        /** Comprar uma peça (摸) */
        DRAW(0),
        
        /** Comer - formar sequência (吃) */
        CHOW(1),
        
        /** Pong - formar trinca (碰) */
        PONG(2),
        
        /** Kong - formar quadra (槓) */
        KONG(3),
        
        /** Adicionar à quadra (加槓) */
        ADDED_KONG(4),
        
        /** Quadra oculta (暗槓) */
        CONCEALED_KONG(5),
        
        /** Riichi - declarar mão pronta (立直) */
        RIICHI(6),
        
        /** Ron - vitória com descarte do oponente (榮) */
        RON(7),
        
        /** Hu/Tsumo - vitória por auto-compra (胡) */
        HU(8);

        private final int value;

        /**
         * Construtor do enum ActionType.
         * 
         * @param value Valor inteiro associado ao tipo de ação
         */
        ActionType(int value) {
            this.value = value;
        }

        /**
         * Retorna o valor inteiro do tipo de ação.
         * 
         * @return Valor inteiro (0-8)
         */
        public int getValue() {
            return value;
        }

        /**
         * Converte um valor inteiro para o ActionType correspondente.
         * 
         * @param value Valor inteiro (0-8)
         * @return ActionType correspondente
         * @throws IllegalArgumentException se o valor for inválido
         */
        public static ActionType fromValue(int value) {
            for (ActionType actionType : ActionType.values()) {
                if (actionType.getValue() == value) {
                    return actionType;
                }
            }
            throw new IllegalArgumentException("Valor inválido para ActionType: " + value);
        }
    }

    /**
     * Construtor que cria uma ação com tipo inteiro e lista de peças.
     * 
     * @param type Tipo da ação (0-8)
     * @param tiles Lista de peças envolvidas
     * @throws IllegalArgumentException se o tipo for inválido
     */
    public Action(int type, List<Tile> tiles) {
        setType(type);
        setTiles(tiles);
    }

    /**
     * Construtor que cria uma ação com ActionType e lista de peças.
     * 
     * @param actionType Tipo da ação (enum)
     * @param tiles Lista de peças envolvidas
     */
    public Action(ActionType actionType, List<Tile> tiles) {
        setActionType(actionType);
        setTiles(tiles);
    }

    /**
     * Retorna o tipo da ação como inteiro.
     * 
     * @return Tipo da ação (0-8)
     */
    public int getType() {
        return type;
    }

    /**
     * Retorna o tipo da ação como enum ActionType.
     * 
     * @return ActionType correspondente
     */
    public ActionType getActionType() {
        return ActionType.fromValue(type);
    }

    /**
     * Retorna a lista de peças envolvidas na ação.
     * A lista retornada é imutável para evitar modificações acidentais.
     * 
     * @return Lista imutável de peças
     */
    public List<Tile> getTiles() {
        return Collections.unmodifiableList(tiles);
    }

    /**
     * Define o tipo da ação a partir de um inteiro.
     * 
     * @param type Tipo da ação (0-8)
     * @throws IllegalArgumentException se o tipo estiver fora do range válido
     */
    public void setType(int type) {
        // Valida se o tipo está no range válido
        if (type < 0 || type > 8) {
            throw new IllegalArgumentException("Tipo de ação inválido: " + type);
        }
        this.type = type;
    }

    /**
     * Define o tipo da ação a partir de um ActionType.
     * 
     * @param actionType Tipo da ação (enum)
     */
    public void setActionType(ActionType actionType) {
        this.type = actionType.getValue();
    }

    /**
     * Define a lista de peças envolvidas na ação.
     * 
     * @param tiles Lista de peças (não pode ser null)
     * @throws IllegalArgumentException se tiles for null
     */
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
