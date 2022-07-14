package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractLongDeque

/**
 * Long Deque
 *
 * A deque type that deals in unboxed [Long] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
class LongDeque : PrimitiveDeque, AbstractLongDeque<LongDeque> {

  /**
   * Constructs a new, empty `LongDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `LongDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `LongDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: LongArray) : super(values)

  constructor(values: LongArray, head: Int) : super(values, head)

  override fun new(values: LongArray, head: Int) = LongDeque(values, head)

  companion object {

    /**
     * Creates a new [LongDeque] instance wrapping the given values.
     *
     * The returned `LongDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [LongDeque].
     *
     * @return A new `LongDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: Long) = LongDeque(values, 0)
  }

}