import java.util.ArrayList;

// MyHeap class is my implementation of a min-heap.
// This class is used to sort Attempts in the attempts PQ, and Players in the massage-queue in the ExcelFedManager class.
public class MyHeap<E extends Comparable<E>> {

	// Root is index 1, and index 0 is not used.
	// Heap data is stored in the array list.
    private final int ROOT = 1;
    private int capacity,size;
    protected ArrayList<E> myHeap;

    // Getters and Setters:
    int getSize() {return size;}
    public int getCapacity() {return capacity;}
    public void setCapacity(int capacity) {this.capacity = capacity; }

    // Constructor:
    public MyHeap(int capacity) {
        setCapacity(capacity);
        myHeap = new ArrayList<E>(capacity);
        myHeap.add(0, null);
        this.size = 0;
    }

    // Following methods checks whether the heap is empty or full. 
    boolean isEmpty() {
        return size==0;
    }

    boolean isFull() {
        return size==capacity;
    }

    // To add an element to the heap, insert method and percolateUp method in the insert method are used.
    public void insert(E e){
        if( isFull() )
            myHeap.ensureCapacity(myHeap.size()*2);
        capacity = myHeap.size();

        // percolate Up:
        myHeap.add(size+1, e);
        size++;
        percolateUp(e, size);
    }

    public void percolateUp(E e, int startNode) {
        int hole = startNode;
        E temp = e;
        for( ; hole > ROOT && temp.compareTo(myHeap.get(hole/2)) < 0; hole = hole/2 ) {
            temp = myHeap.get(hole);
            if(hole > size) myHeap.add(hole, myHeap.get(hole/2));
            else myHeap.set(hole, myHeap.get(hole/2));
            myHeap.set(hole/2, temp);
        }
        myHeap.set(hole, temp);

    }
    
    // To delete an element from the heap, deleteMin method and percolateDown method in the deleteMin method are used.
    public E deleteMin(){
        if( isEmpty() )
            return null;

        E returnMin = myHeap.get(ROOT);
        myHeap.set(ROOT, myHeap.get(size));
        percolatedown(ROOT);
        myHeap.set(size, null);
        size--;
        return returnMin;
    }

    public void percolatedown(int startNode){
        int child;
        E temp = myHeap.get(startNode);
        int hole;

        for(hole = startNode; hole * 2 <= size; hole = child ){
            child = hole*2;

            if(child != size && myHeap.get(child+1).compareTo(myHeap.get(child))  < 0) child++;
            if(myHeap.get(child).compareTo(temp) < 0) myHeap.set(hole, myHeap.get(child));
            else break;
        }
        myHeap.set(hole, temp);
    }

    // ToString method:
//    public String toString(){
//        String res = "";
//        for(E d: myHeap) {
//            if(d!= null)
//                res += d.toString() + "\n";
//        }
//        res += "\n";
//        return res;
//    }


}
