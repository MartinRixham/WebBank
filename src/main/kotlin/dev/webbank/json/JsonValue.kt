package dev.webbank.json

sealed interface JsonValue {

	fun isNull(): Boolean

	fun isBoolean(): Boolean

	class Valid(private val string: String): JsonValue {

		override fun isNull(): Boolean {
			return string == "null"
		}

		override fun isBoolean(): Boolean {
			return string == "true" || string == "false"
		}
	}

	class Invalid(private val error: String): JsonValue {

		override fun isNull(): Boolean {
			return false
		}

		override fun isBoolean(): Boolean {
			return false
		}

		override fun toString(): String{
			return error
		}
	}

	companion object {
		
		fun parse(string: String?): JsonValue {

			if (string == null) {
				return Invalid("Failed to parse value: No data.")
			}

			if (string == "null" || string == "true" || string == "false") {
				return Valid(string)
			}
			else {
				return Invalid("Failed to parse value: $string is not a JSON value.")
			}
		}
	}
}
