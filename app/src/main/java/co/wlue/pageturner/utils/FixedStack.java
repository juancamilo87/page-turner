package co.wlue.pageturner.utils;

/**
 * Created by researcher on 05/09/16.
 */
public class FixedStack<T extends Comparable<T>> {


    private Object[] stack;
    private int size;
    private int bottom;

    public FixedStack(int size) {

        this.stack = new Object[size];
        this.bottom = -1;
        this.size = size;
    }


    public boolean add(T obj) {
        boolean reallyInserted = false;
        boolean inserted = false;
        for(int i = bottom; i>=0&&!inserted; i--) {
            if(get(i).compareTo(obj) > 0) {
                reallyInserted = insert(obj,i+1);
                inserted = true;
            }
        }
        return reallyInserted;
    }

    public T getBottom()
    {
        if (bottom < 0) return null;
        @SuppressWarnings("unchecked")
        T obj = (T) stack[bottom];
        return obj;
    }

    private boolean insert(T obj, int position)
    {
        if(bottom < size-1) {
            bottom++;
            shiftDown(position);
            stack[position] = obj;
            return true;
        }
        else if((position <= size-1)) {
            pop();
            bottom++;
            shiftDown(position);
            stack[position] = obj;
            return true;
        }
        return false;

    }

    private void shiftDown(int position) {
        for(int i = bottom;i>position; i--) {
            stack[i] = stack[i - 1];
        }
    }

    private void pop() {
        stack[bottom] = null;
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

    public T get(int i) {
        @SuppressWarnings("unchecked")
        final T e = (T) stack[i];
        return e;
    }
}
