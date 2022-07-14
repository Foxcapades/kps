package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractShortDeque

/**
 * Short Deque
 *
 * A deque type that deals in unboxed [Short] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
class ShortDeque : PrimitiveDeque, AbstractShortDeque<ShortDeque> {

  /**
   * Constructs a new, empty `ShortDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `ShortDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `ShortDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: ShortArray) : super(values)

  constructor(values: ShortArray, head: Int) : super(values, head)

  override fun new(values: ShortArray, head: Int) = ShortDeque(values, head)

  companion object {

    /**
     * Creates a new [ShortDeque] instance wrapping the given values.
     *
     * The returned `ShortDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [ShortDeque].
     *
     * @return A new `ShortDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: Short) = ShortDeque(values, 0)
  }

}