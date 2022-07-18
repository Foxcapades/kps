package io.foxcapades.lib.kps.kpd.base

/**
 * # UShort Deque Base
 *
 * Base, templated type backing the `UShortDeque` class.
 *
 * @param D Implementing type.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
@Suppress("NOTHING_TO_INLINE")
abstract class AbstractUShortDeque<D : AbstractUShortDeque<D>> {

  protected var container: UShortArray

  /**
   * Index of the 'first' element in the deque, which may or may not be the
   * first element in the backing array.
   *
   * When the deque is accessed by a given index (external index), the
   * [realHead] value is used to calculate the actual index (internal index).
   *
   * In the following examples, the `|` character represents the [realHead]
   * position and the value underneath it is the actual [realHead] value for the
   * example.
   *
   * *Compacted*
   * ```
   * Deque{1, 2, 3, 4, 5, 6, 0, 0, 0}
   *       |
   *       0
   * ```
   *
   * *Uncompacted*
   * ```
   * Deque{4, 5, 6, 0, 0, 0, 1, 2, 3}
   *                         |
   *                         6
   * ```
   */
  protected var realHead: Int

  /**
   * Indicates whether the data in the backing array is currently inline.
   */
  protected inline val isInline
    get() = realHead <= lastIndex



  /**
   * Number of elements in this deque.
   *
   * This value will always be less than or equal to the value of [cap].
   *
   * **Example**
   * ```
   * // Create a deque with an initial capacity (cap) value of `9`
   * val deque = Deque(9)        // deque == {0, 0, 0, 0, 0, 0, 0, 0, 0}
   *
   * // Deque size will be 0 as no elements have been inserted.
   * assert(deque.size == 0)
   *
   * // Add some elements to the deque
   * deque += [1, 2, 3, 4, 5, 6] // deque == {1, 2, 3, 4, 5, 6, 0, 0, 0}
   *
   * // Deque size will now be 6 as we appended 6 elements to the empty deque.
   * assert(deque.size == 6)
   * ```
   */
  var size: Int
    protected set

  /**
   * Currently allocated capacity.
   *
   * Values may be added to this deque until [size] == [capacity] before the
   * deque will reallocate a larger backing buffer.
   *
   * This value will always be greater than or equal to [size].
   *
   * **Example**
   * ```
   * // Create a deque with an initial capacity value of `9`
   * val deque = Deque(9)        // deque == {0, 0, 0, 0, 0, 0, 0, 0, 0}
   *
   * // Even though we have not yet inserted any elements, the capacity is 9
   * assert(deque.capacity == 9)
   *
   * // Add some elements to the deque
   * deque += [1, 2, 3, 4, 5, 6] // deque == {1, 2, 3, 4, 5, 6, 0, 0, 0}
   *
   * // Deque capacity will still be 9 as we have not yet inserted enough
   * // elements to require a capacity increase
   * assert(deque.capacity == 9)
   *
   * // Increase the capacity to 12
   * deque.ensureCapacity(12)    // deque == {1, 2, 3, 4, 5, 6, 0, 0, 0, 0, 0, 0}
   * ```
   *
   * The backing buffer is always increased to at minimum hold the number of
   * values being inserted, but the rate of increase may scale the size of the
   * capacity faster than that.
   *
   * This is to avoid many repeated re-allocations of the backing container.
   * To put it simply, it would be very expensive to use a deque (or [ArrayList]
   * for that matter) if every new element required a resize.
   */
  val capacity: Int
    get() = container.size

  /**
   * The amount of space left in the currently allocated backing container for
   * this deque.
   *
   * Inserting a number of elements into this deque greater than [space] will
   * cause the deque's backing container to be resized to accommodate the new
   * values.
   *
   * This value is calculated as `cap - size`.
   */
  val space: Int
    get() = container.size - size

  /**
   * Index of the last element in this deque
   *
   * If this deque is empty, this value will be -1.
   */
  inline val lastIndex: Int
    get() = size - 1


  constructor(initCapacity: Int = 0) {
    container = UShortArray(initCapacity)
    size = 0
    realHead = 0
  }

  constructor(values: UShortArray) {
    container = values.copyOf()
    size = values.size
    realHead = 0
  }

  protected constructor(values: UShortArray, head: Int) {
    container = values
    size = values.size
    realHead = head
  }

  /**
   * Creates a new instance of the implementing type.
   *
   * Implementers of this method should utilize the protected constructor
   * provided by this abstract class to implement this method.
   */
  protected abstract fun new(values: UShortArray, head: Int): D

  /**
   * Whether this deque is empty (contains no elements).
   *
   * This value is unrelated to the current [cap] value.
   *
   * This value is a convenience shortcut for `size == 0`
   */
  open fun isEmpty() = size == 0

  // region Container Management

  /**
   * Clears all elements from this deque, leaving it empty, but with the same
   * allocated capacity.
   *
   * **Example**
   * ```
   * val deq = Deque.of(1, 2, 3)
   *
   * assert(deq.size == 3)
   * assert(deq.capacity == 3)
   *
   * deq.clear()
   *
   * assert(deq.size == 0)
   * assert(deq.capacity == 3)
   * ```
   */
  fun clear() {
    realHead = 0
    size = 0
  }

  /**
   * Optional operation that rearranges the data in the internal container to be
   * in a single contiguous block.
   *
   * This is particularly useful when preparing a deque to be used for repeated
   * reads from a stream source as it minimizes the number of internal reads
   * needed to fill the deque's backing container.
   *
   * The size, capacity, and publicly accessible data of this deque will not be
   * altered by this method.
   *
   * This operation happens in `O(n)` time, where `n` is the current deque
   * [capacity].
   *
   * **Example**
   *
   * The following example describes the state of a deque containing the values
   * `1, 2, 3, 4, 5, 6` before and after calling the [compact] method.
   *
   * To create a deque that needs to be compacted, we will need to insert data
   * at both ends of the deque.
   * ```
   * // Create a deque with an initial capacity
   * val deque = Deque(9)
   *
   * // Append some elements to the end of the deque
   * deque += [4, 5, 6] // deque == {4, 5, 6, 0, 0, 0, 0, 0, 0}
   *
   * // Insert some elements at the front of the deque
   * deque.pushHead(3)  // deque == {4, 5, 6, 0, 0, 0, 0, 0, 3}
   * deque.pushHead(2)  // deque == {4, 5, 6, 0, 0, 0, 0, 2, 3}
   * deque.pushHead(1)  // deque == {4, 5, 6, 0, 0, 0, 1, 2, 3}
   *
   * // Compact the deque
   * deque.compact()    // deque == {1, 2, 3, 4, 5, 6, 0, 0, 0}
   * ```
   */
  fun compact() = copyElements(container.size)

  /**
   * Trims the capacity of this deque to be the same as the current size.
   *
   * Callers can use this operation to minimize the storage used by a deque
   * instance to only the space necessary to hold [size] values.
   *
   * This operation is optional and is not necessary in most deque use cases.
   * Instances where many deques are in play, deques are pre-sized with large
   * initial capacities, and/or deques are held for long periods of time are
   * examples of situations where this action may be desirable.
   *
   * The data in the deque will be [compacted][compact] as part of this
   * operation.
   *
   * This operation happens in `O(n)` time, where `n` is the current deque
   * [size].
   *
   * **Examples**
   *
   * *Uncompacted*
   *
   * The following example describes the internal state change of an uncompacted
   * ("out of line") deque when [trimToSize] is called.
   *
   * ```
   * // Create our deque
   * val deque = Deque(9)
   *
   * // Populate it
   * populateDeque(deque) // deque == {4, 5, 6, 0, 0, 0, 1, 2, 3}
   *
   * // Trim it
   * deque.trimToSize()   // deque == {1, 2, 3, 4, 5, 6}
   * ```
   *
   * *Compacted*
   *
   * The following example describes the internal state change of a compacted
   * ("inline") deque when [trimToSize] is called.
   *
   * ```
   * // Create our deque
   * val deque = Deque(9)
   *
   * // Populate it
   * populateDeque(deque) // deque == {1, 2, 3, 4, 5, 6, 0, 0, 0}
   *
   * // Trim it
   * deque.trimToSize()   // deque == {1, 2, 3, 4, 5, 6}
   */
  fun trimToSize() = copyElements(size)

  /**
   * Ensures that this deque has at least the given capacity allocated.
   *
   * If the current capacity of this deque is less than the given value, the
   * underlying container will be resized to have a capacity of *at least*
   * [minCapacity].
   *
   * If the current capacity of this deque is already greater than or equal to
   * the given value, this method does nothing.
   *
   * @param minCapacity Minimum capacity this deque must have.
   */
  fun ensureCapacity(minCapacity: Int) {
    when {
      // If they gave us an invalid capacity
      minCapacity < 0               -> throw IllegalArgumentException()
      // If we already have the desired capacity
      minCapacity <= container.size -> {}
      // If we previously had a capacity of 0
      container.isEmpty()           -> container = UShortArray(minCapacity)
      // If we need to resize
      else                          -> copyElements(newCap(container.size, minCapacity))
    }
  }

  // endregion Container Management



  // region Data Copying

  /**
   * Creates a copy of this deque and its data.
   *
   * The copied deque instance will have the same capacity as the original.
   */
  fun copy() = new(container, realHead)



  /**
   * Creates a new deque containing the elements from this deque that fall
   * within the specified index range.
   *
   * **Example**
   * ```
   * val deque = Deque{0, 1, 2, 3, 4, 5}
   *
   * deque.slice(1, 4) // Deque{1, 2, 3}
   * ```
   *
   * @param start Inclusive start position of the range of values to slice.
   *
   * @param end Exclusive end position of the range of values to slice.
   *
   * @return A new deque containing the elements from this deque that fall
   * within the given specified index range.
   *
   * @throws IndexOutOfBoundsException If [start] is less than `0`, if [end] is
   * greater than [size], or if [start] is greater than [end].
   */
  fun slice(start: Int, end: Int) = new(sliceToArray(start, end), 0)

  /**
   * Creates a new deque containing the elements from this deque that fall
   * within the given index range.
   *
   * **Example**
   * ```
   * val deque = Deque{0, 1, 2, 3, 4, 5}
   *
   * deque.slice(1..4) // Deque{1, 2, 3}
   * ```
   *
   * @param range Inclusive range of indices that will be sliced.
   *
   * @return A new deque containing the elements from this deque that fall
   * within the given specified index range.
   *
   * @throws IndexOutOfBoundsException If the start of the given range is less
   * than `0`, if the end of the given range is greater than or equal to [size],
   * or if the start of the given range is greater than the end.
   */
  inline fun slice(range: IntRange) = slice(range.first, range.last + 1)

  /**
   * Copies data from this deque into the given array.
   *
   * If either this deque, or the given array are empty, nothing is copied.
   *
   * If this deque's size is greater than the length of the given array, only
   * those values that can fit into the given array will be copied.
   *
   * If the given array's size is greater than the size of this deque, at most
   * [size] values will be copied into the given array.
   *
   * @param array Array into which values should be copied from this deque.
   *
   * @param offset Offset in the input array at which values should start to be
   * copied.
   *
   * @param len The maximum number of values to read.
   *
   * @return The number of values actually read into the given array.
   *
   * @throws IndexOutOfBoundsException If [offset] is negative or is greater
   * than the size of [array], or if len is greater than `array.size - offset`.
   */
  fun copyInto(array: UShortArray, offset: Int = 0, len: Int = array.size - offset): Int {

    // If they gave us a bad offset, or if the length is greater than the
    // max slots avail between the size and offset, throw an exception.
    if (offset !in array.indices || len > array.size - offset)
      throw IndexOutOfBoundsException()

    // If the input array has a size of 0, there is nothing to do.
    //
    // If the size of this deque is empty, there is nothing to do.
    //
    // If len < 1 then there is nothing to do.
    if (array.isEmpty() || size == 0 || len < 1)
      return 0

    // Figure out how many values we are actually going to be copying.
    val count = if (len > size) size else len

    // Figure out the real location of the last value we will copy
    val tail = internalIndex(count - 1)

    // If the tail is after the head, then the data we want is inline and we
    // can do a single copy.
    if (tail >= realHead) {
      container.copyInto(array, offset, realHead, tail + 1)
      return count
    }

    // The tail is before the head.  This means that we are going to read all
    // the way to the end of our internal array, then read at least one value
    // from the front of the array.
    //
    // [4, 5, 6, 1, 2, 3]
    //  ---|     |------

    // Read the values from the end of our internal array into the target array
    container.copyInto(array, offset, realHead, container.size)

    // Read the values from the front of our internal array into the target
    // array
    container.copyInto(array, offset + (container.size - realHead), 0, tail + 1)

    return count
  }

  /**
   * Creates a new array containing the elements from this deque that fall
   * within the specified index range.
   *
   * **Example**
   * ```
   * val deque = Deque{0, 1, 2, 3, 4, 5}
   *
   * deque.sliceToArray(1, 4) // [1, 2, 3]
   * ```
   *
   * @param start Inclusive start position of the range of values to slice.
   *
   * @param end Exclusive end position of the range of values to slice.
   *
   * @return A new array containing the elements from this deque that fall
   * within the given specified index range.
   *
   * @throws IndexOutOfBoundsException If [start] is less than `0`, if [end] is
   * greater than [size], or if [start] is greater than [end].
   */
  fun sliceToArray(start: Int, end: Int): UShortArray {
    // If they gave us one or more invalid indices, throw an exception
    if (start !in 0 until size || start > end || end > size)
      throw IndexOutOfBoundsException()

    val realSize = end - start

    // Shortcuts
    when (realSize) {
      0    -> return UShortArray(0)
      1    -> return UShortArray(1) { container[internalIndex(start)] }
      size -> return toArray()
    }

    val realStart = internalIndex(start)
    val realEnd = internalIndex(end)

    val out = UShortArray(realSize)

    // If the values are inline, we can just arraycopy out
    if (realStart < realEnd) {
      container.copyInto(out, 0, realStart, realEnd)
    }

    // The values are out of line, we have to do 2 copies
    else {
      container.copyInto(out, 0, realStart, container.size)
      container.copyInto(out, container.size - realStart, 0, realEnd)
    }

    return out
  }

  /**
   * Creates a new array containing the elements from this deque that fall
   * within the given index range.
   *
   * **Example**
   * ```
   * val deque = Deque{0, 1, 2, 3, 4, 5}
   *
   * deque.slice(1..4) // Deque{1, 2, 3}
   * ```
   *
   * @param range Inclusive range of indices that will be sliced.
   *
   * @return A new array containing the elements from this deque that fall
   * within the given specified index range.
   *
   * @throws IndexOutOfBoundsException If the start of the given range is less
   * than `0`, if the end of the given range is greater than or equal to [size],
   * or if the start of the given range is greater than the end.
   */
  inline fun sliceToArray(range: IntRange) = sliceToArray(range.first, range.last + 1)

  /**
   * Returns a new array containing the contents of this deque in order from
   * head to tail.
   *
   * The length of the returned array will be equal to [size].
   *
   * **Example**
   * ```
   * val deq = Deque(6)
   * deq += [4, 5, 6]
   * deq.pushFront(3)
   * deq.pushFront(2)
   * deq.pushFront(1)
   *
   * deq.toArray() // [1, 2, 3, 4, 5, 6]
   * ```
   *
   * @return A new array containing the data from this deque.
   */
  fun toArray(): UShortArray {
    // If the deque is empty, just return an empty array.
    if (size == 0)
      return UShortArray(0)

    // Get the real position of the last element in the deque.
    val realTail = internalIndex(lastIndex)

    // If the last element in the deque is also the last element in the backing
    // array, then we can just copy out the values in one shot.
    if (realHead <= realTail)
      return container.copyOfRange(realHead, realTail + 1)

    // Since the last element in the deque is before the first in the backing
    // array, we have to do 2 copies to get the data in line to return.

    // Create the output array
    val out = UShortArray(size)

    // Copy the first deque elements out of the back of the backing array
    container.copyInto(out, 0, realHead, container.size)

    // Copy the last deque elements out of the front of the backing array
    container.copyInto(out, container.size - realHead, 0, realTail + 1)

    // Return the new array
    return out
  }

  /**
   * Returns a new list containing the contents of this deque in order from head
   * to tail.
   *
   * The length of the returned list will be equal to [size].
   *
   * **Example**
   * ```
   * val deq = Deque(6)
   * deq += [4, 5, 6]
   * deq.pushFront(3)
   * deq.pushFront(2)
   * deq.pushFront(1)
   *
   * deq.toList() // List[1, 2, 3, 4, 5, 6]
   * ```
   *
   * @return A new list containing the data from this deque.
   */
  inline fun toList() = toArray().asList()

  /**
   * Combines the content of this deque with the given other deque to create a
   * new deque instance with the concatenated content of both original deques.
   *
   * Does not modify the state of either input deque.
   *
   * **Example**
   * ```
   * val deq1 = Deque.of(1, 2, 3) // Deque{1, 2, 3}
   * val deq2 = Deque.of(4, 5, 6) // Deque{4, 5, 6}
   * val deq3 = deq1 + deq2       // Deque{1, 2, 3, 4, 5, 6}
   * ```
   *
   * @param rhs Deque whose contents will be concatenated with the contents of
   * this deque to create a new deque instance.
   *
   * @return A new deque instance containing the concatenated contents of this
   * deque and the given input deque.
   */
  operator fun plus(rhs: AbstractUShortDeque<*>) =
    new(UShortArray(size + rhs.size).also { copyInto(it); rhs.copyInto(it, size) }, 0)

  // endregion Data Copying



  // region Data Access

  /**
   * The first element in this deque.
   *
   * @throws NoSuchElementException If this deque is empty.
   */
  val front
    get() =
      if (size == 0)
        throw NoSuchElementException()
      else
        container[realHead]

  /**
   * Inline alias of [front].
   */
  inline val first get() = front



  /**
   * Returns the first element, or `null` if the deque is empty.
   *
   * @return The first element, or `null` if the deque is empty.
   */
  fun frontOrNull() =
    if (size == 0)
      null
    else
      container[realHead]

  /**
   * Inline alias of [frontOrNull]
   */
  inline fun firstOrNull() = frontOrNull()



  /**
   * Returns the first element, or the given [value] if the deque is empty.
   *
   * @return The first element, or [value] if the deque is empty.
   */
  fun frontOr(value: UShort) =
    if (size == 0)
      value
    else
      container[realHead]

  /**
   * Inline alias of [frontOr]
   */
  inline fun firstOr(value: UShort) = frontOr(value)



  /**
   * The last element in this deque.
   *
   * @throws NoSuchElementException If this deque is empty.
   */
  val back
    get() =
      if (size == 0)
        throw NoSuchElementException()
      else
        container[internalIndex(lastIndex)]

  /**
   * Inline alias of [back]
   */
  inline val last get() = back



  /**
   * Returns the last element, or `null` if the deque is empty.
   *
   * @return The first element, or `null` if the deque is empty.
   */
  fun backOrNull() =
    if (size == 0)
      null
    else
      container[internalIndex(lastIndex)]

  /**
   * Inline alias of [backOrNull]
   */
  inline fun lastOrNull() = backOrNull()



  /**
   * Returns the last element, or the given [value] if the deque is empty.
   *
   * @return The first element, or [value] if the deque is empty.
   */
  fun backOr(value: UShort) =
    if (size == 0)
      value
    else
      container[internalIndex(lastIndex)]

  /**
   * Inline alias of [backOr]
   */
  inline fun lastOr(value: UShort) = backOr(value)



  /**
   * Gets the value at the given index from this deque.
   *
   * **Example**
   * ```
   * val deq = Deque.of(1, 2, 3)
   *
   * assert(deq[0] == 1)
   * ```
   *
   * @param index Index of the value that should be retrieved.
   *
   * @throws IndexOutOfBoundsException If the given index is less than zero or
   * is greater than [lastIndex].
   */
  operator fun get(index: Int) = container[vei(index)]



  /**
   * Tests whether this deque contains the given value.
   *
   * **Example**
   * ```
   * val deq = Deque.of(1, 2, 3, 4)
   *
   * assert(1 in deq)
   * assert(5 !in deq)
   * ```
   *
   * @return `true` if at least one value matching the given input was found in
   * this deque.  `false` if no values matching the given input were found in
   * this deque.
   */
  operator fun contains(value: UShort): Boolean {
    for (v in container)
      if (v == value)
        return true

    return false
  }



  /**
   * Returns a live iterator over the contents of this deque.
   *
   * This iterator is live in that changes to the underlying deque will be
   * reflected in the iterator's method calls.
   *
   * @return Iterator over the contents of this deque.
   */
  fun iterator() = object : ListIterator<UShort> {
    var position = -1

    override fun hasNext() = position < lastIndex
    override fun hasPrevious() = position > 0
    override fun next() = get(++position)
    override fun nextIndex() = position + 1
    override fun previous() = get(--position)
    override fun previousIndex() = position - 1
  }

  // endregion Data Access



  // region Data Insertion

  /**
   * Pushes the given value onto the front of this deque.
   *
   * If the capacity of this deque was equal to its size at the time of this
   * method call, the internal container will be resized to accommodate the new
   * value.
   *
   * @param value Value that will be pushed onto the front of this deque.
   */
  fun pushFront(value: UShort) {
    ensureCapacity(size + 1)
    realHead = decremented(realHead)
    container[realHead] = value
    size++
  }

  /**
   * Inline alias of [pushFront]
   */
  inline fun pushFirst(value: UShort) = pushFront(value)



  /**
   * Pushes the given value onto the back of this deque.
   *
   * If the capacity of this deque was equal to its size at the time of this
   * method call, the internal container will be resized to accommodate the new
   * value.
   *
   * @param value Value that will be pushed onto the back of this deque.
   */
  fun pushBack(value: UShort) {
    ensureCapacity(size + 1)
    container[internalIndex(size)] = value
    size++
  }

  /**
   * Pushes the given array of values onto the back of this deque.
   *
   * The capacity of this deque will be increased if necessary to accommodate
   * the new values.
   *
   * **Example**
   * ```
   * val deq = Deque.of(1, 2, 3)  // Deque[1, 2, 3]
   * deq.pushBack([4, 5, 6])      // Deque[1, 2, 3, 4, 5, 6]
   * ```
   *
   * @param values Array of values to push onto the back of this deque.
   */
  fun pushBack(values: UShortArray) {
    // If the input values array size is:
    when (values.size) {
      // zero, then there is nothing to do
      0 -> return
      // one, then do a single copy and bail
      1 -> return pushBack(values[0])
    }

    // If the current deque size is zero:
    if (size == 0) {
      // Set the backing container to a copy of the input array
      container = values.copyOf()

      // Make sure our head position is zero (it should have been anyway but
      // better safe than sorry)
      realHead = 0

      // Set our deque's size to the size of the input array.
      size = values.size

      // tuck'n'roll
      return
    }

    // Make sure we have enough room to hold all the new values
    ensureCapacity(size + values.size)

    // Calculate the current insert offset position
    val offset = internalIndex(size)

    // Figure out what the new last index will be
    val newTail = internalIndex(size + (values.size - 1))

    // If the new last position is after the current offset position, then this
    // is a single copy operation:
    //
    // (| = head, ^ = offset, * = new tail)
    // [1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0]
    //  |        ^              *
    if (newTail > offset) {
      // Copy the data into the internal array
      values.copyInto(container, offset)

      // Increase the size of our deque to reflect the new values added
      size += values.size

      // nothin' else to do
      return
    }

    // So the new last position is before the current offset position in the
    // backing array:
    //
    // (| = head, ^ = offset, * = new tail)
    // [0, 0, 0, 0, 0, 1, 2, 3, 0, 0]
    //           *     |        ^
    //
    // This means we're gonna need to do 2 copies, one to fill out the end of
    // the backing array, and another to put the rest at the start of the
    // backing array.

    // Copy the first bit of data to the end of our backing array.
    values.copyInto(container, offset, 0, container.size - offset)

    // Copy the remainder to the start of our backing array.
    values.copyInto(container, 0, container.size - offset)

    // Update our deque size to reflect the new values
    size += values.size

    // aaaaand, we're done!
  }

  /**
   * Pushes the collection of values onto the back of this deque.
   *
   * The capacity of this deque will be increased if necessary to accommodate
   * the new values.
   *
   * **Example**
   * ```
   * val deq = Deque.of(1, 2, 3)  // Deque[1, 2, 3]
   * deq.pushBack(List[4, 5, 6])  // Deque[1, 2, 3, 4, 5, 6]
   * ```
   *
   * @param values Collection of values to push onto the back of this deque.
   */
  fun pushBack(values: Collection<UShort>) {
    // If the given collection is empty
    if (values.isEmpty())
    // then there's nothing to do
      return

    // Make sure we have room for all the new goodies
    ensureCapacity(size + values.size)

    // Ugly add by iteration
    for (v in values)
      pushBack(v)
  }

  /**
   * Pushes the contents of the given deque onto the back of this deque.
   *
   * The capacity of this deque will be increased if necessary to accommodate
   * the new values.
   *
   * @param values Deque of values to push onto the back of this deque.
   */
  fun pushBack(values: AbstractUShortDeque<*>) {
    // If the input deque is empty
    if (values.isEmpty())
    // what do they expect us to do?
      return

    // If the backing array is empty, or our deque is empty:
    if (container.isEmpty() || size == 0) {
      // Copy over the other deque's backing array
      container = values.container.copyOf()

      // Set our head position to the correct value
      realHead = values.realHead

      // Update our container's size
      size = values.size

      // done!
      return
    }

    // If the other deque has only a single value
    if (values.size == 1) {
      // Do a silly push
      pushBack(values.front)

      // and leave
      return
    }

    // Make sure we have the room necessary to insert their values.
    ensureCapacity(size + values.size)

    // Figure out the current offset for new values
    val offset = internalIndex(size)

    // Calc what the new last index will be
    val newTail = internalIndex(size + values.lastIndex)

    // If their deque is currently inline
    if (values.isInline) {

      // AND we can do an inline insert (wooo, lucky us!)
      if (newTail > offset) {

        // Do our single copy
        values.container.copyInto(container, offset, values.realHead, values.realHead + values.size)

        // Update our size value
        size += values.size

        // flop right out of this function
        return
      }

      // Ugh, so we can't do a single insert, luckily though, their data _is_
      // inline, so we can get away with just 2 copies

      // Figure out how many values we can write to the end of our backing array
      val chunkSize = container.size - offset

      // Copy over [chunkSize] values from their inner array to the back of our
      // inner array, starting at their [realHead] and ending at their
      // [realHead] + [chunkSize].
      values.container.copyInto(container, offset, values.realHead, realHead + chunkSize)

      // Copy over the remaining values from their inner array to the front of
      // our inner array starting at an offset of their [realHead] + [chunkSize]
      // and ending at their total [size] - [chunkSize].
      values.container.copyInto(container, 0, values.realHead + chunkSize, values.size - chunkSize)

      // Update our size value
      size += values.size

      // Get out before it's too late
      return
    }

    // So their backing array isn't inline... it's not necessarily the worst
    // case scenario, so lets be optimistic and keep going.

    // If we can do an inline insert of the new values
    if (newTail > offset) {

      // Figure out how many values they have tucked away at the end of their
      // backing array for us to copy
      val chunkSize = values.container.size - values.realHead

      // Copy over the head of their data from the back of their backing array
      // to our array starting at [offset].
      values.container.copyInto(container, offset, values.realHead, chunkSize)

      // Copy over the remaining data from the front of their backing array to
      // our array starting at [offset] + [chunkSize].
      values.container.copyInto(container, offset + chunkSize, 0, values.size - chunkSize)

      // Update our size value
      size += values.size

      // Seriously, we need to get out of this function now
      return
    }

    // FUCK.

    // Okay, so it is the worst case scenario, both us and them are out of line.
    // This could be hella ugly to copy over and has a lot of different cases
    // relating to the size of the input deque's front/back chunks vs our
    // front/back chunks... so we're going to take the easy way out instead.
    pushBack(values.toArray())
  }



  /**
   * Inline alias of [pushBack]
   */
  inline fun pushLast(value: UShort) = pushBack(value)

  /**
   * Inline alias of [pushBack]
   */
  inline fun pushLast(values: UShortArray) = pushBack(values)

  /**
   * Inline alias of [pushBack]
   */
  inline fun pushLast(values: Collection<UShort>) = pushBack(values)

  /**
   * Inline alias of [pushBack]
   */
  inline fun pushLast(values: AbstractUShortDeque<*>) = pushBack(values)



  /**
   * Inline alias of [pushBack]
   */
  inline operator fun plusAssign(value: UShort) = pushBack(value)

  /**
   * Inline alias of [pushBack]
   */
  inline operator fun plusAssign(values: UShortArray) = pushBack(values)

  /**
   * Inline alias of [pushBack]
   */
  inline operator fun plusAssign(values: Collection<UShort>) = pushBack(values)

  /**
   * Inline alias of [pushBack]
   */
  inline operator fun plusAssign(values: AbstractUShortDeque<*>) = pushBack(values)



  /**
   * Sets the value at the given index in this deque.
   *
   * **Example**
   * ```
   * val deq = Deque.of(1, 2, 3) // Deque{1, 2, 3}
   *
   * deq[0] = 4                  // Deque{4, 2, 3}
   * deq[1] = 5                  // Deque{4, 5, 3}
   * deq[2] = 6                  // Deque{4, 5, 6}
   * ```
   *
   * @param index Index at which the value should be set/overwritten.
   *
   * @param value Value to set.
   *
   * @throws IndexOutOfBoundsException If the given index is less than zero or
   * is greater than [lastIndex].
   */
  operator fun set(index: Int, value: UShort) = container.set(vei(index), value)



  /**
   * Inserts the given value into this deque at the specified index.
   *
   * **Example**:
   * ```
   * val deq = Deque.of(1, 2, 3, 4)  // Deque{1, 2, 3, 4}
   * deq.insert(2, 9)                // Deque{1, 2, 9, 3, 4}
   * ```
   *
   * @param index Index at which the value should be inserted.
   *
   * Must be a value between zero and [size] (inclusive).
   *
   * @param value Value to insert.
   *
   * @throws IndexOutOfBoundsException If the given index is less than zero or
   * is greater than [size].
   */
  fun insert(index: Int, value: UShort) {
    // If the index is 0 then they are just doing a front push, no need to do
    // anything fancy here.
    if (index == 0) {
      return pushFront(value)
    }

    // If the index is equal to the size, then they are just doing a back push,
    // again, no reason to do anything fancy.
    if (index == size) {
      return pushBack(value)
    }

    // So they _are_ trying to insert something into the "middle" of the deque.

    // Make sure we have room for the new element.
    ensureCapacity(size + 1)

    // At this point we know nothing about the internal state of the backing
    // array.  We could be inline, out of line, stacked at the beginning of the
    // array, sitting in the middle, stacked at the end, etc...
    //
    // [1, 2, 3, _]
    // [_, 1, 2, 3]
    // [3, _, 1, 2]
    // [2, 3, _, 1]

    // Is the given insertion index closer to the head or tail of the deque
    if (index < (size + 1) shr 1) { // shr == quick rough half
      // The insertion index is closer to the head of the deque, so we will be
      // shifting the front portion "back" a position to make room for the new
      // value.

      // Figure out the new head position by rolling back by one.  Since the
      // current head position may be at 0, this may put the head at the tail
      // end of the backing array.
      val newHead = decremented(realHead)

      // Figure out the insertion index for the new value.  We are moving data
      // backwards to make room for it so decrement it by one position.  (Again,
      // this may roll it around to the back of the backing array from position
      // `0`).
      val insert = decremented(vei(index))

      // If the insertion position is greater than or equal to the current head
      // position, then all but possibly the first element have stayed on the
      // same side of the backing array, meaning we can do a simple array copy
      // to move the data backwards (except for the head which _may_ have rolled
      // around to the tail of the backing array)
      //
      // Examples:
      // (| = old head, ^ = insert position)
      //
      //  |
      // [1, 2, 3, 4, 5, _]  // Before
      // [2, 3, _, 4, 5, 1]  // After
      //        ^
      //     |
      // [_, 1, 2, 3, 4, 5]  // Before
      // [1, 2, 3, _, 4, 5]  // After
      //           ^
      //        |
      // [5, _, 1, 2, 3, 4]  // Before
      // [5, 1, 2, 3, _, 4]  // After
      //              ^
      //           |
      // [4, 5, _, 1, 2, 3]  // Before
      // [4, 5, 1, 2, 3, _]  // After
      //                 ^
      if (insert >= realHead) {

        // Since the head may have rolled around to the tail of the backing
        // array, we will do that copy separately.
        container[newHead] = container[realHead]

        // Now copy everything else that is moving backwards one position.
        // 1. target = container (we're copying to the same target)
        // 2. offset = realHead (we're copying everything after the current head
        //             backwards 1 spot so the old head will now contain the
        //             value that was at head + 1)
        // 3. start  = realHead + 1 (the first value we will copy back 1, so it
        //             will now be in the old head position)
        // 4. end    = insert position + 1 (exclusive index of the last value
        //             to copy, so everything from `start` to 1 before this
        //             index will be shifted backwards by 1.
        container.copyInto(container, realHead, realHead + 1, insert + 1)

      }

      // If the insertion position is less than the current head position, then
      // the backing array is or will go all wonky and we need to do multiple
      // array copies to shift everything around.
      //
      // Example
      //
      // This example deque will be used throughout the comments in the code in
      // this block to detail the steps to get from the "Before" state to the
      // "After" state.
      //
      // (| = old head, ^ = insert position)
      //
      //                 |
      // [2, 3, 4, 5, _, 1]  // Before
      // [3, _, 4, 5, 1, 2]  // After
      //     ^
      else {
        // Since the head is somewhere near the tail of the backing array, that
        // means our current empty space is in the "middle" of the backing
        // array, before our first value, but after our last value.
        //
        // So we can safely shift everything from the current head position
        // until the end of the backing array backwards by one position, leaving
        // the last slot in the backing array empty to copy into from the head
        // of the backing array.
        //
        // [2, 3, 4, 5, _, 1]  // Before
        // [2, 3, 4, 5, 1, _]  // After
        container.copyInto(container, realHead - 1, realHead, container.size)

        // Copy the value from the head of the backing array "back" one slot to
        // the tail of the backing array.
        //
        // [2, 3, 4, 5, 1, _]  // Before
        // [_, 3, 4, 5, 1, 2]  // After
        container[container.size - 1] = container[0]

        // Now that we're situated, move the necessary data from the front of
        // our backing array backwards one slot to fill the now empty first
        // backing array position.
        //
        // When we are done, the new "empty" slot will be the position into
        // which we will be inserting our new value.
        //
        // [_, 3, 4, 5, 1, 2]  // Before
        // [3, _, 4, 5, 1, 2]  // After
        container.copyInto(container, 0, 1, insert + 1)
      }

      // At this point, some way or another, we have moved the data around in
      // our internal array to make room for the data we want to insert.

      // Insert our new value.
      container[insert] = value

      // Update our internal head position
      realHead = newHead

    } else {
      // The insertion position is closer to the tail of the deque, so we will
      // be moving the data at the back of the deque forward a position to make
      // room for the new value being inserted.

      // Position of the value we will be inserting.
      val insert = vei(index)

      // New position of the last value in the deque.
      val newTail = internalIndex(size)

      // If the insertion index is before the new tail position, then we can
      // safely move the current values in the backing array forward one slot
      // in one array copy.
      //
      // Examples
      // (| = new tail, ^ = insert position)
      //
      //                 |
      // [1, 2, 3, 4, 5, _]  // Before
      // [1, 2, 3, _, 4, 5]  // After
      //           ^
      //              |
      // [2, 3, 4, 5, _, 1]  // Before
      // [2, 3, _, 4, 5, 1]  // After
      //        ^
      //           |
      // [3, 4, 5, _, 1, 2]  // Before
      // [3, _, 4, 5, 1, 2]  // After
      //     ^
      //        |
      // [4, 5, _, 1, 2, 3]  // Before
      // [_, 4, 5, 1, 2, 3]  // After
      //  ^
      if (insert < newTail) {

        // Move everything forward in one shot.
        // 1. target = container (we're copying back into the same array)
        // 2. offset = insert + 1 (our new position is 1 after our current
        //             position, so "+ 1")
        // 3. start  = insert (start copying from the current location where we
        //             want to insert a value, since we have to move that slot
        //             forward)
        // 4. end    = newTail (exclusive end location, so we want to copy
        //             everything from `insert` until 1 before the new tail
        //             position forward 1 slot)
        container.copyInto(container, insert + 1, insert, newTail)
      }

      // If the insertion index is after the new tail position, then the backing
      // array is or will go all wonky and we have to do multiple array copies
      // to get everything sorted out.
      //
      // Example
      //
      // The following example deque will be used in the comments in the else
      // block below to help visualize the steps that are being taken on the
      // backing array as they happen.
      //
      // (| = new tail, ^ = insert position)
      //
      //     |
      // [6, _, 1, 2, 3, 4, 5]  // Before
      // [5, 6, 1, 2, 3, _, 4]  // After
      //                 ^
      else {

        // Move the data at the head of our backing array forward by one slot.
        //
        // [6, _, 1, 2, 3, 4, 5]  // Before
        // [_, 6, 1, 2, 3, 4, 5]  // After
        container.copyInto(container, 1, 0, newTail)

        // Move the value from the end of the backing array to the front of the
        // backing array.
        //
        // [_, 6, 1, 2, 3, 4, 5]  // Before
        // [5, 6, 1, 2, 3, 4, _]  // After
        container[0] = container[container.size - 1]

        // Move the remaining elements that are after our insert position at the
        // tail of the backing array forwards 1.
        //
        // [5, 6, 1, 2, 3, 4, _]  // Before
        // [5, 6, 1, 2, 3, _, 4]  // After
        container.copyInto(container, insert + 1, insert, container.size - 1)
      }

      // Finally, insert our value into the now available slot
      container[insert] = value
    }

    size++
  }

  // endregion Data Insertion



  // region Data Removal

  /**
   * Removes the first element from this deque and returns it.
   *
   * @return The former first element in this deque.
   *
   * @throws NoSuchElementException If this deque is empty.
   */
  fun popFront(): UShort {
    if (size == 0)
      throw NoSuchElementException()

    val c = container[realHead]

    realHead = incremented(realHead)
    size--

    return c
  }

  /**
   * Inline alias of [popFront]
   */
  inline fun popFirst() = popFront()

  /**
   * Removes the first element from this deque and returns it, if this deque has
   * a first element, else returns `null`.
   *
   * @return Either the former first element of this deque, or `null` if this
   * deque was empty.
   */
  fun popFrontOrNull(): UShort? {
    if (size == 0)
      return null

    val c = container[realHead]

    realHead = incremented(realHead)
    size--

    return c
  }

  /**
   * Inline alias of [popFrontOrNull]
   */
  inline fun popFirstOrNull() = if (size == 0) null else popFront()

  /**
   * Removes the first element from this deque and returns in, if this deque has
   * a first element, else returns the given default value.
   *
   * @return Either the former first element of the deque, or [value] if this
   * deque was empty.
   */
  fun popFrontOr(value: UShort): UShort {
    if (size == 0)
      return value

    val c = container[realHead]

    realHead = incremented(realHead)
    size--

    return c
  }

  /**
   * Inline alias of [popFrontOr]
   */
  inline fun popFirstOr(value: UShort) = popFrontOr(value)

  /**
   * Removes the first [n] elements of this deque.
   *
   * If this deque is empty, or if [n] is less than `1`, this method does
   * nothing.
   *
   * If [n] is greater than or equal to [size], this method clears the deque
   * entirely (same as calling [clear]).
   *
   * @param n Number of elements to remove from this deque.
   *
   * Defaults to `1`.
   */
  fun removeFront(n: Int = 1) {
    when {
      n < 1     -> {}
      size == 0 -> {}
      n >= size -> clear()
      else      -> {
        realHead = internalIndex(n)
        size -= n
      }
    }
  }

  /**
   * Inline alias of [removeFront]
   */
  inline fun removeFirst(n: Int = 1) = removeFront(n)

  /**
   * Removes up to [n] elements from the back of this deque.
   *
   * If this deque is empty or if [n] is less than `1`, this method does
   * nothing.
   *
   * If [n] is greater than or equal to [size], this method clears the deque
   * entirely (same as calling [clear]).
   *
   * @param n Number of elements to remove from the back of this deque.
   *
   * Defaults to `1`.
   */
  fun removeBack(n: Int = 1) {
    when {
      n < 1     -> {}
      size == 0 -> {}
      n >= size -> clear()
      else      -> size -= n
    }
  }

  /**
   * Inline alias of [removeBack]
   */
  inline fun removeLast(n: Int = 1) = removeBack(n)

  /**
   * Removes the last element from this deque and returns it.
   *
   * @return The former last element in this deque.
   *
   * @throws NoSuchElementException If this deque is empty.
   */
  fun popBack(): UShort {
    if (size == 0)
      throw NoSuchElementException()

    val c = container[internalIndex(lastIndex)]
    size--
    return c
  }

  /**
   * Inline alias of [popBack]
   */
  inline fun popLast() = popBack()

  /**
   * Removes the last element from this deque and returns it, if this deque has
   * a last element, else returns `null`.
   *
   * @return Either the former last element of this deque, or `null` if this
   * deque was empty.
   */
  fun popBackOrNull(): UShort? {
    if (size == 0)
      return null

    val c = container[internalIndex(lastIndex)]
    size--
    return c
  }

  /**
   * Inline alias of [popBackOrNull]
   */
  inline fun popLastOrNull() = popBackOrNull()

  // endregion Data Removal



  // region Class Junk

  override fun toString() = "UShortDeque($size/${container.size})"

  override fun equals(other: Any?) =
    if (other is AbstractUShortDeque<*>)
      container.contentEquals(other.container)
    else
      false

  override fun hashCode() = container.contentHashCode()

  // endregion Class Junk



  // region Internals

  /**
   * Valid External Index
   *
   * Ensures the given index is valid for accessing values in this deque.
   *
   * @param i Index to validate.
   *
   * @return Valid index
   *
   * @throws IndexOutOfBoundsException If the given index is invalid.
   */
  protected inline fun vei(i: Int) = if (i < 0 || i > size - 1) throw IndexOutOfBoundsException() else internalIndex(i)

  protected inline fun positiveMod(i: Int) = if (i >= container.size) i - container.size else i

  protected inline fun negativeMod(i: Int) = if (i < 0) i + container.size else i

  protected inline fun internalIndex(i: Int) = positiveMod(realHead + i)

  protected inline fun incremented(i: Int) = if (i == container.size - 1) 0 else i + 1

  protected inline fun decremented(i: Int) = if (i == 0) container.size - 1 else i - 1

  /**
   * Copies the data currently in the backing buffer into a new buffer of size
   * [newCap].
   *
   * The new buffer will be inlined, meaning any 'head' data presently at the
   * tail end of the buffer will be relocated to the beginning of the buffer and
   * any 'tail' data will be put inline after the head data.
   *
   * Example
   * ```
   * newCap   = 8
   * previous = [4, 5, 6, 1, 2, 3]
   * new      = [1, 2, 3, 4, 5, 6, 0, 0]
   * ```
   *
   * This method does not check to see if the resize is necessary ahead of time
   * as it is only called when the necessity of a resize has already been
   * confirmed.
   */
  protected fun copyElements(newCap: Int) {
    val new = UShortArray(newCap)
    container.copyInto(new, 0, realHead, container.size)
    container.copyInto(new, container.size - realHead, 0, realHead)
    realHead = 0
    container = new
  }

  /**
   * Calculate a new capacity for the deque based on the given inputs.
   *
   * @param old Current deque capacity.
   *
   * @param min Minimum required capacity.
   *
   * @return The new capacity the deque should be reallocated to.
   */
  protected fun newCap(old: Int, min: Int): Int {
    val new = old + (old shr 1)

    return when {
      new < min -> min
      else      -> new
    }
  }

  // endregion Internals
}