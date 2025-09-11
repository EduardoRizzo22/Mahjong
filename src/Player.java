import java.lang.*;
import java.util.*;

public abstract class Player{
	private String name;
	private int score;
	protected Hand hand;
	public Player(String s, int i){
		name = s;
		score = i;
	}

	@Override
	public String toString(){
		return "Hi, I am Player "+name+".";
	}

	//the 13 tiles at the beginning
	public void initHand(ArrayList<ArrayList<Tile>> allTiles){
		hand = new Hand(allTiles);
	}
	public abstract Action doSomething(int from, Tile tile);//from 0自摸 1下一家 2對家 3上一家 {a partir de 0=toque em 1=Próxima casa 2=Casa dupla 3=Casa anterior}
	//	if(from == 0){//摸, 立直 加槓, 暗槓, 胡
	//		
	//	}
	//	else if(from == 3){//吃, 碰, 槓, 榮
	//	
	//	}
	//	else{//碰, 槓, 榮
	//	
	//	}
	public abstract void failed();//上一個動作失敗了 {A ação anterior falhou}

	public void addScore(int s){
		score+= s;
	}

	public abstract void GameOver(int type, int from);//type 0流局 1榮 2自摸 from 0自摸 1下一家 2對家 3上一家 {tipo 0 jogo de fluxo 1 honra 2 autotoque de 0 autotoque 1 próxima família 2 pares família 3 família anterior}

}
