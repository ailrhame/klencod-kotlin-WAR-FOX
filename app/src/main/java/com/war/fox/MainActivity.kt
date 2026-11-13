
package com.war.fox

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import kotlin.math.*
import kotlin.random.Random

class MainActivity : Activity() {

    private lateinit var game: BattleView
    private lateinit var root: LinearLayout
    private lateinit var status: TextView
    private lateinit var controls: LinearLayout

    private val countries = listOf(
        Country("العراق", "IQ", 0xFFCE1126.toInt(), 0xFFFFFFFF.toInt(), 0xFF007A3D.toInt()),
        Country("السعودية", "SA", 0xFF006C35.toInt(), 0xFFFFFFFF.toInt(), 0xFF006C35.toInt()),
        Country("مصر", "EG", 0xFFCE1126.toInt(), 0xFFFFFFFF.toInt(), 0xFF000000.toInt()),
        Country("الأردن", "JO", 0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF007A3D.toInt()),
        Country("فلسطين", "PS", 0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF007A3D.toInt()),
        Country("سوريا", "SY", 0xFFCE1126.toInt(), 0xFFFFFFFF.toInt(), 0xFF000000.toInt()),
        Country("لبنان", "LB", 0xFFED1C24.toInt(), 0xFFFFFFFF.toInt(), 0xFFED1C24.toInt()),
        Country("الإمارات", "AE", 0xFF00732F.toInt(), 0xFFFFFFFF.toInt(), 0xFF000000.toInt()),
        Country("قطر", "QA", 0xFF8A1538.toInt(), 0xFFFFFFFF.toInt(), 0xFF8A1538.toInt()),
        Country("الكويت", "KW", 0xFF007A3D.toInt(), 0xFFFFFFFF.toInt(), 0xFFCE1126.toInt()),
        Country("البحرين", "BH", 0xFFCE1126.toInt(), 0xFFFFFFFF.toInt(), 0xFFCE1126.toInt()),
        Country("عُمان", "OM", 0xFFCE1126.toInt(), 0xFFFFFFFF.toInt(), 0xFF007A3D.toInt()),
        Country("اليمن", "YE", 0xFFCE1126.toInt(), 0xFFFFFFFF.toInt(), 0xFF000000.toInt()),
        Country("المغرب", "MA", 0xFF006233.toInt(), 0xFFCC0000.toInt(), 0xFF006233.toInt()),
        Country("تونس", "TN", 0xFFCE1126.toInt(), 0xFFFFFFFF.toInt(), 0xFFCE1126.toInt()),
        Country("الجزائر", "DZ", 0xFF006633.toInt(), 0xFFFFFFFF.toInt(), 0xFFCE1126.toInt()),
        Country("ليبيا", "LY", 0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF239E46.toInt()),
        Country("السودان", "SD", 0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF007A3D.toInt()),
        Country("تركيا", "TR", 0xFFE30A17.toInt(), 0xFFFFFFFF.toInt(), 0xFFE30A17.toInt()),
        Country("ألمانيا", "DE", 0xFF000000.toInt(), 0xFFFFCC00.toInt(), 0xFFDD0000.toInt()),
        Country("فرنسا", "FR", 0xFF0055A4.toInt(), 0xFFFFFFFF.toInt(), 0xFFEF4135.toInt()),
        Country("إيطاليا", "IT", 0xFF009246.toInt(), 0xFFFFFFFF.toInt(), 0xFFCE2B37.toInt()),
        Country("إسبانيا", "ES", 0xFFAA151B.toInt(), 0xFFFFC400.toInt(), 0xFFAA151B.toInt()),
        Country("المملكة المتحدة", "GB", 0xFF012169.toInt(), 0xFFFFFFFF.toInt(), 0xFFC8102E.toInt()),
        Country("روسيا", "RU", 0xFFFFFFFF.toInt(), 0xFF0039A6.toInt(), 0xFFD52B1E.toInt()),
        Country("الصين", "CN", 0xFFDE2910.toInt(), 0xFFFFDE00.toInt(), 0xFFDE2910.toInt()),
        Country("اليابان", "JP", 0xFFFFFFFF.toInt(), 0xFFBC002D.toInt(), 0xFFFFFFFF.toInt()),
        Country("الهند", "IN", 0xFFFF9933.toInt(), 0xFFFFFFFF.toInt(), 0xFF138808.toInt()),
        Country("باكستان", "PK", 0xFF01411C.toInt(), 0xFFFFFFFF.toInt(), 0xFF01411C.toInt()),
        Country("البرازيل", "BR", 0xFF009739.toInt(), 0xFFFFDF00.toInt(), 0xFF002776.toInt()),
        Country("الأرجنتين", "AR", 0xFF74ACDF.toInt(), 0xFFFFFFFF.toInt(), 0xFF74ACDF.toInt()),
        Country("الولايات المتحدة", "US", 0xFFB22234.toInt(), 0xFFFFFFFF.toInt(), 0xFF3C3B6E.toInt())
    )

    private val selected = mutableListOf<Country>()
    private var speed = 1f
    private var arenaScale = 1f
    private var mode = "Random Battle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(16, 17, 23)
        window.navigationBarColor = Color.rgb(16, 17, 23)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        buildUI()
        startRandom()
    }

    private fun buildUI() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(16, 17, 23))
        }

        val header = TextView(this).apply {
            text = "⚔  WAR FOX"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(255, 190, 60))
            typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            setPadding(8, 18, 8, 12)
        }
        root.addView(header)

        status = TextView(this).apply {
            text = "READY FOR BATTLE"
            gravity = Gravity.CENTER
            textSize = 13f
            setTextColor(Color.LTGRAY)
            setPadding(8, 4, 8, 8)
        }
        root.addView(status)

        game = BattleView(this)
        root.addView(game, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        controls = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 12)
        }
        root.addView(controls)

        val row1 = LinearLayout(this)
        addButton(row1, "▶ RANDOM") { startRandom() }
        addButton(row1, "🌍 WORLD CUP") { startWorldCup() }
        addButton(row1, "✚ CUSTOM") { showSelection() }
        controls.addView(row1)

        val row2 = LinearLayout(this)
        addButton(row2, "0.5×") { setSpeed(.5f) }
        addButton(row2, "1×") { setSpeed(1f) }
        addButton(row2, "2×") { setSpeed(2f) }
        addButton(row2, "4×") { setSpeed(4f) }
        controls.addView(row2)

        val row3 = LinearLayout(this)
        addButton(row3, "ARENA −") { arenaScale = (arenaScale - .1f).coerceAtLeast(.65f); game.invalidate() }
        addButton(row3, "ARENA +") { arenaScale = (arenaScale + .1f).coerceAtMost(1.15f); game.invalidate() }
        addButton(row3, "CONTINENTAL") { showContinental() }
        addButton(row3, "RESTART") { game.restart() }
        controls.addView(row3)

        setContentView(root)
    }

    private fun addButton(row: LinearLayout, text: String, action: () -> Unit) {
        val b = Button(this).apply {
            this.text = text
            textSize = 10f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(Color.rgb(38, 41, 52))
                cornerRadius = 18f
                setStroke(1, Color.rgb(70, 75, 90))
            }
            setOnClickListener { action() }
        }
        row.addView(b, LinearLayout.LayoutParams(0, 48, 1f).apply {
            setMargins(3, 3, 3, 3)
        })
    }

    private fun setSpeed(value: Float) {
        speed = value
        game.speed = value
        status.text = "SPEED ${value}×  •  $mode"
    }

    private fun startRandom() {
        mode = "Random Battle"
        selected.clear()
        selected.addAll(countries.shuffled().take(Random.nextInt(3, 11)))
        game.setCountries(selected)
        status.text = "RANDOM BATTLE  •  ${selected.size} COUNTRIES"
    }

    private fun startWorldCup() {
        mode = "World Cup"
        selected.clear()
        selected.addAll(countries.shuffled().take(32))
        game.setCountries(selected)
        status.text = "WORLD CUP  •  32 COUNTRIES"
    }

    private fun showContinental() {
        val options = arrayOf("Europe", "Asia", "Arab Countries")
        AlertDialog.Builder(this)
            .setTitle("CONTINENTAL")
            .setItems(options) { _, which ->
                mode = options[which]
                val list = when (which) {
                    0 -> countries.filter { it.code in listOf("DE","FR","IT","ES","GB","RU") }
                    1 -> countries.filter { it.code in listOf("CN","JP","IN","PK","IQ","SA","TR") }
                    else -> countries.filter { it.code in listOf("IQ","SA","EG","JO","PS","SY","LB","AE","QA","KW","BH","OM","YE","MA","TN","DZ","LY","SD") }
                }
                selected.clear()
                selected.addAll(list)
                game.setCountries(selected)
                status.text = "$mode  •  ${selected.size} COUNTRIES"
            }.show()
    }

    private fun showSelection() {
        val checked = BooleanArray(countries.size) { countries[it] in selected }
        AlertDialog.Builder(this)
            .setTitle("SELECT COUNTRIES")
            .setMultiChoiceItems(
                countries.map { "${it.name} (${it.code})" }.toTypedArray(),
                checked
            ) { _, which, isChecked -> checked[which] = isChecked }
            .setPositiveButton("START") { _, _ ->
                selected.clear()
                countries.forEachIndexed { i, c -> if (checked[i]) selected.add(c) }
                if (selected.size < 2) selected.addAll(countries.take(2))
                mode = "Custom"
                game.setCountries(selected.take(32))
                status.text = "CUSTOM BATTLE  •  ${selected.size} COUNTRIES"
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    internal fun ghostPanel() {
        val names = game.balls.map { it.country.name }.toTypedArray()
        if (names.isEmpty()) return

        val first = Spinner(this)
        val second = Spinner(this)
        val third = Spinner(this)
        listOf(first, second, third).forEach {
            it.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
        }

        val lock = Switch(this).apply {
            text = "LOCK RESULTS — DIRECTOR MODE"
            setTextColor(Color.WHITE)
        }

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            setBackgroundColor(Color.rgb(25, 27, 35))
        }

        panel.addView(TextView(this).apply {
            text = "⚡ WAR FOX CONTROL"
            textSize = 20f
            setTextColor(Color.rgb(255, 190, 60))
        })
        panel.addView(TextView(this).apply {
            text = "🥇 FIRST PLACE"
            setTextColor(Color.WHITE)
        })
        panel.addView(first)
        panel.addView(TextView(this).apply {
            text = "🥈 SECOND PLACE"
            setTextColor(Color.WHITE)
        })
        panel.addView(second)
        panel.addView(TextView(this).apply {
            text = "🥉 THIRD PLACE"
            setTextColor(Color.WHITE)
        })
        panel.addView(third)
        panel.addView(lock)

        AlertDialog.Builder(this)
            .setView(panel)
            .setPositiveButton("APPLY") { _, _ ->
                game.director = if (lock.isChecked) {
                    listOf(first.selectedItemPosition, second.selectedItemPosition, third.selectedItemPosition)
                } else null
                game.invalidate()
            }
            .setNegativeButton("CLOSE", null)
            .show()
    }

    override fun onDestroy() {
        game.release()
        super.onDestroy()
    }
}

data class Country(
    val name: String,
    val code: String,
    val top: Int,
    val middle: Int,
    val bottom: Int
)

data class Fighter(
    val country: Country,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float = 22f,
    var health: Float = 100f,
    var alive: Boolean = true,
    val trail: ArrayDeque<PointF> = ArrayDeque()
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val color: Int,
    val size: Float
)

data class Shockwave(var x: Float, var y: Float, var radius: Float, var life: Float)

class BattleView(context: Context) : View(context) {

    val balls = mutableListOf<Fighter>()
    var speed = 1f
    var director: List<Int>? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particles = mutableListOf<Particle>()
    private val shocks = mutableListOf<Shockwave>()
    private val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 35)

    private var arenaX = 0f
    private var arenaY = 0f
    private var arenaR = 0f
    private var lastTime = 0L
    private var shake = 0f
    private var winner: Fighter? = null
    private var victoryTime = 0f
    private var ghostHits = 0
    private var ghostStart = 0L
    private var ghostPanelShown = false

    private val cream = Color.rgb(250, 250, 250)
    private val black = Color.rgb(20, 20, 20)

    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        isFocusable = true
    }

    fun setCountries(list: List<Country>) {
        balls.clear()
        particles.clear()
        shocks.clear()
        winner = null
        director = null
        val count = list.size.coerceIn(2, 32)
        val r = min(width, height) * .34f
        list.take(count).forEachIndexed { i, country ->
            val angle = i * (PI * 2 / count)
            val distance = sqrt(Random.nextFloat()) * r
            balls.add(
                Fighter(
                    country,
                    arenaX + cos(angle).toFloat() * distance,
                    arenaY + sin(angle).toFloat() * distance,
                    Random.nextFloat() * 280f - 140f,
                    Random.nextFloat() * 280f - 140f
                )
            )
        }
        restart()
    }

    fun restart() {
        winner = null
        victoryTime = 0f
        balls.forEach {
            it.alive = true
            it.health = 100f
            it.radius = 22f
            it.vx = Random.nextFloat() * 300f - 150f
            it.vy = Random.nextFloat() * 300f - 150f
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val now = SystemClock.elapsedRealtime()
        val dt = if (lastTime == 0L) .016f else ((now - lastTime) / 1000f).coerceIn(0f, .04f)
        lastTime = now

        arenaX = width / 2f
        arenaY = height / 2f
        arenaR = min(width, height) * .44f * arenaScale

        canvas.drawColor(cream)

        if (winner == null) updatePhysics(dt * speed)

        val save = canvas.save()
        if (shake > .2f) {
            canvas.translate(
                Random.nextFloat() * shake - shake / 2,
                Random.nextFloat() * shake - shake / 2
            )
            shake *= .84f
        }

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawCircle(arenaX, arenaY, arenaR, paint)

        paint.color = black
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawCircle(arenaX, arenaY, arenaR, paint)
        paint.style = Paint.Style.FILL

        drawEffects(canvas)
        balls.filter { it.alive }.forEach { drawBall(canvas, it) }
        canvas.restoreToCount(save)

        if (winner != null) drawVictory(canvas, winner!!)

        if (balls.count { it.alive } <= 1 && winner == null) {
            winner = balls.firstOrNull { it.alive }
            victoryTime = 0f
        }

        if (winner == null || particles.isNotEmpty() || victoryTime < 6f) {
            postInvalidateOnAnimation()
        }
    }

    private fun updatePhysics(dt: Float) {
        val alive = balls.filter { it.alive }
        if (alive.size <= 1) return

        alive.forEach { b ->
            b.trail.addFirst(PointF(b.x, b.y))
            while (b.trail.size > 8) b.trail.removeLast()

            b.vx *= .999f
            b.vy *= .999f
            b.x += b.vx * dt
            b.y += b.vy * dt

            val dx = b.x - arenaX
            val dy = b.y - arenaY
            val dist = hypot(dx, dy)
            val limit = arenaR - b.radius

            if (dist > limit) {
                val nx = dx / dist
                val ny = dy / dist
                b.x = arenaX + nx * limit
                b.y = arenaY + ny * limit
                val dot = b.vx * nx + b.vy * ny
                b.vx -= 2f * dot * nx
                b.vy -= 2f * dot * ny
                b.vx *= .96f
                b.vy *= .96f
                shocks.add(Shockwave(b.x, b.y, 4f, 1f))
                tone.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            }
        }

        for (i in alive.indices) {
            for (j in i + 1 until alive.size) {
                collide(alive[i], alive[j])
            }
        }

        particles.forEach {
            it.x += it.vx * dt
            it.y += it.vy * dt
            it.vx *= .98f
            it.vy *= .98f
            it.life -= dt
        }
        particles.removeAll { it.life <= 0f }

        shocks.forEach {
            it.radius += 160f * dt
            it.life -= dt * 1.7f
        }
        shocks.removeAll { it.life <= 0f }

        director?.let { ranking ->
            if (ranking.isNotEmpty()) {
                val protected = ranking.take(3).mapNotNull { index -> balls.getOrNull(index) }
                protected.forEach { f ->
                    f.health = max(f.health, 35f)
                    f.radius = max(f.radius, 17f)
                }
            }
        }
    }

    private fun collide(a: Fighter, b: Fighter) {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val distance = hypot(dx, dy)
        val minDistance = a.radius + b.radius
        if (distance <= 0f || distance >= minDistance) return

        val nx = dx / distance
        val ny = dy / distance
        val overlap = minDistance - distance

        a.x -= nx * overlap / 2
        a.y -= ny * overlap / 2
        b.x += nx * overlap / 2
        b.y += ny * overlap / 2

        val relative = (b.vx - a.vx) * nx + (b.vy - a.vy) * ny
        if (relative >= 0f) return

        val impulse = -relative * .92f
        a.vx -= impulse * nx
        a.vy -= impulse * ny
        b.vx += impulse * nx
        b.vy += impulse * ny

        val damage = (abs(relative) * .055f).coerceIn(0f, 30f)
        a.health -= damage
        b.health -= damage

        shocks.add(Shockwave((a.x + b.x) / 2, (a.y + b.y) / 2, 5f, 1f))
        if (relative < -200f) {
            shake = max(shake, min(abs(relative) * .025f, 15f))
            tone.startTone(ToneGenerator.TONE_PROP_ACK, 55)
        }

        if (a.health <= 0f) eliminate(a)
        if (b.health <= 0f) eliminate(b)
    }

    private fun eliminate(f: Fighter) {
        if (!f.alive) return
        f.alive = false
        repeat(32) {
            val angle = Random.nextFloat() * PI.toFloat() * 2
            val velocity = Random.nextFloat() * 280f + 40f
            particles.add(
                Particle(
                    f.x, f.y,
                    cos(angle) * velocity,
                    sin(angle) * velocity,
                    Random.nextFloat() * 1.2f + .4f,
                    listOf(f.country.top, f.country.middle, f.country.bottom).random(),
                    Random.nextFloat() * 7f + 2f
                )
            )
        }
        shocks.add(Shockwave(f.x, f.y, 10f, 1.5f))
        shake = 18f
        tone.startTone(ToneGenerator.TONE_PROP_NACK, 180)
    }

    private fun drawBall(canvas: Canvas, f: Fighter) {
        val r = f.radius
        val save = canvas.save()
        canvas.translate(f.x, f.y)

        f.trail.forEachIndexed { index, p ->
            paint.color = Color.argb((20 - index * 2).coerceAtLeast(0), f.country.top, f.country.middle, f.country.bottom)
            paint.style = Paint.Style.FILL
            canvas.drawCircle(p.x - f.x, p.y - f.y, r * (1f - index / 10f), paint)
        }

        paint.shader = null
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, r, paint)

        val flagSave = canvas.save()
        val path = Path().apply { addCircle(0f, 0f, r - 1f, Path.Direction.CW) }
        canvas.clipPath(path)

        val rect = RectF(-r, -r, r, r)
        val stripe = rect.height() / 3
        paint.color = f.country.top
        canvas.drawRect(rect.left, rect.top, rect.right, rect.top + stripe, paint)
        paint.color = f.country.middle
        canvas.drawRect(rect.left, rect.top + stripe, rect.right, rect.top + stripe * 2, paint)
        paint.color = f.country.bottom
        canvas.drawRect(rect.left, rect.top + stripe * 2, rect.right, rect.bottom, paint)

        if (f.country.code == "JP") {
            paint.color = Color.rgb(188, 0, 45)
            canvas.drawCircle(0f, 0f, r * .35f, paint)
        } else if (f.country.code == "IQ") {
            paint.color = Color.GREEN
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = r * .45f
            canvas.drawText("الله", 0f, r * .15f, paint)
        } else if (f.country.code == "TR") {
            paint.color = Color.WHITE
            canvas.drawCircle(-r * .1f, 0f, r * .34f, paint)
            paint.color = f.country.top
            canvas.drawCircle(r * .02f, 0f, r * .27f, paint)
        }

        canvas.restoreToCount(flagSave)

        paint.color = Color.argb(180, 20, 20, 20)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawCircle(0f, 0f, r, paint)
        paint.style = Paint.Style.FILL

        paint.color = black
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = max(9f, r * .38f)
        paint.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        canvas.drawText(f.country.code, 0f, r + 13f, paint)

        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawCircle(0f, 0f, r * .78f, paint)
        paint.style = Paint.Style.FILL

        canvas.restoreToCount(save)
    }

    private fun drawEffects(canvas: Canvas) {
        shocks.forEach {
            paint.color = Color.argb((it.life * 150).toInt().coerceIn(0, 255), 255, 130, 30)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas.drawCircle(it.x, it.y, it.radius, paint)
        }
        paint.style = Paint.Style.FILL
        particles.forEach {
            paint.color = Color.argb((it.life * 255).toInt().coerceIn(0, 255), Color.red(it.color), Color.green(it.color), Color.blue(it.color))
            canvas.drawCircle(it.x, it.y, it.size, paint)
        }
    }

    private fun drawVictory(canvas: Canvas, f: Fighter) {
        victoryTime += .016f
        val alpha = (victoryTime * 180).toInt().coerceIn(0, 220)
        paint.color = Color.argb(alpha, 10, 10, 20)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        val scale = 1f + min(victoryTime, 2f) * .15f
        canvas.save()
        canvas.translate(width / 2f, height / 2f)
        canvas.scale(scale, scale)

        paint.color = f.country.middle
        canvas.drawCircle(0f, 0f, 65f, paint)
        paint.color = f.country.top
        canvas.drawRect(-60f, -45f, 60f, -15f, paint)
        paint.color = f.country.bottom
        canvas.drawRect(-60f, 15f, 60f, 45f, paint)

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        paint.textSize = 28f
        canvas.drawText("VICTORY", 0f, 105f, paint)
        paint.textSize = 18f
        canvas.drawText(f.country.name, 0f, 135f, paint)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val now = SystemClock.elapsedRealtime()
            if (event.x > width * .82f && event.y < height * .15f) {
                if (now - ghostStart > 3000) ghostHits = 0
                ghostStart = now
                ghostHits++
                if (ghostHits >= 5 && !ghostPanelShown) {
                    ghostHits = 0
                    ghostPanelShown = true
                    (context as? MainActivity)?.ghostPanel()
                }
                return true
            }
            if (winner != null) {
                restart()
                return true
            }
        }
        if (event.action == MotionEvent.ACTION_UP) ghostPanelShown = false
        return true
    }

    fun release() {
        tone.release()
    }
}
