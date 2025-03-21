package com.github.catomon.moemarathon.teavm.leaderboard

import org.teavm.jso.JSBody
import org.teavm.jso.JSObject

interface FetchCallback : JSObject {
    fun complete(result: String)
    fun error(message: String)
}

@JSBody(params = ["url", "callback"], script =
"fetch(url)\n" +
    ".then(response => response.json())\n" +
    ".then(data => callback.complete(JSON.stringify(data)))\n" +
    ".catch(error => callback.error(error.message));")
external fun fetch(url: String, callback: FetchCallback)

@JSBody(params = ["url", "callback"], script =
"fetch(url)\n" +
    ".then(response => callback.complete(response.ok ? 'Success' : 'Failed'))\n" +
    ".catch(error => callback.error(error.message));")
external fun submitScore(url: String, callback: FetchCallback)
