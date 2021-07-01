package com.github.af2905.coroutineexception.extention

import com.github.af2905.coroutineexception.MainActivity
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

fun CoroutineScope.repeatIsActive() {
    repeat(5) {
        TimeUnit.MILLISECONDS.sleep(300)
        MainActivity.log("Coroutine_${coroutineContext[CoroutineName]?.name} isActive $isActive")
    }
}