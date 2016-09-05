package co.wlue.pageturner.utils;

/**
 * Created by researcher on 05/09/16.
 */
public class FixedDoubleStack<T extends Comparable<T>> {


    private Object[] stackOne;
    private Object[] stackTwo;
    private int size;
    private int bottom;

    public FixedDoubleStack(int size) {

        this.stackOne = new Object[size];
        this.stackTwo = new Object[size];
        this.bottom = -1;
        this.size = size;
    }


    public boolean add(T objOne, T objTwo) {
        boolean reallyInserted = false;
        boolean inserted = false;
        for(int i = bottom; i>=0&&!inserted; i--) {
            if(get(i)[0].compareTo(objOne) > 0) {
                reallyInserted = insert(objOne, objTwo,i+1);
                inserted = true;
            }
        }
        return reallyInserted;
    }

    @SuppressWarnings("unchecked")
    public T[] getBottom()
    {
        if (bottom < 0) return null;

        T[] obj = (T[]) new Object[2];
        obj[0] = (T) stackOne[bottom];
        obj[1] = (T) stackTwo[bottom];
        return obj;
    }

    @SuppressWarnings("unchecked")
    public T[] getTop()
    {
        T[] obj = (T[]) new Object[2];
        obj[0] = (T) stackOne[0];
        obj[1] = (T) stackTwo[0];
        return obj;
    }

    private boolean insert(T objOne, T objTwo, int position)
    {
        if(bottom < size-1) {
            bottom++;
            shiftDown(position);
            stackOne[position] = objOne;
            stackTwo[position] = objTwo;
            return true;
        }
        else if((position <= size-1)) {
            pop();
            bottom++;
            shiftDown(position);
            stackOne[position] = objOne;
            stackTwo[position] = objTwo;
            return true;
        }
        return false;

    }

    private void shiftDown(int position) {
        for(int i = bottom;i>position; i--) {
            stackOne[i] = stackOne[i - 1];
            stackTwo[i] = stackTwo[i - 1];
        }
    }

    private void pop() {
        stackOne[bottom] = null;
        stackTwo[bottom] = null;
        bottom--;
    }

    public int size()
    {
        return size;
    }

    public int elements()
    {
        return bottom + 1;
    }

    @SuppressWarnings("unchecked")
    public T[] get(int i) {
        T[] obj = (T[]) new Object[2];
        obj[0] = (T) stackOne[i];
        obj[1] = (T) stackTwo[i];
        return obj;
    }
}
