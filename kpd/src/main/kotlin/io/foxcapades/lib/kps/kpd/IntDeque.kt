package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractIntDeque

/**
 * Int Deque
 *
 * A deque type that deals in unboxed [Int] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
class IntDeque : PrimitiveDeque, AbstractIntDeque<IntDeque> {

  /**
   * Constructs a new, empty `IntDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `IntDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `IntDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: IntArray) : super(values)

  constructor(values: IntArray, head: Int) : super(values, head)

  override fun new(values: IntArray, head: Int) = IntDeque(values, head)

  companion object {

    /**
     * Creates a new [IntDeque] instance wrapping the given values.
     *
     * The returned `IntDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [IntDeque].
     *
     * @return A new `IntDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: Int) = IntDeque(values, 0)
  }

}