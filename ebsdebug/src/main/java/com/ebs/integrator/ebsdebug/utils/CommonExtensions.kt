@file:Suppress("unused")

package com.ebs.integrator.ebsdebug.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.time.Duration

inline fun <reified T> tryNull(tryAction: () -> T?): T? {
    return try {
        tryAction()
    } catch (ex: Exception) {
        null
    }
}

inline fun <reified T> tryNullLog(tryAction: () -> T?): T? {
    return try {
        tryAction()
    } catch (ex: Exception) {
        Log.d("TryNullLog", "_".repeat(20))
        ex.printStackTrace()
        null
    }
}

inline fun tryVerifyAction(tryAction: () -> Unit): Boolean {
    return try {
        tryAction()
        true
    } catch (ex: Exception) {
        Log.d("tryVerifyAction", "_".repeat(20))
        ex.printStackTrace()
        false
    }
}

inline fun <reified T> tryUntilNotNull(activenessCondition: Boolean, tryAction: () -> T?): T? {
    var a: T? = null
    while (activenessCondition && a == null) {
        a = tryNull(tryAction)
    }
    return a
}

inline fun <reified T> tryLogException(tryAction: () -> T?): T? {
    return try {
        tryAction()
    } catch (ex: Exception) {
        //FirebaseCrashlytics.getInstance().recordException(ex)
        ex.printStackTrace()
        null
    }
}

inline fun <reified T> T?.getOrCreate(create: () -> T): T {
    return this ?: create()
}

inline fun <reified T> T?.lockOnNotNull(action: () -> Unit) {
    if (this == null) {
        action()
    }
}

inline fun <reified Target : View> Target.delayViewAction(
    delay: Duration,
    crossinline action: (Target) -> Unit
) {
    findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.IO) {
        delay(delay)
        withContext(Dispatchers.Main) {
            action(this@delayViewAction)
        }
    }
}

interface LifeCycleScopeHandler {

    var scope: LifecycleCoroutineScope?
    fun getViewScope() = scope ?: throw Exception("Scope is not registered")

    fun register(lifecycle: Lifecycle, observer: LifecycleObserver) {
        lifecycle.addObserver(observer)
        scope = lifecycle.coroutineScope
    }

}

inline infix fun <reified T : Comparable<T>> T.constrain(limit: Pair<T, T>): T {
    return if (this > limit.second) limit.second else if (this < limit.first) limit.first else this
}

inline infix fun <reified T : Comparable<T>> T.assertInInfiniteRange(range: Pair<T, T>): T {
    return when {
        this < range.first -> range.second
        this > range.second -> range.first
        else -> this
    }

}

inline fun <reified T> Collection<T>.findAndPosition(predicate: (T) -> Boolean): Pair<T?, Int> {
    return find(predicate)?.let {
        it to indexOf(it)
    } ?: (null to -1)
}

inline fun <reified T, reified B : T> Collection<T>.findAndPositionAndCast(predicate: (T) -> Boolean): Pair<B?, Int> {
    return find(predicate)?.let {
        it as B to indexOf(it)
    } ?: (null to -1)
}

inline fun <reified B> Collection<*>.safeMapCast(): List<B> {
    return mapNotNull { if (it is B) it else null }
}

val Collection<Any>.indicesSize get() = size - 1

fun <T> WeakReference<T>?.weakGet() = this?.get()

inline fun <reified T> WeakReference<T>?.weakGet(crossinline weakAction: T.() -> Unit) {
    this?.get()?.let {
        weakAction(it)
    }
}

inline fun <reified T: Any?> WeakReference<T?>?.weakGetRef(crossinline weakAction: T.() -> Unit) {
    this?.get()?.let {
        weakAction(it)
    }
}

fun <T> MutableList<T>.addAll(vararg objects: Collection<T>) {
    objects.forEach {
        addAll(it)
    }
}

inline fun <T, B, C> Pair<T?, B?>.let(action: (T, B) -> C): C? {
    return first?.let { f ->
        second?.let { s ->
            action(f, s)
        }
    }
}

/**
 * Does not work in anonymous classes
 */
inline fun <reified T> T.log(message: String) {
    Log.d(T::class.simpleName, message)
}

/**
 * Does not work in anonymous classes
 */
inline fun <reified T> T.log(message: Any?, tag: String = "result") {
    Log.d(T::class.simpleName, "$tag = $message")
}

inline fun <reified T: Fragment> T.fabricateWithArgs(args: Bundle): T {
    return this.apply {
        arguments = args
    }
}

fun <T> Collection<T>.multiply(times: Int): List<T> {
    return mutableListOf<T>().apply {
        repeat(times) {
            addAll(this@multiply)
        }
    }
}

val Context.screenWidth
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight
    get() = resources.displayMetrics.heightPixels

/**
 * @return [ColorInt]
 */
fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

suspend fun <K, V> Map<K, V>.assertGet(key: K): V {
    while (true) {
        get(key)?.let {
            return it
        }
        delay(250)
    }
}