import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * Circularly doubly-linked list.
 * @author Sam Rebelsky
 * @author Pranav K Bhandari
 */

public class SimpleCDLL<T> implements SimpleList<T> {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  private int iterCount; // count of changes made by an iterator
  private Node2<T> dummy; // dummy node to be used
  private int size; // number of elements in the list
  
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Creates an empty CDLL
   */
  public SimpleCDLL() {
    this.dummy = new Node2<T>(null);
    this.dummy.next = this.dummy;
    this.dummy.prev = this.dummy;
    this.size = 0;
  } // SimpleCDLL()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+
  
  public Iterator<T> iterator() {
    return listIterator();
  } // iterator()

  public ListIterator<T> listIterator() {
    return new ListIterator<T>() {
      int pos = 0; // position of next value to be returned

      Node2<T> prev = SimpleCDLL.this.dummy;
      Node2<T> next = SimpleCDLL.this.dummy.next;

      Node2<T> update = null; // next value to be updated be remove or set
      
      int numChanges = SimpleCDLL.this.iterCount; // number of changes since creation of Iterator

      public boolean hasNext() {
        this.failFast();
        return (this.pos < SimpleCDLL.this.size);
      } // hasNext()

      public boolean hasPrevious() {
        this.failFast();
        return (this.pos > 0);
      } // hasPrevious()

      public T next() {
        this.failFast();
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if
        
        this.update = this.next;

        // Advancing the cursor
        this.prev = this.next;
        this.next = this.next.next;

        this.pos++;

        // returning the desired value
        return this.update.value;
      } // next()

      public T previous() {
        this.failFast();
        if (!this.hasPrevious()) {
          throw new NoSuchElementException();
        } // if

        this.update = this.prev;

        // moving the cursor back
        this.next = this.prev;
        this.prev = this.prev.prev;

        this.pos--;

        return this.next.value;
      } //previous()

      public int nextIndex() {
        this.failFast();
        return this.pos;
      } // nextIndex()

      public int previousIndex() {
        this.failFast();
        return this.pos - 1;
      } // previousIndex()

      public void remove() {
        this.failFast();
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // arrived here by call to previous
        if (this.next == this.update) {
          this.next = this.update.next;
        } // if

        // arrived here by call to next
        if (this.prev == this.update) {
          this.prev = this.update.prev;
          this.pos--;
        } // if

        this.update.remove();

        SimpleCDLL.this.size--;
        SimpleCDLL.this.iterCount++;

        this.numChanges++;
        this.update = null;
      }

      public void set(T val) {
        this.failFast();
        if (this.update == null) {
          throw new IllegalStateException();
        } // if
        this.update.value = val;
        this.update = null;
      }

      public void add(T val) {
        this.failFast();
        this.prev = this.prev.insertAfter(val);
        this.update = null;

        SimpleCDLL.this.size++;
        SimpleCDLL.this.iterCount++;
        
        this.numChanges++;
        this.pos++;
      } // add(T val)

      private void failFast() {
        if (this.numChanges != SimpleCDLL.this.iterCount) {
          throw new ConcurrentModificationException();
        }// if
      } // failFast()
    };
  }
}
