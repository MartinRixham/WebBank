package dev.webbank.json

sealed interface JsonValue {

	fun isNull(): Boolean

	fun isBoolean(): Boolean

	fun isNumber(): Boolean

	fun isString(): Boolean

	fun isObject(): Boolean

	fun isArray(): Boolean

	fun getBoolean(): Boolean

	fun getNumber(): Double

	fun getString(): String

	fun getObject(): JsonObject.Read

	fun getArray(): JsonArray.Read

	class Valid(private val string: String): JsonValue {

		override fun isNull(): Boolean {
			return string == "null"
		}

		override fun isBoolean(): Boolean {
			return string == "true" || string == "false"
		}

		override fun isNumber(): Boolean {
			try {
				string.toDouble()
				return true
			}
			catch (e: NumberFormatException) {
				return false
			}
		}

		override fun isString(): Boolean {
			return string[0] == '"'
		}

		override fun isObject(): Boolean {
			return string[0] == '{'
		}

		override fun isArray(): Boolean {
			return string[0] == '['
		}

		override fun getBoolean(): Boolean {
			return string == "true"
		}

		override fun getNumber(): Double {
			try {
				return string.toDouble()
			}
			catch (e: NumberFormatException) {
				return Double.NaN
			}
		}

		override fun getString(): String {
			return string.substring(1, string.length - 1)
		}

		override fun getObject(): JsonObject.Read {
			return JsonObject.parse(string)
		}

		override fun getArray(): JsonArray.Read {
			return JsonArray.parse(string)
		}
	}

	class Invalid(private val error: String): JsonValue {

		override fun isNull(): Boolean {
			return false
		}

		override fun isBoolean(): Boolean {
			return false
		}

		override fun isNumber(): Boolean {
			return false
		}

		override fun isString(): Boolean {
			return false
		}

		override fun isObject(): Boolean {
			return false
		}

		override fun isArray(): Boolean {
			return false
		}

		override fun getBoolean(): Boolean {
			return false
		}

		override fun getNumber(): Double {
			return Double.NaN
		}

		override fun getString(): String {
			return ""
		}

		override fun getObject(): JsonObject.Read {
			return JsonObject.Invalid(error)
		}

		override fun getArray(): JsonArray.Read {
			return JsonArray.Invalid(error)
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

			try {
				string.toDouble()
				return Valid(string)
			}
			catch (e: NumberFormatException) {
				if (string.length == 0) {
					return Invalid("Failed to parse value: No data.")
				}
				else if (string.length == 1) {
					return Invalid("Failed to parse value: $string is not a JSON value.")
				}
				else if (string == "null" ||
					string == "true" ||
					string == "false" ||
					(string[0] == '"' && string[string.length - 1] == '"') ||
					(string[0] == '{' && string[string.length - 1] == '}') ||
					(string[0] == '[' && string[string.length - 1] == ']')) {
					return Valid(string)
				}
				else {
					return Invalid("Failed to parse value: $string is not a JSON value.")
				}
			}
		}
	}
}
