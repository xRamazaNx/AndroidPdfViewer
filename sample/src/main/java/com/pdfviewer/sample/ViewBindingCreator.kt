package com.pdfviewer.sample

import android.view.InflateException
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType


// Activity that contains ViewBinding generic type
fun <VB : ViewBinding> CommonActivity<VB>.inflateViewBinding(container: ViewGroup? = null): VB {
    return inflateViewBinding(javaClass, layoutInflater, container)
}
//
//// Fragment that contains ViewBinding generic type
//fun <VB : ViewBinding> CommonFragment<VB>.inflateViewBinding(container: ViewGroup? = null): VB {
//    return inflateViewBinding(javaClass, layoutInflater, container)
//}
//
//// Fragment that contains ViewBinding generic type
//fun <VB : ViewBinding> CommonDialogBottomSheet<VB>.inflateViewBinding(container: ViewGroup? = null): VB {
//    return inflateViewBinding(javaClass, layoutInflater, container)
//}

// find viewBinding generic type and call inflate
private fun <VB : ViewBinding> inflateViewBinding(
    javaClass: Class<*>,
    inflater: LayoutInflater,
    container: ViewGroup?
): VB {

    var parentDepth = 50

    var javaClazz = javaClass

    var clazz: Class<in VB>?

    do {
        clazz = (javaClazz.genericSuperclass as? ParameterizedType)
            ?.actualTypeArguments
            ?.filterIsInstance<Class<in VB>>()
            ?.find { vbClass ->
                vbClass.simpleName.endsWith("Binding", false)
            }

        if (clazz == null) {
            javaClazz = javaClazz.superclass
        } else {
            break
        }
    } while (parentDepth-- > 0)

    val viewBindingType = clazz
        ?: throw InflateException("The one of generic types must be the ViewBinding generic type.")

    return inflateTargetViewBinding(viewBindingType, inflater, container)
}

@Suppress("UNCHECKED_CAST")
// find viewBinding generic type and call inflate
fun <VB : ViewBinding> inflateTargetViewBinding(
    viewBindingJavaClass: Class<in VB>,
    inflater: LayoutInflater,
    container: ViewGroup?
): VB {
    val inflateMethod = viewBindingJavaClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )

    return inflateMethod.invoke(null, inflater, container, false) as VB
}