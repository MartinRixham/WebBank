package dev.webbank.json

object JsonArray {

	sealed interface Read: Iterable<JsonValue> {

		fun getBoolean(index: Int): Boolean

		fun getNumber(index: Int): Double

		fun getString(index: Int): String

		fun getObject(index: Int): JsonObject.Read

		fun getArray(index: Int): Read

		fun getValue(index: Int): JsonValue

		fun length(): Int
	}

	class Write() {

		private val list: MutableList<String> = mutableListOf()

		fun push(value: Boolean): Write {
			list.add(value.toString())
			return this
		}

		fun pushAll(vararg values: Boolean): Write {
			list.addAll(values.map(Boolean::toString))
			return this
		}

		fun push(value: Double): Write {
			list.add(value.toString())
			return this
		}

		fun pushAll(vararg values: Double): Write {
			list.addAll(values.map(Double::toString))
			return this
		}

		fun push(value: String): Write {
			list.add("\"$value\"")
			return this
		}

		fun pushAll(vararg values: String): Write {
			list.addAll(values.map { value -> "\"$value\"" })
			return this
		}

		fun push(value: JsonObject.Write): Write {
			list.add(value.toString())
			return this
		}

		fun pushAll(vararg values: JsonObject.Write): Write {
			list.addAll(values.map(JsonObject.Write::toString))
			return this
		}

		fun push(value: Write): Write {
			list.add(value.toString())
			return this
		}

		fun pushAll(vararg values: Write): Write {
			list.addAll(values.map(Write::toString))
			return this
		}

		override fun toString(): String {
			val builder = StringBuilder()
			builder.append('[')
			for (value in list) {
				builder.append("$value,")
			}
			builder.deleteCharAt(builder.length - 1)
			builder.append(']')
			return builder.toString()
		}
	}

	class Invalid(private val error: String): Read {

		override fun getBoolean(index: Int): Boolean {
			return false
		}

		override fun getNumber(index: Int): Double {
			return Double.NaN
		}

		override fun getString(index: Int): String {
			return ""
		}

		override fun getObject(index: Int): JsonObject.Read {
			return JsonObject.Invalid(error)
		}

		override fun getArray(index: Int): Read {
			return Invalid(error)
		}

		override fun getValue(index: Int): JsonValue {
			return JsonValue.Invalid(error)
		}

		override fun length(): Int {
			return 0
		}

		override fun iterator(): Iterator<JsonValue> = object: Iterator<JsonValue> {

			override fun hasNext(): Boolean {
				return false
			}

			override fun next(): JsonValue {
				throw IndexOutOfBoundsException()
			}
		}

		override fun toString(): String {
			return error
		}
	}

	class Valid(private val list: List<String>): Read {

		override fun getBoolean(index: Int): Boolean {
			return list.getOrNull(index) == "true"
		}

		override fun getNumber(index: Int): Double {
			val value = list.getOrNull(index)
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

		override fun getString(index: Int): String {
			val value = list.getOrNull(index)
			if (value == null) {
				return ""
			}
			else {
				return value.substring(1, value.length - 1)
			}
		}

		override fun getObject(index: Int): JsonObject.Read {
			val value = list.getOrNull(index)
			return JsonObject.parse(value)
		}

		override fun getArray(index: Int): Read {
			val value = list.getOrNull(index)
			return parse(value)
		}

		override fun getValue(index: Int): JsonValue {
			return JsonValue.parse(list.getOrNull(index))
		}

		override fun length(): Int {
			return list.size
		}

		override fun iterator(): Iterator<JsonValue> = object: Iterator<JsonValue> {

			var index: Int = 0;

			override fun hasNext(): Boolean {
				return index < list.size
			}

			override fun next(): JsonValue {
				return JsonValue.parse(list[index])
			}
		}
	}

	private const val START: Byte = 0
	private const val BEFORE_VALUE: Byte = 1
	private const val IN_VALUE: Byte = 2
	private const val AFTER_VALUE: Byte = 3
	private const val END: Byte = 4

	private const val LITERAL: Byte = 0
	private const val STRING: Byte = 1
	private const val OBJECT: Byte = 2
	private const val ARRAY: Byte = 3

	fun parse(string: String?): Read {
		if (string == null) {
			return Invalid("Failed to parse array: No data.")
		}

		val list = mutableListOf<String>()
		var state: Byte = START
		var depth = 0
		var builder = StringBuilder()
		var escape = false
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
						list.add(builder.toString())
						state = AFTER_VALUE
					}
				}
				else if (type == OBJECT) {
					builder.append(character)
					if (character == '}') {
						if (depth == 0) {
							list.add(builder.toString())
							state = AFTER_VALUE
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
							list.add(builder.toString())
							state = AFTER_VALUE
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
					if (character == ',')  {
						list.add(builder.toString())
						state = AFTER_VALUE
					}
					else {
						builder.append(character)
					}
				}
			}
			else if (state == START) {
				if (character == '[') {
					state = BEFORE_VALUE
				}
				else if (!character.isWhitespace()) {
					return Invalid("Failed to parse array at character $i: Started with $character instead of [.")
				}
			}
			else if (state == BEFORE_VALUE) {
				if (character == ']') {
					state = END
				}
				else if (!character.isWhitespace()) {
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
			else if (state == AFTER_VALUE) {
				if (character == ']') {
					state = END
				}
				else if (character == ',') {
					state == BEFORE_VALUE
				}
				else {
					return Invalid("Failed to parse array at character $i: Found $character when expecting ,.")
				}
			}
			else if (!character.isWhitespace()) {
				return Invalid("Failed to parse array at character $i: Found $character after end of array.")
			}
		}

		if (state == END) {
			return Valid(list)
		}
		else {
			return Invalid("Failed to parse array: Ran out of characters before end of array.")
		}
	}
}
