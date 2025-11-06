import java.util.*;

enum Status {
	FREE, RICHI, WIN
}
public class AI extends Player{

	private final int DRAW = 0 ;
	private final int CHOW = 1 ;
	private final int PONG = 2 ;
	private final int RICHI = 6 ;
	private final int RON = 7 ;
	private final int HU = 8 ;
	private int exposed ;
	private Status status ;
	private Tile prevTile ;
	private Action prevAct ;

	public AI(String name, int score){
		super(name, score ) ;
		exposed = 0 ;
		status = Status.FREE ;
		prevTile = null ;
		prevAct = null ;
	}

	//ask the player whether to draw/chow/pong/kong/reach/hu or not
	private boolean doChow(Tile tile){
		if( hand.chowable(tile) == 0 )
			return false ;
	
		Hand tmp = new Hand(hand.getAll()) ;
		tmp.add(tile) ;
		Collections.sort(tmp.getAll().get(tile.getSuit()));

		/* remove all shuns in the hand */
		int i = 0 ;
		int s = tmp.getAll().get(tile.getSuit()).size() ;
		while(i < s - 2){
			Tile a = tmp.getAll().get(tile.getSuit()).get(i);
			Tile b = tmp.getAll().get(tile.getSuit()).get(i+1);
			Tile c = tmp.getAll().get(tile.getSuit()).get(i+2);
			if(a.getIndex() + 1 == b.getIndex() && b.getIndex() + 1 == c.getIndex()){
				tmp.discard(a) ;
				tmp.discard(b) ;
				tmp.discard(c) ;
				s = tmp.getAll().get(tile.getSuit()).size() ;
				continue ;
			}
			else {
				i++ ;
				s = tmp.getAll().get(tile.getSuit()).size() ;
			}
		}

		/* check if the tile you want to chow is left or not */
		s = tmp.getAll().get(tile.getSuit()).size() ;
		for( int j = 0 ; j < s ; j++ ){
			if( tmp.getAll().get(tile.getSuit()).get(j).equals(tile) )
				return false ;
		}
		return true ;
	}

	private boolean doPong(Tile tile){
		if( !hand.pongable(tile) )
			return false ;
	
		Hand tmp = new Hand(hand.getAll()) ;
		tmp.add(tile) ;
		Collections.sort(tmp.getAll().get(tile.getSuit()));

		/* remove all shuns in the hand */
		int i = 0 ;
		int s = tmp.getAll().get(tile.getSuit()).size() ;
		while(i < s - 2){
			Tile a = tmp.getAll().get(tile.getSuit()).get(i);
			Tile b = tmp.getAll().get(tile.getSuit()).get(i+1);
			Tile c = tmp.getAll().get(tile.getSuit()).get(i+2);
			if(a.getIndex() + 1 == b.getIndex() && b.getIndex() + 1 == c.getIndex()){
				tmp.discard(a) ;
				tmp.discard(b) ;
				tmp.discard(c) ;
				s = tmp.getAll().get(tile.getSuit()).size() ;
				continue ;
			}
			else {
				i++ ;
				s = tmp.getAll().get(tile.getSuit()).size() ;
			}
		}

		/* check if the tile you want to pong is left and size >= 3 or not */
		s = tmp.getAll().get(tile.getSuit()).size() ;
		for( int j = 0 ; j < s ; j++ ){
			if( tmp.getAll().get(tile.getSuit()).get(j).equals(tile) && tmp.getAll().get(tile.getSuit()).get(j).getSize() >= 3 )
				return true ;
		}
		return false ;
	}

	private boolean doRichi(Tile tile){
		if( exposed == 0 ){
			ArrayList<Tile> tingTile = hand.tingable(tile) ;
			if( tingTile != null && tingTile.size() > 0 )
				return true ;
		}
		return false ;
	}

	private boolean doHu(Tile tile){
		if( hand.tingable(tile) == null )
			return true ;
		else
			return false ;
	}

	/**
	 * Estratégia melhorada de descarte - Torna a IA mais inteligente e desafiadora
	 * Prioridades:
	 * 1. Descartar peças de Honra únicas (Ventos/Dragões sem pares)
	 * 2. Descartar peças com menor potencial de combinação
	 * 3. Evitar descartar peças que formam ou podem formar combinações
	 */
	private Tile decideDiscard(Hand _hand){
		ArrayList<Tile> allHandTiles = new ArrayList<Tile>();
		
		// Coletar todas as peças da mão em uma única lista
		for(int suit = 0; suit <= 3; suit++){
			for(Tile tile : _hand.getAll().get(suit)){
				for(int i = 0; i < tile.getSize(); i++){
					allHandTiles.add(new Tile(tile.getIndex()));
				}
			}
		}

		// Estratégia 1: Priorizar descarte de peças de Honra únicas
		Tile uniqueHonor = findUniqueHonorTile(allHandTiles);
		if(uniqueHonor != null){
			return uniqueHonor;
		}

		// Estratégia 2: Avaliar potencial de cada peça e descartar a pior
		Tile worstTile = null;
		int lowestPotential = Integer.MAX_VALUE;

		for(Tile tile : allHandTiles){
			int potential = evaluateTilePotential(tile, allHandTiles);
			if(potential < lowestPotential){
				lowestPotential = potential;
				worstTile = tile;
			}
		}

		// Fallback: se nenhuma estratégia funcionou, usar a lógica antiga
		if(worstTile == null){
			return fallbackDiscard(_hand);
		}

		return worstTile;
	}

	/**
	 * Encontra uma peça de Honra única na mão (sem pares)
	 * @param hand Lista de todas as peças na mão
	 * @return Peça de Honra única ou null se não houver
	 */
	private Tile findUniqueHonorTile(ArrayList<Tile> hand){
		ArrayList<Tile> honorTiles = new ArrayList<Tile>();
		
		// Filtrar apenas peças de Honra (suit == 3)
		for(Tile tile : hand){
			if(tile.getSuit() == 3){
				honorTiles.add(tile);
			}
		}

		// Procurar peças de Honra que aparecem apenas uma vez
		for(Tile honorTile : honorTiles){
			int count = 0;
			for(Tile tile : hand){
				if(tile.equals(honorTile)){
					count++;
				}
			}
			if(count == 1){
				return honorTile; // Encontrou uma Honra única - descartar!
			}
		}

		return null; // Não há Honras únicas
	}

	/**
	 * Avalia o potencial de uma peça para formar combinações
	 * Quanto maior o valor, mais útil é a peça
	 * tile Peça a ser avaliada
	 * hand Lista de todas as peças na mão
	 * return Valor do potencial (maior = melhor)
	 */
	private int evaluateTilePotential(Tile tile, ArrayList<Tile> hand){
		int potential = 0;

		// 1. Contar quantas peças idênticas existem (pares/triplos são valiosos)
		int sameCount = 0;
		for(Tile t : hand){
			if(t.equals(tile)){
				sameCount++;
			}
		}
		potential += sameCount * 10; // Pares valem muito

		// 2. Contar peças próximas (adjacentes) para possíveis sequências
		if(tile.getSuit() != 3){ // Apenas para peças numéricas (não Honra)
			int nearbyCount = countNearbyTiles(tile, hand);
			potential += nearbyCount * 5; // Peças próximas valem moderadamente
		}

		// 3. Penalizar peças de Honra isoladas
		if(tile.getSuit() == 3 && sameCount == 1){
			potential -= 20; // Honra única é ruim
		}

		// 4. Penalizar peças terminais isoladas (1 ou 9)
		if(tile.getSuit() != 3 && (tile.getValue() == 0 || tile.getValue() == 8) && sameCount == 1){
			potential -= 10; // Terminais isolados são menos úteis
		}

		// 5. Bonificar peças centrais (4, 5, 6) - mais flexíveis para sequências
		if(tile.getSuit() != 3 && tile.getValue() >= 3 && tile.getValue() <= 5){
			potential += 5;
		}

		return potential;
	}

	/**
	 * Conta quantas peças adjacentes ou próximas existem na mão
	 * tile Peça de referência
	 * hand Lista de todas as peças na mão
	 * return Número de peças próximas
	 */
	private int countNearbyTiles(Tile tile, ArrayList<Tile> hand){
		int count = 0;

		// Verificar peças adjacentes (-2, -1, +1, +2)
		for(int offset = -2; offset <= 2; offset++){
			if(offset == 0) continue; // Pular a própria peça

			int targetIndex = tile.getIndex() + offset;
			int targetValue = tile.getValue() + offset;
			
			// Verificar se está dentro dos limites do naipe
			if(targetValue >= 0 && targetValue <= 8){
				for(Tile t : hand){
					if(t.getSuit() == tile.getSuit() && t.getIndex() == targetIndex){
						count++;
						break;
					}
				}
			}
		}

		return count;
	}

	/**
	 * Lógica de descarte original como fallback
	 * Usado apenas se as estratégias melhoradas falharem
	 */
	private Tile fallbackDiscard(Hand _hand){
		Hand tmp = new Hand(_hand.getAll());

		/* initialize discard tile */
		Tile res = null;
		for(int suit = 3; suit >= 0; suit--){
			if(tmp.getAll().get(suit).size() > 0){
				res = tmp.getAll().get(suit).get(0);
				break;
			}
		}

		/* remove all shuns in the hand */
		for(int suit = 0; suit <= 2; suit++){
			Collections.sort(tmp.getAll().get(suit));

			int i = 0;
			int s = tmp.getAll().get(suit).size();
			while(i < s - 2){
				Tile a = tmp.getAll().get(suit).get(i);
				Tile b = tmp.getAll().get(suit).get(i+1);
				Tile c = tmp.getAll().get(suit).get(i+2);
				if(a.getIndex() + 1 == b.getIndex() && b.getIndex() + 1 == c.getIndex()){
					tmp.discard(a);
					tmp.discard(b);
					tmp.discard(c);
					s = tmp.getAll().get(suit).size();
					continue;
				}
				else{
					i++;
					s = tmp.getAll().get(suit).size();
				}
			}
		}

		for(int suit = 3; suit >= 0; suit--){
			if(tmp.getAll().get(suit).size() > 0){
				res = tmp.getAll().get(suit).get(0);
				break;
			}
		}

		/* remove all triplets in the hand */
		for(int suit = 0; suit <= 3; suit++){
			Collections.sort(tmp.getAll().get(suit));

			int i = 0;
			int s = tmp.getAll().get(suit).size();
			while(i < s){
				Tile a = tmp.getAll().get(suit).get(i);
				if(a.getSize() >= 3){
					tmp.discard(a);
					tmp.discard(a);
					tmp.discard(a);
					s = tmp.getAll().get(suit).size();
					continue;
				}
				else{
					i++;
					s = tmp.getAll().get(suit).size();
				}
			}
		}

		for(int suit = 3; suit >= 0; suit--){
			if(tmp.getAll().get(suit).size() > 0){
				res = tmp.getAll().get(suit).get(0);
				break;
			}
		}

		/* remove all pairs in the hand */
		for(int suit = 0; suit <= 3; suit++){
			Collections.sort(tmp.getAll().get(suit));

			int i = 0;
			int s = tmp.getAll().get(suit).size();
			while(i < s){
				Tile a = tmp.getAll().get(suit).get(i);
				if(a.getSize() >= 2){
					tmp.discard(a);
					tmp.discard(a);
					s = tmp.getAll().get(suit).size();
					continue;
				}
				else{
					i++;
					s = tmp.getAll().get(suit).size();
				}
			}
		}

		for(int suit = 3; suit >= 0; suit--){
			if(tmp.getAll().get(suit).size() > 0){
				res = tmp.getAll().get(suit).get(0);
				break;
			}
		}

		return res;
	}

	private Action win(int actionType){ /* status: RON or HU */
		ArrayList<Tile> allTiles = new ArrayList<Tile>() ;
		for( int i = 0 ; i <= 3 ; i++ ){
			ArrayList<Tile> tmp = hand.getAll().get(i) ;
			for( int t = 0 ; t < tmp.size() ; t++ )
				allTiles.add( tmp.get(t) ) ;
		}
		status = Status.WIN ;
		prevAct = new Action(actionType, allTiles) ;
		return prevAct ;
	}

	public Action doSomething(int from, Tile tile){ //from 0 draw 1 next 2 opposite 3 previous
		if(from == 0){ //draw, richi, add kong, private kong, hu
			if( doHu(tile) ){ /* huable */
				hand.add(tile) ;
				prevTile = tile.copy() ;
				return win(HU) ;
			}
			else if( status == Status.RICHI ){
				prevTile = tile.copy() ;
				ArrayList<Tile> discardList = new ArrayList<Tile>() ;
				discardList.add( tile ) ;

				prevAct = new Action(DRAW, discardList) ;
				return prevAct ;
			}
			else if( doRichi(tile) ){
				ArrayList<Tile> tingTile = hand.tingable(tile) ;
				hand.add(tile) ;
				prevTile = tile.copy() ;

				ArrayList<Tile> discardList = new ArrayList<Tile>() ;
				Tile discardTile = tingTile.get(0) ;
				discardList.add( discardTile ) ;
				hand.discard( discardTile ) ;

				status = Status.RICHI ;
				prevAct = new Action(RICHI, discardList) ;
				return prevAct ;	
			}
			else {
				hand.add(tile) ;
				prevTile = tile.copy() ;

				ArrayList<Tile> discardList = new ArrayList<Tile>() ;
				Tile discardTile = decideDiscard(hand) ;
				discardList.add( discardTile ) ;
				hand.discard( discardTile ) ;

				prevAct = new Action(DRAW, discardList) ;
				return prevAct ;
			}
		}
		else if(from == 3){//chow, pong, kong, ron
			if( doHu(tile) ){
				hand.add(tile) ;
				prevTile = tile.copy() ;
				return win(RON) ;
			}
			else if( status == Status.RICHI ){
				prevAct = null ;
				return prevAct ;
			}
			else if( doChow(tile) ){
				int flag = hand.chowable(tile) ;
				hand.add(tile) ;
				prevTile = tile.copy() ;
				if( (flag & 0b001) > 0 ){
					hand.discard(new Tile(tile.getIndex()-2)) ;
					hand.discard(new Tile(tile.getIndex()-1)) ;
					hand.discard(tile) ;
					exposed++ ;

					Tile discardTile = decideDiscard(hand) ;
					ArrayList<Tile> discardList = new ArrayList<Tile>() ;

					discardList.add( discardTile ) ;
					discardList.add(new Tile(tile.getIndex()-2)) ;
					discardList.add(new Tile(tile.getIndex()-1)) ;
					discardList.add(tile) ;
					hand.discard( discardTile ) ;

					prevAct = new Action(CHOW, discardList) ;
					return prevAct ;
				}
				else if( (flag & 0b010) > 0 ){
					hand.discard(new Tile(tile.getIndex()-1)) ;
					hand.discard(tile) ;
					hand.discard(new Tile(tile.getIndex()+1)) ;
					exposed++ ;

					Tile discardTile = decideDiscard(hand) ;
					ArrayList<Tile> discardList = new ArrayList<Tile>() ;

					discardList.add( discardTile ) ;
					discardList.add(new Tile(tile.getIndex()-1)) ;
					discardList.add(tile) ;
					discardList.add(new Tile(tile.getIndex()+1)) ;
					hand.discard( discardTile ) ;

					prevAct = new Action(CHOW, discardList) ;
					return prevAct ;
				}
				else {
					hand.discard(tile) ;
					hand.discard(new Tile(tile.getIndex()+1)) ;
					hand.discard(new Tile(tile.getIndex()+2)) ;
					exposed++ ;

					Tile discardTile = decideDiscard(hand) ;
					ArrayList<Tile> discardList = new ArrayList<Tile>() ;

					discardList.add( discardTile ) ;
					discardList.add(tile) ;
					discardList.add(new Tile(tile.getIndex()+1)) ;
					discardList.add(new Tile(tile.getIndex()+2)) ;
					hand.discard( discardTile ) ;

					prevAct = new Action(CHOW, discardList) ;
					return prevAct ;
				}
			}
			else if( doPong(tile) ){
				hand.add(tile) ;
				prevTile = tile.copy() ;
				hand.discard(tile) ;
				hand.discard(tile) ;
				hand.discard(tile) ;
				exposed++ ;

				Tile discardTile = decideDiscard(hand) ;
				ArrayList<Tile> discardList = new ArrayList<Tile>() ;
				discardList.add( discardTile ) ;
				discardList.add(tile) ;
				discardList.add(tile) ;
				discardList.add(tile) ;
				hand.discard( discardTile ) ;

				prevAct = new Action(PONG, discardList) ;
				return prevAct ;
			}
			else
				return null ;
		}
		else{// pong, kong, ron
			if( doHu(tile) ){ /* huable */
				hand.add(tile) ;
				prevTile = tile.copy() ;
				return win(RON) ;
			}
			else if( status == Status.RICHI ){
				prevAct = null ;
				return prevAct ;
			}
			else if( doPong(tile) ){
				hand.add(tile) ;
				prevTile = tile.copy() ;
				hand.discard(tile) ;
				hand.discard(tile) ;
				hand.discard(tile) ;
				exposed++ ;

				Tile discardTile = decideDiscard(hand) ;
				ArrayList<Tile> discardList = new ArrayList<Tile>() ;
				discardList.add( discardTile ) ;
				discardList.add(tile) ;
				discardList.add(tile) ;
				discardList.add(tile) ;
				hand.discard( discardTile ) ;

				prevAct = new Action(PONG, discardList) ;
				return prevAct ;
			}
			else
				return null ;
		}
	}

	//if chow/pong failed, use this method to notify player
	// se chow/pong falhar, use este método para notificar o jogador
	public void failed(){
		if( exposed > 0 )
			exposed-- ;
		for( int i = 0 ; i < prevAct.getTiles().size() ; i++ )
			hand.add( prevAct.getTiles().get(i) ) ;
		hand.discard(prevTile) ;
	}

	public void GameOver(int type, int from){
		exposed = 0 ;
		status = Status.FREE ;
		prevTile = null ;
		prevAct = null ;
	}
}
