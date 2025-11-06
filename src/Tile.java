/**
 * Representa uma peça (tile) do jogo Mahjong.
 * 
 * <p>Cada peça possui:
 * <ul>
 *   <li><b>Naipe (suit)</b>: 0=萬(caracteres), 1=筒(círculos), 2=條(bambus), 3=字(honras)</li>
 *   <li><b>Valor (value)</b>: 0-8 para naipes numéricos, 0-6 para honras</li>
 *   <li><b>Índice (index)</b>: Identificador único calculado como (suit * 9 + value)</li>
 *   <li><b>Tamanho (size)</b>: Quantidade de peças idênticas na mão</li>
 * </ul>
 * </p>
 * 
 * <p>Honras (suit=3) incluem:
 * <ul>
 *   <li>Ventos: 東(Leste), 南(Sul), 西(Oeste), 北(Norte)</li>
 *   <li>Dragões: 中(Vermelho), 發(Verde), 白(Branco)</li>
 * </ul>
 * </p>
 */
public class Tile implements Comparable<Tile>{
    
    /** Dicionário de naipes: 萬(Dez mil), 筒(Cilindro), 條(Faixa) */
    private static final String[] SUIT_DICTIONARY = {"萬", "筒", "條"};
    
    /** Dicionário de valores numéricos em chinês: 一 a 九 (1 a 9) */
    private static final String[] VALUE_DICTIONARY = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
    
    /** Dicionário de honras: 東南西北 (ventos) + 中發白 (dragões) */
    private static final String[] WORD_DICTIONARY = {"東", "南", "西", "北", "中", "發", "白"};
    
    /** Naipe da peça (0-3) */
    private final int suit;
    
    /** Valor da peça (0-8 ou 0-6 para honras) */
    private final int value;
    
    /** Índice único da peça */
    private final int index;
    
    /** Quantidade de peças idênticas */
    private int size;

    /**
     * Construtor que cria uma peça a partir de um índice.
     * 
     * <p>O índice é convertido em suit e value através das fórmulas:
     * <ul>
     *   <li>suit = index / 9</li>
     *   <li>value = index % 9</li>
     * </ul>
     * </p>
     * 
     * @param i Índice da peça (0-35)
     */
    public Tile(int i){
        this.suit = i / 9;
        this.value = i % 9;
        this.index = i;
        this.size = 1;
    }

    /**
     * Retorna o naipe da peça.
     * 
     * @return 0=萬, 1=筒, 2=條, 3=字(honras)
     */
    public int getSuit() {
        return suit;
    }

    /**
     * Retorna o valor da peça.
     * 
     * @return Valor de 0 a 8 (ou 0-6 para honras)
     */
    public int getValue() {
        return value;
    }

    /**
     * Retorna o índice único da peça.
     * 
     * @return Índice calculado como (suit * 9 + value)
     */
    public int getIndex() {
        return index;
    }

    /**
     * Retorna a quantidade de peças idênticas.
     * 
     * @return Número de peças (1-4 tipicamente)
     */
    public int getSize(){
        return size;
    }

    /**
     * Adiciona ao tamanho da peça (incrementa a contagem).
     * 
     * @param s Valor a ser adicionado (deve ser não-negativo)
     * @throws IllegalArgumentException se s for negativo
     */
    public void addSize(int s){
        if (s < 0) {
            throw new IllegalArgumentException("Tamanho não pode ser negativo");
        }
        this.size += s;
    }

    /**
     * Define o tamanho da peça (contagem absoluta).
     * 
     * @param s Novo tamanho (deve ser positivo)
     * @throws IllegalArgumentException se s for menor ou igual a zero
     */
    public void setSize(int s){
        if (s <= 0) {
            throw new IllegalArgumentException("Tamanho deve ser positivo");
        }
        this.size = s;
    }
    
    /**
     * Retorna a representação em string da peça em caracteres chineses.
     * 
     * <p>Exemplos:
     * <ul>
     *   <li>Peça numérica: "三萬" (3 de caracteres)</li>
     *   <li>Peça de honra: "東" (vento leste)</li>
     * </ul>
     * </p>
     * 
     * @return String representando a peça
     */
    @Override
    public String toString(){
        if(suit == 3)
            return WORD_DICTIONARY[value];
        else
            return VALUE_DICTIONARY[value] + SUIT_DICTIONARY[suit];
    }

    /**
     * Compara esta peça com outro objeto para igualdade.
     * Duas peças são iguais se possuem o mesmo índice.
     * 
     * @param that Objeto a ser comparado
     * @return true se as peças são iguais, false caso contrário
     */
    @Override
    public boolean equals(Object that) {
        if(this == that) return true;
        if(that == null || getClass() != that.getClass()) return false;
        Tile tile = (Tile) that;
        return this.index == tile.index;
    }
    
    /**
     * Retorna o hash code da peça baseado no seu índice.
     * 
     * @return Hash code da peça
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }
    
    /**
     * Compara esta peça com outra para ordenação.
     * Peças são ordenadas por índice crescente.
     * 
     * @param that Peça a ser comparada
     * @return Valor negativo, zero ou positivo se esta peça é menor, igual ou maior
     */
    @Override
    public int compareTo(Tile that) {
        return Integer.compare(this.index, that.index);
    }

    /**
     * Cria uma cópia independente desta peça.
     * A cópia terá o mesmo índice e tamanho.
     * 
     * @return Nova instância de Tile com os mesmos atributos
     */
    public Tile copy(){
        Tile t = new Tile(index);
        t.setSize(size);
        return t;
    }

    /**
     * Retorna uma peça adjacente com o deslocamento especificado.
     * 
     * <p>Utilizado para verificar sequências (chow). Por exemplo:
     * <ul>
     *   <li>offset = +1: próxima peça na sequência</li>
     *   <li>offset = -1: peça anterior na sequência</li>
     * </ul>
     * </p>
     * 
     * @param offset Deslocamento relativo (-8 a +8 para peças numéricas, -6 a +6 para honras)
     * @return Peça adjacente ou null se o deslocamento for inválido
     */
    public Tile getAdjacentTile(int offset){
        if(suit != 3 && (value + offset < 0 || value + offset > 8)){
            return null;        
        }
        if(suit == 3 && (value + offset < 0 || value + offset > 6)){
            return null;        
        }
        Tile t = new Tile(index + offset);
        t.setSize(size);
        return t;
    }

    /**
     * Verifica se esta peça é uma honra (vento ou dragão).
     * 
     * @return true se suit == 3, false caso contrário
     */
    public boolean isHonorTile() {
        return suit == 3;
    }

    /**
     * Verifica se esta peça é numérica (萬, 筒 ou 條).
     * 
     * @return true se suit está entre 0 e 2, false caso contrário
     */
    public boolean isNumericTile() {
        return suit >= 0 && suit <= 2;
    }

    /**
     * Verifica se esta peça é do mesmo naipe que outra.
     * 
     * @param other Outra peça para comparação
     * @return true se ambas têm o mesmo suit, false caso contrário
     */
    public boolean isSameSuit(Tile other) {
        return this.suit == other.suit;
    }

    /**
     * Verifica se esta peça é terminal (1 ou 9).
     * Peças terminais são importantes para determinados yakus no Mahjong.
     * 
     * @return true se for peça numérica com valor 0 ou 8 (representando 1 ou 9)
     */
    public boolean isTerminal() {
        return isNumericTile() && (value == 0 || value == 8);
    }
}