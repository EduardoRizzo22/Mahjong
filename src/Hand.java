import java.lang.*;
import java.util.*;

import java.lang.*;
import java.util.*;

public class Hand {
	
	private ArrayList<ArrayList<Tile>> allTiles;

	public Hand(){
		allTiles = new ArrayList<ArrayList<Tile>>();
		allTiles.add(new ArrayList<Tile>());	//wan
		allTiles.add(new ArrayList<Tile>());	//tong
		allTiles.add(new ArrayList<Tile>());	//tiao
		allTiles.add(new ArrayList<Tile>());	//zi
	}

	public Hand(ArrayList<ArrayList<Tile>> all){
		//printList(all);
		//System.out.println(all.get(0).size() + " " + all.get(1).size() + " " + all.get(2).size() + " " + all.get(3).size());
		allTiles = new ArrayList<ArrayList<Tile>>();
		allTiles.add(new ArrayList<Tile>());	//wan
		allTiles.add(new ArrayList<Tile>());	//tong
		allTiles.add(new ArrayList<Tile>());	//tiao
		allTiles.add(new ArrayList<Tile>());	//zi
		for(int i = 0;i < 4;i++){
			for(Tile temp: all.get(i)){
				allTiles.get(i).add(temp.same());
			}		

		}
	}

	private ArrayList<Tile> ting = new ArrayList<Tile>();

	public ArrayList<ArrayList<Tile>> getAll(){
		return allTiles;	
	}

	public void add(Tile n){
		Tile newTile = n.same();
		newTile.setSize(1);
		int index = allTiles.get(newTile.suit).indexOf(newTile);
		if(index >= 0) allTiles.get(newTile.suit).get(index).addSize(1);
		else allTiles.get(newTile.suit).add(newTile);
		sort();
	}
	
	public boolean discard(Tile n){ //If no this tile in hand return false (An error) {Se não, este bloco em mãos retornará falso (um erro)}
		Tile discardTile = n.same();
		discardTile.setSize(1);
		int index = allTiles.get(discardTile.suit).indexOf(discardTile);
		if(index < 0) return false;
		if(allTiles.get(discardTile.suit).get(index).getSize() > 1){ 
			allTiles.get(discardTile.suit).get(index).addSize(-1);
			return true;			
		}
		allTiles.get(discardTile.suit).remove(index);
		sort();
		return true;
	}

	public boolean replace(Tile n, Tile o){ //If this old tile is not in hand return false (An error) {Se este bloco antigo não estiver em mãos, retorne falso (um erro)}
		Tile oldTile = o.same();
		oldTile.setSize(1);
		Tile newTile = n.same();
		newTile.setSize(1);
		int oldIndex = allTiles.get(oldTile.suit).indexOf(oldTile);
		if(oldIndex < 0) return false;
		if(allTiles.get(oldTile.suit).get(oldIndex).getSize() > 1) allTiles.get(oldTile.suit).get(oldIndex).addSize(-1);			
		else allTiles.get(oldTile.suit).remove(oldIndex);

		int newIndex = allTiles.get(newTile.suit).indexOf(newTile);

		if(newIndex >= 0) allTiles.get(newTile.suit).get(newIndex).addSize(1);
		else allTiles.get(newTile.suit).add(newTile);
		sort();
		return true;
	}

	public boolean pongable(Tile newTile){
		for(Tile t : allTiles.get(newTile.suit)){
			if(newTile.index == t.index && t.getSize() >= 2){
				return true;			
			}		
		}	
		return false;
	}

	public int chowable(Tile newTile){
		int flag = 0;		
		if(newTile.suit == 3) return 0;
		if(newTile.value >= 2)
			 if(allTiles.get(newTile.suit).contains(new Tile(newTile.index - 1)))
				 if(allTiles.get(newTile.suit).contains(new Tile(newTile.index - 2)))
					flag |= 0b001;
		if(newTile.value <= 7)
			 if(allTiles.get(newTile.suit).contains(new Tile(newTile.index + 1)))
				 if(allTiles.get(newTile.suit).contains(new Tile(newTile.index + 2)))
					flag |= 0b100;

		if(newTile.value <= 8 && newTile.value >= 1)
			 if(allTiles.get(newTile.suit).contains(new Tile(newTile.index - 1)))
				 if(allTiles.get(newTile.suit).contains(new Tile(newTile.index + 1)))
					flag |= 0b010;
				
		return flag;
	}

	public boolean kongable(Tile newTile){
		for(Tile t : allTiles.get(newTile.suit)){
			if(newTile.index == t.index && t.getSize() == 3){
				return true;			
			}		
		}	
		return false;
	}
	
	/*public boolean huable(Tile newTile){
		for(Tile t : ting){
			if(newTile.index == t.index) return true;		
		}	
		return false;
	}*/	

	public ArrayList<Tile> tingable(Tile newTile){
		boolean takepair = true;
		ArrayList<Tile> noPairTing = new ArrayList<Tile>();
		noPairTing.add(newTile);	

		ArrayList<Tile> res = new ArrayList<Tile>();

		add(newTile);

		sort();		

		ArrayList<Tile> pair = new ArrayList<Tile>();
		

		for(ArrayList<Tile> temp:allTiles){
			for(Tile t:temp){
				if(t.getSize() >= 2) pair.add(t);
			}		
		}
		for(int i = 0;i < pair.size() + 1;i++){

			//System.out.println("Pair:" +pair.get(i));
			Hand tempHand = new Hand(allTiles);

			//System.out.println("tempHand_origin:" + tempHand);
			if(i != pair.size()){
				tempHand.discard(pair.get(i));
				tempHand.discard(pair.get(i));
			}else{
				takepair = false;
			}

			//System.out.println("tempHand:" + tempHand);
			ArrayList<Tile> Triplet = new ArrayList<Tile>();
			for(ArrayList<Tile> temp:tempHand.getAll()){
				for(Tile t:temp){
					if(t.getSize() >= 3){
						Triplet.add(t);				
					}
				}		
			}
			for(Tile t:Triplet){
				tempHand.discard(t);				
				tempHand.discard(t);
				tempHand.discard(t);				
			} 

			
			//System.out.println("tempHand:" + tempHand);
			ArrayList<Hand> shunTemp = new ArrayList<Hand>();
			for(int j = 0;j < 8;j++){
				Hand temp = new Hand(tempHand.getAll());
				temp.takeShun(0, ((j & 0b001) == 0));
				temp.takeShun(1, ((j & 0b010) == 0));
				temp.takeShun(2, ((j & 0b100) == 0));
				shunTemp.add(temp);
			}
			
			for(int j = 0;j < 8;j++){
				//System.out.println(shunTemp.get(j));
				ArrayList<ArrayList<Tile>> temp = shunTemp.get(j).getAll();
				int nLeft = 0;
				for(int k = 0;k < 3;k++){
					if(!temp.get(k).isEmpty()){
						nLeft++;								
					}
				}	
				if(nLeft == 0){
					discard(newTile);
					return null;
				}
				ArrayList<Tile> theHope;
				//System.out.println("nLeft:" + nLeft);
				if(nLeft <= 2){
					theHope = new ArrayList<Tile>();
					for(ArrayList<Tile> content:temp) theHope.addAll(content);
					

					//System.out.println("theHope:" + theHope);
					if(theHope.size() == 2 && takepair == true){
						//System.out.println(theHope.get(0).getSize() + " " + theHope.get(1).getSize());
						if(theHope.get(0).getSize() == 2 && theHope.get(1).getSize() == 1){
							Tile t1 = theHope.get(1).same();
							Tile t2 = theHope.get(0).same();
							if(res.indexOf(t1) < 0) res.add(t1);			
						}
						if(theHope.get(0).getSize() == 1 && theHope.get(1).getSize() == 2){
							Tile t1 = theHope.get(0).same();
							Tile t2 = theHope.get(1).same();
							if(res.indexOf(t1) < 0) res.add(t1);							
						}
										
					}
					if(theHope.size() == 3 && takepair == true){
						if(theHope.get(0).getSize() == 1 && theHope.get(1).getSize() == 1 && theHope.get(2).getSize() == 1){
							if(theHope.get(0).index + 1 == theHope.get(1).index && theHope.get(0).suit == theHope.get(1).suit && theHope.get(0).suit != 3){
								Tile t3 = theHope.get(2).same();
								if(res.indexOf(t3) < 0) res.add(t3);
								Tile t1 = theHope.get(0).same(-1);
								Tile t2 = theHope.get(1).same(1);
											
							}
							if(theHope.get(1).index + 1 == theHope.get(2).index && theHope.get(1).suit == theHope.get(2).suit && theHope.get(1).suit != 3){
								Tile t3 = theHope.get(0).same();
								if(res.indexOf(t3) < 0) res.add(t3);
								Tile t1 = theHope.get(1).same(-1);
								//System.out.println("t1:" + t1);
								Tile t2 = theHope.get(2).same(1);
							}
						}					
					}
				
					if(theHope.size() == 2 && takepair == false){
						if(theHope.get(0).getSize() == 1 && theHope.get(1).getSize() == 1){
							Tile t1 = theHope.get(0).same();
							Tile t2 = theHope.get(1).same();
							if(res.indexOf(t1) < 0) res.add(t1);
							if(res.indexOf(t2) < 0) res.add(t2);
						}
					}
						
				}	
				
			}			
		}		
		// if(noPairTing.size() > 1){
				
			// for(Tile t:noPairTing) if(res.indexOf(t) < 0) res.add(t);
		// }

		discard(newTile);
		//System.out.println("Hand:" + this);
		//System.out.println("New Tile:" + newTile);
		//System.out.println("Discard:");
		//for(Tile temp:res){
		//	System.out.print(temp + " ");
			
		//}
		//System.out.println("");	
		return res;
	}

	public void takeShun(int suit, boolean direction){
		Collections.sort(allTiles.get(suit));
		int i = 0;
		if(direction){
			int s = allTiles.get(suit).size();
			while(i < s - 2){
				//System.out.println(i + " " + allTiles.get(suit).size());
				Tile a = allTiles.get(suit).get(i);
				Tile b = allTiles.get(suit).get(i+1);
				Tile c = allTiles.get(suit).get(i+2);
				if(a.index + 1 == b.index && b.index + 1 == c.index){
					discard(a);
					discard(b);
					discard(c);
				}else{
					i++;
				}		
				s = allTiles.get(suit).size();
			}					
		}else{
			int s = allTiles.get(suit).size();
			int j = 3;
			i = s - j;
			while(i >= 0){
				//System.out.println(suit + " " + allTiles.get(suit).size());
				Tile a = allTiles.get(suit).get(i);
				Tile b = allTiles.get(suit).get(i+1);
				Tile c = allTiles.get(suit).get(i+2);
				if(a.index + 1 == b.index && b.index + 1 == c.index){
					discard(a);
					discard(b);
					discard(c);
				}else{
					j++;
				}		
				s = allTiles.get(suit).size();
				i = s - j;	
			}
		}
	}

	public void sort(){
		Collections.sort(allTiles.get(0));	
		Collections.sort(allTiles.get(1));
		Collections.sort(allTiles.get(2));
		Collections.sort(allTiles.get(3));
	}

	public String toString(){
		String s = "";
		for(ArrayList<Tile> temp:allTiles){
			for(Tile t:temp){
				for(int i = 0;i < t.getSize();i++)
				s += (t.toString() + " ");	
			}		
		}	
		return s;
	}	

	public void printList(ArrayList<ArrayList<Tile>> a){
		String s = "";
		for(ArrayList<Tile> temp:a){
			for(Tile t:temp){
				for(int i = 0;i < t.getSize();i++)
				s += (t.toString() + " ");	
			}		
		}
		System.out.println(s);
	}
	
}


