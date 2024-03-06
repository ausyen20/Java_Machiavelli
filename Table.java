import java.util.ArrayList;

public class Table {
    
    private ArrayList<ArrayList<Card>> cardsonTable;

    
    public Table(){
        cardsonTable = new ArrayList<ArrayList<Card>>();
    }

    public void addSelected2Table(ArrayList<Card> selects){
        cardsonTable.add(selects);
    }

    public ArrayList<ArrayList<Card>> getTable(){
        return this.cardsonTable;
    }

    public void setTable(ArrayList<ArrayList<Card>> table){
        cardsonTable = table;
    }

    public int getTableSize(){
        return cardsonTable.size();
    }

    public ArrayList<Card> getListViaIndex(int index){
        return cardsonTable.get(index);
    }

    public void replaceListviaIndex(ArrayList<Card> temp ,int index){
        cardsonTable.set(index, temp);
    }
    public ArrayList<Card> getLastFromTable(){
        return cardsonTable.get(cardsonTable.size() - 1);
    }

    public void removeFromTable(int index){
        cardsonTable.remove(index);
    }

}
