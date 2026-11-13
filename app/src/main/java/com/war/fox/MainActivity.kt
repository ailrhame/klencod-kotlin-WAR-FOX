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

    override fun onBackPressed() {
        if (this::web.isInitialized && web.canGoBack()) web.goBack()
        else super.onBackPressed()
    }

    override fun onPause() { super.onPause(); if (this::web.isInitialized) web.onPause() }
    override fun onResume() { super.onResume(); if (this::web.isInitialized) web.onResume() }
    override fun onDestroy() {
        if (this::web.isInitialized) { web.loadUrl("about:blank"); web.destroy() }
        super.onDestroy()
    }
}

private val HTML_GAME: String = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover">
<title>WAR FOX</title>
<style>
*{margin:0;padding:0;box-sizing:border-box;user-select:none;-webkit-user-select:none;-webkit-tap-highlight-color:transparent}
html,body{width:100%;height:100%;overflow:hidden;background:#0a0a0f;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;color:#fff;touch-action:none;overscroll-behavior:none}
canvas{display:block;position:absolute;inset:0}
#screens{position:absolute;inset:0;pointer-events:none;z-index:10}
.screen{position:absolute;inset:0;display:none;flex-direction:column;align-items:center;justify-content:center;background:radial-gradient(circle at 50% 40%,rgba(30,30,40,0.96),rgba(8,8,12,0.99));padding:24px;pointer-events:auto;overflow-y:auto}
.screen.active{display:flex}
.title{font-size:44px;font-weight:900;letter-spacing:6px;background:linear-gradient(180deg,#ffd76a,#ffb400,#cc7a00);-webkit-background-clip:text;background-clip:text;color:transparent;filter:drop-shadow(0 0 20px rgba(255,180,0,0.5));margin-bottom:6px}
.subtitle{font-size:11px;letter-spacing:8px;color:#666;margin-bottom:36px}
.btn{background:linear-gradient(180deg,#2a2a35,#16161d);border:1px solid #3a3a48;color:#fff;padding:14px 28px;border-radius:12px;font-size:14px;font-weight:700;letter-spacing:1.5px;margin:5px 0;cursor:pointer;min-width:240px;text-transform:uppercase;transition:transform .1s,background .15s,border-color .15s;font-family:inherit}
.btn:active{transform:scale(0.95);background:linear-gradient(180deg,#ffb400,#cc9000);color:#0a0a0f;border-color:#ffb400}
.btn.small{padding:9px 16px;min-width:0;font-size:11px;letter-spacing:1px}
.btn.back{position:absolute;top:16px;left:16px;z-index:5}
.hdr{font-size:22px;font-weight:900;letter-spacing:4px;margin-bottom:18px;margin-top:60px;color:#ffb400;text-align:center}
.grid{display:grid;grid-template-columns:repeat(4,1fr);gap:8px;max-height:56vh;overflow-y:auto;padding:8px;width:100%;max-width:560px;background:rgba(0,0,0,0.3);border-radius:14px;border:1px solid #222}
.grid::-webkit-scrollbar{width:3px}.grid::-webkit-scrollbar-thumb{background:#444;border-radius:2px}
.grid::-webkit-scrollbar-track{background:transparent}
.cc{aspect-ratio:1;border-radius:50%;overflow:hidden;border:2px solid #2a2a35;cursor:pointer;position:relative;background:#111;transition:transform .15s,border-color .15s}
.cc.selected{border-color:#ffb400;box-shadow:0 0 14px rgba(255,180,0,0.7);transform:scale(0.94)}
.cc img{width:100%;height:100%;object-fit:cover;pointer-events:none}
#hud{position:absolute;top:0;left:0;right:0;padding:14px;display:none;justify-content:space-between;align-items:flex-start;z-index:5;pointer-events:none}
#hud.on{display:flex}
.spd{pointer-events:auto;display:flex;gap:4px;background:rgba(15,15,20,0.85);padding:4px;border-radius:9px;border:1px solid #2a2a35}
.spd button{background:transparent;border:none;color:#666;padding:6px 9px;border-radius:6px;font-size:11px;font-weight:800;cursor:pointer;font-family:inherit;letter-spacing:0.5px}
.spd button.on{background:#ffb400;color:#0a0a0f}
.counter{font-size:10px;letter-spacing:2px;color:#888;margin-top:8px;background:rgba(15,15,20,0.85);padding:6px 10px;border-radius:8px;border:1px solid #2a2a35;text-align:center}
#winner{position:absolute;inset:0;display:none;flex-direction:column;align-items:center;justify-content:center;background:radial-gradient(circle at 50% 45%,rgba(25,20,10,0.97),rgba(5,5,8,0.99));z-index:50;padding:28px;pointer-events:auto}
#winner.show{display:flex}
#winner .medal{font-size:64px;margin-bottom:12px;animation:popIn .5s cubic-bezier(.34,1.56,.64,1)}
#winner .flag{width:170px;height:170px;border-radius:50%;overflow:hidden;border:4px solid #ffb400;box-shadow:0 0 80px rgba(255,180,0,0.7),0 0 24px rgba(255,180,0,0.4) inset;margin-bottom:22px;animation:zoomIn .7s cubic-bezier(.34,1.56,.64,1)}
#winner .flag img{width:100%;height:100%;object-fit:cover;display:block}
#winner .nm{font-size:32px;font-weight:900;letter-spacing:3px;margin-bottom:6px;text-align:center}
#winner .plc{font-size:11px;letter-spacing:7px;color:#ffb400;margin-bottom:28px}
@keyframes zoomIn{from{transform:scale(.2);opacity:0}to{transform:scale(1);opacity:1}}
@keyframes popIn{from{transform:scale(0) rotate(-45deg);opacity:0}to{transform:scale(1) rotate(0);opacity:1}}
#ghost{position:absolute;top:0;right:0;width:60px;height:60px;z-index:100;opacity:0;background:transparent;pointer-events:auto}
#secret{position:absolute;top:14px;right:14px;width:250px;background:rgba(12,12,18,0.98);border:1px solid #ffb400;border-radius:14px;padding:14px;display:none;z-index:200;box-shadow:0 0 50px rgba(255,180,0,0.35),0 12px 30px rgba(0,0,0,0.7);pointer-events:auto}
#secret.show{display:block}
#secret h3{font-size:10px;letter-spacing:3px;color:#ffb400;margin-bottom:12px;text-align:center;font-weight:800}
#secret .f{margin-bottom:9px}
#secret .f label{display:block;font-size:9px;letter-spacing:1.5px;color:#777;margin-bottom:4px;font-weight:700}
#secret select{width:100%;background:#0a0a0f;border:1px solid #333;color:#fff;padding:7px 8px;border-radius:6px;font-size:11px;font-family:inherit;outline:none}
#secret select:focus{border-color:#ffb400}
#secret .tg{display:flex;justify-content:space-between;align-items:center;padding:10px 0 0;margin-top:10px;border-top:1px solid #1a1a24}
#secret .tg span{font-size:10px;color:#aaa;letter-spacing:1px;font-weight:700}
#secret .sw{width:38px;height:22px;background:#2a2a35;border-radius:11px;position:relative;cursor:pointer;transition:background .15s}
#secret .sw.on{background:#ffb400}
#secret .sw::after{content:'';position:absolute;top:2px;left:2px;width:18px;height:18px;background:#fff;border-radius:50%;transition:left .2s;box-shadow:0 1px 3px rgba(0,0,0,0.5)}
#secret .sw.on::after{left:18px}
</style>
</head>
<body>
<canvas id="c"></canvas>
<div id="hud">
  <div><div class="spd" id="spd">
    <button data-s="0.5">0.5x</button>
    <button data-s="1" class="on">1x</button>
    <button data-s="2">2x</button>
    <button data-s="4">4x</button>
  </div></div>
  <div class="counter" id="cnt">0 FIGHTERS</div>
</div>
<div id="screens">
  <div class="screen active" id="menu">
    <div class="title">WAR FOX</div>
    <div class="subtitle">FLAG BATTLE ROYALE</div>
    <button class="btn" data-mode="random">RANDOM BATTLE</button>
    <button class="btn" data-mode="worldcup">WORLD CUP 32</button>
    <button class="btn" data-mode="continental">CONTINENTAL</button>
    <button class="btn" data-mode="custom">CUSTOM PICK</button>
  </div>
  <div class="screen" id="select">
    <button class="btn small back" id="b1">&#8592; BACK</button>
    <div class="hdr" id="selT">SELECT FIGHTERS</div>
    <div class="grid" id="grid"></div>
    <button class="btn" id="goBtn" style="margin-top:14px">START BATTLE</button>
  </div>
  <div class="screen" id="cont">
    <button class="btn small back" id="b2">&#8592; BACK</button>
    <div class="hdr">PICK CONTINENT</div>
    <button class="btn" data-cont="europe">EUROPE</button>
    <button class="btn" data-cont="asia">ASIA</button>
    <button class="btn" data-cont="arab">ARAB WORLD</button>
    <button class="btn" data-cont="americas">AMERICAS</button>
    <button class="btn" data-cont="africa">AFRICA</button>
  </div>
</div>
<div id="winner">
  <div class="medal">&#127942;</div>
  <div class="flag"><img id="wimg" alt="" src=""></div>
  <div class="nm" id="wnm"></div>
  <div class="plc" id="wpl">CHAMPION</div>
  <button class="btn" id="again">PLAY AGAIN</button>
  <button class="btn" id="mm">MAIN MENU</button>
</div>
<div id="ghost"></div>
<div id="secret">
  <h3>&#9881; GHOST PANEL</h3>
  <div class="f"><label>1ST PLACE (GOLD)</label><select id="f1"><option value="">-- none --</option></select></div>
  <div class="f"><label>2ND PLACE (SILVER)</label><select id="f2"><option value="">-- none --</option></select></div>
  <div class="f"><label>3RD PLACE (BRONZE)</label><select id="f3"><option value="">-- none --</option></select></div>
  <div class="tg"><span>LOCK RESULTS</span><div class="sw" id="lk"></div></div>
</div>
<script>
(function(){
'use strict';

var C={us:'United States',gb:'United Kingdom',fr:'France',de:'Germany',it:'Italy',es:'Spain',pt:'Portugal',nl:'Netherlands',be:'Belgium',ch:'Switzerland',at:'Austria',pl:'Poland',se:'Sweden',no:'Norway',dk:'Denmark',fi:'Finland',ie:'Ireland',gr:'Greece',ua:'Ukraine',tr:'Turkey',ru:'Russia',jp:'Japan',cn:'China',kr:'South Korea',in:'India',pk:'Pakistan',id:'Indonesia',th:'Thailand',vn:'Vietnam',ph:'Philippines',my:'Malaysia',sg:'Singapore',ir:'Iran',sa:'Saudi Arabia',ae:'UAE',iq:'Iraq',eg:'Egypt',ma:'Morocco',dz:'Algeria',tn:'Tunisia',za:'South Africa',ng:'Nigeria',ke:'Kenya',et:'Ethiopia',gh:'Ghana',br:'Brazil',ar:'Argentina',mx:'Mexico',ca:'Canada',au:'Australia',nz:'New Zealand'};

var CONT={europe:['gb','fr','de','it','es','pt','nl','be','ch','at','pl','se','no','dk','fi','ie','gr','ua'],asia:['jp','cn','kr','in','pk','id','th','vn','ph','my','sg','ir','ru'],arab:['sa','ae','iq','eg','ma','dz','tn'],americas:['us','ca','br','ar','mx'],africa:['za','ng','ke','et','gh','eg','ma','dz','tn']};

var q=function(id){return document.getElementById(id);};
var canvas=q('c'),ctx=canvas.getContext('2d');
var W=0,H=0,cx=0,cy=0,dpr=1,arenaR=200,arenaScale=1;
var balls=[],particles=[],shocks=[];
var state='menu';
var gameSpeed=1;
var shakeX=0,shakeY=0,shakeMag=0;
var last=0;
var audioCtx=null;
var lockRes=false;
var targetSurv=1;
var transitioning=false;
var victory=null;
var eliminationOrder=[];
var lastConfig=null;
var roundLabel='';

function resize(){
  W=window.innerWidth;H=window.innerHeight;
  dpr=window.devicePixelRatio||1;
  canvas.width=W*dpr;canvas.height=H*dpr;
  canvas.style.width=W+'px';canvas.style.height=H+'px';
  ctx.setTransform(dpr,0,0,dpr,0,0);
  cx=W/2;cy=H/2;
  arenaR=Math.min(W,H)*0.43*arenaScale;
}
window.addEventListener('resize',resize);
window.addEventListener('orientationchange',function(){setTimeout(resize,150);});

function initAudio(){
  if(audioCtx)return;
  try{audioCtx=new (window.AudioContext||window.webkitAudioContext)();}catch(e){audioCtx=null;}
}

function sndClack(v){
  if(!audioCtx)return;
  var t=audioCtx.currentTime;
  var o=audioCtx.createOscillator(),g=audioCtx.createGain();
  o.type='square';
  o.frequency.setValueAtTime(600+Math.random()*500,t);
  o.frequency.exponentialRampToValueAtTime(120,t+0.06);
  g.gain.setValueAtTime(Math.min(0.12,v*0.015),t);
  g.gain.exponentialRampToValueAtTime(0.0001,t+0.09);
  o.connect(g);g.connect(audioCtx.destination);
  o.start(t);o.stop(t+0.1);
}

function sndShatter(){
  if(!audioCtx)return;
  var t=audioCtx.currentTime;
  for(var i=0;i<5;i++){
    var o=audioCtx.createOscillator(),g=audioCtx.createGain();
    o.type='sawtooth';
    o.frequency.setValueAtTime(200+Math.random()*800,t+i*0.015);
    o.frequency.exponentialRampToValueAtTime(60,t+i*0.015+0.15);
    g.gain.setValueAtTime(0.06,t+i*0.015);
    g.gain.exponentialRampToValueAtTime(0.0001,t+i*0.015+0.16);
    o.connect(g);g.connect(audioCtx.destination);
    o.start(t+i*0.015);o.stop(t+i*0.015+0.18);
  }
}

function sndWin(){
  if(!audioCtx)return;
  var t=audioCtx.currentTime;
  var notes=[523,659,784,1047,1319];
  for(var i=0;i<notes.length;i++){
    var o=audioCtx.createOscillator(),g=audioCtx.createGain();
    o.type='triangle';
    o.frequency.value=notes[i];
    g.gain.setValueAtTime(0,t+i*0.12);
    g.gain.linearRampToValueAtTime(0.12,t+i*0.12+0.02);
    g.gain.exponentialRampToValueAtTime(0.0001,t+i*0.12+0.5);
    o.connect(g);g.connect(audioCtx.destination);
    o.start(t+i*0.12);o.stop(t+i*0.12+0.55);
  }
}

function Ball(code){
  this.code=code;
  this.name=C[code]||code.toUpperCase();
  this.r=26;
  this.x=0;this.y=0;this.vx=0;this.vy=0;
  this.mass=1;
  this.hp=100;this.maxHp=100;
  this.forced=0;
  this.alive=true;
  this.trail=[];
  this.img=null;
  this.loaded=false;
  this.flash=0;
  this.angle=Math.random()*Math.PI*2;
  this.spin=(Math.random()-0.5)*0.04;
}

Ball.prototype.load=function(){
  var self=this;
  var im=new Image();
  im.onload=function(){self.img=im;self.loaded=true;};
  im.onerror=function(){self.loaded=false;};
  im.src='https://flagcdn.com/w320/'+self.code+'.png';
};

function spawnBalls(codes){
  balls=[];particles=[];shocks=[];eliminationOrder=[];
  var n=codes.length;
  var baseR=Math.max(14,Math.min(arenaR/(Math.sqrt(n)*1.35),32));
  for(var i=0;i<n;i++){
    var b=new Ball(codes[i]);
    b.r=baseR;
    var ang=(i/n)*Math.PI*2;
    var dist=arenaR*0.62;
    b.x=cx+Math.cos(ang)*dist;
    b.y=cy+Math.sin(ang)*dist;
    var vx=(Math.random()-0.5)*6,vy=(Math.random()-0.5)*6;
    b.vx=vx;b.vy=vy;
    b.load();
    balls.push(b);
  }
  applyForced();
  updateCnt();
}

function findBall(code){
  for(var i=0;i<balls.length;i++)if(balls[i].code===code)return balls[i];
  return null;
}

function applyForced(){
  var sels=[q('f1'),q('f2'),q('f3')];
  var ranks=[1,2,3];
  for(var i=0;i<balls.length;i++)balls[i].forced=0;
  for(var i=0;i<3;i++){
    var code=sels[i].value;
    if(!code)continue;
    var b=findBall(code);
    if(b&&b.alive)b.forced=ranks[i];
  }
}

function dmgMult(b){
  if(!lockRes||!b.forced)return 1;
  if(b.forced===1)return 0.15;
  if(b.forced===2)return 0.30;
  if(b.forced===3)return 0.50;
  return 1;
}

function spawnSparks(x,y,n,col){
  for(var i=0;i<n;i++){
    var a=Math.random()*Math.PI*2,s=Math.random()*3.5+1;
    particles.push({x:x,y:y,vx:Math.cos(a)*s,vy:Math.sin(a)*s,life:1,col:col,sz:Math.random()*2.5+1});
  }
}

function spawnExplosion(x,y,r){
  for(var i=0;i<44;i++){
    var a=Math.random()*Math.PI*2,s=Math.random()*7+2;
    particles.push({x:x,y:y,vx:Math.cos(a)*s,vy:Math.sin(a)*s,life:1,col:'#fff',sz:Math.random()*4+1.5});
  }
  shocks.push({x:x,y:y,r:r,life:1});
}

function addShake(m){shakeMag=Math.min(shakeMag+m,22);}

function eliminate(b){
  if(!b.alive)return;
  b.alive=false;
  spawnExplosion(b.x,b.y,b.r);
  sndShatter();
  addShake(14);
  eliminationOrder.push(b);
  updateCnt();
  checkEnd();
}

function updateBall(b,dt){
  if(!b.alive)return;
  if(lockRes&&b.forced>0){
    b.hp=Math.min(b.maxHp,b.hp+0.55*dt);
    if(b.forced===1){
      var best=null,bd=1e9;
      for(var i=0;i<balls.length;i++){
        var o=balls[i];
        if(!o.alive||o.forced===1)continue;
        var ddx=o.x-b.x,ddy=o.y-b.y;
        var d2=ddx*ddx+ddy*ddy;
        if(d2<bd){bd=d2;best=o;}
      }
      if(best&&bd>1){
        var d=Math.sqrt(bd);
        b.vx+=(best.x-b.x)/d*0.045*dt;
        b.vy+=(best.y-b.y)/d*0.045*dt;
      }
    }
  }
  b.x+=b.vx*dt;
  b.y+=b.vy*dt;
  var dx=b.x-cx,dy=b.y-cy;
  var dist=Math.sqrt(dx*dx+dy*dy);
  var lim=arenaR-b.r;
  if(dist>lim){
    var nx=dx/dist,ny=dy/dist;
    b.x=cx+nx*lim;b.y=cy+ny*lim;
    var dot=b.vx*nx+b.vy*ny;
    b.vx-=2*dot*nx;b.vy-=2*dot*ny;
    b.vx*=0.9;b.vy*=0.9;
    if(Math.abs(dot)>2)spawnSparks(b.x,b.y,3,'#ff4444');
  }
  b.vx*=0.992;b.vy*=0.992;
  if(Math.abs(b.vx)<0.015)b.vx=0;
  if(Math.abs(b.vy)<0.015)b.vy=0;
  b.angle+=b.spin*dt;
  b.trail.push({x:b.x,y:b.y,a:1});
  if(b.trail.length>10)b.trail.shift();
  for(var i=0;i<b.trail.length;i++)b.trail[i].a*=0.88;
  if(b.flash>0)b.flash-=dt*0.05;
  if(b.flash<0)b.flash=0;
}

function collide(a,b){
  var dx=b.x-a.x,dy=b.y-a.y;
  var d=Math.sqrt(dx*dx+dy*dy);
  var minD=a.r+b.r;
  if(d>=minD||d===0)return;
  var nx=dx/d,ny=dy/d;
  var ov=minD-d;
  var tm=a.mass+b.mass;
  a.x-=nx*ov*(b.mass/tm);
  a.y-=ny*ov*(b.mass/tm);
  b.x+=nx*ov*(a.mass/tm);
  b.y+=ny*ov*(a.mass/tm);
  var rvx=b.vx-a.vx,rvy=b.vy-a.vy;
  var vel=rvx*nx+rvy*ny;
  if(vel>0)return;
  var e=0.94;
  var j=-(1+e)*vel/(1/a.mass+1/b.mass);
  var ix=j*nx,iy=j*ny;
  a.vx-=ix/a.mass;a.vy-=iy/a.mass;
  b.vx+=ix/b.mass;b.vy+=iy/b.mass;
  var impact=Math.abs(vel);
  if(impact>1.2){
    var dmg=impact*0.55;
    a.hp-=dmg*(b.mass/tm)*dmgMult(a);
    b.hp-=dmg*(a.mass/tm)*dmgMult(b);
    a.flash=1;b.flash=1;
    spawnSparks((a.x+b.x)/2,(a.y+b.y)/2,Math.min(12,Math.floor(impact*1.8)),'#ffffff');
    addShake(impact*0.45);
    sndClack(impact);
    if(a.hp<=0)eliminate(a);
    if(b.hp<=0)eliminate(b);
  }
}

function forcedEndgame(dt){
  if(!lockRes)return;
  var alive=[],forced=[];
  for(var i=0;i<balls.length;i++)if(balls[i].alive){alive.push(balls[i]);if(balls[i].forced)forced.push(balls[i]);}
  if(forced.length!==alive.length||forced.length<=1)return;
  var worst=null;
  for(var i=0;i<forced.length;i++){
    if(!worst||forced[i].forced>worst.forced)worst=forced[i];
  }
  if(worst&&worst.forced>1){
    worst.hp-=30*dt;
    worst.flash=Math.max(worst.flash,0.5);
    if(worst.hp<=0)eliminate(worst);
  }
}

function checkEnd(){
  if(transitioning)return;
  var alive=[];
  for(var i=0;i<balls.length;i++)if(balls[i].alive)alive.push(balls[i]);
  if(alive.length<=targetSurv){
    transitioning=true;
    if(targetSurv===1){
      setTimeout(function(){endGame(alive[0]||null);},700);
    }else{
      setTimeout(function(){nextRound(alive);},900);
    }
  }
}

function nextRound(survivors){
  balls=survivors.slice();
  targetSurv=Math.max(1,Math.floor(targetSurv/2));
  var n=balls.length;
  for(var i=0;i<n;i++){
    var b=balls[i];
    var ang=(i/n)*Math.PI*2;
    var dist=arenaR*0.62;
    b.x=cx+Math.cos(ang)*dist;
    b.y=cy+Math.sin(ang)*dist;
    b.vx=(Math.random()-0.5)*8;
    b.vy=(Math.random()-0.5)*8;
    b.hp=b.maxHp;
    b.flash=0;
    b.trail=[];
  }
  particles=[];shocks=[];
  transitioning=false;
  updateCnt();
}

function endGame(winner){
  state='victory';
  sndWin();
  if(!winner){state='battle';return;}
  victory={b:winner,zoom:1,t:performance.now()};
  setTimeout(function(){showWinner(winner);},1100);
}

function showWinner(w){
  q('wimg').src='https://flagcdn.com/w320/'+w.code+'.png';
  q('wnm').textContent=w.name;
  q('wpl').textContent='CHAMPION';
  q('winner').classList.add('show');
}

function updateCnt(){
  var alive=0;
  for(var i=0;i<balls.length;i++)if(balls[i].alive)alive++;
  var lbl=alive+' FIGHTER'+(alive===1?'':'S');
  if(targetSurv>1)lbl+=' / TARGET '+targetSurv;
  q('cnt').textContent=roundLabel?roundLabel+' - '+lbl:lbl;
}

function update(dt){
  if(transitioning)return;
  shakeMag*=0.88;
  shakeX=(Math.random()-0.5)*shakeMag;
  shakeY=(Math.random()-0.5)*shakeMag;
  for(var i=0;i<balls.length;i++)updateBall(balls[i],dt);
  for(var i=0;i<balls.length;i++){
    for(var j=i+1;j<balls.length;j++){
      if(balls[i].alive&&balls[j].alive)collide(balls[i],balls[j]);
    }
  }
  forcedEndgame(dt);
  for(var i=particles.length-1;i>=0;i--){
    var p=particles[i];
    p.x+=p.vx*dt;p.y+=p.vy*dt;
    p.vx*=0.95;p.vy*=0.95;
    p.life-=0.022*dt;
    if(p.life<=0)particles.splice(i,1);
  }
  for(var i=shocks.length-1;i>=0;i--){
    shocks[i].life-=0.028*dt;
    if(shocks[i].life<=0)shocks.splice(i,1);
  }
}

function drawFlagBall(b){
  if(b.trail.length>1){
    for(var i=0;i<b.trail.length;i++){
      var t=b.trail[i];
      ctx.globalAlpha=t.a*0.22;
      ctx.fillStyle='#222';
      ctx.beginPath();
      ctx.arc(t.x,t.y,b.r*(i/b.trail.length)*0.85,0,Math.PI*2);
      ctx.fill();
    }
    ctx.globalAlpha=1;
  }
  ctx.beginPath();
  ctx.arc(b.x+2,b.y+3,b.r,0,Math.PI*2);
  ctx.fillStyle='rgba(0,0,0,0.18)';
  ctx.fill();
  ctx.save();
  ctx.beginPath();
  ctx.arc(b.x,b.y,b.r,0,Math.PI*2);
  ctx.clip();
  if(b.loaded&&b.img){
    ctx.translate(b.x,b.y);
    ctx.rotate(b.angle);
    var size=b.r*2;
    var iw=b.img.width,ih=b.img.height;
    var ar=iw/ih;
    var dw,dh;
    if(ar>=1){dh=size;dw=size*ar;}else{dw=size;dh=size/ar;}
    ctx.drawImage(b.img,-dw/2,-dh/2,dw,dh);
    ctx.rotate(-b.angle);
    ctx.translate(-b.x,-b.y);
    var gr=ctx.createRadialGradient(b.x-b.r*0.32,b.y-b.r*0.32,b.r*0.1,b.x,b.y,b.r*1.15);
    gr.addColorStop(0,'rgba(255,255,255,0.35)');
    gr.addColorStop(0.7,'rgba(0,0,0,0)');
    gr.addColorStop(1,'rgba(0,0,0,0.4)');
    ctx.fillStyle=gr;
    ctx.fillRect(b.x-b.r,b.y-b.r,b.r*2,b.r*2);
  }else{
    ctx.fillStyle='#555';
    ctx.fillRect(b.x-b.r,b.y-b.r,b.r*2,b.r*2);
    ctx.fillStyle='#fff';
    ctx.font='bold '+(b.r*0.7)+'px sans-serif';
    ctx.textAlign='center';ctx.textBaseline='middle';
    ctx.fillText(b.code.toUpperCase().slice(0,2),b.x,b.y);
  }
  ctx.restore();
  ctx.beginPath();
  ctx.arc(b.x,b.y,b.r,0,Math.PI*2);
  ctx.lineWidth=1.5;
  ctx.strokeStyle='rgba(0,0,0,0.85)';
  ctx.stroke();
  var pct=b.hp/b.maxHp;
  if(pct<0.999){
    var bw=b.r*1.6,bh=3,bx=b.x-bw/2,by=b.y-b.r-8;
    ctx.fillStyle='rgba(0,0,0,0.6)';
    ctx.fillRect(bx,by,bw,bh);
    ctx.fillStyle=pct>0.5?'#4caf50':pct>0.25?'#ff9800':'#f44336';
    ctx.fillRect(bx,by,bw*pct,bh);
  }
  if(b.flash>0){
    ctx.beginPath();
    ctx.arc(b.x,b.y,b.r,0,Math.PI*2);
    ctx.fillStyle='rgba(255,255,255,'+(b.flash*0.5)+')';
    ctx.fill();
  }
}

function drawAll(){
  ctx.setTransform(dpr,0,0,dpr,0,0);
  ctx.fillStyle='#FAFAFA';
  ctx.fillRect(0,0,W,H);
  if(state==='menu'||state==='select'||state==='cont'){
    ctx.save();
    ctx.globalAlpha=0.06;
    for(var i=0;i<50;i++){
      var x=(i*137.5)%W,y=(i*211.3)%H;
      ctx.beginPath();ctx.arc(x,y,2,0,Math.PI*2);
      ctx.fillStyle='#000';ctx.fill();
    }
    ctx.restore();
    return;
  }
  ctx.save();
  ctx.translate(shakeX,shakeY);
  if(state==='victory'&&victory){
    var z=victory.zoom;
    ctx.translate(W/2,H/2);
    ctx.scale(z,z);
    ctx.translate(-victory.b.x,-victory.b.y);
  }
  ctx.beginPath();
  ctx.arc(cx,cy,arenaR,0,Math.PI*2);
  ctx.fillStyle='#F4F4EE';
  ctx.fill();
  ctx.lineWidth=2;
  ctx.strokeStyle='#0a0a0f';
  ctx.stroke();
  ctx.save();
  ctx.beginPath();
  ctx.arc(cx,cy,arenaR,0,Math.PI*2);
  ctx.clip();
  for(var i=0;i<balls.length;i++)if(balls[i].alive)drawFlagBall(balls[i]);
  for(var i=0;i<particles.length;i++){
    var p=particles[i];
    ctx.globalAlpha=p.life;
    ctx.fillStyle=p.col;
    ctx.beginPath();
    ctx.arc(p.x,p.y,p.sz*p.life,0,Math.PI*2);
    ctx.fill();
  }
  ctx.globalAlpha=1;
  for(var i=0;i<shocks.length;i++){
    var s=shocks[i];
    ctx.strokeStyle='rgba(255,255,255,'+s.life+')';
    ctx.lineWidth=2*s.life;
    ctx.beginPath();
    ctx.arc(s.x,s.y,s.r+(1-s.life)*70,0,Math.PI*2);
    ctx.stroke();
  }
  ctx.restore();
  ctx.restore();
}

function loop(now){
  if(!last)last=now;
  var dt=Math.min((now-last)/16.667,3);
  last=now;
  var eff=dt*gameSpeed;
  if(state==='battle'){update(eff);updateCnt();}
  if(state==='victory'&&victory){
    var t=(now-victory.t)/1000;
    victory.zoom=1+Math.min(t*1.6,1.6);
    for(var i=particles.length-1;i>=0;i--){
      var p=particles[i];
      p.x+=p.vx*eff*0.3;p.y+=p.vy*eff*0.3;
      p.vx*=0.95;p.vy*=0.95;
      p.life-=0.015*eff;
      if(p.life<=0)particles.splice(i,1);
    }
  }
  drawAll();
  requestAnimationFrame(loop);
}

function setScreen(id){
  var ss=document.querySelectorAll('.screen');
  for(var i=0;i<ss.length;i++)ss[i].classList.remove('active');
  if(id)q(id).classList.add('active');
}

function startBattle(codes,mode,target,label){
  if(!codes||codes.length<2)return;
  initAudio();
  if(audioCtx&&audioCtx.state==='suspended')audioCtx.resume();
  arenaScale=1;
  state='battle';
  targetSurv=target||1;
  roundLabel=label||'';
  transitioning=false;
  victory=null;
  setScreen(null);
  q('hud').classList.add('on');
  q('winner').classList.remove('show');
  lastConfig={codes:codes.slice(),mode:mode,target:target,label:label};
  spawnBalls(codes);
  populateSecret();
}

function pickRandom(arr,n){
  var a=arr.slice();
  for(var i=a.length-1;i>0;i--){var j=Math.floor(Math.random()*(i+1));var t=a[i];a[i]=a[j];a[j]=t;}
  return a.slice(0,n);
}

function goMenu(){
  state='menu';
  setScreen('menu');
  q('hud').classList.remove('on');
  q('winner').classList.remove('show');
  q('secret').classList.remove('show');
  balls=[];particles=[];shocks=[];victory=null;
}

function buildGrid(codes,multi){
  var grid=q('grid');
  grid.innerHTML='';
  for(var i=0;i<codes.length;i++){
    var code=codes[i];
    var d=document.createElement('div');
    d.className='cc';
    d.dataset.code=code;
    var im=document.createElement('img');
    im.src='https://flagcdn.com/w160/'+code+'.png';
    im.alt=C[code]||code;
    im.loading='lazy';
    d.appendChild(im);
    if(multi){
      d.addEventListener('click',function(){
        this.classList.toggle('selected');
      });
    }
    grid.appendChild(d);
  }
}

function getSelected(){
  var out=[];
  var el=document.querySelectorAll('.cc.selected');
  for(var i=0;i<el.length;i++)out.push(el[i].dataset.code);
  return out;
}

function populateSecret(){
  var sels=[q('f1'),q('f2'),q('f3')];
  for(var i=0;i<sels.length;i++){
    var s=sels[i];
    var cur=s.value;
    var html='<option value="">-- none --</option>';
    for(var j=0;j<balls.length;j++){
      html+='<option value="'+balls[j].code+'">'+balls[j].name+'</option>';
    }
    s.innerHTML=html;
    if(cur){
      for(var j=0;j<balls.length;j++){
        if(balls[j].code===cur){s.value=cur;break;}
      }
    }
  }
}

document.addEventListener('click',function(e){
  var t=e.target;
  if(t.classList.contains('btn')&&t.dataset.mode){
    initAudio();
    if(audioCtx&&audioCtx.state==='suspended')audioCtx.resume();
    var m=t.dataset.mode;
    if(m==='random'){
      var all=Object.keys(C);
      var n=3+Math.floor(Math.random()*8);
      startBattle(pickRandom(all,n),'random',1,'RANDOM');
    }else if(m==='worldcup'){
      var all=Object.keys(C);
      startBattle(pickRandom(all,32),'worldcup',16,'GROUP');
    }else if(m==='continental'){
      setScreen('cont');
    }else if(m==='custom'){
      var all=Object.keys(C);
      buildGrid(all,true);
      q('selT').textContent='SELECT 2-30 FIGHTERS';
      setScreen('select');
    }
  }
  if(t.dataset.cont){
    var codes=CONT[t.dataset.cont]||[];
    if(codes.length<2)return;
    startBattle(codes.slice(),'cont',1,t.dataset.cont.toUpperCase());
  }
});

q('b1').addEventListener('click',function(){setScreen('menu');});
q('b2').addEventListener('click',function(){setScreen('menu');});

q('goBtn').addEventListener('click',function(){
  var sel=getSelected();
  if(sel.length<2){
    q('selT').textContent='PICK AT LEAST 2 ('+sel.length+' SELECTED)';
    return;
  }
  if(sel.length>30)sel=sel.slice(0,30);
  startBattle(sel,'custom',1,'CUSTOM');
});

q('again').addEventListener('click',function(){
  if(!lastConfig)return goMenu();
  startBattle(lastConfig.codes.slice(),lastConfig.mode,lastConfig.mode==='worldcup'?16:1,lastConfig.label||'');
});

q('mm').addEventListener('click',goMenu);

var spdBtns=q('spd').querySelectorAll('button');
for(var i=0;i<spdBtns.length;i++){
  spdBtns[i].addEventListener('click',function(){
    for(var j=0;j<spdBtns.length;j++)spdBtns[j].classList.remove('on');
    this.classList.add('on');
    gameSpeed=parseFloat(this.dataset.s);
  });
}

var ghostTaps=0,ghostT=null;
q('ghost').addEventListener('pointerdown',function(e){
  e.preventDefault();
  ghostTaps++;
  if(ghostT)clearTimeout(ghostT);
  ghostT=setTimeout(function(){ghostTaps=0;},3000);
  if(ghostTaps>=5){
    ghostTaps=0;
    q('secret').classList.toggle('show');
    if(q('secret').classList.contains('show'))populateSecret();
  }
});

q('f1').addEventListener('change',function(){applyForced();});
q('f2').addEventListener('change',function(){applyForced();});
q('f3').addEventListener('change',function(){applyForced();});

q('lk').addEventListener('click',function(){
  lockRes=!lockRes;
  this.classList.toggle('on',lockRes);
  applyForced();
});

resize();
requestAnimationFrame(loop);

})();
</script>
</body>
</html>
"""