package com.webbank.account

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import redis.clients.jedis.UnifiedJedis

class JedisHashes(private val redis: UnifiedJedis) : Hashes {

    // Jedis talks to Redis with blocking calls, so they are kept off the
    // threads Netty answers requests on.
    override suspend fun set(key: String, fields: Map<String, String>) {
        withContext(Dispatchers.IO) {
            redis.hset(key, fields)
        }
    }
}
