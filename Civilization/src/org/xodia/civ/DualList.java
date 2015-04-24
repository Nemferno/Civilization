package org.xodia.civ;

import java.util.ArrayList;
import java.util.List;

public class DualList<V, K> {

	private List<V> keyOneList;
	private List<K> keyTwoList;
	
	public DualList(int size){
		keyOneList = new ArrayList<V>(size);
		keyTwoList = new ArrayList<K>(size);
	}
	
	public DualList(){
		this(16);
	}
	
	public void add(V v, K k){
		keyOneList.add(v);
		keyTwoList.add(k);
	}
	
	public void remove(int index){
		keyOneList.remove(index);
		keyTwoList.remove(index);
	}
	
	public void remove(Object o){
		int index = 0;
		for(V v : keyOneList){
			if(v == o){
				index++;
				break;
			}
		}
		
		remove(index);
	}
	
	public int size(){
		return keyOneList.size();
	}
	
	public Object[][] get(int index){
		Object[][] list = new Object[keyOneList.size()][2];
		
		for(int i = 0; i < keyOneList.size(); i++){
			list[i][0] = keyOneList.get(i);
			list[i][1] = keyTwoList.get(i);
		}
		
		return list;
	}
	
}
