package com.war.fox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

class MainActivity : Activity() {

    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 28) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        val bg = Color.parseColor("#ECE9E1")
        val root = FrameLayout(this)
        root.setBackgroundColor(bg)
        web = WebView(this)
        web.setBackgroundColor(bg)
        web.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val s = web.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.mediaPlaybackRequiresUserGesture = false
        s.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        s.allowFileAccess = false
        s.setSupportZoom(false)
        s.builtInZoomControls = false
        s.cacheMode = WebSettings.LOAD_DEFAULT
        web.overScrollMode = View.OVER_SCROLL_NEVER
        web.isVerticalScrollBarEnabled = false
        web.isHorizontalScrollBarEnabled = false
        web.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        web.webChromeClient = WebChromeClient()
        web.webViewClient = WebViewClient()
        root.addView(web)
        setContentView(root)
        hideSystemBars()
        web.loadDataWithBaseURL("https://warfox.app/", GameHtml.build(), "text/html", "UTF-8", null)
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        web.evaluateJavascript("(window.wfBack?window.wfBack():false)") { r ->
            if (r != "true") finish()
        }
    }

    override fun onPause() {
        web.evaluateJavascript("(window.wfPause?window.wfPause():0)", null)
        web.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        web.onResume()
        web.resumeTimers()
    }

    override fun onDestroy() {
        (web.parent as? ViewGroup)?.removeView(web)
        web.destroy()
        super.onDestroy()
    }
}

object GameHtml {

    private val HEAD = """<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no,viewport-fit=cover">
<title>WAR FOX</title>
<style>
*{box-sizing:border-box;-webkit-tap-highlight-color:transparent;user-select:none;-webkit-user-select:none;margin:0;padding:0}
html,body{width:100%;height:100%;overflow:hidden;background:#ECE9E1;font-family:sans-serif;color:#111;touch-action:manipulation}
.screen{position:fixed;left:0;top:0;right:0;bottom:0;display:flex;flex-direction:column;align-items:center;opacity:0;pointer-events:none;transform:scale(1.04);transition:opacity .35s ease,transform .35s ease;z-index:10;background:#ECE9E1}
.screen.active{opacity:1;pointer-events:auto;transform:none}
.logo{margin-top:12vh;font-size:clamp(46px,16vw,84px);font-weight:900;letter-spacing:4px;direction:ltr;line-height:1}
.logo b{display:inline-block;background:#111;color:#FAFAFA;padding:0 12px;border-radius:14px;margin-left:8px}
.sub{margin-top:10px;font-size:15px;color:#555;letter-spacing:1px}
.stack{margin-top:34px;width:min(86vw,380px);display:flex;flex-direction:column;gap:14px}
.btn{appearance:none;-webkit-appearance:none;border:2px solid #111;background:#111;color:#FAFAFA;font-size:18px;font-weight:800;padding:15px 18px;border-radius:16px;cursor:pointer;transition:transform .12s ease,background .2s ease,color .2s ease;font-family:inherit}
.btn:active{transform:scale(.95)}
.btn.alt{background:#FAFAFA;color:#111}
.btn.sm{font-size:14px;padding:9px 14px;border-radius:12px}
.btn[disabled]{opacity:.35;pointer-events:none}
.stepper{display:flex;align-items:center;justify-content:center;gap:16px;font-size:16px;font-weight:700;margin-top:2px}
.stepper button{width:40px;height:40px;border-radius:50%;border:2px solid #111;background:#FAFAFA;font-size:22px;font-weight:900;cursor:pointer;font-family:inherit}
.stepper span{min-width:34px;text-align:center}
.stepper #rndVal{font-size:22px;font-weight:900}
.top{width:100%;display:flex;align-items:center;justify-content:space-between;padding:calc(env(safe-area-inset-top,0px) + 14px) 14px 8px;gap:10px}
.top h2{font-size:20px;font-weight:900}
.list{width:min(90vw,420px);display:flex;flex-direction:column;gap:12px;overflow-y:auto;padding:8px 0 30px;-webkit-overflow-scrolling:touch;min-height:0}
#search{width:min(90vw,420px);padding:12px 14px;font-size:16px;border:2px solid #111;border-radius:12px;background:#FAFAFA;font-family:inherit;outline:none;user-select:text;-webkit-user-select:text}
#grid{width:100%;flex:1;min-height:0;overflow-y:auto;display:grid;grid-template-columns:repeat(auto-fill,minmax(86px,1fr));gap:10px;padding:12px 14px 14px;align-content:start;-webkit-overflow-scrolling:touch}
.cell{background:#FAFAFA;border:2px solid #d6d2c8;border-radius:14px;padding:8px 4px;display:flex;flex-direction:column;align-items:center;gap:6px;font-size:12px;font-weight:700;text-align:center;cursor:pointer;transition:transform .12s ease,border-color .15s ease}
.cell img{width:52px;height:52px;border-radius:50%;object-fit:cover;border:1px solid #111;background:#ddd}
.cell.on{border-color:#111;background:#fff;box-shadow:0 0 0 2px #111 inset;transform:scale(.97)}
.cell.on span::after{content:" ✓"}
.bar{width:100%;display:flex;gap:10px;justify-content:center;padding:10px 14px calc(env(safe-area-inset-bottom,0px) + 12px);background:#ECE9E1;border-top:2px solid #111}
#game{background:#ECE9E1}
#cv{position:absolute;left:0;top:0;width:100%;height:100%;display:block}
#alive{position:absolute;top:calc(env(safe-area-inset-top,0px) + 12px);left:14px;font-weight:900;font-size:18px;background:#FAFAFA;border:2px solid #111;border-radius:12px;padding:4px 12px}
#stage{position:absolute;top:calc(env(safe-area-inset-top,0px) + 16px);left:50%;transform:translateX(-50%);font-weight:900;font-size:15px;color:#222;white-space:nowrap}
#feed{position:absolute;top:calc(env(safe-area-inset-top,0px) + 58px);left:14px;display:flex;flex-direction:column;gap:4px;pointer-events:none;font-size:12px;font-weight:800}
.fi{background:rgba(17,17,17,.85);color:#fff;padding:3px 9px;border-radius:10px;animation:fi .3s ease}
@keyframes fi{from{opacity:0;transform:translateX(-14px)}to{opacity:1;transform:none}}
#hud{position:absolute;left:0;right:0;bottom:0;padding:26px 14px calc(env(safe-area-inset-bottom,0px) + 12px);display:flex;flex-direction:column;gap:10px;align-items:center;background:linear-gradient(to top,#ECE9E1 72%,rgba(236,233,225,0))}
.row{display:flex;gap:8px;align-items:center;justify-content:center;flex-wrap:wrap}
.spd{min-width:52px}
.btn.alt.spd.on{background:#111;color:#FAFAFA}
#hud label{font-size:13px;font-weight:800;display:flex;align-items:center;gap:8px}
#sizeR{width:120px;accent-color:#111}
#banner{position:absolute;left:50%;top:42%;transform:translate(-50%,-50%) scale(.9);font-size:clamp(24px,7.5vw,42px);font-weight:900;background:#111;color:#FAFAFA;padding:10px 22px;border-radius:16px;opacity:0;pointer-events:none;transition:opacity .3s ease,transform .3s ease;text-align:center;max-width:90vw}
#banner.show{opacity:1;transform:translate(-50%,-50%) scale(1)}
#victory{position:absolute;left:0;top:0;right:0;bottom:0;display:flex;align-items:center;justify-content:center;background:rgba(250,250,250,0);opacity:0;pointer-events:none;transition:opacity .6s ease,background .6s ease;z-index:40}
#victory.show{opacity:1;pointer-events:auto;background:rgba(250,250,250,.84)}
.vbox{display:flex;flex-direction:column;align-items:center;gap:12px;padding:20px;transform:scale(.8);transition:transform .6s cubic-bezier(.2,1.4,.4,1)}
#victory.show .vbox{transform:none}
.crown{font-size:44px}
#vImg{width:min(52vw,220px);height:min(52vw,220px);border-radius:50%;object-fit:cover;border:4px solid #111;box-shadow:0 12px 40px rgba(0,0,0,.35);background:#ddd}
#vName{font-size:clamp(28px,9vw,46px);font-weight:900;text-align:center}
#vPodium{display:flex;flex-direction:column;gap:6px;font-size:16px;font-weight:800;text-align:center}
.vbtns{display:flex;gap:10px;margin-top:8px}
#ghost{position:fixed;top:0;right:0;width:84px;height:84px;opacity:0;z-index:100;background:transparent}
#gp{position:fixed;top:calc(env(safe-area-inset-top,0px) + 64px);left:50%;transform:translateX(-50%) translateY(-14px) scale(.96);width:min(92vw,360px);background:rgba(14,14,20,.97);color:#EDEDF2;border:1px solid #34343f;border-radius:18px;box-shadow:0 20px 60px rgba(0,0,0,.6),0 0 0 1px rgba(255,215,0,.12);padding:14px;z-index:90;opacity:0;pointer-events:none;transition:opacity .25s ease,transform .25s ease}
#gp.show{opacity:1;pointer-events:auto;transform:translateX(-50%)}
#gp .gh{display:flex;justify-content:space-between;align-items:center;font-weight:900;font-size:15px;letter-spacing:.5px;padding-bottom:10px;border-bottom:1px solid #2a2a35;margin-bottom:10px;cursor:grab;touch-action:none}
#gp .gr{display:flex;align-items:center;gap:10px;margin:8px 0;font-size:14px;font-weight:700}
#gp .gr i{font-style:normal;font-size:22px;width:28px;text-align:center}
#gp select{flex:1;background:#1b1b25;color:#EDEDF2;border:1px solid #3a3a48;border-radius:10px;padding:9px;font-size:14px;font-family:inherit;outline:none}
#gp .tg{width:100%;margin-top:10px;padding:12px;border-radius:12px;border:1px solid #3a3a48;background:#1b1b25;color:#aab;font-weight:900;font-size:15px;font-family:inherit;cursor:pointer;transition:all .2s ease}
#gp .tg.on{background:linear-gradient(135deg,#d4a017,#f5d76e);color:#16161d;border-color:#f5d76e;box-shadow:0 0 18px rgba(245,215,110,.35)}
#gp .gx{background:none;border:none;color:#aab;font-size:20px;cursor:pointer}
#gstat{margin-top:8px;font-size:12px;color:#8a8a99;text-align:center;min-height:16px}
</style>
</head>
<body>
"""

    private val BODY = """<div id="menu" class="screen active">
  <div class="logo">WAR<b>FOX</b></div>
  <div class="sub">معركة أعلام الدول</div>
  <div class="stack">
    <button class="btn" id="bRandom">معركة عشوائية</button>
    <div class="stepper"><span>عدد المقاتلين</span><button id="rndMinus">−</button><span id="rndVal">6</span><button id="rndPlus">+</button></div>
    <button class="btn" id="bWorld">كأس العالم (32 دولة)</button>
    <button class="btn" id="bCont">بطولة قارّية</button>
    <button class="btn alt" id="bCustom">وضع مخصص</button>
  </div>
</div>
<div id="cont" class="screen">
  <div class="top"><button class="btn alt sm" id="contBack">رجوع</button><h2>اختر البطولة القارّية</h2><span></span></div>
  <div class="list" id="contList"></div>
</div>
<div id="select" class="screen">
  <div class="top"><button class="btn alt sm" id="selBack">رجوع</button><h2>اختر المقاتلين</h2><span id="selCount">0</span></div>
  <input id="search" type="text" placeholder="ابحث عن دولة...">
  <div id="grid"></div>
  <div class="bar">
    <button class="btn alt sm" id="selRand">عشوائي</button>
    <button class="btn alt sm" id="selClear">مسح</button>
    <button class="btn sm" id="selStart">ابدأ المعركة</button>
  </div>
</div>
<div id="game" class="screen">
  <canvas id="cv"></canvas>
  <div id="alive">0</div>
  <div id="stage"></div>
  <div id="feed"></div>
  <div id="banner"></div>
  <div id="hud">
    <div class="row" id="spdRow">
      <button class="btn alt sm spd" data-s="0.5">0.5x</button>
      <button class="btn alt sm spd on" data-s="1">1x</button>
      <button class="btn alt sm spd" data-s="2">2x</button>
      <button class="btn alt sm spd" data-s="4">4x</button>
    </div>
    <div class="row">
      <label>حجم الساحة<input type="range" id="sizeR" min="55" max="100" value="92"></label>
      <button class="btn alt sm" id="bPause">إيقاف</button>
      <button class="btn sm" id="bExit">خروج</button>
    </div>
  </div>
  <div id="victory">
    <div class="vbox">
      <div class="crown">🏆</div>
      <img id="vImg" alt="">
      <div id="vName"></div>
      <div id="vPodium"></div>
      <div class="vbtns">
        <button class="btn" id="vAgain">العب مجدداً</button>
        <button class="btn alt" id="vMenu">القائمة</button>
      </div>
    </div>
  </div>
</div>
<div id="ghost"></div>
<div id="gp">
  <div class="gh" id="gHead"><span>⚙ لوحة التحكم الخفية</span><button class="gx" id="gClose">✕</button></div>
  <div class="gr"><i>🥇</i><select id="g1"></select></div>
  <div class="gr"><i>🥈</i><select id="g2"></select></div>
  <div class="gr"><i>🥉</i><select id="g3"></select></div>
  <button class="tg" id="gLock">قفل النتائج: متوقف</button>
  <div id="gstat"></div>
</div>
    <div class="row">
      <label>حجم الساحة<input type="range" id="sizeR" min="55" max="100" value="92"></label>
      <button class="btn alt sm" id="bPause">إيقاف</button>
      <button class="btn sm" id="bExit">خروج</button>
    </div>
  </div>
  <div id="victory">
    <div class="vbox">
      <div class="crown">🏆</div>
      <img id="vImg" alt="">
      <div id="vName"></div>
      <div id="vPodium"></div>
      <div class="vbtns">
        <button class="btn" id="vAgain">العب مجدداً</button>
        <button class="btn alt" id="vMenu">القائمة</button>
      </div>
    </div>
  </div>
</div>
<div id="ghost"></div>
<div id="gp">
  <div class="gh" id="gHead"><span>⚙ لوحة التحكم الخفية</span><button class="gx" id="gClose">✕</button></div>
  <div class="gr"><i>🥇</i><select id="g1"></select></div>
  <div class="gr"><i>🥈</i><select id="g2"></select></div>
  <div class="gr"><i>🥉</i><select id="g3"></select></div>
  <button class="tg" id="gLock">قفل النتائج: متوقف</button>
  <div id="gstat"></div>
</div>
"""

    private val JS1 = """
function G(id){return document.getElementById(id)}
function rnd(a,b){return a+Math.random()*(b-a)}
function clamp(v,a,b){return v<a?a:(v>b?b:v)}
function shuffle(a){a=a.slice();for(var i=a.length-1;i>0;i--){var j=Math.floor(Math.random()*(i+1));var t=a[i];a[i]=a[j];a[j]=t}return a}
function pickN(a,n){return shuffle(a).slice(0,n)}
function go(id){var s=document.querySelectorAll('.screen');for(var i=0;i<s.length;i++)s[i].classList.toggle('active',s[i].id===id)}
function furl(c,w){return 'https://flagcdn.com/w'+w+'/'+c+'.png'}

var RAW=[
'fr|فرنسا|eu','de|ألمانيا|eu','es|إسبانيا|eu','it|إيطاليا|eu','gb|بريطانيا|eu','pt|البرتغال|eu','nl|هولندا|eu','be|بلجيكا|eu',
'hr|كرواتيا|eu','ch|سويسرا|eu','at|النمسا|eu','dk|الدنمارك|eu','se|السويد|eu','no|النرويج|eu','pl|بولندا|eu','cz|التشيك|eu',
'rs|صربيا|eu','gr|اليونان|eu','ua|أوكرانيا|eu','ie|أيرلندا|eu','fi|فنلندا|eu','hu|المجر|eu','ro|رومانيا|eu','sk|سلوفاكيا|eu',
'bg|بلغاريا|eu','is|آيسلندا|eu','al|ألبانيا|eu','ru|روسيا|eu',
'tr|تركيا|as','jp|اليابان|as','kr|كوريا الجنوبية|as','cn|الصين|as','in|الهند|as','ir|إيران|as','th|تايلاند|as','vn|فيتنام|as',
'id|إندونيسيا|as','my|ماليزيا|as','ph|الفلبين|as','pk|باكستان|as','bd|بنغلاديش|as','kz|كازاخستان|as','uz|أوزبكستان|as',
'af|أفغانستان|as','np|نيبال|as','sg|سنغافورة|as','sa|السعودية|as','ae|الإمارات|as','qa|قطر|as','kw|الكويت|as','bh|البحرين|as',
'om|عُمان|as','iq|العراق|as','jo|الأردن|as','lb|لبنان|as','sy|سوريا|as','ye|اليمن|as','ps|فلسطين|as',
'eg|مصر|af','ma|المغرب|af','dz|الجزائر|af','tn|تونس|af','ly|ليبيا|af','sd|السودان|af','sn|السنغال|af','ng|نيجيريا|af',
'gh|غانا|af','cm|الكاميرون|af','ci|ساحل العاج|af','za|جنوب أفريقيا|af','ke|كينيا|af','et|إثيوبيا|af','ml|مالي|af',
'tz|تنزانيا|af','ao|أنغولا|af','cd|الكونغو الديمقراطية|af','mr|موريتانيا|af','so|الصومال|af',
'us|أمريكا|nam','ca|كندا|nam','mx|المكسيك|nam','cu|كوبا|nam','jm|جامايكا|nam','cr|كوستاريكا|nam','pa|بنما|nam','hn|هندوراس|nam',
'br|البرازيل|sam','ar|الأرجنتين|sam','uy|الأوروغواي|sam','co|كولومبيا|sam','cl|تشيلي|sam','pe|بيرو|sam','ec|الإكوادور|sam',
'py|باراغواي|sam','ve|فنزويلا|sam','bo|بوليفيا|sam','au|أستراليا|oc','nz|نيوزيلندا|oc'
];
var ARAB=' dz bh eg iq jo kw lb ly ma om qa sa sd sy tn ae ye ps mr so ';
var COUNTRIES=[],CMAP={};
for(var ci=0;ci<RAW.length;ci++){
  var cp=RAW[ci].split('|');
  var co0={c:cp[0],n:cp[1],k:cp[2],a:ARAB.indexOf(' '+cp[0]+' ')>=0};
  COUNTRIES.push(co0);CMAP[co0.c]=co0;
}
function cname(c){return CMAP[c]?CMAP[c].n:c}

var CONTS=[
 {id:'euas',n:'أوروبا ضد آسيا'},{id:'arab',n:'الدول العربية'},{id:'eu',n:'أوروبا'},
 {id:'as',n:'آسيا'},{id:'af',n:'أفريقيا'},{id:'am',n:'الأمريكتان'},{id:'all',n:'قارات العالم'}
];
function contList(id){
  var L=COUNTRIES,f=function(k){return L.filter(function(x){return x.k===k})};
  if(id==='euas')return pickN(f('eu'),8).concat(pickN(f('as'),8));
  if(id==='arab')return pickN(L.filter(function(x){return x.a}),16);
  if(id==='am')return pickN(L.filter(function(x){return x.k==='nam'||x.k==='sam'}),16);
  if(id==='all')return pickN(L,16);
  return pickN(f(id),16);
}

var ST={speed:1,paused:false,scale:.92,rnd:6};
var GH={on:false,r:['','','']};

var bt=0;
function banner(t,ms){
  var b=G('banner');b.textContent=t;b.classList.add('show');clearTimeout(bt);
  bt=setTimeout(function(){b.classList.remove('show')},ms||1400);
}
function feed(t){
  var f=G('feed'),d=document.createElement('div');d.className='fi';d.textContent=t;f.appendChild(d);
  while(f.children.length>4)f.removeChild(f.firstChild);
  setTimeout(function(){if(d.parentNode)d.parentNode.removeChild(d)},3800);
}

var FL={};
function ph(c){
  var cv=document.createElement('canvas');cv.width=cv.height=256;var x=cv.getContext('2d'),h=0;
  for(var i=0;i<c.length;i++)h=(h*31+c.charCodeAt(i))%360;
  x.fillStyle='hsl('+h+',60%,55%)';x.beginPath();x.arc(128,128,128,0,6.2832);x.fill();
  x.fillStyle='#fff';x.font='bold 100px sans-serif';x.textAlign='center';x.textBaseline='middle';
  x.fillText(c.toUpperCase(),128,136);
  return cv;
}
function mk(img){
  var cv=document.createElement('canvas');cv.width=cv.height=256;var x=cv.getContext('2d');
  var w=img.naturalWidth||300,h=img.naturalHeight||200;
  var s=Math.max(256/w,256/h),dw=w*s,dh=h*s;
  x.drawImage(img,(256-dw)/2,(256-dh)/2,dw,dh);
  x.globalCompositeOperation='destination-in';
  x.beginPath();x.arc(128,128,128,0,6.2832);x.fill();
  return cv;
}
function flag(c){
  if(FL[c])return FL[c];
  var f={c:c,sp:ph(c),ok:false};
  FL[c]=f;
  function fb(){
    if(f.tried)return;f.tried=1;
    var i2=new Image();
    i2.onload=function(){try{f.sp=mk(i2);f.ok=true}catch(e){}};
    i2.src=furl(c,320);
  }
  var im=new Image();
  im.onload=function(){try{f.sp=mk(im);f.ok=true}catch(e){fb()}};
  im.onerror=function(){fb()};
  im.src='https://flagcdn.com/'+c+'.svg';
  return f;
}

var AC=null,master=null,NB=null,lastClack=0;
function aInit(){
  if(!AC){
    try{
      AC=new (window.AudioContext||window.webkitAudioContext)();
      master=AC.createGain();master.gain.value=.7;master.connect(AC.destination);
    }catch(e){AC=null}
  }
  if(AC&&AC.state==='suspended')AC.resume();
}
document.addEventListener('pointerdown',aInit,{passive:true});
document.addEventListener('touchstart',aInit,{passive:true});
function nbuf(){
  if(NB)return NB;
  var n=AC.sampleRate;NB=AC.createBuffer(1,n,AC.sampleRate);
  var d=NB.getChannelData(0);for(var i=0;i<n;i++)d[i]=Math.random()*2-1;
  return NB;
}
function tone(f,t,d,type,g,fe){
  var o=AC.createOscillator(),v=AC.createGain(),s=AC.currentTime+t;
  o.type=type||'sine';o.frequency.setValueAtTime(f,s);
  if(fe)o.frequency.exponentialRampToValueAtTime(fe,s+d);
  v.gain.setValueAtTime(0.0001,s);v.gain.exponentialRampToValueAtTime(g,s+0.008);
  v.gain.exponentialRampToValueAtTime(0.0001,s+d);
  o.connect(v);v.connect(master);o.start(s);o.stop(s+d+0.03);
}
function noise(t,d,g,ft,fq,sw){
  var s=AC.currentTime+t,src=AC.createBufferSource(),fl=AC.createBiquadFilter(),v=AC.createGain();
  src.buffer=nbuf();src.loop=true;fl.type=ft;fl.frequency.setValueAtTime(fq,s);
  if(sw)fl.frequency.exponentialRampToValueAtTime(sw,s+d);
  v.gain.setValueAtTime(0.0001,s);v.gain.exponentialRampToValueAtTime(g,s+Math.min(.02,d/3));
  v.gain.exponentialRampToValueAtTime(0.0001,s+d);
  src.connect(fl);fl.connect(v);v.connect(master);src.start(s);src.stop(s+d+.05);
}
function sClack(p){
  if(!AC)return;
  var n=performance.now();if(n-lastClack<35)return;lastClack=n;
  var g=Math.min(.5,.12+p*.4),f=700+Math.random()*500+p*600;
  tone(f,0,.07,'triangle',g,f*.5);tone(f*2.3,0,.04,'square',g*.3,f);noise(0,.03,g*.6,'highpass',2500);
}
function sShatter(){
  if(!AC)return;
  noise(0,.55,.5,'highpass',1800,5000);noise(0,.32,.4,'lowpass',900,150);tone(900,0,.35,'sawtooth',.16,120);
  for(var i=0;i<6;i++)tone(1800+Math.random()*3000,i*.035,.08,'triangle',.1,1000);
}
function sVictory(){
  if(!AC)return;
  var ch=[[261.6,329.6,392],[293.7,349.2,440],[329.6,392,493.9],[392,493.9,587.3],[523.3,659.3,784]];
  for(var i=0;i<ch.length;i++){
    var t=i*.28,d=(i===4)?2.4:.5;
    for(var j=0;j<3;j++){tone(ch[i][j],t,d,'sawtooth',.06);tone(ch[i][j]/2,t,d+.1,'triangle',.1)}
  }
  tone(1046.5,1.4,1.7,'sine',.12);
  for(var k=0;k<48;k++)noise(1.2+k*.07,.07,.08+Math.random()*.1,'bandpass',1500+Math.random()*2500);
  noise(1.2,3.4,.16,'bandpass',1200,2000);
}

var parts=[],rings=[],shake=0;
function addRing(x,y,r,pw,col){
  rings.push({x:x,y:y,r:r*.6,mr:r*(1.6+pw*1.2),a:.5+pw*.3,col:col||'#111',w:2+pw*3});
}
function spark(x,y,n,pw){
  for(var i=0;i<n&&parts.length<700;i++){
    var a=Math.random()*6.283,s=80+Math.random()*260*pw;
    parts.push({k:0,x:x,y:y,vx:Math.cos(a)*s,vy:Math.sin(a)*s,s:1.5+Math.random()*2.5,life:1,dec:2.2+Math.random()*1.6,col:Math.random()<.5?'#111':'#e0a800'});
  }
}
function burst(b){
  var i,a,s;
  for(i=0;i<26;i++){
    a=Math.random()*6.283;s=120+Math.random()*420;
    parts.push({k:1,x:b.x,y:b.y,vx:Math.cos(a)*s+b.vx*.3,vy:Math.sin(a)*s+b.vy*.3,s:b.r*(.16+Math.random()*.26),life:1,dec:.5+Math.random()*.6,rot:Math.random()*6,vr:(Math.random()-.5)*14,sp:b.f.sp,sx:40+Math.random()*88,sy:40+Math.random()*88});
  }
  spark(b.x,b.y,22,2);
  addRing(b.x,b.y,b.r*2.2,1,'#111');addRing(b.x,b.y,b.r*1.4,.6,'#e0a800');
  shake=Math.min(26,shake+14);
}
function stepFx(dt){
  var i,p,g,d=Math.pow(.25,dt);
  for(i=parts.length-1;i>=0;i--){
    p=parts[i];p.x+=p.vx*dt;p.y+=p.vy*dt;p.vx*=d;p.vy*=d;
    if(p.k)p.rot+=p.vr*dt;
    p.life-=p.dec*dt;if(p.life<=0)parts.splice(i,1);
  }
  for(i=rings.length-1;i>=0;i--){
    g=rings[i];g.r+=(g.mr-g.r)*Math.min(1,dt*7);g.a-=dt*1.5;if(g.a<=0)rings.splice(i,1);
  }
  shake*=Math.pow(.003,dt);if(shake<.05)shake=0;
}
function drawFx(c){
  var i,p,g;
  for(i=0;i<rings.length;i++){
    g=rings[i];c.globalAlpha=Math.max(0,g.a);c.strokeStyle=g.col;c.lineWidth=g.w;
    c.beginPath();c.arc(g.x,g.y,g.r,0,6.2832);c.stroke();
  }
  for(i=0;i<parts.length;i++){
    p=parts[i];c.globalAlpha=Math.max(0,Math.min(1,p.life));
    if(p.k){
      c.save();c.translate(p.x,p.y);c.rotate(p.rot);
      c.drawImage(p.sp,p.sx,p.sy,64,64,-p.s,-p.s,p.s*2,p.s*2);c.restore();
    }else{
      c.fillStyle=p.col;c.beginPath();c.arc(p.x,p.y,p.s,0,6.2832);c.fill();
    }
  }
  c.globalAlpha=1;
}

function Ball(co,x,y,r,gr){
  var a=Math.random()*6.283,sp=140+Math.random()*120;
  this.co=co;this.f=flag(co.c);this.x=x;this.y=y;this.r=r;
  this.vx=Math.cos(a)*sp;this.vy=Math.sin(a)*sp;
  this.m=r*r;this.mm=1;this.hp=100;this.max=100;this.gr=gr||0;
  this.trail=[];this.flash=0;this.dead=false;this.rot=Math.random()*6.28;this.w=0;
}
Ball.prototype.pushTrail=function(){
  this.trail.push({x:this.x,y:this.y});
  if(this.trail.length>7)this.trail.shift();
};
Ball.prototype.draw=function(c,names){
  var k=.8+.2*clamp(this.hp/this.max,0,1),R=this.r*k,i,t,g;
  if(this.trail.length>1&&Math.hypot(this.vx,this.vy)>240){
    for(i=0;i<this.trail.length;i++){
      t=this.trail[i];c.globalAlpha=(i+1)/this.trail.length*.16;
      c.drawImage(this.f.sp,t.x-R,t.y-R,R*2,R*2);
    }
    c.globalAlpha=1;
  }
  c.save();c.translate(this.x,this.y);c.rotate(this.rot);
  c.drawImage(this.f.sp,-R,-R,R*2,R*2);c.restore();
  g=c.createRadialGradient(this.x-R*.35,this.y-R*.4,R*.1,this.x,this.y,R);
  g.addColorStop(0,'rgba(255,255,255,.38)');g.addColorStop(.55,'rgba(255,255,255,0)');g.addColorStop(1,'rgba(0,0,0,.22)');
  c.fillStyle=g;c.beginPath();c.arc(this.x,this.y,R,0,6.2832);c.fill();
  if(this.flash>0){
    c.fillStyle='rgba(255,255,255,'+Math.min(.85,this.flash)+')';
    c.beginPath();c.arc(this.x,this.y,R,0,6.2832);c.fill();
  }
  c.lineWidth=Math.max(1.5,R*.07);c.strokeStyle='#111';
  c.beginPath();c.arc(this.x,this.y,R,0,6.2832);c.stroke();
  if(this.hp<this.max-.5){
    c.strokeStyle='hsl('+Math.round(clamp(this.hp,0,100)*1.2)+',75%,42%)';c.lineWidth=3;
    c.beginPath();c.arc(this.x,this.y,R+5,-1.5708,-1.5708+6.2832*clamp(this.hp/this.max,0,1));c.stroke();
  }
  if(names){
    c.font='bold '+Math.round(clamp(R*.5,10,15))+'px sans-serif';c.textAlign='center';
    c.lineWidth=3;c.strokeStyle='#FAFAFA';c.fillStyle='#111';
    c.strokeText(this.co.n,this.x,this.y+R+18);c.fillText(this.co.n,this.x,this.y+R+18);
  }
};
  c.save();c.translate(this.x,this.y);c.rotate(this.rot);
  c.drawImage(this.f.sp,-R,-R,R*2,R*2);c.restore();
  g=c.createRadialGradient(this.x-R*.35,this.y-R*.4,R*.1,this.x,this.y,R);
  g.addColorStop(0,'rgba(255,255,255,.38)');g.addColorStop(.55,'rgba(255,255,255,0)');g.addColorStop(1,'rgba(0,0,0,.22)');
  c.fillStyle=g;c.beginPath();c.arc(this.x,this.y,R,0,6.2832);c.fill();
  if(this.flash>0){
    c.fillStyle='rgba(255,255,255,'+Math.min(.85,this.flash)+')';
    c.beginPath();c.arc(this.x,this.y,R,0,6.2832);c.fill();
  }
  c.lineWidth=Math.max(1.5,R*.07);c.strokeStyle='#111';
  c.beginPath();c.arc(this.x,this.y,R,0,6.2832);c.stroke();
  if(this.hp<this.max-.5){
    c.strokeStyle='hsl('+Math.round(clamp(this.hp,0,100)*1.2)+',75%,42%)';c.lineWidth=3;
    c.beginPath();c.arc(this.x,this.y,R+5,-1.5708,-1.5708+6.2832*clamp(this.hp/this.max,0,1));c.stroke();
  }
  if(names){
    c.font='bold '+Math.round(clamp(R*.5,10,15))+'px sans-serif';c.textAlign='center';
    c.lineWidth=3;c.strokeStyle='#FAFAFA';c.fillStyle='#111';
    c.strokeText(this.co.n,this.x,this.y+R+18);c.fillText(this.co.n,this.x,this.y+R+18);
  }
};
"""

    private val JS2 = """
var cv=G('cv'),ctx=cv.getContext('2d'),DPR=1,A={x:0,y:0,R:100,W:0,H:0,base:100};
var RK={},GM={on:false},LAST=null;
function layout(){
  DPR=Math.min(2,window.devicePixelRatio||1);
  var w=window.innerWidth,h=window.innerHeight;
  cv.width=Math.round(w*DPR);cv.height=Math.round(h*DPR);
  A.W=w;A.H=h;A.base=Math.min(w*.96,h*.74)/2;A.x=w/2;A.y=h*.43;
  setArena();
}
function setArena(){A.R=A.base*ST.scale}
function pr(b){return b.gr>0?b.gr:9}

function setupRanks(list){
  RK={};
  if(!GH.on)return;
  var s=GH.r.slice(),mx=0,i,used={},pool;
  for(i=0;i<3;i++)if(s[i]){mx=i+1;used[s[i]]=1}
  if(!mx)return;
  pool=shuffle(list.filter(function(x){return !used[x.c]}));
  for(i=0;i<mx;i++){if(!s[i]&&pool.length)s[i]=pool.pop().c}
  for(i=0;i<mx;i++){if(s[i]&&!RK[s[i]])RK[s[i]]=i+1}
}
function ensureForced(list,cap){
  if(!GH.on)return list;
  var codes=GH.r.filter(function(c){return c}),have={},isF={},i,j,c;
  list.forEach(function(x){have[x.c]=1});
  codes.forEach(function(x){isF[x]=1});
  for(i=0;i<codes.length;i++){
    c=codes[i];
    if(!have[c]&&CMAP[c]){
      if(list.length<cap){list.push(CMAP[c])}
      else{for(j=0;j<list.length;j++){if(!isF[list[j].c]){list[j]=CMAP[c];break}}}
      have[c]=1;
    }
  }
  return list;
}

function Match(list,opt){
  var n=list.length,R=A.R,r=clamp(R*Math.sqrt(.21/n),R*.07,R*.19),i,k,t,b,o,x,y,a,d,ok,sp,an;
  this.balls=[];this.out=[];this.time=0;this.stopAt=opt.stopAt||1;this.ending=false;this.freeze=1;
  for(i=0;i<n;i++){
    ok=false;x=A.x;y=A.y;
    for(t=0;t<300&&!ok;t++){
      a=Math.random()*6.2832;d=Math.sqrt(Math.random())*(R-r*1.3);
      x=A.x+Math.cos(a)*d;y=A.y+Math.sin(a)*d;ok=true;
      for(k=0;k<this.balls.length;k++){
        o=this.balls[k];
        if(Math.hypot(o.x-x,o.y-y)<o.r+r+4){ok=false;break}
      }
    }
    b=new Ball(list[i],x,y,r,RK[list[i].c]||0);
    b.rf=r/R;b.m=b.rf*b.rf*1000;b.mm=b.gr>0?1+.12*(4-b.gr):1;
    sp=R*rnd(.9,1.4);an=Math.random()*6.2832;
    b.vx=Math.cos(an)*sp;b.vy=Math.sin(an)*sp;
    this.balls.push(b);
  }
  this.forced=this.balls.some(function(q){return q.gr>0});
}
Match.prototype.living=function(){return this.balls.filter(function(b){return !b.dead})};
Match.prototype.alive=function(){var c=0,i;for(i=0;i<this.balls.length;i++){if(!this.balls[i].dead)c++}return c};
Match.prototype.gf=function(v,a){
  var pv=pr(v),pa=pr(a);
  if(pv<pa)return .3;
  if(pv>pa)return 1.4;
  return 1;
};
Match.prototype.kill=function(v){
  if(v.dead)return;
  v.dead=true;v.hp=0;this.out.push(v);
  burst(v);sShatter();feed('❌ '+v.co.n);
  if(!this.ending&&this.alive()<=this.stopAt)this.ending=true;
};
Match.prototype.dmg=function(v,d){
  if(v.dead)return;
  var i,o,pv,w=false;
  v.hp-=d;
  if(this.forced&&pr(v)<9){
    pv=pr(v);
    for(i=0;i<this.balls.length;i++){
      o=this.balls[i];
      if(!o.dead&&o!==v&&pr(o)>pv){w=true;break}
    }
    if(w&&v.hp<18)v.hp=18;
  }
  if(v.hp<=.01){
    if(this.ending){v.hp=1;return}
    this.kill(v);
  }
};
Match.prototype.hit=function(v,a,dmg){
  if(v.dead)return;
  if(this.forced)dmg*=this.gf(v,a);
  v.flash=.8;
  this.dmg(v,dmg);
};
Match.prototype.steer=function(lv,dt){
  if(!this.forced||lv.length<2)return;
  var i,j,b,o,t,bd,d,pb,po,want,acc=A.R*1.1*dt;
  for(i=0;i<lv.length;i++){
    b=lv[i];t=null;bd=1e9;pb=pr(b);
    for(j=0;j<lv.length;j++){
      if(j===i)continue;
      o=lv[j];po=pr(o);
      want=pb<9?(po>pb):(po===9);
      if(!want)continue;
      d=Math.hypot(o.x-b.x,o.y-b.y);
      if(d<bd){bd=d;t=o}
    }
    if(t&&bd>b.r+t.r){
      b.vx+=(t.x-b.x)/bd*acc;b.vy+=(t.y-b.y)/bd*acc;
    }
  }
};
Match.prototype.chip=function(lv,dt){
  var i,b,k=this.time<32?0:(this.time-32)*.12+.6;
  for(i=0;i<lv.length&&!this.ending;i++){
    b=lv[i];
    if(this.forced&&pr(b)<9&&b.hp<70)b.hp=Math.min(70,b.hp+dt*1.2);
    if(k>0)this.dmg(b,k*dt*(pr(b)<9?.4:1));
  }
};
Match.prototype.coll=function(a,b){
  if(a.dead||b.dead)return;
  var dx=b.x-a.x,dy=b.y-a.y,rs=a.r+b.r,d2=dx*dx+dy*dy;
  if(d2>=rs*rs)return;
  if(d2<.0001){dx=.01;dy=0;d2=.0001}
  var d=Math.sqrt(d2),nx=dx/d,ny=dy/d,ma=a.m*a.mm,mb=b.m*b.mm,tm=ma+mb,ov=rs-d;
  a.x-=nx*ov*(mb/tm);a.y-=ny*ov*(mb/tm);b.x+=nx*ov*(ma/tm);b.y+=ny*ov*(ma/tm);
  var vn=(a.vx-b.vx)*nx+(a.vy-b.vy)*ny;
  if(vn<=0)return;
  var jj=1.96*vn/(1/ma+1/mb);
  a.vx-=jj*nx/ma;a.vy-=jj*ny/ma;b.vx+=jj*nx/mb;b.vy+=jj*ny/mb;
  var pw=clamp(vn/(A.R*2.2),0,1.6),px=a.x+nx*a.r,py=a.y+ny*a.r;
  sClack(pw);
  if(pw>.12){spark(px,py,Math.round(3+pw*8),pw);addRing(px,py,Math.min(a.r,b.r),pw)}
  if(pw>.7)shake=Math.min(24,shake+pw*7);
  if(!this.ending&&vn>A.R*.18){
    var raw=3+50*pw*pw;
    this.hit(a,b,raw*2*mb/tm);
    this.hit(b,a,raw*2*ma/tm);
  }
};
Match.prototype.step=function(dt){
  var bs=this.balls,i,j,b,sp,dx,dy,d,lim,nx,ny,vn,dr,gk,R=A.R,vmax=R*4.2,vmin=R*.55,lv;
  for(i=0;i<bs.length;i++){
    b=bs[i];
    if(!b.dead){b.r=b.rf*R;b.flash=Math.max(0,b.flash-dt*3.5)}
  }
  if(this.freeze>0){this.freeze-=dt;return}
  this.time+=dt;
  lv=this.living();
  if(!this.ending){this.steer(lv,dt);this.chip(lv,dt)}
  for(i=0;i<lv.length;i++){
    b=lv[i];
    dr=Math.pow(.8,dt);b.vx*=dr;b.vy*=dr;
    sp=Math.hypot(b.vx,b.vy);
    if(sp<vmin&&!this.ending){
      if(sp<1){b.vx=rnd(-1,1)*vmin;b.vy=rnd(-1,1)*vmin}
      else{gk=1+dt*1.6;b.vx*=gk;b.vy*=gk}
    }
    if(sp>vmax){b.vx*=vmax/sp;b.vy*=vmax/sp}
    b.x+=b.vx*dt;b.y+=b.vy*dt;
    b.rot+=b.vx*dt/(b.r*6);
    dx=b.x-A.x;dy=b.y-A.y;d=Math.hypot(dx,dy);lim=R-b.r;
    if(d>lim&&d>0){
      nx=dx/d;ny=dy/d;
      b.x=A.x+nx*lim;b.y=A.y+ny*lim;
      vn=b.vx*nx+b.vy*ny;
      if(vn>0){
        b.vx-=1.9*vn*nx;b.vy-=1.9*vn*ny;
        if(vn>R*.6){sClack(Math.min(1,vn/(R*2))*.5);spark(b.x+nx*b.r,b.y+ny*b.r,3,.4)}
      }
    }
  }
  for(i=0;i<lv.length;i++){
    for(j=i+1;j<lv.length;j++)this.coll(lv[i],lv[j]);
  }
};
Match.prototype.winner=function(){
  var l=this.living().sort(function(a,b){return b.hp-a.hp});
  return l[0]||this.out[this.out.length-1];
};
Match.prototype.ranking=function(){
  var lv=this.living().sort(function(a,b){
    var pa=pr(a),pb=pr(b);
    if(pa!==pb)return pa-pb;
    return b.hp-a.hp;
  }),r=[],i;
  for(i=0;i<lv.length;i++)r.push(lv[i].co);
  for(i=this.out.length-1;i>=0;i--)r.push(this.out[i].co);
  return r;
};

function launch(list,opt,cb){
  GM.m=new Match(list,opt);GM.cur=opt;GM.cb=cb;GM.fired=false;GM.holdT=0;GM.la=-1;
  G('stage').textContent=opt.label;
  banner(opt.label,1300);
}
function seq(arr,labelFn,stop,onDone,fin){
  var res=[],i=0;
  (function nx(){
    if(i>=arr.length){onDone(res);return}
    var g=arr[i++];
    launch(g,{label:labelFn(i-1),stopAt:stop,final:!!fin&&i===arr.length},function(rk){res.push(rk);nx()});
  })();
}
function ko(players,title,after){
  var pairs=[],i,tot;
  for(i=0;i<players.length;i+=2)pairs.push([players[i],players[i+1]]);
  tot=pairs.length;
  seq(pairs,function(k){return title+(tot>1?' — '+(k+1)+'/'+tot:'')},1,function(res){
    var w=[],l=[],q;
    for(q=0;q<res.length;q++){w.push(res[q][0]);l.push(res[q][1])}
    after(w,l);
  },false);
}
function finish(pod){
  var w=pod[0],h='';
  G('vImg').src=furl(w.c,320);
  G('vName').textContent='الفائز: '+w.n;
  if(pod[1])h+='<div>🥈 '+pod[1].n+'</div>';
  if(pod[2])h+='<div>🥉 '+pod[2].n+'</div>';
  G('vPodium').innerHTML=h;
  G('victory').classList.add('show');
}
function worldRun(){
  var L=GM.list,forced=[],rest=[],gs=[[],[],[],[],[],[],[],[]],i,k;
  for(i=0;i<L.length;i++){(RK[L[i].c]?forced:rest).push(L[i])}
  forced.sort(function(a,b){return RK[a.c]-RK[b.c]});
  rest=shuffle(rest);
  for(i=0;i<forced.length&&i<8;i++)gs[i].push(forced[i]);
  for(i=0;i<rest.length;i++){
    for(k=0;k<8;k++){if(gs[k].length<4){gs[k].push(rest[i]);break}}
  }
  gs=shuffle(gs);
  seq(gs,function(g){return 'المجموعة '+'ABCDEFGH'.charAt(g)+' — يتأهل اثنان'},2,function(res){
    var br=[],W=[],Rr=[],q,c0,seed={1:0,2:8,3:4},third;
    for(q=0;q<8;q++){W.push(res[q][0]);Rr.push(res[q][1])}
    for(q=0;q<8;q+=2)br.push(W[q],Rr[q+1]);
    for(q=0;q<8;q+=2)br.push(W[q+1],Rr[q]);
    function put(code,idx){
      var f=-1,t,x;
      for(t=0;t<br.length;t++){if(br[t].c===code){f=t;break}}
      if(f<0||f===idx)return;
      x=br[idx];br[idx]=br[f];br[f]=x;
    }
    for(c0 in RK){if(seed[RK[c0]]!==undefined)put(c0,seed[RK[c0]])}
    ko(br,'دور الـ16',function(w1){
      ko(w1,'ربع النهائي',function(w2){
        ko(w2,'نصف النهائي',function(w3,l3){
          ko(l3,'المركز الثالث',function(w4){
            third=w4[0];
            launch([w3[0],w3[1]],{label:'النهائي',stopAt:1,final:true},function(rk){
              finish([rk[0],rk[1],third]);
            });
          });
        });
      });
    });
  },false);
}
function begin(mode,list,title){
  G('victory').classList.remove('show');
  GM={on:true,mode:mode,title:title,list:list,m:null,cur:null,cb:null,fired:false,holdT:0,sm:1,zm:1,fx:0,fy:0,acc:0,vic:false,la:-1};
  parts.length=0;rings.length=0;shake=0;
  G('feed').innerHTML='';
  setupRanks(list);
  go('game');layout();
  ST.paused=false;G('bPause').textContent='إيقاف';
  if(mode==='world'){worldRun()}
  else{launch(list,{label:title,stopAt:1,final:true},function(rk){finish(rk.slice(0,3))})}
}
function startRandom(){
  var n=ST.rnd,list=ensureForced(pickN(COUNTRIES,n),n);
  LAST=startRandom;
  begin('random',list,'معركة عشوائية');
}
function startWorld(){
  var list=ensureForced(pickN(COUNTRIES,32),32);
  LAST=startWorld;
  begin('world',list,'كأس العالم');
}
function startCont(id){
  var i,t='بطولة قارّية',list=ensureForced(contList(id),16);
  for(i=0;i<CONTS.length;i++){if(CONTS[i].id===id)t=CONTS[i].n}
  LAST=function(){startCont(id)};
  begin('cont',list,t);
}
function startCustom(sel){
  var base=sel.slice();
  LAST=function(){startCustom(base)};
  begin('custom',ensureForced(base.slice(),99),'معركة مخصصة');
}
function gameUpdate(rdt){
  if(!GM||!GM.on||!GM.m)return;
  var m=GM.m,step=1/120,g=0,w,al;
  GM.acc+=Math.min(rdt,.05)*ST.speed*GM.sm;
  while(GM.acc>=step&&g<48){m.step(step);stepFx(step);GM.acc-=step;g++}
  if(g>=48)GM.acc=0;
  al=m.alive();
  if(GM.la!==al){GM.la=al;G('alive').textContent='المتبقي: '+al}
  if(!m.ending)return;
  GM.holdT+=rdt;
  if(GM.cur.final){
    w=m.winner();
    if(!GM.vic){GM.vic=true;sVictory();banner('🏆 '+w.co.n,2600)}
    GM.sm+=(.22-GM.sm)*Math.min(1,rdt*2.5);
    GM.zm+=(1.9-GM.zm)*Math.min(1,rdt*1.1);
    GM.fx+=((w.x-A.x)-GM.fx)*Math.min(1,rdt*2);
    GM.fy+=((w.y-A.y)-GM.fy)*Math.min(1,rdt*2);
    if(GM.holdT>3&&!GM.fired){GM.fired=true;GM.cb(m.ranking())}
  }else if(GM.holdT>1.5&&!GM.fired){
    GM.fired=true;GM.cb(m.ranking());
  }
}
function startCont(id){
  var i,t='بطولة قارّية',list=ensureForced(contList(id),16);
  for(i=0;i<CONTS.length;i++){if(CONTS[i].id===id)t=CONTS[i].n}
  LAST=function(){startCont(id)};
  begin('cont',list,t);
}
function startCustom(sel){
  var base=sel.slice();
  LAST=function(){startCustom(base)};
  begin('custom',ensureForced(base.slice(),99),'معركة مخصصة');
}
function gameUpdate(rdt){
  if(!GM||!GM.on||!GM.m)return;
  var m=GM.m,step=1/120,g=0,w,al;
  GM.acc+=Math.min(rdt,.05)*ST.speed*GM.sm;
  while(GM.acc>=step&&g<48){m.step(step);stepFx(step);GM.acc-=step;g++}
  if(g>=48)GM.acc=0;
  al=m.alive();
  if(GM.la!==al){GM.la=al;G('alive').textContent='المتبقي: '+al}
  if(!m.ending)return;
  GM.holdT+=rdt;
  if(GM.cur.final){
    w=m.winner();
    if(!GM.vic){GM.vic=true;sVictory();banner('🏆 '+w.co.n,2600)}
    GM.sm+=(.22-GM.sm)*Math.min(1,rdt*2.5);
    GM.zm+=(1.9-GM.zm)*Math.min(1,rdt*1.1);
    GM.fx+=((w.x-A.x)-GM.fx)*Math.min(1,rdt*2);
    GM.fy+=((w.y-A.y)-GM.fy)*Math.min(1,rdt*2);
    if(GM.holdT>3&&!GM.fired){GM.fired=true;GM.cb(m.ranking())}
  }else if(GM.holdT>1.5&&!GM.fired){
    GM.fired=true;GM.cb(m.ranking());
  }
}
"""

    private val JS3 = """
var _bg=begin;
begin=function(m,l,t){for(var i=0;i<l.length;i++)flag(l[i].c);_bg(m,l,t)};

function render(){
  var c=ctx,m=GM.m,i,b,sx=0,sy=0,names;
  c.setTransform(DPR,0,0,DPR,0,0);
  c.fillStyle='#ECE9E1';c.fillRect(0,0,A.W,A.H);
  c.save();
  if(shake>0&&!ST.paused){sx=rnd(-shake,shake);sy=rnd(-shake,shake)}
  c.translate(sx,sy);
  c.translate(A.x,A.y);c.scale(GM.zm,GM.zm);c.translate(-A.x-GM.fx,-A.y-GM.fy);
  c.fillStyle='#FAFAFA';c.beginPath();c.arc(A.x,A.y,A.R,0,6.2832);c.fill();
  c.lineWidth=3;c.strokeStyle='#111';c.stroke();
  if(m){
    names=m.alive()<=10;
    for(i=0;i<m.balls.length;i++){
      b=m.balls[i];
      if(b.dead)continue;
      if(!ST.paused)b.pushTrail();
      b.draw(c,names);
    }
  }
  drawFx(c);
  c.restore();
}

var lt=0;
function loop(ts){
  requestAnimationFrame(loop);
  var rdt=lt?(ts-lt)/1000:.016;
  lt=ts;
  if(rdt>.1)rdt=.1;
  if(GM&&GM.on){
    if(!ST.paused&&!document.hidden)gameUpdate(rdt);
    render();
  }
}

function stopGame(){
  if(GM){GM.on=false;GM.m=null}
  ST.paused=false;G('bPause').textContent='إيقاف';
  G('victory').classList.remove('show');
  G('banner').classList.remove('show');
  parts.length=0;rings.length=0;shake=0;
  go('menu');
}

var SEL={},selBuilt=false,contBuilt=false;
function upSel(){
  var n=Object.keys(SEL).length;
  G('selCount').textContent=n+'/32';
  G('selStart').disabled=n<2;
}
function syncCells(){
  var cs=document.querySelectorAll('.cell'),i;
  for(i=0;i<cs.length;i++)cs[i].classList.toggle('on',!!SEL[cs[i].getAttribute('data-c')]);
  upSel();
}
function buildGrid(){
  if(selBuilt)return;
  selBuilt=true;
  var g=G('grid'),arr=COUNTRIES.slice().sort(function(a,b){return a.n.localeCompare(b.n,'ar')});
  arr.forEach(function(co){
    var d=document.createElement('div'),im=document.createElement('img'),s=document.createElement('span');
    d.className='cell';d.setAttribute('data-c',co.c);d.setAttribute('data-n',(co.n+' '+co.c).toLowerCase());
    im.src=furl(co.c,80);im.loading='lazy';im.alt='';s.textContent=co.n;
    d.appendChild(im);d.appendChild(s);
    d.addEventListener('click',function(){
      if(SEL[co.c]){delete SEL[co.c];d.classList.remove('on')}
      else if(Object.keys(SEL).length<32){SEL[co.c]=1;d.classList.add('on')}
      upSel();
    });
    g.appendChild(d);
  });
}
function buildCont(){
  if(contBuilt)return;
  contBuilt=true;
  CONTS.forEach(function(k){
    var b=document.createElement('button');
    b.className='btn';b.textContent=k.n;
    b.addEventListener('click',function(){startCont(k.id)});
    G('contList').appendChild(b);
  });
}

G('bRandom').addEventListener('click',startRandom);
G('bWorld').addEventListener('click',startWorld);
G('bCont').addEventListener('click',function(){buildCont();go('cont')});
G('bCustom').addEventListener('click',function(){buildGrid();upSel();go('select')});
G('rndMinus').addEventListener('click',function(){ST.rnd=clamp(ST.rnd-1,3,10);G('rndVal').textContent=ST.rnd});
G('rndPlus').addEventListener('click',function(){ST.rnd=clamp(ST.rnd+1,3,10);G('rndVal').textContent=ST.rnd});
G('contBack').addEventListener('click',function(){go('menu')});
G('selBack').addEventListener('click',function(){go('menu')});
G('selClear').addEventListener('click',function(){SEL={};syncCells()});
G('selRand').addEventListener('click',function(){
  SEL={};
  pickN(COUNTRIES,8).forEach(function(co){SEL[co.c]=1});
  syncCells();
});
G('selStart').addEventListener('click',function(){
  var list=COUNTRIES.filter(function(co){return SEL[co.c]});
  if(list.length<2)return;
  startCustom(list);
});
G('search').addEventListener('input',function(){
  var q=this.value.trim().toLowerCase(),cs=document.querySelectorAll('.cell'),i;
  for(i=0;i<cs.length;i++)cs[i].style.display=(!q||cs[i].getAttribute('data-n').indexOf(q)>=0)?'':'none';
});

var sb=document.querySelectorAll('.spd');
for(var si=0;si<sb.length;si++){
  sb[si].addEventListener('click',function(){
    ST.speed=parseFloat(this.getAttribute('data-s'));
    for(var j=0;j<sb.length;j++)sb[j].classList.toggle('on',sb[j]===this);
  });
}
G('sizeR').addEventListener('input',function(){ST.scale=parseInt(this.value,10)/100;setArena()});
G('bPause').addEventListener('click',function(){
  ST.paused=!ST.paused;
  this.textContent=ST.paused?'استئناف':'إيقاف';
});
G('bExit').addEventListener('click',stopGame);
G('vMenu').addEventListener('click',stopGame);
G('vAgain').addEventListener('click',function(){if(LAST)LAST()});

function applyLive(){
  if(!GM||!GM.on||!GM.m||!GM.list)return;
  var m=GM.m,i,b;
  setupRanks(GM.list);
  for(i=0;i<m.balls.length;i++){
    b=m.balls[i];
    b.gr=RK[b.co.c]||0;
    b.mm=b.gr>0?1+.12*(4-b.gr):1;
  }
  m.forced=m.balls.some(function(q){return !q.dead&&q.gr>0});
}
function gstat(){
  var n=GH.r.filter(function(x){return x}).length;
  G('gstat').textContent=GH.on?('🔒 مفعّل — '+n+' من 3 مختارة'):'غير مفعّل — لن يتم التحكم بالنتيجة';
  G('gLock').textContent=GH.on?'قفل النتائج: مفعّل ✓':'قفل النتائج: متوقف';
  G('gLock').classList.toggle('on',GH.on);
}
var gBuilt=false;
function setGP(idx,v){
  var j;
  GH.r[idx]=v;
  if(v){
    for(j=0;j<3;j++){
      if(j!==idx&&GH.r[j]===v){GH.r[j]='';G('g'+(j+1)).value=''}
    }
  }
  gstat();
  if(GH.on)applyLive();
}
function buildGP(){
  if(gBuilt)return;
  gBuilt=true;
  var arr=COUNTRIES.slice().sort(function(a,b){return a.n.localeCompare(b.n,'ar')});
  var h='<option value="">— بدون —</option>',i;
  arr.forEach(function(co){h+='<option value="'+co.c+'">'+co.n+'</option>'});
  for(i=1;i<=3;i++){
    G('g'+i).innerHTML=h;
    (function(k){G('g'+k).addEventListener('change',function(){setGP(k-1,this.value)})})(i);
  }
}
function openGP(){
  buildGP();
  for(var i=0;i<3;i++)G('g'+(i+1)).value=GH.r[i]||'';
  gstat();
  G('gp').classList.add('show');
}
var gt=[];
G('ghost').addEventListener('pointerdown',function(){
  var n=Date.now();
  gt.push(n);
  if(gt.length>5)gt.shift();
  if(gt.length===5&&n-gt[0]<=3000){gt=[];openGP()}
});
G('gClose').addEventListener('click',function(){G('gp').classList.remove('show')});
G('gLock').addEventListener('click',function(){
  GH.on=!GH.on;
  gstat();
  applyLive();
});
var dg=null;
G('gHead').addEventListener('pointerdown',function(e){
  if(e.target.id==='gClose')return;
  var r=G('gp').getBoundingClientRect();
  dg={dx:e.clientX-r.left,dy:e.clientY-r.top};
  try{G('gHead').setPointerCapture(e.pointerId)}catch(x){}
});
G('gHead').addEventListener('pointermove',function(e){
  if(!dg)return;
  var p=G('gp');
  p.style.transform='none';
  p.style.left=clamp(e.clientX-dg.dx,0,window.innerWidth-p.offsetWidth)+'px';
  p.style.top=clamp(e.clientY-dg.dy,0,window.innerHeight-p.offsetHeight)+'px';
});
G('gHead').addEventListener('pointerup',function(){dg=null});

window.wfBack=function(){
  if(G('gp').classList.contains('show')){G('gp').classList.remove('show');return true}
  var a=document.querySelector('.screen.active');
  if(!a||a.id==='menu')return false;
  if(a.id==='game')stopGame();else go('menu');
  return true;
};
window.wfPause=function(){
  if(GM&&GM.on&&!ST.paused){ST.paused=true;G('bPause').textContent='استئناف'}
  return 0;
};
document.addEventListener('visibilitychange',function(){if(document.hidden)window.wfPause()});
window.addEventListener('resize',function(){
  var ox=A.x,oy=A.y,i,b;
  layout();
  if(GM&&GM.m){
    for(i=0;i<GM.m.balls.length;i++){b=GM.m.balls[i];b.x+=A.x-ox;b.y+=A.y-oy}
  }
});
document.addEventListener('contextmenu',function(e){e.preventDefault()});

layout();
upSel();
requestAnimationFrame(loop);
"""

    fun build(): String = HEAD + BODY + "<script>" + JS1 + JS2 + JS3 + "</script></body></html>"
}