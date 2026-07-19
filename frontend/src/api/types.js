/** @typedef {'CUSTOMER' | 'RESTAURANT_OWNER'} UserRole */

/**
 * @typedef {Object} UserDTO
 * @property {number} id
 * @property {string} username
 * @property {string} email
 * @property {string} [phone]
 * @property {string} [firstName]
 * @property {string} [lastName]
 * @property {number} [restaurantId]
 */

/**
 * @typedef {Object} UserCreateDTO
 * @property {string} username
 * @property {string} email
 * @property {string} password
 * @property {string} [phone]
 * @property {string} [firstName]
 * @property {string} [lastName]
 */

/**
 * @typedef {Object} PhoneDTO
 * @property {string} phone
 */

/**
 * @typedef {Object} PasswordUpdateDTO
 * @property {string} currentPassword
 * @property {string} newPassword
 */

/**
 * @typedef {Object} AddressDTO
 * @property {number} [id]
 * @property {string} street
 * @property {string} city
 * @property {string} state
 * @property {string} zipCode
 * @property {string} [country]
 * @property {boolean} [isDefault]
 */

/**
 * @typedef {Object} AddressCreateDTO
 * @property {string} street
 * @property {string} city
 * @property {string} state
 * @property {string} zipCode
 * @property {string} [country]
 * @property {boolean} [isDefault]
 */

/**
 * @typedef {Object} RestaurantDTO
 * @property {number} id
 * @property {string} name
 * @property {string} [description]
 * @property {string} [cuisine]
 * @property {number} [rating]
 * @property {number} [ownerId]
 * @property {string} [address]
 */

/**
 * @typedef {Object} RestaurantCreateDTO
 * @property {string} name
 * @property {string} [description]
 * @property {string} [cuisine]
 * @property {string} [address]
 */

/**
 * @typedef {Object} RestaurantUpdateDTO
 * @property {string} name
 * @property {string} [description]
 * @property {string} [cuisine]
 * @property {string} [address]
 */

/**
 * @typedef {Object} MenuItemDTO
 * @property {number} [id]
 * @property {string} name
 * @property {string} [description]
 * @property {number} price
 * @property {number} [rating]
 * @property {number} [restaurantId]
 * @property {boolean} [available]
 */

/**
 * @typedef {Object} CartItemDTO
 * @property {number} id
 * @property {number} menuItemId
 * @property {string} [menuItemName]
 * @property {number} quantity
 * @property {number} [price]
 * @property {number} [subtotal]
 */

/**
 * @typedef {Object} CartDTO
 * @property {number} id
 * @property {number} userId
 * @property {CartItemDTO[]} cartItems
 * @property {number} [totalAmount]
 * @property {number} [total]
 */

/**
 * @typedef {'PENDING' | 'CONFIRMED' | 'PREPARING' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'CANCELLED'} OrderStatus
 */

/**
 * @typedef {Object} OrderDTO
 * @property {number} id
 * @property {number} userId
 * @property {number} [restaurantId]
 * @property {number} [addressId]
 * @property {OrderStatus} orderStatus
 * @property {number} [totalAmount]
 * @property {string} [createdAt]
 * @property {string} [orderDate]
 * @property {CartItemDTO[]} [orderItems]
 * @property {string} [restaurantName]
 */

/**
 * @typedef {Object} OrderHistoryDTO
 * @property {number} orderId
 * @property {OrderStatus} orderStatus
 * @property {number} totalAmount
 * @property {string} orderDate
 * @property {string} [restaurantName]
 */

export {}
