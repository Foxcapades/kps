package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractCharDeque

/**
 * Char Deque
 *
 * A deque type that deals in unboxed [Char] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
class CharDeque : PrimitiveDeque, CharSequence, AbstractCharDeque<CharDeque> {

  /**
   * Constructs a new, empty `CharDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `CharDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `CharDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: CharArray) : super(values)

  constructor(values: CharArray, head: Int) : super(values, head)

  override fun isEmpty() = super<AbstractCharDeque>.isEmpty()

  override fun new(values: CharArray, head: Int) = CharDeque(values, head)

  override val length = size

  override fun subSequence(startIndex: Int, endIndex: Int) = slice(startIndex, endIndex)

  /**
   * Returns the contents of this deque as a [String].
   *
   * @return The contents of this deque as a `String`.
   */
  fun stringValue() = String(toArray())

  companion object {

    /**
     * Creates a new [CharDeque] instance wrapping the given values.
     *
     * The returned `CharDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [CharDeque].
     *
     * @return A new `CharDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: Char) = CharDeque(values, 0)
  }
}