package com.war.fox

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : Activity() {
    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        web = WebView(this).apply {
            setBackgroundColor(0xFF0A0A0F.toInt())
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        with(web.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            allowFileAccess = false
            allowContentAccess = false
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        web.webViewClient = WebViewClient()
        web.loadDataWithBaseURL(
            "https://flagcdn.com/",
            HTML_GAME,
            "text/html",
            "UTF-8",
            null
        )
        setContentView(web)
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (this::web.isInitialized && web.canGoBack()) web.goBack()
        else if (this::web.isInitialized) {
            web.evaluateJavascript(
                "if(document.getElementById('secret').classList.contains('show')){closeGhostPanel();true}else{false}"
            ) { result ->
                if (result != "true") runOnUiThread { finish() }
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::web.isInitialized) web.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (this::web.isInitialized) web.onResume()
    }

    override fun onDestroy() {
        if (this::web.isInitialized) {
            web.loadUrl("about:blank")
            web.destroy()
        }
        super.onDestroy()
    }

    companion object {
        private val HTML_GAME = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no,viewport-fit=cover">
<title>WAR FOX</title>
<style>
*{margin:0;padding:0;box-sizing:border-box;user-select:none;-webkit-user-select:none;-webkit-tap-highlight-color:transparent}
html,body{width:100%;height:100%;overflow:hidden;background:#0a0a0f;font-family:Arial,sans-serif;color:#fff;touch-action:pan-y;overscroll-behavior:none}
canvas{display:block;position:absolute;inset:0}
#screens{position:absolute;inset:0;z-index:10;pointer-events:none}
.screen{position:absolute;inset:0;display:none;flex-direction:column;align-items:center;justify-content:center;background:radial-gradient(circle at 50% 40%,#1e1e28f5,#08080cfb);padding:22px;pointer-events:auto;overflow-y:auto}
.screen.active{display:flex}
.title{font-size:43px;font-weight:900;letter-spacing:6px;background:linear-gradient(#ffe18a,#ffb400,#bd7100);-webkit-background-clip:text;color:transparent;filter:drop-shadow(0 0 18px #ffb45566)}
.subtitle{font-size:10px;letter-spacing:6px;color:#777;margin:8px 0 25px}
.btn{background:linear-gradient(#292934,#15151d);border:1px solid #3b3b49;color:#fff;padding:13px 22px;border-radius:11px;font-size:13px;font-weight:800;letter-spacing:1px;margin:5px 0;min-width:230px;cursor:pointer}
.btn:active{background:#ffb400;color:#111;transform:scale(.97)}
.btn.small{padding:9px 13px;min-width:0;font-size:10px}
.back{position:absolute;top:14px;left:14px}
.hdr{font-size:20px;font-weight:900;letter-spacing:3px;color:#ffb400;margin:58px 0 15px;text-align:center}
.grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:8px;width:100%;max-width:580px;max-height:57vh;overflow-y:auto;padding:9px;background:#0005;border:1px solid #292934;border-radius:13px}
.cc{aspect-ratio:1;border-radius:50%;overflow:hidden;border:2px solid #30303b;background:#17171d;position:relative}
.cc.selected{border:3px solid #ffb400;box-shadow:0 0 13px #ffb47799;transform:scale(.95)}
.cc img{width:100%;height:100%;object-fit:cover}
.cc .tiny{position:absolute;left:0;right:0;bottom:0;background:#000b;font-size:7px;text-align:center;padding:3px 1px;white-space:nowrap;overflow:hidden}
#hud{position:absolute;top:0;left:0;right:0;padding:12px;display:none;justify-content:space-between;align-items:flex-start;z-index:5;pointer-events:none}
#hud.on{display:flex}
.spd{pointer-events:auto;display:flex;gap:3px;background:#101017e8;padding:4px;border:1px solid #30303b;border-radius:8px}
.spd button{background:transparent;border:0;color:#aaa;padding:6px 8px;border-radius:5px;font-size:10px;font-weight:bold}
.spd button.on{background:#ffb400;color:#111}
.counter{font-size:10px;letter-spacing:1px;color:#ddd;background:#101017e8;padding:8px;border:1px solid #30303b;border-radius:8px}
#winner{position:absolute;inset:0;display:none;flex-direction:column;align-items:center;justify-content:center;background:radial-gradient(circle,#29200cf5,#050508fa);z-index:50;padding:25px}
#winner.show{display:flex}
.medal{font-size:58px;margin-bottom:12px}
.wflag{width:155px;height:155px;border-radius:50%;overflow:hidden;border:4px solid #ffb400;box-shadow:0 0 55px #ffb47799;margin-bottom:18px}
.wflag img{width:100%;height:100%;object-fit:cover}
.nm{font-size:28px;font-weight:900;text-align:center;margin-bottom:8px}
.plc{font-size:11px;letter-spacing:5px;color:#ffb400;margin-bottom:24px}
#ghost{position:absolute;top:0;right:0;width:62px;height:62px;z-index:100;opacity:0;pointer-events:auto}
#secret{position:absolute;top:12px;right:10px;width:min(270px,calc(100vw - 20px));max-height:90vh;overflow-y:auto;background:#101017f7;border:1px solid #ffb400;border-radius:13px;padding:13px;display:none;z-index:200;box-shadow:0 0 35px #ffb45555}
#secret.show{display:block}
#secret h3{text-align:center;color:#ffb400;font-size:11px;letter-spacing:2px;margin-bottom:9px}
#secret .f{margin:9px 0}
#secret label{display:block;color:#aaa;font-size:9px;letter-spacing:1px;margin-bottom:4px}
#secret select{width:100%;background:#08080d;color:#fff;border:1px solid #444;padding:8px;border-radius:6px}
#secret .tg{display:flex;justify-content:space-between;align-items:center;border-top:1px solid #333;padding-top:10px;margin-top:10px;font-size:10px}
.sw{width:40px;height:22px;border-radius:20px;background:#444;position:relative}
.sw:after{content:'';position:absolute;top:2px;left:2px;width:18px;height:18px;background:white;border-radius:50%}
.sw.on{background:#ffb400}
.sw.on:after{left:20px}
.note{font-size:9px;color:#888;text-align:center;margin:7px 0}
@media(max-height:620px){.title{font-size:34px}.subtitle{margin-bottom:12px}.btn{padding:10px 18px}.grid{max-height:48vh}}
</style>
</head>
<body>
<canvas id="c"></canvas>
<div id="hud">
 <div class="spd" id="spd">
  <button data-s=".5">0.5x</button><button data-s="1" class="on">1x</button>
  <button data-s="2">2x</button><button data-s="4">4x</button>
 </div>
 <div class="counter" id="cnt">0 FIGHTERS</div>
</div>
<div id="screens">
 <div class="screen active" id="menu">
  <div class="title">WAR FOX</div><div class="subtitle">FLAG BATTLE ROYALE</div>
  <button class="btn" data-mode="random">RANDOM BATTLE</button>
  <button class="btn" data-mode="worldcup">WORLD CUP 32</button>
  <button class="btn" data-mode="continental">CONTINENTAL</button>
  <button class="btn" data-mode="custom">CUSTOM PICK</button>
  <button class="btn" data-mode="historical">HISTORICAL STATES</button>
 </div>
 <div class="screen" id="select">
  <button class="btn small back" id="b1">← BACK</button>
  <div class="hdr" id="selT">SELECT FIGHTERS</div>
  <div class="grid" id="grid"></div>
  <button class="btn" id="goBtn" style="margin-top:12px">START BATTLE</button>
 </div>
 <div class="screen" id="cont">
  <button class="btn small back" id="b2">← BACK</button>
  <div class="hdr">PICK CONTINENT</div>
  <button class="btn" data-cont="europe">EUROPE</button>
  <button class="btn" data-cont="asia">ASIA</button>
  <button class="btn" data-cont="arab">ARAB WORLD</button>
  <button class="btn" data-cont="americas">AMERICAS</button>
  <button class="btn" data-cont="africa">AFRICA</button>
 </div>
</div>
<div id="winner">
 <div class="medal">🏆</div><div class="wflag"><img id="wimg" alt=""></div>
 <div class="nm" id="wnm"></div><div class="plc">CHAMPION</div>
 <button class="btn" id="again">PLAY AGAIN</button><button class="btn" id="mm">MAIN MENU</button>
</div>
<div id="ghost" aria-label="Ghost panel activation area"></div>
<div id="secret">
 <h3>⚙ GHOST / DIRECTOR PANEL</h3>
 <button class="btn small" id="ghostClose" style="width:100%;margin:0 0 8px">EXIT GHOST PANEL ✕</button>
 <div class="note">Director controls: selected placements are visibly configured here.</div>
 <div class="f"><label>1ST PLACE — GOLD</label><select id="f1"><option value="">-- none --</option></select></div>
 <div class="f"><label>2ND PLACE — SILVER</label><select id="f2"><option value="">-- none --</option></select></div>
 <div class="f"><label>3RD PLACE — BRONZE</label><select id="f3"><option value="">-- none --</option></select></div>
 <div class="tg"><span>ENABLE DIRECTOR RESULTS</span><div class="sw" id="lk"></div></div>
</div>
<script>
(function(){
'use strict';
var COUNTRY_DATA = `
af:Afghanistan|al:Albania|dz:Algeria|ad:Andorra|ao:Angola|ag:Antigua and Barbuda|ar:Argentina|am:Armenia|au:Australia|at:Austria|az:Azerbaijan|bs:Bahamas|bh:Bahrain|bd:Bangladesh|bb:Barbados|by:Belarus|be:Belgium|bz:Belize|bj:Benin|bt:Bhutan|bo:Bolivia|ba:Bosnia and Herzegovina|bw:Botswana|br:Brazil|bn:Brunei|bg:Bulgaria|bf:Burkina Faso|bi:Burundi|cv:Cabo Verde|kh:Cambodia|cm:Cameroon|ca:Canada|cf:Central African Republic|td:Chad|cl:Chile|cn:China|co:Colombia|km:Comoros|cg:Republic of the Congo|cd:Democratic Republic of the Congo|cr:Costa Rica|ci:Cote d'Ivoire|hr:Croatia|cu:Cuba|cy:Cyprus|cz:Czechia|dk:Denmark|dj:Djibouti|dm:Dominica|do:Dominican Republic|ec:Ecuador|eg:Egypt|sv:El Salvador|gq:Equatorial Guinea|er:Eritrea|ee:Estonia|sz:Eswatini|et:Ethiopia|fj:Fiji|fi:Finland|fr:France|ga:Gabon|gm:Gambia|ge:Georgia|de:Germany|gh:Ghana|gr:Greece|gd:Grenada|gt:Guatemala|gn:Guinea|gw:Guinea-Bissau|gy:Guyana|ht:Haiti|hn:Honduras|hu:Hungary|is:Iceland|in:India|id:Indonesia|ir:Iran|iq:Iraq|ie:Ireland|il:Israel|it:Italy|jm:Jamaica|jp:Japan|jo:Jordan|kz:Kazakhstan|ke:Kenya|ki:Kiribati|kp:North Korea|kr:South Korea|kw:Kuwait|kg:Kyrgyzstan|la:Laos|lv:Latvia|lb:Lebanon|ls:Lesotho|lr:Liberia|ly:Libya|li:Liechtenstein|lt:Lithuania|lu:Luxembourg|mg:Madagascar|mw:Malawi|my:Malaysia|mv:Maldives|ml:Mali|mt:Malta|mh:Marshall Islands|mr:Mauritania|mu:Mauritius|mx:Mexico|fm:Micronesia|md:Moldova|mc:Monaco|mn:Mongolia|me:Montenegro|ma:Morocco|mz:Mozambique|mm:Myanmar|na:Namibia|nr:Nauru|np:Nepal|nl:Netherlands|nz:New Zealand|ni:Nicaragua|ne:Niger|ng:Nigeria|mk:North Macedonia|no:Norway|om:Oman|pk:Pakistan|pw:Palau|pa:Panama|pg:Papua New Guinea|py:Paraguay|pe:Peru|ph:Philippines|pl:Poland|pt:Portugal|qa:Qatar|ro:Romania|ru:Russia|rw:Rwanda|kn:Saint Kitts and Nevis|lc:Saint Lucia|vc:Saint Vincent and the Grenadines|ws:Samoa|sm:San Marino|st:Sao Tome and Principe|sa:Saudi Arabia|sn:Senegal|rs:Serbia|sc:Seychelles|sl:Sierra Leone|sg:Singapore|sk:Slovakia|si:Slovenia|sb:Solomon Islands|so:Somalia|za:South Africa|ss:South Sudan|es:Spain|lk:Sri Lanka|sd:Sudan|sr:Suriname|se:Sweden|ch:Switzerland|sy:Syria|tj:Tajikistan|tz:Tanzania|th:Thailand|tl:Timor-Leste|tg:Togo|to:Tonga|tt:Trinidad and Tobago|tn:Tunisia|tr:Turkey|tm:Turkmenistan|tv:Tuvalu|ug:Uganda|ua:Ukraine|ae:United Arab Emirates|gb:United Kingdom|us:United States|uy:Uruguay|uz:Uzbekistan|vu:Vanuatu|va:Vatican City|ve:Venezuela|vn:Vietnam|ye:Yemen|zm:Zambia|zw:Zimbabwe|xk:Kosovo|tw:Taiwan|ps:Palestine
`;
var C={};
COUNTRY_DATA.replace(/\n/g,'').split('|').forEach(function(x){var p=x.trim().split(':');if(p.length===2)C[p[0]]=p[1];});

var HISTORICAL={
 hist_roman:{name:'Roman Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Vexilloid_of_the_Roman_Empire.svg'},
 hist_byzantine:{name:'Byzantine Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Byzantine_imperial_flag.svg'},
 hist_ottoman:{name:'Ottoman Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_the_Ottoman_Empire.svg'},
 hist_abbasid:{name:'Abbasid Caliphate',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Black_flag.svg'},
 hist_umayyad:{name:'Umayyad Caliphate',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Umayyad_flag.svg'},
 hist_mongol:{name:'Mongol Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_the_Mongol_Empire.svg'},
 hist_persian:{name:'Persian Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Derafsh_Kaviani.svg'},
 hist_safavid:{name:'Safavid Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Safavid_Flag.svg'},
 hist_qing:{name:'Qing Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_China_(1889%E2%80%931912).svg'},
 hist_russian:{name:'Russian Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Russian_Empire.svg'},
 hist_german:{name:'German Empire',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_the_German_Empire.svg'},
 hist_austria:{name:'Austria-Hungary',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Austria-Hungary_(1869-1918).svg'},
 hist_ussr:{name:'Soviet Union',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_the_Soviet_Union.svg'},
 hist_yugoslavia:{name:'Yugoslavia',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Yugoslavia.svg'},
 hist_iraq:{name:'Kingdom of Iraq',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Iraq_(1924%E2%80%931959).svg'},
 hist_egypt:{name:'Kingdom of Egypt',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Egypt_(1922%E2%80%931958).svg'},
 hist_prussia:{name:'Kingdom of Prussia',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_the_Kingdom_of_Prussia_(1750).svg'},
 hist_italy:{name:'Kingdom of Italy',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Italy_(1861-1946).svg'},
 hist_grancolombia:{name:'Gran Colombia',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_the_Republic_of_Colombia.svg'},
 hist_hawaii:{name:'Kingdom of Hawaii',url:'https://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Hawaii.svg'}
};
function countryName(code){return C[code]||(HISTORICAL[code]&&HISTORICAL[code].name)||code.toUpperCase();}
function flagUrl(code,size){return HISTORICAL[code]?HISTORICAL[code].url:'https://flagcdn.com/'+(size||'w320')+'/'+code+'.png';}
var CONT={
 europe:['gb','fr','de','it','es','pt','nl','be','ch','at','pl','se','no','dk','fi','ie','gr','ua','ru','al','ad','ba','bg','hr','cz','ee','hu','is','lv','lt','md','me','mk','ro','rs','sk','si'],
 asia:['jp','cn','kr','kp','in','pk','id','th','vn','ph','my','sg','ir','iq','sa','ae','tr','af','am','az','bh','bd','bt','bn','kh','ge','jo','kz','kw','kg','la','lb','mv','mn','mm','np','om','qa','lk','sy','tj','tl','tm','uz','ye','ps','tw'],
 arab:['iq','sa','ae','eg','ma','dz','tn','ly','jo','lb','sy','ye','om','qa','bh','kw','ps','sd','mr','so','dj','km'],
 americas:['us','ca','br','ar','mx','cl','co','pe','uy','ve','bo','ec','py','gy','sr','cu','ht','do','jm','pa','cr','ni','hn','gt','sv','bz'],
 africa:['za','ng','ke','et','eg','ma','dz','tn','ly','sd','sn','tz','ug','gh','ci','cm','ao','mz','zw','zm','rw','so','ss','na','bw']
};
var q=function(id){return document.getElementById(id);};
var canvas=q('c'),ctx=canvas.getContext('2d');
var W=0,H=0,cx=0,cy=0,dpr=1,arenaR=200;
var balls=[],particles=[],shocks=[],state='menu',gameSpeed=1;
var shakeX=0,shakeY=0,shakeMag=0,last=0,audioCtx=null,lockRes=false;
var targetSurv=1,transitioning=false,victory=null,lastConfig=null,roundLabel='';
function resize(){W=innerWidth;H=innerHeight;dpr=devicePixelRatio||1;canvas.width=W*dpr;canvas.height=H*dpr;canvas.style.width=W+'px';canvas.style.height=H+'px';ctx.setTransform(dpr,0,0,dpr,0,0);cx=W/2;cy=H/2;arenaR=Math.min(W,H)*.43;}
addEventListener('resize',resize);
addEventListener('orientationchange',function(){setTimeout(resize,150);});
function initAudio(){if(audioCtx)return;try{audioCtx=new (AudioContext||webkitAudioContext)();}catch(e){}}
function tone(freq,duration,type,volume){if(!audioCtx)return;var t=audioCtx.currentTime,o=audioCtx.createOscillator(),g=audioCtx.createGain();o.type=type||'square';o.frequency.value=freq;g.gain.setValueAtTime(volume||.06,t);g.gain.exponentialRampToValueAtTime(.0001,t+duration);o.connect(g);g.connect(audioCtx.destination);o.start(t);o.stop(t+duration);}
function sndClack(v){tone(650+Math.random()*350,.08,'square',Math.min(.12,v*.012));}
function sndShatter(){for(var i=0;i<4;i++)setTimeout(function(){tone(180+Math.random()*600,.16,'sawtooth',.045);},i*18);}
function sndWin(){[523,659,784,1047,1319].forEach(function(n,i){setTimeout(function(){tone(n,.45,'triangle',.1);},i*120);});}
function Ball(code){this.code=code;this.name=countryName(code);this.r=25;this.x=0;this.y=0;this.vx=0;this.vy=0;this.mass=1;this.hp=100;this.maxHp=100;this.forced=0;this.alive=true;this.trail=[];this.img=null;this.loaded=false;this.flash=0;this.angle=Math.random()*6.28;this.spin=(Math.random()-.5)*.04;}
Ball.prototype.load=function(){var self=this,im=new Image();im.onload=function(){self.img=im;self.loaded=true;};im.onerror=function(){self.loaded=false;};im.src=flagUrl(self.code,'w320');};
function spawnBalls(codes){balls=[];particles=[];shocks=[];var n=codes.length,baseR=Math.max(12,Math.min(arenaR/(Math.sqrt(n)*1.35),32));for(var i=0;i<n;i++){var b=new Ball(codes[i]);b.r=baseR;var a=i/n*6.283,d=arenaR*.62;b.x=cx+Math.cos(a)*d;b.y=cy+Math.sin(a)*d;b.vx=(Math.random()-.5)*6;b.vy=(Math.random()-.5)*6;b.load();balls.push(b);}applyForced();updateCnt();}
function findBall(code){return balls.find(function(b){return b.code===code;})||null;}
function applyForced(){var sels=[q('f1'),q('f2'),q('f3')];balls.forEach(function(b){b.forced=0;});sels.forEach(function(s,i){if(s&&s.value){var b=findBall(s.value);if(b&&b.alive)b.forced=i+1;}});}
function dmgMult(b){if(!lockRes||!b.forced)return 1;return b.forced===1?.15:b.forced===2?.3:.5;}
function sparks(x,y,n,col){for(var i=0;i<n;i++){var a=Math.random()*6.28,s=Math.random()*4+1;particles.push({x:x,y:y,vx:Math.cos(a)*s,vy:Math.sin(a)*s,life:1,col:col,sz:Math.random()*3+1});}}
function explosion(x,y,r){for(var i=0;i<38;i++){var a=Math.random()*6.28,s=Math.random()*7+2;particles.push({x:x,y:y,vx:Math.cos(a)*s,vy:Math.sin(a)*s,life:1,col:'#fff',sz:Math.random()*4+1});}shocks.push({x:x,y:y,r:r,life:1});}
function eliminate(b){if(!b.alive)return;b.alive=false;explosion(b.x,b.y,b.r);sndShatter();shakeMag=Math.min(22,shakeMag+12);updateCnt();checkEnd();}
function updateBall(b,dt){if(!b.alive)return;if(lockRes&&b.forced){b.hp=Math.min(b.maxHp,b.hp+.55*dt);if(b.forced===1){var best=null,bd=1e12;balls.forEach(function(o){if(!o.alive||o.forced===1)return;var dx=o.x-b.x,dy=o.y-b.y,d=dx*dx+dy*dy;if(d<bd){bd=d;best=o;}});if(best&&bd>1){var d=Math.sqrt(bd);b.vx+=(best.x-b.x)/d*.045*dt;b.vy+=(best.y-b.y)/d*.045*dt;}}}
b.x+=b.vx*dt;b.y+=b.vy*dt;var dx=b.x-cx,dy=b.y-cy,d=Math.sqrt(dx*dx+dy*dy),lim=arenaR-b.r;if(d>lim){var nx=dx/d,ny=dy/d;b.x=cx+nx*lim;b.y=cy+ny*lim;var dot=b.vx*nx+b.vy*ny;b.vx-=2*dot*nx;b.vy-=2*dot*ny;b.vx*=.9;b.vy*=.9;if(Math.abs(dot)>2)sparks(b.x,b.y,3,'#ff5555');}
b.vx*=.992;b.vy*=.992;if(Math.abs(b.vx)<.015)b.vx=0;if(Math.abs(b.vy)<.015)b.vy=0;b.angle+=b.spin*dt;b.trail.push({x:b.x,y:b.y,a:1});if(b.trail.length>9)b.trail.shift();b.trail.forEach(function(t){t.a*=.88;});b.flash=Math.max(0,b.flash-dt*.05);}
function collide(a,b){var dx=b.x-a.x,dy=b.y-a.y,d=Math.sqrt(dx*dx+dy*dy),min=a.r+b.r;if(d>=min||d===0)return;var nx=dx/d,ny=dy/d,ov=min-d,tm=a.mass+b.mass;a.x-=nx*ov*(b.mass/tm);a.y-=ny*ov*(b.mass/tm);b.x+=nx*ov*(a.mass/tm);b.y+=ny*ov*(a.mass/tm);var rvx=b.vx-a.vx,rvy=b.vy-a.vy,vel=rvx*nx+rvy*ny;if(vel>0)return;var j=-(1+.94)*vel/(1/a.mass+1/b.mass),ix=j*nx,iy=j*ny;a.vx-=ix/a.mass;a.vy-=iy/a.mass;b.vx+=ix/b.mass;b.vy+=iy/b.mass;var impact=Math.abs(vel);if(impact>1.2){var dmg=impact*.55;a.hp-=dmg*(b.mass/tm)*dmgMult(a);b.hp-=dmg*(a.mass/tm)*dmgMult(b);a.flash=b.flash=1;sparks((a.x+b.x)/2,(a.y+b.y)/2,Math.min(12,Math.floor(impact*1.8)),'#fff');shakeMag=Math.min(22,shakeMag+impact*.35);sndClack(impact);if(a.hp<=0)eliminate(a);if(b.hp<=0)eliminate(b);}}
function forcedEndgame(dt){if(!lockRes)return;var alive=balls.filter(function(b){return b.alive;}),forced=alive.filter(function(b){return b.forced;});if(forced.length!==alive.length||forced.length<=1)return;var worst=forced.reduce(function(a,b){return !a||b.forced>a.forced?b:a;},null);if(worst&&worst.forced>1){worst.hp-=30*dt;if(worst.hp<=0)eliminate(worst);}}
function checkEnd(){if(transitioning)return;var alive=balls.filter(function(b){return b.alive;});if(alive.length<=targetSurv){transitioning=true;if(targetSurv===1)setTimeout(function(){endGame(alive[0]||null);},650);else setTimeout(function(){nextRound(alive);},800);}}
function nextRound(survivors){balls=survivors.slice();targetSurv=Math.max(1,Math.floor(targetSurv/2));var n=balls.length;balls.forEach(function(b,i){var a=i/n*6.283,d=arenaR*.6;b.x=cx+Math.cos(a)*d;b.y=cy+Math.sin(a)*d;b.vx=(Math.random()-.5)*8;b.vy=(Math.random()-.5)*8;b.hp=b.maxHp;b.flash=0;b.trail=[];});particles=[];shocks=[];transitioning=false;updateCnt();}
function endGame(w){state='victory';sndWin();if(!w){state='battle';return;}victory={b:w,zoom:1,t:performance.now()};setTimeout(function(){q('wimg').src=flagUrl(w.code,'w320');q('wnm').textContent=w.name;q('winner').classList.add('show');},900);}
function updateCnt(){var n=balls.filter(function(b){return b.alive;}).length;q('cnt').textContent=(roundLabel?roundLabel+' · ':'')+n+' FIGHTERS'+(targetSurv>1?' / TARGET '+targetSurv:'');}
function update(dt){if(transitioning)return;shakeMag*=.88;shakeX=(Math.random()-.5)*shakeMag;shakeY=(Math.random()-.5)*shakeMag;balls.forEach(function(b){updateBall(b,dt);});for(var i=0;i<balls.length;i++)for(var j=i+1;j<balls.length;j++)if(balls[i].alive&&balls[j].alive)collide(balls[i],balls[j]);forcedEndgame(dt);for(var p=particles.length-1;p>=0;p--){var x=particles[p];x.x+=x.vx*dt;x.y+=x.vy*dt;x.vx*=.95;x.vy*=.95;x.life-=.022*dt;if(x.life<=0)particles.splice(p,1);}for(var s=shocks.length-1;s>=0;s--){shocks[s].life-=.028*dt;if(shocks[s].life<=0)shocks.splice(s,1);}}
function drawBall(b){b.trail.forEach(function(t,i){ctx.globalAlpha=t.a*.2;ctx.fillStyle='#222';ctx.beginPath();ctx.arc(t.x,t.y,b.r*(i/b.trail.length)*.8,0,6.28);ctx.fill();});ctx.globalAlpha=1;ctx.beginPath();ctx.arc(b.x+2,b.y+3,b.r,0,6.28);ctx.fillStyle='#0003';ctx.fill();ctx.save();ctx.beginPath();ctx.arc(b.x,b.y,b.r,0,6.28);ctx.clip();if(b.loaded&&b.img){ctx.translate(b.x,b.y);ctx.rotate(b.angle);var size=b.r*2,ar=b.img.width/b.img.height,dw=ar>=1?size*ar:size,dh=ar>=1?size:size/ar;ctx.drawImage(b.img,-dw/2,-dh/2,dw,dh);ctx.restore();}else{ctx.fillStyle='#555';ctx.fillRect(b.x-b.r,b.y-b.r,b.r*2,b.r*2);ctx.fillStyle='#fff';ctx.font='bold '+b.r*.65+'px Arial';ctx.textAlign='center';ctx.textBaseline='middle';ctx.fillText(b.code.slice(0,2).toUpperCase(),b.x,b.y);ctx.restore();}
ctx.beginPath();ctx.arc(b.x,b.y,b.r,0,6.28);ctx.lineWidth=1.5;ctx.strokeStyle='#000';ctx.stroke();var pct=Math.max(0,b.hp/b.maxHp);if(pct<.999){var bw=b.r*1.6,bx=b.x-bw/2,by=b.y-b.r-7;ctx.fillStyle='#0009';ctx.fillRect(bx,by,bw,3);ctx.fillStyle=pct>.5?'#4caf50':pct>.25?'#ff9800':'#f44336';ctx.fillRect(bx,by,bw*pct,3);}if(b.flash>0){ctx.beginPath();ctx.arc(b.x,b.y,b.r,0,6.28);ctx.fillStyle='rgba(255,255,255,'+(b.flash*.4)+')';ctx.fill();}}
function drawAll(){ctx.setTransform(dpr,0,0,dpr,0,0);ctx.fillStyle='#0a0a0f';ctx.fillRect(0,0,W,H);if(state==='menu'||state==='select'||state==='cont')return;ctx.save();ctx.translate(shakeX,shakeY);ctx.beginPath();ctx.arc(cx,cy,arenaR,0,6.28);ctx.fillStyle='#f4f4ee';ctx.fill();ctx.lineWidth=2;ctx.strokeStyle='#ffb400';ctx.stroke();ctx.save();ctx.beginPath();ctx.arc(cx,cy,arenaR,0,6.28);ctx.clip();balls.forEach(function(b){if(b.alive)drawBall(b);});particles.forEach(function(p){ctx.globalAlpha=p.life;ctx.fillStyle=p.col;ctx.beginPath();ctx.arc(p.x,p.y,p.sz*p.life,0,6.28);ctx.fill();});ctx.globalAlpha=1;shocks.forEach(function(s){ctx.strokeStyle='rgba(255,255,255,'+s.life+')';ctx.lineWidth=2*s.life;ctx.beginPath();ctx.arc(s.x,s.y,s.r+(1-s.life)*70,0,6.28);ctx.stroke();});ctx.restore();ctx.restore();}
function loop(now){if(!last)last=now;var dt=Math.min((now-last)/16.667,3);last=now;var eff=dt*gameSpeed;if(state==='battle'){update(eff);updateCnt();}if(state==='victory'&&victory){victory.zoom=Math.min(2.6,1+(now-victory.t)/1000*1.5);}drawAll();requestAnimationFrame(loop);}
function setScreen(id){document.querySelectorAll('.screen').forEach(function(s){s.classList.remove('active');});if(id)q(id).classList.add('active');}
function populateSecret(){['f1','f2','f3'].forEach(function(id){var s=q(id),cur=s.value;s.innerHTML='<option value="">-- none --</option>';balls.forEach(function(b){var o=document.createElement('option');o.value=b.code;o.textContent=b.name;s.appendChild(o);});if(cur&&findBall(cur))s.value=cur;});}
function startBattle(codes,mode,target,label){if(!codes||codes.length<2)return;initAudio();if(audioCtx&&audioCtx.state==='suspended')audioCtx.resume();state='battle';targetSurv=target||1;roundLabel=label||'';transitioning=false;victory=null;setScreen(null);q('hud').classList.add('on');q('winner').classList.remove('show');lastConfig={codes:codes.slice(),mode:mode,target:target||1,label:label||''};spawnBalls(codes);populateSecret();}
function pickRandom(arr,n){var a=arr.slice();for(var i=a.length-1;i>0;i--){var j=Math.floor(Math.random()*(i+1)),t=a[i];a[i]=a[j];a[j]=t;}return a.slice(0,n);}
function goMenu(){state='menu';setScreen('menu');q('hud').classList.remove('on');q('winner').classList.remove('show');q('secret').classList.remove('show');balls=[];particles=[];shocks=[];victory=null;}
function buildGrid(codes){var grid=q('grid');grid.innerHTML='';codes.forEach(function(code){var d=document.createElement('div');d.className='cc';d.dataset.code=code;var im=document.createElement('img');im.src=flagUrl(code,'w160');im.alt=countryName(code);im.loading='lazy';d.appendChild(im);var label=document.createElement('div');label.className='tiny';label.textContent=countryName(code);d.appendChild(label);d.addEventListener('click',function(){d.classList.toggle('selected');});grid.appendChild(d);});}
function getSelected(){return Array.from(document.querySelectorAll('.cc.selected')).map(function(e){return e.dataset.code;});}
document.addEventListener('click',function(e){var t=e.target.closest('button');if(!t)return;if(t.dataset.mode){initAudio();var m=t.dataset.mode;if(m==='random'){var all=Object.keys(C);startBattle(pickRandom(all,3+Math.floor(Math.random()*8)),'random',1,'RANDOM');}else if(m==='worldcup'){startBattle(pickRandom(Object.keys(C),32),'worldcup',16,'WORLD CUP');}else if(m==='continental')setScreen('cont');else if(m==='custom'){buildGrid(Object.keys(C).concat(Object.keys(HISTORICAL)));q('selT').textContent='SELECT 2–30 FIGHTERS';setScreen('select');}else if(m==='historical'){buildGrid(Object.keys(HISTORICAL));q('selT').textContent='SELECT HISTORICAL STATES';setScreen('select');}}if(t.dataset.cont){var a=CONT[t.dataset.cont]||[];startBattle(a,'continental',1,t.dataset.cont.toUpperCase());}});
q('b1').onclick=function(){setScreen('menu');};q('b2').onclick=function(){setScreen('menu');};
q('goBtn').onclick=function(){var a=getSelected();if(a.length<2){q('selT').textContent='PICK AT LEAST 2 FIGHTERS';return;}startBattle(a.slice(0,30),'custom',1,'CUSTOM');};
q('again').onclick=function(){if(!lastConfig)return goMenu();startBattle(lastConfig.codes.slice(),lastConfig.mode,lastConfig.target,lastConfig.label);};
q('mm').onclick=goMenu;
q('spd').querySelectorAll('button').forEach(function(b){b.onclick=function(){q('spd').querySelectorAll('button').forEach(function(x){x.classList.remove('on');});b.classList.add('on');gameSpeed=parseFloat(b.dataset.s);};});
var ghostTaps=0,ghostTimer=null;
q('ghost').addEventListener('pointerdown',function(e){e.preventDefault();ghostTaps++;if(ghostTimer)clearTimeout(ghostTimer);ghostTimer=setTimeout(function(){ghostTaps=0;},3000);if(ghostTaps>=5){ghostTaps=0;q('secret').classList.toggle('show');if(q('secret').classList.contains('show'))populateSecret();}});
function closeGhostPanel(){q('secret').classList.remove('show');}
q('ghostClose').addEventListener('click',function(e){e.preventDefault();e.stopPropagation();closeGhostPanel();});
['f1','f2','f3'].forEach(function(id){q(id).addEventListener('change',applyForced);});
q('lk').onclick=function(){lockRes=!lockRes;q('lk').classList.toggle('on',lockRes);applyForced();};
document.addEventListener('keydown',function(e){if(e.key==='Escape'&&q('secret').classList.contains('show'))closeGhostPanel();});
resize();requestAnimationFrame(loop);
})();
</script>
</body>
</html>"""
    }
}
