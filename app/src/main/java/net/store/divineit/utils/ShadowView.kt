package net.store.divineit.utils

import android.content.Context
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.Gravity
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import net.store.divineit.R
import net.store.divineit.utils.ShadowView
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.util.LruCache
import android.view.View

class ShadowView : View {
    constructor(context: Context) : super(context) {
        initialize(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context, attrs, defStyleAttr, 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize(context, attrs, defStyleAttr, defStyleRes)
    }

    /**
     * Initializes the view.
     *
     * @param context      The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     * supplies default values for the view, used only if
     * defStyleAttr is 0 or can not be found in the theme. Can be 0
     * to not look for defaults.
     * @see View
     */
    private fun initialize(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        var gravity = Gravity.TOP

        // Get the attributes.
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.ShadowView, defStyleAttr, defStyleRes)
        try {
            gravity = a.getInt(R.styleable.ShadowView_android_gravity, gravity)
        } finally {
            a.recycle()
        }

        // Set the gradient as background.
        background = makeCubicGradientScrimDrawable(0x77000000, 8, gravity)
    }

    /**
     * Creates an approximated cubic gradient using a multi-stop linear gradient.
     */
    private fun makeCubicGradientScrimDrawable(
        baseColor: Int,
        numberOfStops: Int,
        gravity: Int
    ): Drawable {

        // Generate a cache key by hashing together the inputs, based on the method described in the Effective Java book
        var numStops = numberOfStops
        var cacheKeyHash = baseColor
        cacheKeyHash = 31 * cacheKeyHash + numStops
        cacheKeyHash = 31 * cacheKeyHash + gravity
        val cachedGradient = cubicGradientScrimCache[cacheKeyHash]
        if (cachedGradient != null) {
            return cachedGradient
        }
        numStops = numStops.coerceAtLeast(2)
        val paintDrawable = PaintDrawable()
        paintDrawable.shape = RectShape()
        val stopColors = IntArray(numStops)
        val red = Color.red(baseColor)
        val green = Color.green(baseColor)
        val blue = Color.blue(baseColor)
        val alpha = Color.alpha(baseColor)
        for (i in 0 until numStops) {
            val x = i * 1f / (numStops - 1)
            val opacity = constrain(
                0f, 1f, Math.pow(x.toDouble(), 3.0)
                    .toFloat()
            )
            stopColors[i] = Color.argb((alpha * opacity).toInt(), red, green, blue)
        }
        val x0: Float
        val x1: Float
        val y0: Float
        val y1: Float
        when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.START -> {
                x0 = 1f
                x1 = 0f
            }
            Gravity.END -> {
                x0 = 0f
                x1 = 1f
            }
            else -> {
                x0 = 0f
                x1 = 0f
            }
        }
        when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.TOP -> {
                y0 = 1f
                y1 = 0f
            }
            Gravity.BOTTOM -> {
                y0 = 0f
                y1 = 1f
            }
            else -> {
                y0 = 0f
                y1 = 0f
            }
        }
        paintDrawable.shaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    width * x0,
                    height * y0,
                    width * x1,
                    height * y1,
                    stopColors, null,
                    Shader.TileMode.CLAMP
                )
            }
        }
        cubicGradientScrimCache.put(cacheKeyHash, paintDrawable)
        return paintDrawable
    }

    private fun constrain(min: Float, max: Float, v: Float): Float {
        return min.coerceAtLeast(max.coerceAtMost(v))
    }

    companion object {
        private val cubicGradientScrimCache = LruCache<Int, Drawable>(10)
    }
}