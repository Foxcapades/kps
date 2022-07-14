package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractUShortDeque

/**
 * UShort Deque
 *
 * A deque type that deals in unboxed [UShort] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
class UShortDeque : PrimitiveDeque, AbstractUShortDeque<UShortDeque> {

  /**
   * Constructs a new, empty `UShortDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `UShortDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `UShortDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: UShortArray) : super(values)

  constructor(values: UShortArray, head: Int) : super(values, head)

  override fun new(values: UShortArray, head: Int) = UShortDeque(values, head)

  companion object {

    /**
     * Creates a new [UShortDeque] instance wrapping the given values.
     *
     * The returned `UShortDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [UShortDeque].
     *
     * @return A new `UShortDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: UShort) = UShortDeque(values, 0)
  }

}