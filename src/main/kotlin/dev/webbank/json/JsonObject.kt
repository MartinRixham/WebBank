package dev.webbank.json

object JsonObject {

	operator fun invoke(): Write {
		return Write()
	}

	sealed interface Read {

		fun hasValue(key: String): Boolean

		fun getBoolean(key: String): Boolean

		fun getNumber(key: String): Double

		fun getString(key: String): String

		fun getObject(key: String): Read

		fun getArray(key: String): JsonArray.Read

		fun getValue(key: String): JsonValue

		fun validate(): String

		fun getKeys(): Set<String>
	}

	class Write() {

		private val map: MutableMap<String, String> = mutableMapOf()

		fun put(key: String, value: Boolean): Write {
			map.set(key, value.toString())
			return this
		}

		fun put(key: String, value: Double): Write {
			map.set(key, value.toString())
			return this
		}

		fun put(key: String, value: String): Write {
			map.set(key, "\"$value\"")
			return this
		}

		fun put(key: String, value: Write): Write {
			map.set(key, value.toString())
			return this
		}

		fun put(key: String, value: JsonArray.Write): Write {
			map.set(key, value.toString())
			return this
		}

		fun read(): Read {
			return Valid(map)
		}

		override fun toString(): String {
			val builder = StringBuilder()
			builder.append('{')
			for (pair in map.entries) {
				builder.append("\"${pair.key}\":")
				builder.append("${pair.value},")
			}
			builder.deleteCharAt(builder.length - 1)
			builder.append('}')
			return builder.toString()
		}
	}

	class Invalid(private val error: String): Read {

		override fun hasValue(key: String): Boolean {
			return false
		}

		override fun getBoolean(key: String): Boolean {
			return false
		}

		override fun getNumber(key: String): Double {
			return Double.NaN
		}

		override fun getString(key: String): String {
			return ""
		}

		override fun getObject(key: String): Read {
			return Invalid(error)
		}

		override fun getArray(key: String): JsonArray.Read {
			return JsonArray.Invalid(error)
		}

		override fun getValue(key: String): JsonValue {
			return JsonValue.Invalid(error)
		}

		override fun validate(): String {
			return error
		}

		override fun getKeys(): Set<String> {
			return emptySet()
		}

		override fun equals(other: Any?): Boolean {
			return hashCode() == other.hashCode()
		}

		override fun hashCode(): Int {
			return error.hashCode()
		}

		override fun toString(): String {
			return error
		}
	}

	class Valid(private val map: Map<String, String>): Read {

		override fun hasValue(key: String): Boolean {
			return map.containsKey(key)
		}

		override fun getBoolean(key: String): Boolean {
			return map.get(key) == "true"
		}

		override fun getNumber(key: String): Double {
			val value = map.get(key)
			if (value == null) {
				return Double.NaN
			}
			else {
				try {
					return value.toDouble()
				}
				catch (e: NumberFormatException) {
					return Double.NaN
				}
			}
		}

		override fun getString(key: String): String {
			val value = map.get(key)
			if (value == null) {
				return ""
			}
			else {
				return value.substring(1, value.length - 1)
			}
		}

		override fun getObject(key: String): Read {
			return parse(map.get(key))
		}

		override fun getArray(key: String): JsonArray.Read {
			return JsonArray.parse(map.get(key))
		}

		override fun getValue(key: String): JsonValue {
			return JsonValue.Valid(key)
		}

		override fun validate(): String {
			val errors = mutableMapOf<String, String>()

			for ((key, value) in map) {
				val error = JsonValue.parse(value).validate()
				if (error.length > 0) {
					errors.put(key, error)
				}
			}

			if (errors.size == 0) {
				return ""
			}
			else {
				return "{ ${errors.map { (key, error) ->
					""""Value of $key": "$error""""
				}.joinToString(", ")} }"
			}
		}

		override fun getKeys(): Set<String> {
			return map.keys
		}

		override fun equals(other: Any?): Boolean {
			return hashCode() == other.hashCode()
		}

		override fun hashCode(): Int {
			var hash = 0
			for (value in map.values) {
				hash = hash xor JsonValue.parse(value).hashCode()
			}
			return hash
		}
	}

	private const val START: Byte = 0
	private const val BEFORE_KEY: Byte = 1
	private const val IN_KEY: Byte = 2
	private const val AFTER_KEY: Byte = 3
	private const val BEFORE_VALUE: Byte = 4
	private const val IN_VALUE: Byte = 5
	private const val END: Byte = 6

	private const val LITERAL: Byte = 0
	private const val STRING: Byte = 1
	private const val OBJECT: Byte = 2
	private const val ARRAY: Byte = 3

	fun parse(string: String?): Read {
		if (string == null) {
			return Invalid("Failed to parse object: No data.")
		}

		val map = mutableMapOf<String, String>()
		var state: Byte = START
		var depth = 0
		var builder = StringBuilder()
		var escape = false
		var key = ""
		var type: Byte = LITERAL;

		for ((i, character) in string.withIndex()) {
			if (state == IN_VALUE) {
				if (escape) {
					builder.append(character)
					escape == false
				}
				else if (character == '\\') {
					escape == true
				}
				else if (type == STRING) {
					builder.append(character)
					if (character == '"') {
						map.set(key, builder.toString())
						state = BEFORE_KEY
					}
				}
				else if (type == OBJECT) {
					builder.append(character)
					if (character == '}') {
						if (depth == 0) {
							map.set(key, builder.toString())
							state = BEFORE_KEY
						}
						else {
							depth -= 1
						}
					}
					else if (character == '{') {
						depth += 1
					}
				}
				else if (type == ARRAY) {
					builder.append(character)
					if (character == ']') {
						if (depth == 0) {
							map.set(key, builder.toString())
							state = BEFORE_KEY
						}
						else {
							depth -= 1
						}
					}
					else if (character == '[') {
						depth += 1
					}
				}
				else {
					if (character == '}') {
						state = END
					}
					else if (character == ',')  {
						map.set(key, builder.toString())
						state = BEFORE_KEY
					}
					else {
						builder.append(character)
					}
				}
			}
			else if (state == START) {
				if (character == '{') {
					state = BEFORE_KEY
				}
				else if (!character.isWhitespace()) {
					return Invalid("Failed to parse object at character $i: Started with $character instead of {.")
				}
			}
			else if (state == BEFORE_KEY) {
				if (character == '}') {
					state = END
				}
				else if (character == '"') {
					builder.clear()
					state = IN_KEY
				}
				else if (character != ',' &&
					!character.isWhitespace()) {
					return Invalid("Failed to parse object at character $i: Found $character when expecting key.")
				}
			}
			else if (state == IN_KEY) {
				if (escape) {
					builder.append(character)
					escape = false
				}
				else if (character == '\\') {
					escape = true
				}
				else if (character == '"') {
					key = builder.toString()
					state = AFTER_KEY
				}
				else {
					builder.append(character)
				}
			}
			else if (state == AFTER_KEY) {
				if (character == ':') {
					state = BEFORE_VALUE
				}
				else if (!character.isWhitespace()) {
					return Invalid("Failed to parse object at character $i: Found $character when expecting :.")
				}
			}
			else if (state == BEFORE_VALUE) {
				if (!character.isWhitespace()) {
					if (character == '{') {
						depth = 0
						type = OBJECT
					}
					else if (character == '[') {
						depth = 0
						type = ARRAY
					}
					else if (character == '"') {
						type = STRING
					}
					else {
						type = LITERAL
					}
					builder.clear()
					builder.append(character)
					state = IN_VALUE
				}
			}
			else if (!character.isWhitespace()) {
				return Invalid("Failed to parse object at character $i: Found $character after end of object.")
			}
		}

		if (state == END) {
			return Valid(map)
		}
		else {
			return Invalid("Failed to parse object: Ran out of characters before end of object.")
		}
	}
}
