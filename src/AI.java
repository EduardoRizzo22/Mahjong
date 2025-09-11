import java.lang.*;
import java.util.*;

enum Status {
	FREE, RICHI, WIN
}
public class AI extends Player{

	private final int DRAW = 0 ;
	private final int CHOW = 1 ;
	private final int PONG = 2 ;
	private final int KONG = 3 ;
	private final int ADD_KONG = 4 ;
	private final int CONCEAL_KONG = 5 ;
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
		Collections.sort(tmp.getAll().get(tile.suit));

		/* remove all shuns in the hand */
		int i = 0 ;
		int s = tmp.getAll().get(tile.suit).size() ;
		while(i < s - 2){
			Tile a = tmp.getAll().get(tile.suit).get(i);
			Tile b = tmp.getAll().get(tile.suit).get(i+1);
			Tile c = tmp.getAll().get(tile.suit).get(i+2);
			if(a.index + 1 == b.index && b.index + 1 == c.index){
				tmp.discard(a) ;
				tmp.discard(b) ;
				tmp.discard(c) ;
				s = tmp.getAll().get(tile.suit).size() ;
				continue ;
			}
			else {
				i++ ;
				s = tmp.getAll().get(tile.suit).size() ;
			}
		}

		/* check if the tile you want to chow is left or not */
		s = tmp.getAll().get(tile.suit).size() ;
		for( int j = 0 ; j < s ; j++ ){
			if( tmp.getAll().get(tile.suit).get(j).equals(tile) )
				return false ;
		}
		return true ;
	}

	private boolean doPong(Tile tile){
		if( !hand.pongable(tile) )
			return false ;
	
		Hand tmp = new Hand(hand.getAll()) ;
		tmp.add(tile) ;
		Collections.sort(tmp.getAll().get(tile.suit));

		/* remove all shuns in the hand */
		int i = 0 ;
		int s = tmp.getAll().get(tile.suit).size() ;
		while(i < s - 2){
			Tile a = tmp.getAll().get(tile.suit).get(i);
			Tile b = tmp.getAll().get(tile.suit).get(i+1);
			Tile c = tmp.getAll().get(tile.suit).get(i+2);
			if(a.index + 1 == b.index && b.index + 1 == c.index){
				tmp.discard(a) ;
				tmp.discard(b) ;
				tmp.discard(c) ;
				s = tmp.getAll().get(tile.suit).size() ;
				continue ;
			}
			else {
				i++ ;
				s = tmp.getAll().get(tile.suit).size() ;
			}
		}

		/* check if the tile you want to pong is left and size >= 3 or not */
		s = tmp.getAll().get(tile.suit).size() ;
		for( int j = 0 ; j < s ; j++ ){
			if( tmp.getAll().get(tile.suit).get(j).equals(tile) && tmp.getAll().get(tile.suit).get(j).getSize() >= 3 )
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

	private Tile decideDiscard(Hand _hand){

		Hand tmp = new Hand(_hand.getAll()) ;

		/* initialize discard tile */
		Tile res = null ;
		for( int suit = 3 ; suit >= 0 ; suit-- ){
			if( tmp.getAll().get(suit).size() > 0 ){
				res = tmp.getAll().get(suit).get(0) ;
				break ;
			}
		}

		/* remove all shuns in the hand */
		for( int suit = 0 ; suit <= 2 ; suit++ ){
			Collections.sort(tmp.getAll().get(suit));

			int i = 0 ;
			int s = tmp.getAll().get(suit).size() ;
			while(i < s - 2){
				Tile a = tmp.getAll().get(suit).get(i);
				Tile b = tmp.getAll().get(suit).get(i+1);
				Tile c = tmp.getAll().get(suit).get(i+2);
				if(a.index + 1 == b.index && b.index + 1 == c.index){
					tmp.discard(a) ;
					tmp.discard(b) ;
					tmp.discard(c) ;
					s = tmp.getAll().get(suit).size() ;
					continue ;
				}
				else {
					i++ ;
					s = tmp.getAll().get(suit).size() ;
				}
			}
		}

		for( int suit = 3 ; suit >= 0 ; suit-- ){
			if( tmp.getAll().get(suit).size() > 0 ){
				res = tmp.getAll().get(suit).get(0) ;
				break ;
			}
		}

		/* remove all triplets in the hand */
		for( int suit = 0 ; suit <= 3 ; suit++ ){
			Collections.sort(tmp.getAll().get(suit));

			int i = 0 ;
			int s = tmp.getAll().get(suit).size() ;
			while(i < s){
				Tile a = tmp.getAll().get(suit).get(i) ;
				if( a.getSize() >= 3 ){
					tmp.discard(a) ;
					tmp.discard(a) ;
					tmp.discard(a) ;
					s = tmp.getAll().get(suit).size() ;
					continue ;
				}
				else {
					i++ ;
					s = tmp.getAll().get(suit).size() ;
				}
			}
		}

		for( int suit = 3 ; suit >= 0 ; suit-- ){
			if( tmp.getAll().get(suit).size() > 0 ){
				res = tmp.getAll().get(suit).get(0) ;
				break ;
			}
		}

		/* remove all pairs in the hand */
		for( int suit = 0 ; suit <= 3 ; suit++ ){
			Collections.sort(tmp.getAll().get(suit));

			int i = 0 ;
			int s = tmp.getAll().get(suit).size() ;
			while(i < s){
				Tile a = tmp.getAll().get(suit).get(i) ;
				if( a.getSize() >= 2 ){
					tmp.discard(a) ;
					tmp.discard(a) ;
					s = tmp.getAll().get(suit).size() ;
					continue ;
				}
				else {
					i++ ;
					s = tmp.getAll().get(suit).size() ;
				}
			}
		}

		for( int suit = 3 ; suit >= 0 ; suit-- ){
			if( tmp.getAll().get(suit).size() > 0 ){
				res = tmp.getAll().get(suit).get(0) ;
				break ;
			}
		}

		return res ;
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
				prevTile = tile.same() ;
				return win(HU) ;
			}
			else if( status == Status.RICHI ){
				prevTile = tile.same() ;
				ArrayList<Tile> discardList = new ArrayList<Tile>() ;
				discardList.add( tile ) ;

				prevAct = new Action(DRAW, discardList) ;
				return prevAct ;
			}
			else if( doRichi(tile) ){
				ArrayList<Tile> tingTile = hand.tingable(tile) ;
				hand.add(tile) ;
				prevTile = tile.same() ;

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
				prevTile = tile.same() ;

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
				prevTile = tile.same() ;
				return win(RON) ;
			}
			else if( status == Status.RICHI ){
				prevAct = null ;
				return prevAct ;
			}
			else if( doChow(tile) ){
				int flag = hand.chowable(tile) ;
				hand.add(tile) ;
				prevTile = tile.same() ;
				if( (flag & 0b001) > 0 ){
					hand.discard(new Tile(tile.index-2)) ;
					hand.discard(new Tile(tile.index-1)) ;
					hand.discard(tile) ;
					exposed++ ;

					Tile discardTile = decideDiscard(hand) ;
					ArrayList<Tile> discardList = new ArrayList<Tile>() ;

					discardList.add( discardTile ) ;
					discardList.add(new Tile(tile.index-2)) ;
					discardList.add(new Tile(tile.index-1)) ;
					discardList.add(tile) ;
					hand.discard( discardTile ) ;

					prevAct = new Action(CHOW, discardList) ;
					return prevAct ;
				}
				else if( (flag & 0b010) > 0 ){
					hand.discard(new Tile(tile.index-1)) ;
					hand.discard(tile) ;
					hand.discard(new Tile(tile.index+1)) ;
					exposed++ ;

					Tile discardTile = decideDiscard(hand) ;
					ArrayList<Tile> discardList = new ArrayList<Tile>() ;

					discardList.add( discardTile ) ;
					discardList.add(new Tile(tile.index-1)) ;
					discardList.add(tile) ;
					discardList.add(new Tile(tile.index+1)) ;
					hand.discard( discardTile ) ;

					prevAct = new Action(CHOW, discardList) ;
					return prevAct ;
				}
				else {
					hand.discard(tile) ;
					hand.discard(new Tile(tile.index+1)) ;
					hand.discard(new Tile(tile.index+2)) ;
					exposed++ ;

					Tile discardTile = decideDiscard(hand) ;
					ArrayList<Tile> discardList = new ArrayList<Tile>() ;

					discardList.add( discardTile ) ;
					discardList.add(tile) ;
					discardList.add(new Tile(tile.index+1)) ;
					discardList.add(new Tile(tile.index+2)) ;
					hand.discard( discardTile ) ;

					prevAct = new Action(CHOW, discardList) ;
					return prevAct ;
				}
			}
			else if( doPong(tile) ){
				hand.add(tile) ;
				prevTile = tile.same() ;
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
				prevTile = tile.same() ;
				return win(RON) ;
			}
			else if( status == Status.RICHI ){
				prevAct = null ;
				return prevAct ;
			}
			else if( doPong(tile) ){
				hand.add(tile) ;
				prevTile = tile.same() ;
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
		for( int i = 0 ; i < prevAct.tiles.size() ; i++ )
			hand.add( prevAct.tiles.get(i) ) ;
		hand.discard(prevTile) ;
	}

	public void GameOver(int type, int from){
		exposed = 0 ;
		status = Status.FREE ;
		prevTile = null ;
		prevAct = null ;
	}
}
