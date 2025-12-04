package env

import java.io.File

object Env {
    private val cache: MutableMap<String, String> = mutableMapOf()

    init {
        loadDotEnv()
    }

    private fun loadDotEnv() {
        val cwd = System.getProperty("user.dir")
        val paths = listOf("$cwd/.env", "$cwd/.env.local")
        paths.forEach { path ->
            val f = File(path)
            if (f.exists() && f.isFile) {
                f.readLines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
                    .forEach { line ->
                        val idx = line.indexOf('=')
                        if (idx > 0) {
                            val key = line.substring(0, idx).trim()
                            val value = line.substring(idx + 1).trim()
                            cache[key] = value
                        }
                    }
            }
        }
    }

    fun get(key: String, default: String? = null): String? {
        val sys = System.getenv(key)
        if (!sys.isNullOrEmpty()) return sys
        return cache[key] ?: default
    }
}

