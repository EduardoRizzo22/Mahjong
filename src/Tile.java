public class Tile implements Comparable<Tile>{
    
    private static final String[] SUIT_DICTIONARY = {"萬", "筒", "條"}; // Dez mil, cilindro, faixa
    private static final String[] VALUE_DICTIONARY = {"一", "二", "三", "四", "五", "六", "七", "八", "九"}; // "um dois três quatro cinco seis sete oito nove"
    private static final String[] WORD_DICTIONARY = {"東", "南", "西", "北", "中", "發", "白"}; // "Leste", "Sul", "Oeste", "Norte", "Médio", "Fa", "branco"
    
    private final int suit;
    private final int value;
    private final int index;    
    private int size;

    public Tile(int i){
        this.suit = i / 9;
        this.value = i % 9;
        this.index = i;
        this.size = 1;
    }

    // Getters
    public int getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public int getSize(){
        return size;
    }

    // Setters com validação
    public void addSize(int s){
        if (s < 0) {
            throw new IllegalArgumentException("Tamanho não pode ser negativo");
        }
        this.size += s;
    }

    public void setSize(int s){
        if (s <= 0) {
            throw new IllegalArgumentException("Tamanho deve ser positivo");
        }
        this.size = s;
    }
    
    @Override
    public String toString(){
        if(suit == 3)
            return WORD_DICTIONARY[value];
        else
            return VALUE_DICTIONARY[value] + SUIT_DICTIONARY[suit];
    }

    @Override
    public boolean equals(Object that) {
        if(this == that) return true;
        if(that == null || getClass() != that.getClass()) return false;
        Tile tile = (Tile) that;
        return this.index == tile.index;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }
    
    @Override
    public int compareTo(Tile that) {
        return Integer.compare(this.index, that.index);
    }

    public Tile copy(){
        Tile t = new Tile(index);
        t.setSize(size);
        return t;
    }

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

    // Método utilitário para verificar se é uma peça de honra
    public boolean isHonorTile() {
        return suit == 3;
    }

    // Método utilitário para verificar se é uma peça numérica
    public boolean isNumericTile() {
        return suit >= 0 && suit <= 2;
    }

    // Método para verificar se duas peças são do mesmo naipe
    public boolean isSameSuit(Tile other) {
        return this.suit == other.suit;
    }

    // Método para verificar se é uma peça terminal (1 ou 9)
    public boolean isTerminal() {
        return isNumericTile() && (value == 0 || value == 8);
    }
}
