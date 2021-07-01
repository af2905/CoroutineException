package com.github.af2905.coroutineexception

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.af2905.coroutineexception.extention.repeatIsActive
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.bthRun).setOnClickListener { onRun() }
        findViewById<View>(R.id.bthRun2).setOnClickListener { onRun2() }
    }

    private fun onRun() {
        log("onRun, start")
        val handler = CoroutineExceptionHandler { _, throwable -> log("first coroutine exception $throwable") }

        scope.launch(handler) {
            TimeUnit.MILLISECONDS.sleep(1000)
            Integer.parseInt("a")
        }

        scope.launch {
            repeat(5) {
                TimeUnit.MILLISECONDS.sleep(300)
                log("second coroutine isActive $isActive")
            }
        }
        log("onRun, end")
    }

    private fun onRun2() {
        log("onRun2, start")
        val handler = CoroutineExceptionHandler { context, exception ->
            log("$exception was handled in Coroutine_${context[CoroutineName]?.name}")
        }

        val scope2 = CoroutineScope(Job() + Dispatchers.Default + handler)

        scope2.launch(CoroutineName("1")) {

            launch(CoroutineName("1-1")) { this.repeatIsActive() }

            launch(CoroutineName("1-2")) {
                this.repeatIsActive()

                launch(CoroutineName("1-2-1")) {
                    TimeUnit.MILLISECONDS.sleep(1000)
                    log("exception")
                    Integer.parseInt("b")
                }
            }
        }

        scope2.launch(CoroutineName("2")) {
            this.repeatIsActive()

            launch(CoroutineName("2-1")) { this.repeatIsActive() }
            launch(CoroutineName("2-2")) { this.repeatIsActive() }
        }

        log("onRun2, finish")
    }

    companion object {

        private var formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

        fun log(text: String) {
            Log.d("TAG", "${formatter.format(Date())} $text [${Thread.currentThread().name}]")
        }
    }
}