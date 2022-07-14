package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractUByteDeque
import java.io.InputStream

/**
 * UByte Deque
 *
 * A deque type that deals in unboxed [UByte] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
class UByteDeque : PrimitiveDeque, AbstractUByteDeque<UByteDeque> {

  /**
   * Constructs a new, empty `ByteDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `ByteDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `ByteDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: UByteArray) : super(values)

  private constructor(values: UByteArray, head: Int) : super(values, head)

  override fun new(values: UByteArray, head: Int) = UByteDeque(values, head)

  // region Pop Signed

  /**
   * Takes the first byte from the backing [UByteDeque] and translates it into a
   * [Byte] value.
   *
   * If this deque is empty, this method throws a [NoSuchElementException].
   *
   * @return The parsed `Byte` value.
   *
   * @throws NoSuchElementException If this deque is empty when this method is
   * called.
   */
  fun popByte(): Byte = popFront().toByte()

  /**
   * Pops the first 2 bytes from this [UByteDeque] and translates them into a
   * [Short] value.
   *
   * If this deque contains fewer than 2 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * @return The parsed `Short` value.
   *
   * Defaults to `false` (big endian).
   *
   * @throws NoSuchElementException If this deque contains fewer than 2 bytes
   * when this method is called.
   */
  fun popShort(littleEndian: Boolean = false): Short {
    if (size < 2)
      throw NoSuchElementException()

    val head      = realHead
    val lastIndex = internalIndex(1)

    // Our data is inline
    if (head < lastIndex) {
      realHead = head + 2
      size -= 2
      return if (littleEndian)
        ((container[head].toInt()   and 0xFF)         or
        ((container[head+1].toInt() and 0xFF) shl 8)).toShort()
      else
        (((container[head].toInt() and 0xFF) shl 8)  or
        (container[head+1].toInt()  and 0xFF)).toShort()
    }

    size -= 2

    // We are out of line, so just use the internal indices to get the values
    // rather than doing a bunch of complex logic to make it happen.
    val out = if (littleEndian)
      ((container[internalIndex(0)].toInt()  and 0xFF) or
      ((container[internalIndex(1)].toInt() and 0xFF) shl 8)).toShort()
    else
      (((container[internalIndex(0)].toInt() and 0xFF) shl 8) or
      (container[internalIndex(1)].toInt()  and 0xFF)).toShort()

    realHead = internalIndex(2)

    return out
  }

  /**
   * Pops the first 4 bytes from this [UByteDeque] and translates them into an
   * [Int] value.
   *
   * If this deque contains fewer than 4 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * Defaults to `false` (big endian).
   *
   * @return The parsed `Int` value.
   *
   * @throws NoSuchElementException If this deque contains fewer than 4 bytes
   * when this method is called.
   */
  fun popInt(littleEndian: Boolean = false): Int {
    if (size < 4)
      throw NoSuchElementException()

    val head      = realHead
    val lastIndex = internalIndex(3)

    // Our data is inline
    if (head < lastIndex) {
      realHead = head + 4
      size -= 4
      return if (littleEndian)
        (container[head].toInt()    and 0xFF)         or
        ((container[head+1].toInt() and 0xFF) shl 8)  or
        ((container[head+2].toInt() and 0xFF) shl 16) or
        ((container[head+3].toInt() and 0xFF) shl 24)
      else
        ((container[head].toInt() and 0xFF)   shl 24) or
        ((container[head+1].toInt() and 0xFF) shl 16) or
        ((container[head+2].toInt() and 0xFF) shl 8)  or
        (container[head+3].toInt()  and 0xFF)
    }

    size -= 4

    // We are out of line, so just use the internal indices to get the values
    // rather than doing a bunch of complex logic to make it happen.
    val out = if (littleEndian)
      (container[internalIndex(0)].toInt()  and 0xFF)         or
      ((container[internalIndex(1)].toInt() and 0xFF) shl 8)  or
      ((container[internalIndex(2)].toInt() and 0xFF) shl 16) or
      ((container[internalIndex(3)].toInt() and 0xFF) shl 24)
    else
      ((container[internalIndex(0)].toInt() and 0xFF) shl 24) or
      ((container[internalIndex(1)].toInt() and 0xFF) shl 16) or
      ((container[internalIndex(2)].toInt() and 0xFF) shl 8)  or
      (container[internalIndex(3)].toInt()  and 0xFF)

    realHead = internalIndex(4)

    return out
  }

  /**
   * Pops the first 8 bytes from this [UByteDeque] and translates them into a
   * [Long] value.
   *
   * If this deque contains fewer than 8 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * Defaults to `false` (big endian).
   *
   * @return The parsed `Long` value.
   *
   * @throws NoSuchElementException If this deque contains fewer than 8 bytes
   * when this method is called.
   */
  fun popLong(littleEndian: Boolean = false): Long {
    if (size < 8)
      throw NoSuchElementException()

    val head      = realHead
    val lastIndex = internalIndex(7)

    // Our data is inline
    if (head < lastIndex) {
      realHead = head + 8
      size -= 8
      return if (littleEndian)
        (container[head].toLong()  and 0xFFL)         or
        ((container[head+1].toLong() and 0xFFL) shl 8)  or
        ((container[head+2].toLong() and 0xFFL) shl 16) or
        ((container[head+3].toLong() and 0xFFL) shl 24) or
        ((container[head+4].toLong() and 0xFFL) shl 32) or
        ((container[head+5].toLong() and 0xFFL) shl 40) or
        ((container[head+6].toLong() and 0xFFL) shl 48) or
        ((container[head+7].toLong() and 0xFFL) shl 56)
      else
        ((container[head].toLong() and 0xFFL) shl 56) or
        ((container[head+1].toLong() and 0xFFL) shl 48) or
        ((container[head+2].toLong() and 0xFFL) shl 40) or
        ((container[head+3].toLong() and 0xFFL) shl 32) or
        ((container[head+4].toLong() and 0xFFL) shl 24) or
        ((container[head+5].toLong() and 0xFFL) shl 16) or
        ((container[head+6].toLong() and 0xFFL) shl 8)  or
        (container[head+7].toLong()  and 0xFFL)
    }

    size -= 8

    // We are out of line, so just use the internal indices to get the values
    // rather than doing a bunch of complex logic to make it happen.
    val out = if (littleEndian)
      (container[internalIndex(0)].toLong()  and 0xFFL)         or
      ((container[internalIndex(1)].toLong() and 0xFFL) shl 8)  or
      ((container[internalIndex(2)].toLong() and 0xFFL) shl 16) or
      ((container[internalIndex(3)].toLong() and 0xFFL) shl 24) or
      ((container[internalIndex(4)].toLong() and 0xFFL) shl 32) or
      ((container[internalIndex(5)].toLong() and 0xFFL) shl 40) or
      ((container[internalIndex(6)].toLong() and 0xFFL) shl 48) or
      ((container[internalIndex(7)].toLong() and 0xFFL) shl 56)
    else
      ((container[internalIndex(0)].toLong() and 0xFFL) shl 56) or
      ((container[internalIndex(1)].toLong() and 0xFFL) shl 48) or
      ((container[internalIndex(2)].toLong() and 0xFFL) shl 40) or
      ((container[internalIndex(3)].toLong() and 0xFFL) shl 32) or
      ((container[internalIndex(4)].toLong() and 0xFFL) shl 24) or
      ((container[internalIndex(5)].toLong() and 0xFFL) shl 16) or
      ((container[internalIndex(6)].toLong() and 0xFFL) shl 8)  or
      (container[internalIndex(7)].toLong()  and 0xFFL)

    realHead = internalIndex(8)

    return out
  }

  // endregion Pop Signed

  // region Pop Decimal

  /**
   * Pops the first 4 bytes from this [UByteDeque] and translates them into a
   * [Float] value.
   *
   * If this deque contains fewer than 4 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * Defaults to `false` (big endian).
   *
   * @return The `Float` value parsed from the first 4 bytes popped from this
   * deque.
   *
   * @throws NoSuchElementException If this deque contains fewer than 4 bytes
   * when this method is called.
   */
  fun popFloat(littleEndian: Boolean = false): Float =
    Float.fromBits(popInt(littleEndian))

  /**
   * Pops the first 8 bytes from this [UByteDeque] and translates them into a
   * [Double] value.
   *
   * If this deque contains fewer than 8 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * Defaults to `false` (big endian).
   *
   * @return The `Double` value parsed from the first 8 bytes popped from this
   * deque.
   *
   * @throws NoSuchElementException If this deque contains fewer than 8 bytes
   * when this method is called.
   */
  fun popDouble(littleEndian: Boolean = false): Double =
    Double.fromBits(popLong(littleEndian))


  // endregion Pop Decimal

  // region Pop Unsigned

  /**
   * Pops the first 2 bytes from this [UByteDeque] and translates them into a
   * [UShort] value.
   *
   * If this deque contains fewer than 2 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * Defaults to `false` (big endian).
   *
   * @return The parsed `UShort` value.
   *
   * @throws NoSuchElementException If this deque contains fewer than 2 bytes
   * when this method is called.
   */
  fun popUShort(littleEndian: Boolean = false): UShort {
    if (size < 2)
      throw NoSuchElementException()

    val head      = realHead
    val lastIndex = internalIndex(1)

    // Our data is inline
    if (head < lastIndex) {
      realHead = head + 2
      size -= 2
      return if (littleEndian)
        ((container[head].toInt()   and 0xFF) or
        ((container[head+1].toInt() and 0xFF) shl 8)).toUShort()
      else
        (((container[head].toInt() and 0xFF) shl 8)  or
        (container[head+1].toInt() and 0xFF)).toUShort()
    }

    size -= 2

    // We are out of line, so just use the internal indices to get the values
    // rather than doing a bunch of complex logic to make it happen.
    val out = if (littleEndian)
      ((container[internalIndex(0)].toInt() and 0xFF) or
      ((container[internalIndex(1)].toInt() and 0xFF) shl 8)).toUShort()
    else
      (((container[internalIndex(0)].toInt() and 0xFF) shl 8) or
      (container[internalIndex(1)].toInt()   and 0xFF)).toUShort()

    realHead = internalIndex(2)

    return out
  }

  /**
   * Pops the first 4 bytes from this [UByteDeque] and translates them into a
   * [UInt] value.
   *
   * If this deque contains fewer than 4 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * Defaults to `false` (big endian).
   *
   * @return The parsed `UInt` value.
   *
   * @throws NoSuchElementException If this deque contains fewer than 4 bytes
   * when this method is called.
   */
  fun popUInt(littleEndian: Boolean = false): UInt {
    if (size < 4)
      throw NoSuchElementException()

    val head      = realHead
    val lastIndex = internalIndex(3)

    // Our data is inline
    if (head < lastIndex) {
      realHead = head + 4
      size -= 4
      return if (littleEndian)
        (container[head].toUInt()  and 0xFFu)           or
        ((container[head+1].toUInt() and 0xFFu) shl 8)  or
        ((container[head+2].toUInt() and 0xFFu) shl 16) or
        ((container[head+3].toUInt() and 0xFFu) shl 24)
      else
        ((container[head].toUInt() and 0xFFu) shl 24)   or
        ((container[head+1].toUInt() and 0xFFu) shl 16) or
        ((container[head+2].toUInt() and 0xFFu) shl 8)  or
        (container[head+3].toUInt()  and 0xFFu)
    }

    size -= 4

    // We are out of line, so just use the internal indices to get the values
    // rather than doing a bunch of complex logic to make it happen.
    val out = if (littleEndian)
      (container[internalIndex(0)].toUInt()  and 0xFFu)         or
      ((container[internalIndex(1)].toUInt() and 0xFFu) shl 8)  or
      ((container[internalIndex(2)].toUInt() and 0xFFu) shl 16) or
      ((container[internalIndex(3)].toUInt() and 0xFFu) shl 24)
    else
      ((container[internalIndex(0)].toUInt() and 0xFFu) shl 24) or
      ((container[internalIndex(1)].toUInt() and 0xFFu) shl 16) or
      ((container[internalIndex(2)].toUInt() and 0xFFu) shl 8)  or
      (container[internalIndex(3)].toUInt()  and 0xFFu)

    realHead = internalIndex(4)

    return out
  }

  /**
   * Pops the first 8 bytes from this [UByteDeque] and translates them into a
   * [ULong] value.
   *
   * If this deque contains fewer than 8 bytes, this method throws a
   * [NoSuchElementException].
   *
   * @param littleEndian Boolean flag indicating whether the bytes in the deque
   * should be translated to an int with a little endian byte order.
   *
   * Defaults to `false` (big endian).
   *
   * @throws NoSuchElementException If this deque contains fewer than 8 bytes
   * when this method is called.
   */
  fun popULong(littleEndian: Boolean = false): ULong {
    if (size < 8)
      throw NoSuchElementException()

    val head      = realHead
    val lastIndex = internalIndex(7)

    // Our data is inline
    if (head < lastIndex) {
      realHead = head + 8
      size -= 8
      return if (littleEndian)
        (container[head].toULong()  and 0xFFu)         or
        ((container[head+1].toULong() and 0xFFu) shl 8)  or
        ((container[head+2].toULong() and 0xFFu) shl 16) or
        ((container[head+3].toULong() and 0xFFu) shl 24) or
        ((container[head+4].toULong() and 0xFFu) shl 32) or
        ((container[head+5].toULong() and 0xFFu) shl 40) or
        ((container[head+6].toULong() and 0xFFu) shl 48) or
        ((container[head+7].toULong() and 0xFFu) shl 56)
      else
        ((container[head].toULong() and 0xFFu) shl 56) or
        ((container[head+1].toULong() and 0xFFu) shl 48) or
        ((container[head+2].toULong() and 0xFFu) shl 40) or
        ((container[head+3].toULong() and 0xFFu) shl 32) or
        ((container[head+4].toULong() and 0xFFu) shl 24) or
        ((container[head+5].toULong() and 0xFFu) shl 16) or
        ((container[head+6].toULong() and 0xFFu) shl 8)  or
        (container[head+7].toULong()  and 0xFFu)
    }

    size -= 8

    // We are out of line, so just use the internal indices to get the values
    // rather than doing a bunch of complex logic to make it happen.
    val out = if (littleEndian)
      (container[internalIndex(0)].toULong()  and 0xFFu)         or
      ((container[internalIndex(1)].toULong() and 0xFFu) shl 8)  or
      ((container[internalIndex(2)].toULong() and 0xFFu) shl 16) or
      ((container[internalIndex(3)].toULong() and 0xFFu) shl 24) or
      ((container[internalIndex(4)].toULong() and 0xFFu) shl 32) or
      ((container[internalIndex(5)].toULong() and 0xFFu) shl 40) or
      ((container[internalIndex(6)].toULong() and 0xFFu) shl 48) or
      ((container[internalIndex(7)].toULong() and 0xFFu) shl 56)
    else
      ((container[internalIndex(0)].toULong() and 0xFFu) shl 56) or
      ((container[internalIndex(1)].toULong() and 0xFFu) shl 48) or
      ((container[internalIndex(2)].toULong() and 0xFFu) shl 40) or
      ((container[internalIndex(3)].toULong() and 0xFFu) shl 32) or
      ((container[internalIndex(4)].toULong() and 0xFFu) shl 24) or
      ((container[internalIndex(5)].toULong() and 0xFFu) shl 16) or
      ((container[internalIndex(6)].toULong() and 0xFFu) shl 8)  or
      (container[internalIndex(7)].toULong()  and 0xFFu)

    realHead = internalIndex(8)

    return out
  }

  // endregion PopUnsigned

  /**
   * Fills this [UByteDeque] with data from the given [InputStream].
   *
   * This method will read at most `deque.cap - deque.size` bytes from the given
   * `InputStream`.
   *
   * @param stream `InputStream` from which this `ByteDeque` will be filled.
   *
   * @return The number of bytes read into this `ByteDeque` from the given
   * `InputStream`, or `-1` if the end of the `InputStream` had been reached
   * before this method was called.
   */
  fun fillFrom(stream: InputStream): Int {
    // If the current size of the deque is `0` then use the full data array
    // regardless of where the head was previously.
    if (size == 0) {
      realHead = 0
      val red = stream.read(container.asByteArray())

      if (red == -1) {
        size = 0
        return -1
      }

      size = red
      return red
    }

    // If we don't have any space available, then bail here
    if (space == 0)
      return 0

    // If we only have room for one more byte
    if (space == 1) {
      // Read one byte from the stream
      val red = stream.read()

      // If the end of the stream was reached
      if (red == -1)
      // Indicate as such without appending anything to the deque.
        return -1

      // Add the byte to the deque
      this.pushBack(red.toUByte())

      // Indicate that we read 1 byte.
      return 1
    }

    // Figure out the actual position of the last value in the backing array.
    val curOffset = internalIndex(size)

    // If the current last index is before the head of the deque, then we are
    // wonky like: [3, 4, _, _, 1, 2].  We can fill the middle until the head
    // and bail.
    if (curOffset < realHead) {
      // Read the bytes
      val red = stream.read(container.asByteArray(), curOffset, realHead - curOffset)

      // If we hit the end of the stream already
      if (red == -1)
      // Indicate as such and do nothing else
        return -1

      // add the number of bytes we read to the size of the deque
      size += red

      // Return the number of bytes we read.
      return red
    }

    // So the current first open slot is after the head of the deque, this could
    // go one of 2 ways:
    //
    // 1. The head is actually 0, and we can just fill the back of the array
    // 2. the head is not 0, we need to fill at least 1 byte at the back end of
    //    the array, and then we can fill the front end of the array.

    // If the head of the deque is in the 0 position, we can just fill the back
    // half of the array and be done.
    if (realHead == 0) {
      // Read them dang bytes
      val red = stream.read(container.asByteArray(), curOffset, container.size - curOffset)

      // If the stream was over already
      if (red == -1)
      // Indicate it and do nothing more
        return -1

      // Add the number of bytes we read to the size of the deque
      size += red

      // Return the number of bytes we red
      return red
    }

    // Dang, we gotta do 2 reads.  One to fill out 1 or more bytes at the end of
    // the backing array, another to read 1 or more bytes at the beginning of
    // the backing array.

    // Fill the back end of the backing array.
    val red1 = stream.read(container.asByteArray(), curOffset, container.size - curOffset)

    // If the stream was doa
    if (red1 == -1)
    // Indicate it and end here
      return -1

    // So, we read some bytes

    // Add the number of bytes we read to the size of the deque
    size += red1

    // Fill the front end of the backing array
    val red2 = stream.read(container.asByteArray(), 0, realHead)

    // If the stream was done
    if (red2 == -1)
    // return the number of bytes we read the first time
      return red1

    // So the stream had more bytes in it

    // Add the number of bytes we read to the deque's size
    size += red2

    // Return the total number of bytes we read.
    return red1 + red2
  }

  companion object {

    /**
     * Creates a new [UByteDeque] instance wrapping the given values.
     *
     * The returned ByteDeque will have the same size and capacity as the number
     * of values passed to this function.
     *
     * @param values Values to wrap with a new [UByteDeque].
     *
     * @return A new `ByteDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: UByte) = UByteDeque(values, 0)
  }

}