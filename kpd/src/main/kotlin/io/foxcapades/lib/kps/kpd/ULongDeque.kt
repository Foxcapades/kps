package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractULongDeque

/**
 * ULong Deque
 *
 * A deque type that deals in unboxed [ULong] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
class ULongDeque : PrimitiveDeque, AbstractULongDeque<ULongDeque> {

  /**
   * Constructs a new, empty `ULongDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `ULongDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `ULongDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: ULongArray) : super(values)

  constructor(values: ULongArray, head: Int) : super(values, head)

  override fun new(values: ULongArray, head: Int) = ULongDeque(values, head)

  companion object {

    /**
     * Creates a new [ULongDeque] instance wrapping the given values.
     *
     * The returned `ULongDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [ULongDeque].
     *
     * @return A new `ULongDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: ULong) = ULongDeque(values, 0)
  }

}